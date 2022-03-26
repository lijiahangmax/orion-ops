<template>
  <div class="system-analysis-container">
    <!-- 清理配置 -->
    <div class="system-clean-container">
      <div class="system-clean-title normal-title">
        清理配置
      </div>
      <div class="system-clean-form-wrapper">
        <a-form-model v-bind="layout">
          <!-- 文件清理阈值 -->
          <a-form-model-item label="清理阈值(天)">
            <a-input-search class="clean-threshold-input"
                            v-model="fileCleanThreshold"
                            v-limit-pattern="/[^\d]+/g"
                            placeholder="文件清理阈值"
                            :disabled="fileCleanThresholdLoading"
                            :loading="fileCleanThresholdLoading"
                            @search="saveCleanThreshold">
              <template #enterButton>
                <a-icon type="check"/>
              </template>
            </a-input-search>
            <template #extra>
              <span class="help-text">
                设置后清理文件时会删除当前时间减当前值之前修改的所有文件, 如果设置为0则全部清理
              </span>
            </template>
          </a-form-model-item>
          <!-- 自动清理 -->
          <a-form-model-item label="自动清理">
            <a-switch v-model="autoCleanFile"
                      :loading="autoCleanFileLoading"
                      checkedChildren="启用"
                      unCheckedChildren="停用"
                      @change="changeAutoClean"/>
            <template #extra>
              <span class="help-text">
                开启后每天1:30会自动清理超过所配置阈值的文件
              </span>
            </template>
          </a-form-model-item>
        </a-form-model>
      </div>
    </div>
    <!-- 系统统计分析 -->
    <div class="system-analysis-descriptions">
      <a-descriptions title="系统统计分析">
        <!-- 已挂载秘钥数量 -->
        <a-descriptions-item label="已挂载秘钥数量" :span="3">
          <span class="analysis-count">
            <span class="analysis-field">{{ analysis.mountKeyCount }}</span>个
          </span>
        </a-descriptions-item>
        <!-- 临时文件 -->
        <a-descriptions-item label="临时文件" :span="3">
          <span class="analysis-count">
            <span class="analysis-field">{{ analysis.tempFileCount }}</span>个
          </span>
          <span class="analysis-size">
            <span class="analysis-field">{{ analysis.tempFileSize }}</span>
          </span>
          <div class="analysis-func" v-if="analysis.tempFileCount > 0">
            <span class="clear-button"
                  title="清理"
                  v-if="visibleClean[$enum.SYSTEM_CLEAR_TYPE.TEMP_FILE.key]"
                  @click="clear($enum.SYSTEM_CLEAR_TYPE.TEMP_FILE)">
              清理
            </span>
          </div>
        </a-descriptions-item>
        <!-- 日志文件 -->
        <a-descriptions-item label="日志文件" :span="3">
          <span class="analysis-count">
            <span class="analysis-field">{{ analysis.logFileCount }}</span>个
          </span>
          <span class="analysis-size">
            <span class="analysis-field">{{ analysis.logFileSize }}</span>
          </span>
          <div class="analysis-func" v-if="analysis.logFileCount > 0">
            <span class="clear-button"
                  title="清理"
                  v-if="visibleClean[$enum.SYSTEM_CLEAR_TYPE.LOG_FILE.key]"
                  @click="clear($enum.SYSTEM_CLEAR_TYPE.LOG_FILE)">
              清理
            </span>
          </div>
        </a-descriptions-item>
        <!-- 交换区文件 -->
        <a-descriptions-item label="交换区文件" :span="3">
          <span class="analysis-count">
            <span class="analysis-field">{{ analysis.swapFileCount }}</span>个
          </span>
          <span class="analysis-size">
            <span class="analysis-field">{{ analysis.swapFileSize }}</span>
          </span>
          <div class="analysis-func" v-if="analysis.swapFileCount > 0">
             <span class="clear-button"
                   title="清理"
                   v-if="visibleClean[$enum.SYSTEM_CLEAR_TYPE.SWAP_FILE.key]"
                   @click="clear($enum.SYSTEM_CLEAR_TYPE.SWAP_FILE)">
              清理
            </span>
          </div>
        </a-descriptions-item>
        <!-- 旧版本构建产物 -->
        <a-descriptions-item label="旧版本构建产物" :span="3">
          <span class="analysis-count">
            <span class="analysis-field">{{ analysis.distVersionCount }}</span>个
          </span>
          <span class="analysis-size">
            <span class="analysis-field">{{ analysis.distFileSize }}</span>
          </span>
          <div class="analysis-func" v-if="analysis.distVersionCount > 0">
            <span class="clear-button"
                  title="清理"
                  v-if="visibleClean[$enum.SYSTEM_CLEAR_TYPE.DIST_FILE.key]"
                  @click="clear($enum.SYSTEM_CLEAR_TYPE.DIST_FILE)">
              清理
           </span>
          </div>
        </a-descriptions-item>
        <!-- 旧版本应用仓库 -->
        <a-descriptions-item label="旧版本应用仓库" :span="3">
          <span class="analysis-count">
            <span class="analysis-field">{{ analysis.vcsVersionCount }}</span>个
          </span>
          <span class="analysis-size">
            <span class="analysis-field">{{ analysis.vcsVersionFileSize }}</span>
          </span>
          <div class="analysis-func" v-if="analysis.vcsVersionCount > 0">
            <span class="clear-button"
                  title="清理"
                  v-if="visibleClean[$enum.SYSTEM_CLEAR_TYPE.VCS_FILE.key]"
                  @click="clear($enum.SYSTEM_CLEAR_TYPE.VCS_FILE)">
            清理
           </span>
          </div>
        </a-descriptions-item>
        <!-- IP白名单数 -->
        <a-descriptions-item label="IP白名单数" :span="3">
        <span class="analysis-count">
          <span class="analysis-field">{{ analysis.whiteIpListCount }}</span>个
        </span>
        </a-descriptions-item>
        <!-- IP黑名单数 -->
        <a-descriptions-item label="IP黑名单数" :span="3">
        <span class="analysis-count">
          <span class="analysis-field">{{ analysis.blackIpListCount }}</span>个
        </span>
        </a-descriptions-item>
      </a-descriptions>
    </div>
    <!-- 操作 -->
    <div class="system-analysis-handler-container">
      <!-- 重新分析 -->
      <a-tooltip title="每小时自动统计一次">
        <a-button class="re-analysis-button" :loading="reAnalysisLoading" @click="reAnalysis">
          重新分析
        </a-button>
      </a-tooltip>
    </div>
  </div>
</template>

<script>
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

export default {
  name: 'SystemAnalysis',
  data() {
    return {
      layout,
      analysis: {
        mountKeyCount: 0,
        tempFileCount: 0,
        tempFileSize: '0 B',
        logFileCount: 0,
        logFileSize: '0 B',
        swapFileCount: 0,
        swapFileSize: '0 B',
        distVersionCount: 0,
        distFileSize: '0 B',
        vcsVersionCount: 0,
        vcsVersionFileSize: '0 B',
        blackIpListCount: 0,
        whiteIpListCount: 0
      },
      visibleClean: {
        tempFile: true,
        logFile: true,
        swapFile: true,
        distFile: true,
        vcsFile: true
      },
      reAnalysisLoading: false,
      fileCleanThreshold: undefined,
      fileCleanThresholdLoading: false,
      autoCleanFile: false,
      autoCleanFileLoading: false
    }
  },
  methods: {
    clear(type) {
      this.$confirm({
        title: '确认清理',
        content: '确认后会清理超过清理阈值的所有文件, 请先确认数据是否已备份, 清理后数据无法回滚!',
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.visibleClean[type.key] = false
          const pending = this.$message.loading(`正在提交 ${type.label} 清理任务...`)
          this.$api.cleanSystemFile({
            cleanType: type.value
          }).then(() => {
            pending()
            this.$message.success(`已提交 ${type.label} 清理任务`)
          }).catch(() => {
            pending()
            this.visibleClean[type.key] = true
          })
        }
      })
    },
    saveCleanThreshold() {
      this.fileCleanThreshold = ~~this.fileCleanThreshold
      if (this.fileCleanThreshold < 0) {
        this.$message.warning('自动清理阈值不能小于0')
      }
      this.fileCleanThresholdLoading = true
      this.$api.updateSystemOption({
        option: this.$enum.SYSTEM_OPTION_KEY.FILE_CLEAN_THRESHOLD.value,
        value: this.fileCleanThreshold
      }).then(() => {
        this.fileCleanThresholdLoading = false
        this.$message.success('已保存')
      }).catch(() => {
        this.fileCleanThresholdLoading = false
      })
    },
    changeAutoClean() {
      this.autoCleanFileLoading = true
      this.$api.updateSystemOption({
        option: this.$enum.SYSTEM_OPTION_KEY.ENABLE_AUTO_CLEAN_FILE.value,
        value: this.autoCleanFile
      }).then(() => {
        this.autoCleanFileLoading = false
        this.$message.success('已保存')
      }).catch(() => {
        this.autoCleanFileLoading = false
      })
    },
    loadData() {
      this.$api.getSystemAnalysis().then(({ data }) => {
        this.analysis = data
        this.fileCleanThreshold = data.fileCleanThreshold
        this.autoCleanFile = data.autoCleanFile
      })
    },
    reAnalysis() {
      this.reAnalysisLoading = true
      this.$api.reAnalysisSystem().then(({ data }) => {
        this.analysis = data
        this.reAnalysisLoading = false
        for (const visibleCleanKey in this.visibleClean) {
          this.visibleClean[visibleCleanKey] = true
        }
      }).catch(() => {
        this.reAnalysisLoading = false
      })
    }
  },
  mounted() {
    this.loadData()
  }
}
</script>

<style lang="less" scoped>

.system-clean-container {
  width: 550px;

  .system-clean-title {
    margin: 16px 0 0 16px;
  }

  .system-clean-form-wrapper {
    margin: 24px 0 24px 18px;
  }

  .clean-threshold-input {
    width: 200px;
  }
}

.system-analysis-container {
  margin-top: 16px;

  .system-analysis-descriptions {
    margin-top: 18px;
  }

  /deep/ .ant-descriptions-title {
    margin: 0 0 24px 16px;
    color: rgba(0, 0, 0, .85);
    font-weight: 500;
    font-size: 20px;
    line-height: 28px;
  }

  /deep/ .ant-descriptions-item-label {
    width: 128px;
    text-align: right;
  }

  /deep/ .ant-descriptions-item-content {
    padding-left: 8px;
  }

  .analysis-field {
    color: #1890FF;
    margin: 0 4px;
  }

  .analysis-count {
    width: 84px;
    display: inline-block;
    text-align: right;
    padding-right: 24px;
  }

  .analysis-size {
    width: 104px;
    display: inline-block;
    padding-right: 24px;
    text-align: right;
  }

  .analysis-func {
    display: inline-block;

    span {
      color: #1890FF;
      display: inline-block;
      cursor: pointer;
      padding: 0 4px;
    }
  }

  .system-analysis-handler-container {
    margin: 8px 0 24px 28px;
  }

}

</style>
