<template>
  <a-modal v-model="visible"
           :title="title"
           :width="650"
           :okButtonProps="{props: {disabled: loading}}"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="模板名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <a-form-item label="模板内容">
          <Editor :height="350" v-decorator="decorators.value"/>
        </a-form-item>
        <a-form-item label="模板描述">
          <a-textarea v-decorator="decorators.description" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

import { pick } from 'lodash'
import Editor from '@/components/editor/Editor'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入模板名称'
      }, {
        max: 32,
        message: '模板名称长度不能大于32位'
      }]
    }],
    value: ['value', {
      initialValue: undefined,
      rules: [{
        required: true,
        message: '请输入模板内容'
      }, {
        max: 2048,
        message: '模板内容长度不能大于2048位'
      }]
    }],
    description: ['description', {
      rules: [{
        max: 64,
        message: '模板描述长度不能大于64位'
      }]
    }]
  }
}

export default {
  name: 'AddTemplateModal',
  components: {
    Editor
  },
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
      this.title = '新增模板'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改模板'
      this.$api.getTemplateDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'name', 'value', 'description')
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
          res = await this.$api.addTemplate({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateTemplate({
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
