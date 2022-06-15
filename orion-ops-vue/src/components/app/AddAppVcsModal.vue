<template>
  <a-modal v-model="visible"
           :title="title"
           :width="560"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <a-form-item label="url" hasFeedback>
          <a-input v-decorator="decorators.url" allowClear/>
        </a-form-item>
        <a-form-item label="认证方式">
          <a-radio-group v-decorator="decorators.authType">
            <a-radio :value="type.value" v-for="type in VCS_AUTH_TYPE" :key="type.value">
              {{ type.label }}
            </a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="资源用户" v-if="visibleUsername()" hasFeedback>
          <a-input v-decorator="decorators.username" allowClear/>
        </a-form-item>
        <a-form-item label="认证令牌" v-if="!visiblePassword()" style="margin-bottom: 0">
          <a-form-item style="display: inline-block; width: 30%">
            <a-select v-decorator="decorators.tokenType">
              <a-select-option :value="type.value" v-for="type in VCS_TOKEN_TYPE" :key="type.value">
                {{ type.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item style="display: inline-block; width: 70%">
            <a-input v-decorator="decorators.privateToken"
                     :placeholder="getPrivateTokenPlaceholder()"
                     allowClear/>
          </a-form-item>
        </a-form-item>
        <a-form-item label="资源密码" v-if="visiblePassword()">
          <a-input-password v-decorator="decorators.password" allowClear/>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-decorator="decorators.description" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { enumValueOf, VCS_AUTH_TYPE, VCS_TOKEN_TYPE } from '@/lib/enum'
import { pick } from 'lodash'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 16 }
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
    url: ['url', {
      rules: [{
        required: true,
        message: '请输入url'
      }, {
        max: 1024,
        message: 'url长度不能大于1024位'
      }]
    }],
    authType: ['authType', {
      initialValue: VCS_AUTH_TYPE.PASSWORD.value
    }],
    tokenType: ['tokenType', {
      initialValue: VCS_TOKEN_TYPE.GITHUB.value
    }],
    privateToken: ['privateToken', {
      rules: [{
        max: 128,
        message: '令牌长度不能大于256位'
      }, {
        validator: this.validatePrivateToken
      }]
    }],
    username: ['username', {
      rules: [{
        max: 128,
        message: '用户名长度不能大于128位'
      }, {
        validator: this.validateUsername
      }]
    }],
    password: ['password', {
      rules: [{
        max: 128,
        message: '密码长度不能大于128位'
      }, {
        validator: this.validatePassword
      }]
    }],
    description: ['description', {
      rules: [{
        max: 64,
        message: '描述长度不能大于64位'
      }]
    }]
  }
}

export default {
  name: 'AddAppVcsModal',
  data: function() {
    return {
      VCS_AUTH_TYPE,
      VCS_TOKEN_TYPE,
      id: null,
      visible: false,
      title: null,
      loading: false,
      record: null,
      layout,
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    add() {
      this.title = '新增仓库'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改仓库'
      this.$api.getVcsDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      const username = row.username
      const tokenType = row.tokenType
      this.record = pick(Object.assign({}, row), 'name', 'url', 'authType', 'description')
      // 设置数据
      new Promise((resolve) => {
        // 加载数据
        this.$nextTick(() => {
          this.form.setFieldsValue(this.record)
        })
        resolve()
      }).then(() => {
        // 加载令牌类型
        if (this.record.authType === VCS_AUTH_TYPE.TOKEN.value) {
          this.$nextTick(() => {
            this.record.tokenType = tokenType
            this.form.setFieldsValue({ tokenType })
          })
        }
      }).then(() => {
        // 加载用户名
        if (this.visibleUsername(this.record.authType, tokenType)) {
          this.$nextTick(() => {
            this.record.username = username
            this.form.setFieldsValue({ username })
          })
        }
      })
    },
    validatePrivateToken(rule, value, callback) {
      if (!this.id && !value) {
        callback(new Error('请输入私人令牌'))
      } else {
        callback()
      }
    },
    validateUsername(rule, value, callback) {
      if (this.form.getFieldValue('password') && !value) {
        callback(new Error('用户名和密码须同时存在'))
      } else if (this.form.getFieldValue('tokenType') === VCS_TOKEN_TYPE.GITEE.value && !value) {
        callback(new Error('gitee 令牌认证用户名必填'))
      } else {
        callback()
      }
    },
    validatePassword(rule, value, callback) {
      if (this.form.getFieldValue('username') && !value && !this.id) {
        callback(new Error('新增时用户名和密码须同时存在'))
      } else {
        callback()
      }
    },
    visibleUsername(authType = this.form.getFieldValue('authType'), tokenType = this.form.getFieldValue('tokenType')) {
      return authType !== VCS_AUTH_TYPE.TOKEN.value ||
        (authType === VCS_AUTH_TYPE.TOKEN.value && tokenType === VCS_AUTH_TYPE.TOKEN.value)
    },
    visiblePassword() {
      return this.form.getFieldValue('authType') === VCS_AUTH_TYPE.PASSWORD.value
    },
    getPrivateTokenPlaceholder() {
      return enumValueOf(VCS_TOKEN_TYPE, this.form.getFieldValue('tokenType')).description ||
        VCS_TOKEN_TYPE.GITHUB.description
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
          res = await this.$api.addVcs({ ...values })
        } else {
          // 修改
          res = await this.$api.updateVcs({
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
  }
}
</script>

<style scoped>

</style>
