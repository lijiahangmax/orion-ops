<template>
  <a-modal v-model="visible"
           :title="title"
           :width="550"
           :okButtonProps="{props: {disabled: loading}}"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="名称">
          <a-input v-decorator="decorators.name"/>
        </a-form-item>
        <a-form-item label="url">
          <a-input v-decorator="decorators.url"/>
        </a-form-item>
        <a-form-item label="资源用户">
          <a-input v-decorator="decorators.username"/>
        </a-form-item>
        <a-form-item label="资源密码">
          <a-input-password v-decorator="decorators.password"/>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-decorator="decorators.description"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { pick } from 'lodash'
import { validatePort } from '@/lib/validate'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
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
      this.record = pick(Object.assign({}, row), 'name', 'url', 'username', 'description')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    validateUsername(rule, value, callback) {
      if (this.form.getFieldValue('password') && !value) {
        callback(new Error('用户名和密码须同时存在'))
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
