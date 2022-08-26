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
        <a-form-item label="报警组名称">
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <a-form-item label="报警组员">
          <a-select mode="multiple" v-decorator="decorators.userIdList" optionLabelProp="label" allowClear>
            <a-select-option v-for="user of userList"
                             :key="user.id"
                             :value="user.id"
                             :label="user.nickname">
              {{ user.nickname }} ({{ user.username }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="报警通知方式">
          <a-select mode="multiple" v-decorator="decorators.notifyIdList" allowClear>
            <a-select-option v-for="webhook of webhookList"
                             :key="webhook.id"
                             :value="webhook.id">
              {{ webhook.name }}
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
        message: '请输入报警组名称'
      }, {
        max: 64,
        message: '报警组名称长度不能大于64位'
      }]
    }],
    description: ['description', {
      rules: [{
        max: 128,
        message: '描述长度不能大于128位'
      }]
    }],
    userIdList: ['userIdList', {
      rules: [{
        required: true,
        message: '请选择报警组员'
      }]
    }],
    notifyIdList: ['notifyIdList', {
      rules: [{
        required: true,
        message: '请选择报警通知方式'
      }]
    }]
  }
}

export default {
  name: 'AddAlarmGroup',
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
      userList: [],
      webhookList: [],
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    add() {
      this.title = '新增报警组'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改修改报警组'
      this.$api.getAlarmGroupDetail({ id })
      .then(({ data }) => {
        this.initRecord(data)
      })
    },
    async initRecord(row) {
      this.form.resetFields()
      this.visible = true
      await this.loadMetaData()
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'name', 'description', 'userIdList', 'notifyIdList')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    async loadMetaData() {
      if (this.userList.length || this.webhookList.length) {
        return
      }
      this.loading = true
      try {
        const { data: userList } = await this.$api.getUserList({ limit: 10000 })
        this.userList = userList.rows
        const { data: webhookList } = await this.$api.getWebhookConfigList({ limit: 10000 })
        this.webhookList = webhookList.rows
        this.loading = false
      } catch {
        this.$message.error('加载数据失败')
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
          res = await this.$api.addAlarmGroup({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateAlarmGroup({
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
