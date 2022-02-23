<template>
  <a-modal v-model="visible"
           :width="500"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           title="终端配置"
           okText="修改"
           cancelText="关闭"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="终端类型">
          <a-select v-decorator="decorators.terminalType">
            <template v-for="value in supportedTerminalType">
              <a-select-option :key="value" :value="value">{{ value }}</a-select-option>
            </template>
          </a-select>
        </a-form-item>
        <a-form-item label="字体大小">
          <a-input v-decorator="decorators.fontSize" type="number"/>
        </a-form-item>
        <a-form-item label="字体名称">
          <a-input v-decorator="decorators.fontFamily"/>
        </a-form-item>
        <a-form-item label="字体颜色">
          <a-input v-decorator="decorators.fontColor" type="color"/>
        </a-form-item>
        <a-form-item label="背景颜色">
          <a-input v-decorator="decorators.backgroundColor" type="color"/>
        </a-form-item>
        <a-form-item label="启用链接插件">
          <a-switch v-decorator="decorators.enableWebLink"/>
        </a-form-item>
        <a-form-item label="启用webGL加速">
          <a-tooltip>
            <template #title>
              启用webGL加速 (如果可用)<br>
              ⚠试验性功能, 可能不稳定
            </template>
            <a-switch v-decorator="decorators.enableWebGL"/>
          </a-tooltip>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

import { pick } from 'lodash'

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 }
}

function getDecorators() {
  return {
    terminalType: ['terminalType', {
      rules: [{
        required: true,
        message: '请选择终端类型'
      }]
    }],
    fontSize: ['fontSize', {
      rules: [{
        required: true,
        // transform(value) {
        //   return Number(value)
        // },
        validator: this.validateFontSize
      }]
    }],
    fontFamily: ['fontFamily', {
      rules: [{
        required: true,
        message: '请输入字体名称'
      }, {
        max: 64,
        message: '字体名称长度不能大于64位'
      }]
    }],
    fontColor: ['fontColor', {
      rules: [{
        required: true,
        message: '请选择字体颜色'
      }]
    }],
    backgroundColor: ['backgroundColor', {
      rules: [{
        required: true,
        message: '请选择背景色'
      }]
    }],
    enableWebLink: ['enableWebLink', {
      valuePropName: 'checked'
    }],
    enableWebGL: ['enableWebGL', {
      valuePropName: 'checked'
    }]
  }
}

export default {
  name: 'TerminalSettingModal',
  props: {
    machineId: Number
  },
  data: function() {
    return {
      visible: false,
      loading: false,
      id: null,
      supportedTerminalType: [],
      layout,
      setting: null,
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    validateFontSize(rule, value, callback) {
      if (value === '') {
        callback(new Error('请输入字体大小'))
      } else if (parseFloat(value) !== parseInt(value)) {
        callback(new Error('字体必须为整数'))
      } else {
        if (value < 12 || value > 24) {
          callback(new Error('字体大小必须在12-24之间'))
        } else {
          callback()
        }
      }
    },
    async openSetting() {
      this.visible = true
      this.id = null
      this.form.resetFields()
      // 获取终端类型
      if (this.supportedTerminalType.length === 0) {
        this.$api.getTerminalSupportPyt()
          .then(({ data }) => {
            this.supportedTerminalType = data
          })
      }
      // 读取配置
      try {
        const { data } = await this.$api.getTerminalSetting({ machineId: this.machineId })
        this.id = data.id
        this.setting = pick(Object.assign({}, data), 'terminalType', 'fontSize', 'fontFamily', 'fontColor', 'backgroundColor')
        this.setting.enableWebLink = data.enableWebLink === 1
        this.setting.enableWebGL = data.enableWebGL === 1
        this.$nextTick(() => {
          this.form.setFieldsValue(this.setting)
        })
      } catch (e) {
        // ignore
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
        await this.$api.updateTerminalSetting({
          id: this.id,
          terminalType: values.terminalType,
          fontSize: values.fontSize,
          fontFamily: values.fontFamily,
          fontColor: values.fontColor,
          backgroundColor: values.backgroundColor,
          enableWebLink: values.enableWebLink ? 1 : 2,
          enableWebGL: values.enableWebGL ? 1 : 2
        })
        this.$message.success('配置修改成功, 重新连接后后生效')
        this.close()
      } catch (e) {
        this.$message.error('配置修改失败')
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
