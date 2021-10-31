<template>
  <a-modal v-model="visible"
           :title="title"
           :width="450"
           :okButtonProps="{props: {disabled: loading}}"
           :mask="mask"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="代理主机" hasFeedback>
          <a-input v-decorator="decorators.host"/>
        </a-form-item>
        <a-form-item label="代理端口" hasFeedback>
          <a-input v-decorator="decorators.port"/>
        </a-form-item>
        <a-form-item label="代理用户">
          <a-input v-decorator="decorators.username"/>
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-decorator="decorators.password"/>
        </a-form-item>
        <a-form-item label="代理类型">
          <a-select v-decorator="decorators.type" placeholder="请选择">
            <a-select-option :value="type.value" v-for="type in $enum.MACHINE_PROXY_TYPE" :key="type.value">
              {{type.label}}
            </a-select-option>
          </a-select>
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
  import { validatePort } from '../../lib/validate'

  const layout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 17 }
  }

  function getDecorators() {
    return {
      host: ['host', {
        rules: [{
          required: true,
          message: '请输入主机'
        }, {
          max: 32,
          message: '主机长度必须小于等于32位'
        }]
      }],
      port: ['port', {
        initialValue: 22,
        rules: [{
          required: true,
          message: '请输入端口'
        }, {
          validator: validatePort
        }]
      }],
      username: ['username', {
        rules: [{
          max: 128,
          message: '用户名必须小于等于128位'
        }, {
          validator: this.validateUsername
        }]
      }],
      password: ['password', {
        rules: [{
          max: 128,
          message: '密码必须小于等于128位'
        }, {
          validator: this.validatePassword
        }]
      }],
      type: ['type', {
        rules: [{
          required: true,
          message: '请选择类型'
        }]
      }],
      description: ['description', {
        rules: [{
          max: 64,
          message: '描述必须小于等于64位'
        }]
      }]
    }
  }

  export default {
    name: 'AddMachineProxyModal',
    data: function() {
      return {
        id: null,
        visible: false,
        title: null,
        loading: false,
        mask: true,
        record: null,
        layout,
        decorators: getDecorators.call(this),
        form: this.$form.createForm(this)
      }
    },
    methods: {
      add() {
        this.title = '新增代理'
        this.initRecord({})
      },
      update(row) {
        this.title = '修改代理'
        this.initRecord(row)
      },
      initRecord(row) {
        this.form.resetFields()
        this.visible = true
        this.id = row.id
        this.record = pick(Object.assign({}, row), 'host', 'port', 'username', 'type', 'description')
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
            res = await this.$api.addMachineProxy({ ...values })
          } else {
            // 修改
            res = await this.$api.updateMachineProxy({
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
        this.id = null
        this.visible = false
        this.loading = false
      }
    }
  }
</script>

<style scoped>

</style>
