<template>
  <a-modal v-model="settingConfig.visible" title="终端配置" okText="修改" cancelText="关闭"
           width="380px" @ok="updateSetting" :confirmLoading="settingConfig.loading">
    <a-form-model :model="setting" :rules="settingConfig.rules" v-bind="settingConfig.layout">
      <a-form-model-item label="终端类型">
        <a-select v-model="setting.terminalType">
          <template v-for="value in settingConfig.supportedTerminalType">
            <a-select-option :key="value" :value="value">{{ value }}</a-select-option>
          </template>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="字体大小" prop="fontSize">
        <a-input v-model="setting.fontSize" type="number"/>
      </a-form-model-item>
      <a-form-model-item label="字体颜色">
        <a-input v-model="setting.fontColor" type="color"/>
      </a-form-model-item>
      <a-form-model-item label="背景颜色">
        <a-input v-model="setting.backgroundColor" type="color"/>
      </a-form-model-item>
      <a-form-model-item label="是否启用url link">
        <a-switch v-model="setting.useUrlLink"/>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 }
}

export default {
  name: 'TerminalSettingModal',
  data: function() {
    return {
      settingConfig: {
        visible: false,
        loading: false,
        supportedTerminalType: [],
        layout,
        setting: {},
        rules: {
          fontSize: [{
            validator: this.validateFontSize,
            trigger: 'change'
          }]
        }
      }
    }
  },
  methods: {
    openSetting() {
      if (this.settingConfig.supportedTerminalType.length === 0) {
        this.$api.getTerminalSupportPyt()
          .then(({ data }) => {
            this.settingConfig.supportedTerminalType = data
          })
      }
      this.settingConfig.visible = !this.settingConfig.visible
    },
    validateFontSize(rule, value, callback) {
      if (value === '') {
        callback(new Error('请输入字体大小'))
      } else {
        if (value < 8 || value > 32) {
          callback(new Error('字体大小必须在8-32之间'))
        } else {
          callback()
        }
      }
    },
    async updateSetting() {
      this.settingConfig.loading = true
      try {
        await this.$api.updateTerminalSetting(this.setting)
        this.settingConfig.visible = false
        this.$message.success('配置修改成功, 刷新页面生效')
      } catch (e) {
        this.$message.error('配置修改失败')
      }
      this.settingConfig.loading = false
    }
  }
}
</script>

<style scoped>

</style>
