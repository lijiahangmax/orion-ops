<template>
  <div>
    <!-- 机器模态框 -->
    <div class="machine-add-modal-container">
      <a-modal v-model="visible"
               :title="title"
               :width="700"
               :zIndex="805"
               :okButtonProps="{props: {disabled: loading}}"
               :dialogStyle="{ top: '90px' }"
               @ok="check"
               @cancel="close">
        <a-spin :spinning="loading">
          <div class="machine-info-form">
            <a-form :form="form" v-bind="layout">
              <a-form-item label="名称">
                <a-input v-decorator="decorators.name"/>
              </a-form-item>
              <a-form-item label="tag">
                <a-input v-decorator="decorators.tag"/>
              </a-form-item>
              <a-form-item label="ssh信息" style="margin-bottom: 0">
                <a-form-item style="display: inline-block; width: 35%">
                  <a-input addon-before="user" placeholder="用户" v-decorator="decorators.username"/>
                </a-form-item>
                <a-form-item style="display: inline-block; width: 40%">
                  <a-input addon-before="@" placeholder="主机" v-decorator="decorators.host"/>
                </a-form-item>
                <a-form-item style="display: inline-block; width: 25%">
                  <a-input addon-before="-p" placeholder="端口" v-decorator="decorators.sshPort"/>
                </a-form-item>
              </a-form-item>
              <a-form-item label="认证方式" style="margin-bottom: 0">
                <a-form-item style="display: inline-block; width: 30%">
                  <a-select v-decorator="decorators.authType">
                    <a-select-option :value="type.value" v-for="type in $enum.MACHINE_AUTH_TYPE" :key="type.value">
                      {{ type.label }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item style="display: inline-block; width: 70%">
                  <a-input-password v-decorator="decorators.password" v-if="form.getFieldValue('authType') === 1" placeholder="密码"/>
                  <a class="add-secret-key" v-else @click="addKey">添加秘钥</a>
                </a-form-item>
              </a-form-item>
              <a-form-item label="代理">
                <a-select class="proxy-selector" placeholder="请选择" v-decorator="decorators.proxyId" style="width: 75%" allowClear>
                  <a-select-option v-for="proxy in proxyList" :key="proxy.id" :value="proxy.id">
                    <div class="proxy-select-option">
                      <span>{{ proxy.host }}:{{ proxy.port }}</span>
                      <a-tag :color="$enum.valueOf($enum.MACHINE_PROXY_TYPE, proxy.type).color">
                        {{ $enum.valueOf($enum.MACHINE_PROXY_TYPE, proxy.type).label }}
                      </a-tag>
                    </div>
                  </a-select-option>
                </a-select>
                <a class="reload-proxy" title="刷新" @click="getProxyList">
                  <a-icon type="reload"/>
                </a>
              </a-form-item>
              <a-form-item label="描述">
                <a-textarea v-decorator="decorators.description"/>
              </a-form-item>
            </a-form>
          </div>
        </a-spin>
      </a-modal>
    </div>
    <!-- 事件 -->
    <div class="machine-add-modal-event-container">
      <!-- 添加秘钥 -->
      <AddMachineKeyModal ref="addKeyModal"/>
    </div>
  </div>
</template>

<script>
import AddMachineKeyModal from '../machine/AddMachineKeyModal'
import { pick } from 'lodash'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 15 }
}

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入名称'
      }, {
        max: 32,
        message: '名称长度不能大于32位'
      }]
    }],
    tag: ['tag', {
      rules: [{
        required: true,
        message: '请输入tag'
      }, {
        max: 32,
        message: 'tag长度不能大于32位'
      }]
    }],
    username: ['username', {
      rules: [{
        required: true,
        validator: this.validateUsername
      }]
    }],
    host: ['host', {
      rules: [{
        validator: this.validateHost
      }]
    }],
    sshPort: ['sshPort', {
      initialValue: 22,
      rules: [{
        validator: this.validatePort
      }]
    }],
    authType: ['authType', {
      initialValue: 2
    }],
    password: ['password', {
      rules: [{
        max: 128,
        message: '密码长度不能大于128位'
      }, {
        validator: this.validatePassword
      }]
    }],
    proxyId: ['proxyId'],
    description: ['description', {
      rules: [{
        max: 64,
        message: '描述长度不能大于64位'
      }]
    }]
  }
}

export default {
  name: 'AddMachineModal',
  components: {
    AddMachineKeyModal
  },
  data: function() {
    return {
      id: null,
      visible: false,
      title: null,
      loading: false,
      proxyList: [],
      record: null,
      layout,
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    validateUsername(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (value.length > 32) {
        callback(new Error('用户名长度不能大于32位'))
      } else {
        callback()
      }
    },
    validateHost(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入主机'))
      } else if (value.length > 32) {
        callback(new Error('主机长度不能大于32位'))
      } else {
        callback()
      }
    },
    validatePort(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入端口'))
      } else if (parseInt(value) < 2 || parseInt(value) > 65534) {
        callback(new Error('端口必须在2~65534之间'))
      } else {
        callback()
      }
    },
    validatePassword(rule, value, callback) {
      const authType = this.form.getFieldValue('authType')
      if (!value && authType === 1 && !this.id) {
        callback(new Error('新增时用户名和密码须同时存在'))
      } else {
        callback()
      }
    },
    add() {
      this.title = '新增代理'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改代理'
      this.$api.getMachineDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      if (!row.proxyId) {
        row.proxyId = undefined
      }
      this.record = pick(Object.assign({}, row), 'name', 'tag', 'username', 'host',
        'sshPort', 'authType', 'proxyId', 'description')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    addKey() {
      this.$refs.addKeyModal.setMask(false)
      this.$refs.addKeyModal.add()
    },
    getProxyList() {
      this.$api.getMachineProxyList({ limit: 10000 })
        .then(({ data: { rows } }) => {
          this.proxyList = rows
        })
    },
    check() {
      this.loading = true
      this.form.validateFields((err, values) => {
        if (err) {
          this.loading = false
          return
        }
        this.submit(values)
      })
    },
    async submit(values) {
      let res
      try {
        if (!this.id) {
          // 添加
          res = await this.$api.addMachine({ ...values })
        } else {
          // 修改
          res = await this.$api.updateMachine({
            ...values,
            id: this.id
          })
        }
        if (!this.id) {
          this.$message.success('添加成功')
          this.$emit('added', res.data)
        } else {
          this.$message.success('修改成功')
          this.$emit('updated', res.data)
        }
        this.close()
      } catch (e) {
        // ignore
      }
      this.loading = false
    },
    close() {
      this.visible = false
      this.loading = false
    }
  },
  mounted() {
    this.getProxyList()
  }
}
</script>

<style scoped>

.add-secret-key, .add-proxy {
  margin: 5px 0 0 30px;
}

.reload-proxy {
  font-size: 17px;
  margin: 8px 0 0 10px;
}

.proxy-select-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.machine-info-form {
  margin-top: 15px;
}

.proxy-selector /deep/ .ant-select-selection-selected-value {
  width: 100%;
}

</style>
