<template>
  <div class="machine-wrapper">
    <!-- 表单 -->
    <a-spin :spinning="loading">
      <div class="machine-info-form">
        <a-form :form="form" v-bind="layout">
          <h2 class="m0">基本信息</h2>
          <a-divider class="title-divider"/>
          <!-- GROUP_FLAG -->
          <a-form-item v-show="false" label="机器分组">
            <a-tree-select class="machine-input"
                           v-decorator="decorators.groupIdList"
                           :dropdown-style="{maxHeight: '400px', overflow: 'auto'}"
                           :multiple="true"
                           :treeData="treeData"
                           treeNodeLabelProp="label"
                           allowClear/>
          </a-form-item>
          <a-form-item label="机器名称">
            <a-input class="machine-input" v-decorator="decorators.name" allowClear/>
          </a-form-item>
          <a-form-item label="唯一标识">
            <a-input class="machine-input" v-decorator="decorators.tag" allowClear/>
          </a-form-item>
          <a-form-item label="机器描述">
            <a-textarea class="machine-input" v-decorator="decorators.description" allowClear/>
          </a-form-item>
          <h2 class="m0">SSH 配置</h2>
          <a-divider class="title-divider"/>
          <a-form-item label="主机IP">
            <a-input class="machine-input" v-decorator="decorators.host" allowClear/>
            <a class="option-button" @click="testPing">ping</a>
          </a-form-item>
          <a-form-item label="用户名">
            <a-input class="machine-input" v-decorator="decorators.username" allowClear/>
          </a-form-item>
          <a-form-item label="SSH 端口">
            <a-input class="machine-input" v-decorator="decorators.sshPort" allowClear/>
          </a-form-item>
          <a-form-item label="认证方式">
            <a-radio-group class="machine-input" v-decorator="decorators.authType" buttonStyle="solid">
              <a-radio-button :value="type.value" v-for="type in MACHINE_AUTH_TYPE" :key="type.value">
                {{ type.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-item>
          <a-form-item label="认证密码" v-show="form.getFieldValue('authType') === MACHINE_AUTH_TYPE.PASSWORD.value">
            <a-input-password class="machine-input" v-decorator="decorators.password" allowClear/>
          </a-form-item>
          <a-form-item label="认证秘钥" v-show="form.getFieldValue('authType') === MACHINE_AUTH_TYPE.SECRET_KEY.value">
            <a-select class="machine-input" placeholder="请选择" v-decorator="decorators.keyId" allowClear>
              <a-select-option v-for="key in keyList" :key="key.id" :value="key.id">
                {{ key.name }}
              </a-select-option>
            </a-select>
            <a class="reload-icon" title="刷新" @click="getKeyList">
              <a-icon type="reload"/>
            </a>
            <a class="option-button" @click="addKey">新增</a>
          </a-form-item>
          <a-form-item label="机器代理">
            <a-select class="machine-input proxy-selector" placeholder="请选择" v-decorator="decorators.proxyId" allowClear>
              <a-select-option v-for="proxy in proxyList" :key="proxy.id" :value="proxy.id">
                <div class="proxy-select-option">
                  <span>{{ proxy.host }}:{{ proxy.port }}</span>
                  <a-tag :color="proxy.type | formatProxyType('color')">
                    {{ proxy.type | formatProxyType('label') }}
                  </a-tag>
                </div>
              </a-select-option>
            </a-select>
            <a class="reload-icon" title="刷新" @click="getProxyList">
              <a-icon type="reload"/>
            </a>
            <a class="option-button" @click="addProxy">新增</a>
          </a-form-item>
        </a-form>
      </div>
    </a-spin>
    <!-- 页脚 -->
    <div class="machine-footer">
      <a-button @click="testConnect">测试连接</a-button>
      <a-button type="primary" @click="check">确定</a-button>
    </div>
    <!-- 事件 -->
    <div class="machine-add-modal-event-container">
      <!-- 新增秘钥模态框 -->
      <AddMachineKeyModal ref="addKeyModal" @added="getKeyList"/>
      <!-- 新增代理模态框 -->
      <AddMachineProxyModal ref="addProxyModal" @added="getProxyList"/>
    </div>
  </div>
</template>

<script>
import { pick } from 'lodash'
import { enumValueOf, MACHINE_AUTH_TYPE, MACHINE_PROXY_TYPE } from '@/lib/enum'
import { setTreePath, setTreeDataProps } from '@/lib/tree'
import { validatePort } from '@/lib/validate'
import AddMachineKeyModal from '@/components/machine/AddMachineKeyModal'
import AddMachineProxyModal from '@/components/machine/AddMachineProxyModal'

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 }
}

function getDecorators() {
  return {
    groupIdList: ['groupIdList'],
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
        message: '请输入唯一标识'
      }, {
        max: 32,
        message: '唯一标识长度不能大于32位'
      }]
    }],
    description: ['description', {
      rules: [{
        max: 64,
        message: '描述长度不能大于64位'
      }]
    }],
    username: ['username', {
      initialValue: 'root',
      rules: [{
        required: true,
        validator: this.validateUsername
      }]
    }],
    host: ['host', {
      rules: [{
        required: true,
        validator: this.validateHost
      }]
    }],
    sshPort: ['sshPort', {
      initialValue: 22,
      rules: [{
        validator: validatePort
      }]
    }],
    authType: ['authType', {
      initialValue: MACHINE_AUTH_TYPE.SECRET_KEY.value
    }],
    password: ['password', {
      rules: [{
        max: 128,
        message: '密码长度不能大于128位'
      }, {
        validator: this.validatePassword
      }]
    }],
    keyId: ['keyId', {
      rules: [{
        validator: this.validateKeyId
      }]
    }],
    proxyId: ['proxyId']
  }
}

export default {
  name: 'AddMachineModal',
  components: {
    AddMachineProxyModal,
    AddMachineKeyModal
  },
  data: function() {
    return {
      MACHINE_AUTH_TYPE,
      id: null,
      init: false,
      loading: false,
      keyList: [],
      proxyList: [],
      treeData: [],
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
      } else if (value.length > 128) {
        callback(new Error('用户名长度不能大于128位'))
      } else {
        callback()
      }
    },
    validateHost(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入主机IP'))
      } else if (value.length > 128) {
        callback(new Error('主机IP长度不能大于128位'))
      } else {
        callback()
      }
    },
    validateKeyId(rule, value, callback) {
      const authType = this.form.getFieldValue('authType')
      if (!value && authType === MACHINE_AUTH_TYPE.SECRET_KEY.value) {
        callback(new Error('请选择认证秘钥'))
      } else {
        callback()
      }
    },
    validatePassword(rule, value, callback) {
      const authType = this.form.getFieldValue('authType')
      if (!value && authType === MACHINE_AUTH_TYPE.PASSWORD.value && !this.id) {
        callback(new Error('新增时用户名和密码须同时存在'))
      } else {
        callback()
      }
    },
    add() {
      this.initRecord({})
    },
    update(id) {
      this.$api.getMachineDetail({
        id
      }).then(({ data }) => {
        this.initRecord(data)
      })
    },
    initData() {
      // 加载分组
      this.$api.getMachineGroupTree().then(({ data }) => {
        this.treeData = setTreeDataProps(setTreePath(data), node => {
          node.value = node.key
          node.label = node.path
        })
      }).then(() => {
        // 加载秘钥
        this.getKeyList()
      }).then(() => {
        // 加载代理
        this.getProxyList()
      })
      this.init = true
    },
    initRecord(row) {
      if (!this.init) {
        this.initData()
      }
      this.form.resetFields()
      this.id = row.id
      row.proxyId = row.proxyId || undefined
      row.keyId = row.keyId || undefined
      this.record = pick(Object.assign({}, row), 'groupIdList', 'name', 'tag', 'username', 'host',
        'sshPort', 'authType', 'proxyId', 'keyId', 'description')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    addKey() {
      this.$refs.addKeyModal.add()
    },
    addProxy() {
      this.$refs.addProxyModal.add()
    },
    getKeyList() {
      this.$api.getMachineKeyList({
        limit: 10000
      }).then(({ data: { rows } }) => {
        this.keyList = rows
      })
    },
    getProxyList() {
      this.$api.getMachineProxyList({
        limit: 10000
      }).then(({ data: { rows } }) => {
        this.proxyList = rows
      })
    },
    testPing() {
      const host = this.form.getFieldValue('host')
      if (!host) {
        this.$message.error('请输入主机IP')
        return
      }
      const ping = this.$message.loading(`ping ${host}`)
      this.$api.machineDirectTestPing({
        host: host
      }).then(() => {
        ping()
        this.$message.success('ok')
      }).catch(() => {
        ping()
      })
    },
    testConnect() {
      const values = this.form.getFieldsValue()
      const {
        username,
        host,
        sshPort,
        authType,
        password,
        keyId
      } = values
      if (!username) {
        this.$message.error('请输入用户名')
        return false
      }
      if (!host) {
        this.$message.error('请输入主机IP')
        return false
      }
      if (!sshPort) {
        this.$message.error('请输入SSH端口')
        return false
      }
      if (parseInt(sshPort) < 2 || parseInt(sshPort) > 65534) {
        this.$message.error('SSH端口必须在2~65534之间')
        return false
      }
      if (authType === MACHINE_AUTH_TYPE.SECRET_KEY.value && !keyId) {
        this.$message.error('请选择认证秘钥')
        return false
      }
      if (authType === MACHINE_AUTH_TYPE.PASSWORD.value && !password) {
        this.$message.error('请输入认证密码')
        return false
      }
      // 连接
      const ssh = `${username}@${host}:${sshPort}`
      const connecting = this.$message.loading(`connecting ${ssh}`)
      this.$api.machineDirectConnect({
        ...values
      }).then(() => {
        connecting()
        this.$message.success('ok')
      }).catch(() => {
        connecting()
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
          // 新增
          res = await this.$api.addMachine({ ...values })
        } else {
          // 修改
          res = await this.$api.updateMachine({
            ...values,
            id: this.id
          })
        }
        if (!this.id) {
          this.$message.success('新增成功')
          this.$router.back()
        } else {
          this.$message.success('修改成功')
        }
      } catch (e) {
        // ignore
      }
      this.loading = false
    }
  },
  filters: {
    formatProxyType(type, f) {
      return enumValueOf(MACHINE_PROXY_TYPE, type)[f]
    }
  },
  mounted() {
    const id = this.$route.params.id
    if (id) {
      this.update(id)
    } else {
      this.add()
    }
  }
}
</script>

<style lang="less" scoped>
.machine-wrapper {
  background: #FFF;
  padding: 24px;
  border-radius: 4px;
}

.machine-info-form {
  width: 700px;
}

.machine-footer {
  margin-left: 44px;

  button {
    margin-right: 8px;
  }
}

.machine-input {
  width: 380px;
}

.option-button {
  user-select: none;
  margin-left: 8px;
  position: relative;
  top: -2px;
}

.proxy-select-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-divider {
  margin: 8px 0 16px 0;
}

.reload-icon {
  font-size: 18px;
  margin-left: 16px;
}

::v-deep .proxy-selector .ant-select-selection-selected-value {
  width: 100%;
}

</style>
