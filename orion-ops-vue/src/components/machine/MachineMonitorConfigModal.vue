<template>
  <a-modal v-model="visible"
           title="插件配置"
           :width="550"
           :bodyStyle="{padding: '12px'}"
           :maskClosable="false"
           :destroyOnClose="true">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="url">
          <a-input v-decorator="decorators.url" :maxLength="500" allowClear/>
        </a-form-item>
        <a-form-item label="accessToken">
          <a-input v-decorator="decorators.accessToken" :maxLength="500" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
    <template #footer>
      <a-button type="link" @click="check(false)">测试连接</a-button>
      <a-button @click="close">取消</a-button>
      <a-button type="primary" @click="check(true)">保存</a-button>
    </template>
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
    url: ['url', {
      rules: [{
        required: true,
        message: '请输入 url'
      }, {
        max: 500,
        message: 'url 长度不能大于500位'
      }]
    }],
    accessToken: ['accessToken', {
      rules: [{
        required: true,
        message: '请输入 accessToken'
      }, {
        max: 500,
        message: 'accessToken 长度不能大于500位'
      }]
    }]
  }
}

export default {
  name: 'MachineMonitorConfigModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      id: null,
      record: null,
      formData: null,
      layout,
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    open(record) {
      this.id = null
      this.record = record
      this.$api.getMachineMonitorConfig({
        machineId: record.machineId
      }).then(({ data }) => {
        this.form.resetFields()
        this.visible = true
        this.id = data.id
        this.formData = pick(Object.assign({}, data), 'url', 'accessToken')
        this.$nextTick(() => {
          this.form.setFieldsValue(this.formData)
        })
      })
    },
    check(submit) {
      this.loading = true
      this.form.validateFields((err, values) => {
        if (err) {
          this.loading = false
          return
        }
        if (values.url.endsWith('/')) {
          this.loading = false
          this.$message.warning('url 不能以 / 结尾')
          return
        }
        if (submit) {
          this.submit(values)
        } else {
          this.ping(values)
        }
      })
    },
    async ping(values) {
      try {
        const { data } = await this.$api.testPingMachineMonitor(values)
        if (data) {
          this.$message.success(`插件版本 V${data}`)
        } else {
          this.$message.error('无法连接')
        }
      } catch (e) {
        this.$message.error('无法连接')
      }
      this.loading = false
    },
    async submit(values) {
      try {
        const { data } = await this.$api.setMachineMonitorConfig({
          ...values,
          id: this.id
        })
        this.$message.success('保存成功')
        this.record.status = data.status || this.record.status
        this.record.currentVersion = data.currentVersion || this.record.currentVersion
        this.close()
      } catch (e) {
        // ignore
      }
      this.loading = false
    },
    close() {
      this.visible = false
      this.loading = false
      this.id = null
    }
  }
}
</script>

<style scoped>

</style>
