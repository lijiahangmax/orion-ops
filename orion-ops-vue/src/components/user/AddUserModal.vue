<template>
  <a-modal v-model="visible"
           :title="title"
           :width="650"
           :okButtonProps="{props: {disabled: loading}}"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="用户名">
          <a-input :disabled="!!id" v-decorator="decorators.username"/>
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-decorator="decorators.nickname"/>
        </a-form-item>
        <a-form-item v-if="!id" label="密码">
          <a-input-password v-decorator="decorators.password"/>
        </a-form-item>
        <a-form-item label="角色">
          <a-select v-decorator="decorators.role">
            <a-select-option :value="type.value" v-for="type in $enum.ROLE_TYPE" :key="type.value">
              {{ type.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="联系电话">
          <a-input v-decorator="decorators.phone"/>
        </a-form-item>
        <a-form-item label="联系邮箱">
          <a-input v-decorator="decorators.email"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

import { pick } from 'lodash'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    username: ['username', {
      rules: [{
        required: true,
        message: '请输入用户名'
      }, {
        max: 32,
        message: '用户名长度必须小于等于32位'
      }]
    }],
    nickname: ['nickname', {
      rules: [{
        required: true,
        message: '请输入昵称'
      }, {
        max: 32,
        message: '昵称长度必须小于等于32位'
      }]
    }],
    password: ['password', {
      rules: [{
        required: true,
        message: '请输入密码'
      }, {
        min: 8,
        max: 32,
        message: '密码长度必须在8-32位之间'
      }]
    }],
    role: ['role', {
      rules: [{
        required: true,
        message: '请选择角色'
      }]
    }],
    phone: ['phone', {
      rules: [{
        required: true,
        message: '请输入联系电话'
      }, {
        min: 11,
        max: 16,
        message: '请输入正确的联系电话'
      }]
    }],
    email: ['email', {
      rules: [{
        min: 8,
        max: 128,
        message: '联系邮箱长度必须在8-128位之间'
      }]
    }]
  }
}

export default {
  name: 'AddUserModal',
  data: function() {
    return {
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
      this.title = '新增用户'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改用户'
      this.$api.getUserDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'username', 'nickname', 'role', 'phone', 'email')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
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
          values.password = this.$utils.md5(values.password)
          res = await this.$api.addUser({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateUser({
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
