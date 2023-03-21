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
                    @change="changeOption(SYSTEM_OPTION_KEY.RESUME_ENABLE_SCHEDULER_TASK)"/>
          <template #extra>
            <span class="help-text">
              开启后系统启动后会自动恢复启用的调度任务
            </span>
          </template>
        </a-form-model-item>
        <!-- 终端后台主动推送心跳 -->
        <a-form-model-item label="终端后台主动推送心跳">
          <a-switch v-model="option.terminalActivePushHeartbeat"
                    :loading="loading.terminalActivePushHeartbeat"
                    checkedChildren="启用"
                    unCheckedChildren="停用"
                    @change="changeOption(SYSTEM_OPTION_KEY.TERMINAL_ACTIVE_PUSH_HEARTBEAT)"/>
          <template #extra>
            <span class="help-text">
              某些浏览器标签页后台运行可能会导致 setInterval 定时器挂起 <br>
              为了防止 terminal client 心跳主动上报失败, 可以将此开关开启 (大数据量不建议开启)
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
                          @search="changeOption(SYSTEM_OPTION_KEY.SFTP_UPLOAD_THRESHOLD)">
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
        <!-- 统计缓存有效时间 -->
        <a-form-model-item label="统计缓存有效时间">
          <a-input-search class="option-input"
                          v-model="option.statisticsCacheExpire"
                          v-limit-integer
                          placeholder="统计缓存有效时间"
                          :disabled="loading.statisticsCacheExpire"
                          :loading="loading.statisticsCacheExpire"
                          @search="changeOption(SYSTEM_OPTION_KEY.STATISTICS_CACHE_EXPIRE)">
            <template #enterButton>
              <a-icon type="check"/>
            </template>
          </a-input-search>
          <template #extra>
            <span class="help-text">
              设置统计缓存有效时间(分)
            </span>
          </template>
        </a-form-model-item>
      </a-form-model>
    </div>
  </div>
</template>

<script>
import { SYSTEM_OPTION_KEY } from '@/lib/enum'

const layout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 17 }
}
export default {
  name: 'OtherConfig',
  data() {
    return {
      SYSTEM_OPTION_KEY,
      layout,
      option: {
        resumeEnableSchedulerTask: false,
        terminalActivePushHeartbeat: false,
        sftpUploadThreshold: null,
        statisticsCacheExpire: null
      },
      loading: {
        resumeEnableSchedulerTask: false,
        terminalActivePushHeartbeat: false,
        sftpUploadThreshold: false,
        statisticsCacheExpire: false
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

  ::v-deep .ant-form-item {
    margin: 0 0 18px 0;
  }

  .option-input {
    width: 200px;
  }
}

</style>
