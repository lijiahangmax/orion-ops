<template>
  <a-modal v-model="visible"
           :width="550"
           :maskStyle="{opacity: 0.8, animation: 'none'}"
           :dialogStyle="{top: '64px', padding: 0}"
           :maskClosable="false"
           :destroyOnClose="true">
    <!-- 页头 -->
    <template #title>
      <span v-if="selectPipelinePage">选择流水线</span>
      <span v-if="!selectPipelinePage">
        <a-icon class="mx4 pointer span-blue"
                title="重新选择"
                v-if="visibleReselect && id"
                @click="reselectPipelineList"
                type="arrow-left"/>
        执行流水线
      </span>
    </template>
    <!-- 初始化骨架 -->
    <a-skeleton v-if="initiating" active :paragraph="{rows: 8}"/>
    <!-- 主体 -->
    <a-spin v-else :spinning="loading">
      <!-- 流水线选择 -->
      <div class="pipeline-list-container" v-if="selectPipelinePage">
        <!-- 无流水线数据 -->
        <a-empty v-if="!pipelineList.length" description="请先配置应用流水线"/>
        <!-- 流水线列表 -->
        <div class="pipeline-list">
          <div class="pipeline-item" v-for="pipeline of pipelineList"
               :key="pipeline.id"
               @click="choosePipeline(pipeline.id)">
            <div class="pipeline-name">
              <a-icon class="mx8" type="code-sandbox"/>
              {{ pipeline.name }}
            </div>
          </div>
        </div>
      </div>
      <!-- 执行配置 -->
      <div class="pipeline-container" v-else>
        <!-- 执行参数 -->
        <a-form-model v-bind="layout">
          <!-- 执行标题 -->
          <a-form-model-item class="exec-form-item" label="执行标题" required>
            <a-input class="name-input" v-model="submit.title" :maxLength="32" allowClear/>
          </a-form-model-item>
          <!-- 执行类型 -->
          <a-form-model-item class="exec-form-item" label="执行类型" required>
            <a-radio-group v-model="submit.timedExec" buttonStyle="solid">
              <a-radio-button :value="type.value" v-for="type in TIMED_TYPE" :key="type.value">
                {{ type.execLabel }}
              </a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <!-- 调度时间 -->
          <a-form-model-item class="exec-form-item" label="调度时间"
                             v-if="submit.timedExec === TIMED_TYPE.TIMED.value" required>
            <a-date-picker v-model="submit.timedExecTime" :showTime="true" format="YYYY-MM-DD HH:mm:ss"/>
          </a-form-model-item>
          <!-- 执行描述 -->
          <a-form-model-item class="exec-form-item" label="执行描述">
            <a-textarea class="description-input" v-model="submit.description" :maxLength="64" allowClear/>
          </a-form-model-item>
        </a-form-model>
        <a-divider class="detail-divider">流水线操作</a-divider>
        <!-- 执行操作 -->
        <div class="pipeline-wrapper">
          <a-timeline>
            <a-timeline-item v-for="detail of details" :key="detail.id">
              <div class="pipeline-detail-wrapper">
                <!-- 操作名称 -->
                <div class="pipeline-stage-type span-blue">
                  {{ detail.stageType | formatStageType('label') }}
                </div>
                <!-- 应用名称 -->
                <div class="pipeline-app-name">
                  {{ detail.appName }}
                </div>
                <!-- 应用操作 -->
                <div class="pipeline-handler">
                <span class="pipeline-config-message auto-ellipsis-item"
                      :title="getConfigMessage(detail)"
                      v-text="getConfigMessage(detail)"/>
                  <!-- 设置 -->
                  <a-badge class="pipeline-set-badge" :dot="visibleConfigDot(detail)">
                    <span class="span-blue pointer" title="设置" @click="openSetting(detail)">设置</span>
                  </a-badge>
                </div>
              </div>
            </a-timeline-item>
          </a-timeline>
        </div>
      </div>
    </a-spin>
    <!-- 页脚 -->
    <template #footer>
      <!-- 关闭 -->
      <a-button @click="close">关闭</a-button>
      <!-- 执行 -->
      <a-button type="primary"
                :loading="loading"
                :disabled="selectPipelinePage || loading || initiating"
                @click="execPipeline">
        执行
      </a-button>
    </template>
    <!-- 事件 -->
    <div class="pipeline-event-container">
      <!-- 构建配置 -->
      <AppPipelineExecBuildModal ref="buildSetting" @ok="pipelineConfigured"/>
      <!-- 发布配置 -->
      <AppPipelineExecReleaseModal ref="releaseSetting" @ok="pipelineConfigured"/>
    </div>
  </a-modal>
</template>

<script>
import { enumValueOf, STAGE_TYPE, TIMED_TYPE } from '@/lib/enum'
import AppPipelineExecBuildModal from '@/components/app/AppPipelineExecBuildModal'
import AppPipelineExecReleaseModal from '@/components/app/AppPipelineExecReleaseModal'

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 }
}

export default {
  name: 'AppPipelineExecModal',
  components: {
    AppPipelineExecReleaseModal,
    AppPipelineExecBuildModal
  },
  props: {
    visibleReselect: Boolean
  },
  data: function() {
    return {
      TIMED_TYPE,
      selectPipelinePage: false,
      id: null,
      visible: false,
      initiating: false,
      loading: false,
      profileId: null,
      pipelineList: [],
      record: {},
      submit: {
        title: null,
        description: null,
        timedExec: null,
        timedExecTime: null
      },
      details: [],
      layout
    }
  },
  methods: {
    async openPipelineList(profileId) {
      this.profileId = profileId
      this.pipelineList = []
      this.selectPipelinePage = true
      this.loading = false
      this.initiating = true
      this.visible = true
      await this.loadPipelineList()
      this.initiating = false
    },
    async openPipeline(profileId, id) {
      this.profileId = profileId
      this.pipelineList = []
      this.cleanPipelineData()
      this.selectPipelinePage = false
      this.loading = false
      this.initiating = true
      this.visible = true
      await this.selectPipeline(id)
      this.initiating = false
    },
    async choosePipeline(id) {
      this.cleanPipelineData()
      this.selectPipelinePage = false
      this.loading = true
      await this.selectPipeline(id)
      this.loading = false
    },
    async loadPipelineList() {
      const { data: { rows } } = await this.$api.getAppPipelineList({
        profileId: this.profileId,
        limit: 10000
      })
      this.pipelineList = rows
    },
    async selectPipeline(id) {
      this.id = id
      await this.$api.getAppPipelineDetail({
        id
      }).then(({ data }) => {
        this.record = data
        this.details = data.details
        this.submit.title = `执行${data.name}`
      })
    },
    async reselectPipelineList() {
      this.selectPipelinePage = true
      if (this.pipelineList.length) {
        return
      }
      this.initiating = true
      await this.loadPipelineList()
      this.initiating = false
    },
    cleanPipelineData() {
      this.record = {}
      this.details = []
      this.submit.title = null
      this.submit.description = null
      this.submit.timedExec = TIMED_TYPE.NORMAL.value
      this.submit.timedExecTime = null
    },
    visibleConfigDot(detail) {
      if (detail.stageType === STAGE_TYPE.BUILD.value) {
        if (detail.repoId) {
          return !detail.branchName
        } else {
          return false
        }
      } else {
        return false
      }
    },
    getConfigMessage(detail) {
      if (detail.stageType === STAGE_TYPE.BUILD.value) {
        // 构建
        if (detail.repoId) {
          if (detail.branchName) {
            return `${detail.branchName} ${detail.commitId.substring(0, 7)}`
          } else {
            return '请选择构建版本'
          }
        } else {
          return '无需配置'
        }
      } else {
        // 发布
        if (detail.buildSeq) {
          return `发布版本: #${detail.buildSeq}`
        } else {
          return '发布版本: 最新版本'
        }
      }
    },
    openSetting(detail) {
      if (detail.stageType === STAGE_TYPE.BUILD.value) {
        this.$refs.buildSetting.open(detail)
      } else {
        this.$refs.releaseSetting.open(detail)
      }
    },
    pipelineConfigured(detailId, config) {
      this.details.filter(s => s.id === detailId).forEach((detail) => {
        for (const configKey in config) {
          detail[configKey] = config[configKey]
        }
      })
      this.$set(this.details, 0, this.details[0])
    },
    execPipeline() {
      // 检查参数
      if (!this.submit.title) {
        this.$message.warning('请输入执行标题')
        return
      }
      if (this.submit.timedExec === TIMED_TYPE.TIMED.value) {
        if (!this.submit.timedExecTime) {
          this.$message.warning('请选择调度时间')
          return
        }
        if (this.submit.timedExecTime.unix() * 1000 < Date.now()) {
          this.$message.warning('调度时间需要大于当前时间')
          return
        }
      } else {
        this.submit.timedExecTime = undefined
      }
      for (const detail of this.details) {
        if (detail.stageType === STAGE_TYPE.BUILD.value && detail.repoId && !detail.branchName) {
          this.$message.warning(`请选择 ${detail.appName} 构建版本`)
          return
        }
      }
      // 封装数据
      const request = {
        pipelineId: this.id,
        ...this.submit,
        details: []
      }
      for (const detail of this.details) {
        request.details.push({
          id: detail.id,
          branchName: detail.branchName,
          commitId: detail.commitId,
          buildId: detail.buildId,
          title: detail.title,
          description: detail.description,
          machineIdList: detail.machineIdList
        })
      }
      this.loading = true
      this.$api.submitAppPipelineTask(request).then(() => {
        this.$message.success('已创建流水线任务')
        this.$emit('submit')
        this.visible = false
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      this.visible = false
      this.loading = false
    }
  },
  filters: {
    formatStageType(type, f) {
      return enumValueOf(STAGE_TYPE, type)[f]
    }
  }
}
</script>

<style lang="less" scoped>

/deep/ .ant-modal-body {
  padding: 8px;
}

.pipeline-container {
  padding: 16px 16px 0 16px;
}

.pipeline-list {
  margin: 0 4px 0 8px;
  height: 355px;
  overflow-y: auto;

  .pipeline-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 4px;
    padding: 4px 4px 4px 8px;
    background: #F8F9FA;
    border-radius: 4px;
    height: 40px;
    cursor: pointer;

    .pipeline-name {
      width: 300px;
      text-overflow: ellipsis;
      display: block;
      overflow-x: hidden;
      white-space: nowrap;
    }
  }

  .pipeline-item:hover {
    background: #E7F5FF;
  }
}

.exec-form-item {
  margin-bottom: 12px;
}

.detail-divider {
  margin: 0 0 24px 0;
}

.pipeline-detail-wrapper {
  display: flex;
  align-items: center;

  .pipeline-stage-type {
    margin-right: 4px;
  }

  .pipeline-app-name {
    width: 250px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .pipeline-handler {
    width: 180px;
    margin-left: 8px;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .pipeline-config-message {
      margin-right: 8px;
      color: #000000;
      font-size: 12px;
      width: 144px;
      text-align: end;
    }
  }

  .pipeline-set-badge /deep/ .ant-badge-dot {
    margin: -2px;
  }

}

/deep/ .ant-timeline-item-last {
  padding-bottom: 0;
}
</style>
