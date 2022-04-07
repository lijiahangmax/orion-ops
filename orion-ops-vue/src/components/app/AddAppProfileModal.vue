<template>
  <a-modal v-model="visible"
           v-drag-modal
           :title="title"
           :width="650"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="环境名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <a-form-item label="标签" hasFeedback>
          <a-input v-decorator="decorators.tag" allowClear/>
        </a-form-item>
        <a-form-item label="是否需要审核">
          <a-select v-decorator="decorators.releaseAudit">
            <a-select-option :value="type.value" v-for="type in $enum.PROFILE_AUDIT_STATUS" :key="type.value">
              {{ type.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-decorator="decorators.description" allowClear/>
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
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入环境名称'
      }, {
        max: 32,
        message: '环境名称长度不能大于32位'
      }]
    }],
    tag: ['tag', {
      rules: [{
        required: true,
        message: '请输入标签'
      }, {
        max: 32,
        message: '标签长度不能大于32位'
      }]
    }],
    releaseAudit: ['releaseAudit', {
      initialValue: 2,
      rules: [{
        required: true,
        message: '请选择是否需要审核'
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
  name: 'AddAppProfileModal',
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
      this.title = '新增环境'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改环境'
      this.$api.getProfileDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'name', 'tag', 'releaseAudit', 'description')
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
          res = await this.$api.addProfile({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateProfile({
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
