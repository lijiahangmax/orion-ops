<template>
  <a-modal v-model="visible"
           title="修改密码"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           okText="修改"
           cancelText="取消"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item v-if="visibleBeforePassword" has-feedback label="原始密码" prop="beforePassword">
          <a-input-password v-decorator="decorators.beforePassword" autocomplete="off"/>
        </a-form-item>
        <a-form-item has-feedback label="新密码" prop="password">
          <a-input-password v-decorator="decorators.password" autocomplete="off"/>
        </a-form-item>
        <a-form-item has-feedback label="确认新密码" prop="conformPassword">
          <a-input-password v-decorator="decorators.conformPassword" autocomplete="off"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>
<script>

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    beforePassword: ['beforePassword', {
      rules: [{
        required: true,
        validator: this.validateBeforePassword
      }]
    }],
    password: ['password', {
      rules: [{
        required: true,
        validator: this.validatePassword
      }]
    }],
    conformPassword: ['conformPassword', {
      rules: [{
        required: true,
        validator: this.validateConfirmPassword
      }]
    }]
  }
}

export default {
  props: {
    visibleBeforePassword: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      userId: undefined,
      form: this.$form.createForm(this),
      decorators: getDecorators.call(this),
      layout: layout
    }
  },
  methods: {
    validateBeforePassword(rule, value, callback) {
      if (value === '') {
        callback(new Error('请输入原密码'))
      } else {
        if (value.trim().length < 8) {
          callback(new Error('原密码长度不能小于8位'))
        } else {
          callback()
        }
      }
    },
    validatePassword(rule, value, callback) {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else {
        if (value.trim().length < 8) {
          callback(new Error('密码长度不能小于8位'))
        } else if (value.trim().length > 32) {
          callback(new Error('密码长度不能大于32位'))
        } else {
          this.form.validateFields(['conformPassword'], { force: true })
          callback()
        }
      }
    },
    validateConfirmPassword(rule, value, callback) {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.form.getFieldValue('password')) {
        callback(new Error('两次密码不匹配'))
      } else {
        callback()
      }
    },
    open(userId) {
      this.visible = true
      this.userId = userId
    },
    close() {
      this.visible = false
      this.userId = undefined
      this.form.resetFields()
    },
    check() {
      this.loading = true
      this.form.validateFields((err, values) => {
        if (err) {
          this.loading = false
          return
        }
        this.resetPassword(values)
      })
    },
    async resetPassword(values) {
      const updateData = {
        password: this.$utils.md5(values.password)
      }
      if (this.userId) {
        updateData.userId = this.userId
      }
      if (values.beforePassword) {
        updateData.beforePassword = this.$utils.md5(values.beforePassword)
      }
      this.$api.resetPassword(updateData)
        .then(() => {
          this.close()
          this.$message.success('修改成功')
          this.$emit('resetSuccess')
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>
