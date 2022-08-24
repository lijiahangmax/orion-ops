<template>
  <a-modal v-model="visible"
           :title="title"
           :width="650"
           :dialogStyle="{top: '128px'}"
           :bodyStyle="{padding: '24px 8px 0 8px'}"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           :mask="mask"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="webhook 名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <a-form-item label="webhook url" hasFeedback>
          <a-input v-decorator="decorators.url" allowClear/>
        </a-form-item>
        <a-form-item label="webhook 类型">
          <a-select v-decorator="decorators.type" placeholder="请选择">
            <a-select-option v-for="type of WEBHOOK_TYPE" :key="type.value" :value="type.value">
              {{ type.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

import { pick } from 'lodash'
import { WEBHOOK_TYPE } from '@/lib/enum'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入 webhook 名称'
      }, {
        max: 64,
        message: 'webhook 名称长度不能大于64位'
      }]
    }],
    url: ['url', {
      rules: [{
        required: true,
        message: '请输入webhook url'
      }, {
        max: 2048,
        message: 'webhook url 长度不能大于2048位'
      }]
    }],
    type: ['type', {
      rules: [{
        required: true,
        message: '请选择 webhook 类型'
      }]
    }]
  }
}

export default {
  name: 'AddWebhookModal',
  props: {
    mask: Boolean
  },
  data: function() {
    return {
      WEBHOOK_TYPE,
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
      this.title = '新增 webhook'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改 webhook'
      this.$api.getWebhookConfigDetail({ id })
      .then(({ data }) => {
        this.initRecord(data)
      })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'name', 'url', 'type')
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
          res = await this.$api.addWebhookConfig({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateWebhookConfig({
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
