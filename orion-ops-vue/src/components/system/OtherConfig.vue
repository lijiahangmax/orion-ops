<template>
  <div class="other-container">
    <div class="other-title normal-title">
      其他配置
    </div>
    <div class="option-form-wrapper">
      <a-form-model v-bind="layout">
        <!-- 自动恢复调度任务 -->
        <a-form-model-item label="自动恢复调度任务">
          <a-switch v-model="option.resumeEnableSchedulerTask"
                    :loading="loading.resumeEnableSchedulerTask"
                    checkedChildren="启用"
                    unCheckedChildren="停用"
                    @change="changeOption($enum.SYSTEM_OPTION_KEY.RESUME_ENABLE_SCHEDULER_TASK)"/>
          <template #extra>
            <span class="help-text">
              开启后系统启动后会自动恢复启用的调度任务
            </span>
          </template>
        </a-form-model-item>
        <!-- 上传文件最大阈值 -->
        <a-form-model-item label="上传文件最大阈值">
          <a-input-search class="option-input"
                          v-model="option.sftpUploadThreshold"
                          v-limit-integer
                          placeholder="上传文件最大阈值(MB)"
                          :disabled="loading.sftpUploadThreshold"
                          :loading="loading.sftpUploadThreshold"
                          @search="changeOption($enum.SYSTEM_OPTION_KEY.SFTP_UPLOAD_THRESHOLD)">
            <template #enterButton>
              <a-icon type="check"/>
            </template>
          </a-input-search>
          <template #extra>
            <span class="help-text">
              设置 SFTP 上传文件最大阈值(MB), 设置过高可能会导致文件上传失败
            </span>
          </template>
        </a-form-model-item>
      </a-form-model>
    </div>
  </div>
</template>

<script>
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}
export default {
  name: 'OtherConfig',
  data() {
    return {
      layout,
      option: {
        resumeEnableSchedulerTask: false,
        sftpUploadThreshold: null
      },
      loading: {
        resumeEnableSchedulerTask: false,
        sftpUploadThreshold: null
      }
    }
  },
  methods: {
    changeOption(option) {
      this.loading[option.key] = true
      this.$api.updateSystemOption({
        option: option.value,
        value: this.option[option.key]
      }).then(() => {
        this.loading[option.key] = false
        this.$message.success('已保存')
      }).catch(() => {
        this.loading[option.key] = false
      })
    }
  },
  mounted() {
    this.$api.getSystemOptions().then(({ data }) => {
      this.option = data
    })
  }
}
</script>

<style lang="less" scoped>

.other-title {
  margin: 16px 0 0 16px;
}

.option-form-wrapper {
  width: 590px;
  margin: 24px;

  /deep/ .ant-form-item {
    margin: 0 0 18px 0;
  }

  .option-input {
    width: 200px;
  }
}

</style>
