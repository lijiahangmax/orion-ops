<template>
  <a-modal v-model="visible"
           v-drag-modal
           :title="title"
           :width="500"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="key">
          <a-input v-decorator="decorators.key" :disabled="id != null" allowClear/>
        </a-form-item>
        <a-form-item label="value" style="margin-bottom: 12px;">
          <a-textarea v-decorator="decorators.value" :autoSize="{minRows: 4}" allowClear/>
        </a-form-item>
        <a-form-item label="描述" style="margin-bottom: 0;">
          <a-textarea v-decorator="decorators.description" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

import { pick } from 'lodash'

const layout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 20 }
}

function getDecorators() {
  return {
    key: ['key', {
      rules: [{
        required: true,
        message: '请输入key'
      }, {
        max: 128,
        message: 'key长度不能大于128位'
      }]
    }],
    value: ['value', {
      rules: [{
        required: true,
        message: '请输入value'
      }, {
        max: 2048,
        message: 'value长度不能大于2048位'
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
  name: 'AddSystemEnvModal',
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
      this.title = '新增变量'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改变量'
      this.$api.getSystemEnvDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'key', 'value', 'description')
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
          res = await this.$api.addSystemEnv({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateSystemEnv({
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
