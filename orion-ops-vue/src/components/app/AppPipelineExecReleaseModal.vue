<template>
  <a-modal v-model="visible"
           v-drag-modal
           :title="title"
           :width="560"
           :maskStyle="{opacity: 0.4, animation: 'none'}"
           :bodyStyle="{padding: '24px 24px 0 24px'}"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <!-- 发布配置 -->
    <a-spin :spinning="loading">
      <a-form-model v-bind="layout">
        <!-- 发布标题 -->
        <a-form-model-item label="发布标题" required>
          <a-input class="release-form-item-input"
                   v-model="submit.title"
                   placeholder="标题"
                   :maxLength="32"
                   allowClear/>
        </a-form-model-item>
        <!-- 发布版本 -->
        <a-form-model-item label="发布版本">
          <a-select class="release-form-item-input build-selector"
                    v-model="submit.buildId"
                    placeholder="最新版本"
                    allowClear>
            <a-select-option v-for="build of buildList" :key="build.id" :value="build.id">
              <div class="build-item">
                <div class="build-item-left">
                  <a-tag class="build-item-seq" color="#5C7CFA">
                    #{{ build.seq }}
                  </a-tag>
                  <span class="build-item-message">{{ build.description }}</span>
                </div>
                <span class="build-item-date">
                  {{ build.createTime | formatDate('MM-dd HH:mm') }}
                </span>
              </div>
            </a-select-option>
          </a-select>
          <a-icon type="reload" class="reload" title="刷新" @click="loadBuildList"/>
        </a-form-model-item>
        <!-- 发布机器 -->
        <a-form-model-item label="发布机器">
          <MachineChecker ref="machineChecker"
                          placement="bottomLeft"
                          :defaultValue="submit.machineIdList"
                          :query="{idList: appMachineIdList}">
            <template #trigger>
              <span class="span-blue pointer">已选择 {{ submit.machineIdList.length }} 台机器</span>
            </template>
            <template #footer>
              <a-button type="primary" size="small" @click="chooseMachines">确定</a-button>
            </template>
          </MachineChecker>
        </a-form-model-item>
        <!-- 发布描述 -->
        <a-form-model-item label="发布描述" required>
          <a-textarea class="release-form-item-input"
                      v-model="submit.description"
                      style="height: 50px; width: 430px"
                      :maxLength="64"
                      allowClear/>
        </a-form-model-item>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>

import MachineChecker from '@/components/machine/MachineChecker'
import _filters from '@/lib/filters'

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 }
}

export default {
  name: 'AppPipelineExecReleaseModal',
  components: {
    MachineChecker
  },
  data: function() {
    return {
      title: null,
      buildList: [],
      appMachineIdList: [],
      submit: {
        title: null,
        buildId: null,
        buildSeq: null,
        description: null,
        machineIdList: []
      },
      detail: {},
      visible: false,
      loading: false,
      layout
    }
  },
  methods: {
    async open(detail) {
      this.detail = { ...detail }
      this.title = `${detail.appName} 发布配置`
      this.buildList = []
      this.appMachineIdList = []
      this.submit.title = detail.title || `发布 ${detail.appName}`
      this.submit.buildId = detail.buildId
      this.submit.buildSeq = detail.buildSeq
      this.submit.description = detail.description
      this.submit.machineIdList = detail.machineIdList || []
      this.visible = true
      this.loading = true
      // 加载构建列表
      await this.loadBuildList()
      // 加载发布机器
      await this.loadReleaseMachine()
      this.loading = false
    },
    async loadBuildList() {
      await this.$api.getBuildReleaseList({
        appId: this.detail.appId,
        profileId: this.detail.profileId
      }).then(({ data }) => {
        this.buildList = data
      })
    },
    async loadReleaseMachine() {
      await this.$api.getAppMachineId({
        id: this.detail.appId,
        profileId: this.detail.profileId
      }).then(({ data }) => {
        if (data && data.length) {
          this.appMachineIdList = data
          if (!this.submit.machineIdList.length) {
            this.submit.machineIdList = data
          }
        } else {
          this.$message.warning('请先配置应用发布机器')
        }
      })
    },
    chooseMachines() {
      const ref = this.$refs.machineChecker
      if (!ref.checkedList.length) {
        this.$message.warning('请选择发布机器机器')
        return
      }
      this.submit.machineIdList = ref.checkedList
      ref.hide()
    },
    async check() {
      if (!this.submit.title) {
        this.$message.warning('请输入发布标题')
        return
      }
      if (!this.submit.machineIdList.length) {
        this.$message.warning('请选择发布机器')
        return
      }
      this.submit.buildSeq = this.buildList.filter(s => s.id === this.submit.buildId).map(s => s.seq)[0]
      this.$emit('ok', this.detail.id, this.submit)
      this.close()
    },
    close() {
      this.visible = false
      this.loading = false
    }
  },
  filters: {
    ..._filters
  }
}
</script>

<style lang="less" scoped>

.release-form-item-input {
  width: 348px;
}

.reload {
  font-size: 19px;
  margin-left: 16px;
  cursor: pointer;
  color: #339AF0;
}

.reload:hover {
  color: #228BE6;
}

.build-item {
  display: flex;
  justify-content: space-between;

  .build-item-left {
    display: flex;
    align-items: center;
    width: 276px;
  }

  .build-item-seq {
    margin-right: 8px;
  }

  .build-item-message {
    width: 200px;
    display: block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow-x: hidden;
  }

  .build-item-date {
    font-size: 12px;
    margin-right: 4px;
  }
}

.build-selector /deep/ .ant-select-selection-selected-value {
  width: 100%;
}

</style>
