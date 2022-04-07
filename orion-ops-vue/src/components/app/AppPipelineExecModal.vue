<template>
  <a-modal v-model="visible"
           v-drag-modal
           :width="540"
           :maskStyle="{opacity: 0.8, animation: 'none'}"
           :dialogStyle="{top: '64px', padding: 0}"
           :bodyStyle="{padding: '24px 24px 0 24px'}"
           :maskClosable="false"
           :destroyOnClose="true"
           @cancel="close">
    <!-- 页头 -->
    <template #title>
      <span class="span-blue">执行</span> {{ record.name }}
    </template>
    <a-spin :spinning="loading">
      <!-- 执行参数 -->
      <a-form-model v-bind="layout">
        <!-- 执行标题 -->
        <a-form-model-item class="exec-form-item" label="执行标题" required>
          <a-input class="name-input" v-model="submit.title" :maxLength="32" allowClear/>
        </a-form-model-item>
        <!-- 发布类型 -->
        <a-form-model-item class="exec-form-item" label="执行类型" required>
          <a-radio-group v-model="submit.timedExec" buttonStyle="solid">
            <a-radio-button :value="type.value" v-for="type in $enum.TIMED_TYPE" :key="type.value">
              {{ type.execLabel }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <!-- 调度时间 -->
        <a-form-model-item class="exec-form-item" label="调度时间"
                           v-if="submit.timedExec === $enum.TIMED_TYPE.TIMED.value" required>
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
                {{ $enum.valueOf($enum.STAGE_TYPE, detail.stageType).label }}
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
    </a-spin>
    <!-- 页脚 -->
    <template #footer>
      <a-button type="primary" :loading="loading" @click="execPipeline">执行</a-button>
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
import _enum from '@/lib/enum'
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
  data: function() {
    return {
      id: null,
      visible: false,
      loading: false,
      profileId: null,
      record: {},
      submit: {
        title: null,
        description: null,
        timedExec: _enum.TIMED_TYPE.NORMAL.value,
        timedExecTime: null
      },
      details: [],
      layout
    }
  },
  methods: {
    open(id) {
      this.id = id
      this.$api.getAppPipelineDetail({
        id
      }).then(({ data }) => {
        this.record = data
        this.visible = true
        this.profileId = data.profileId
        this.details = data.details
        this.submit.title = `执行${data.name}`
      })
    },
    visibleConfigDot(detail) {
      if (detail.stageType === this.$enum.STAGE_TYPE.BUILD.value) {
        if (detail.vcsId) {
          return !detail.branchName
        } else {
          return false
        }
      } else {
        return false
      }
    },
    getConfigMessage(detail) {
      if (detail.stageType === this.$enum.STAGE_TYPE.BUILD.value) {
        // 构建
        if (detail.vcsId) {
          if (detail.branchName) {
            return `构建分支: ${detail.branchName}/${detail.commitId.substring(0, 7)}`
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
      if (detail.stageType === this.$enum.STAGE_TYPE.BUILD.value) {
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
      console.log(this.submit)
      console.log(this.details)
    },
    close() {
      this.visible = false
      this.loading = false
    }
  },
  mounted() {
    // this.open(3)
  }
}
</script>

<style lang="less" scoped>
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
