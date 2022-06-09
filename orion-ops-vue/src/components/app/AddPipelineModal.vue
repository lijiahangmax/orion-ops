<template>
  <a-modal v-model="visible"
           :title="title"
           :width="578"
           :maskStyle="{opacity: 0.8, animation: 'none'}"
           :dialogStyle="{top: '64px', padding: 0}"
           :maskClosable="false"
           :destroyOnClose="true"
           :footer="false"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form-model v-bind="layout">
        <!-- 流水线名称 -->
        <a-form-model-item label="名称" class="name-form-item" required>
          <a-input class="name-input" v-model="record.name" :maxLength="32" allowClear/>
        </a-form-model-item>
        <!-- 流水线描述 -->
        <a-form-model-item label="描述" class="description-form-item">
          <a-textarea class="description-input" v-model="record.description" :maxLength="64" allowClear/>
        </a-form-model-item>
        <!-- 流水线操作 -->
        <a-form-model-item label="操作" class="detail-form-item" required>
          <div class="pipeline-details-wrapper">
            <template v-for="(detail, index) of record.details">
              <div class="pipeline-detail" :key="detail.id" v-if="detail.visible">
                <!-- 操作 -->
                <a-input-group compact>
                  <!-- 操作类型 -->
                  <a-select style="width: 80px" v-model="detail.stageType" placeholder="请选择">
                    <a-select-option v-for="stage of STAGE_TYPE"
                                     :key="stage.value"
                                     :value="stage.value">
                      {{ stage.label }}
                    </a-select-option>
                  </a-select>
                  <!-- 操作应用 -->
                  <a-select style="width: 271px" v-model="detail.appId" placeholder="请选择应用">
                    <a-select-option v-for="app of appList"
                                     :key="app.id"
                                     :value="app.id">
                      {{ app.name }}
                    </a-select-option>
                  </a-select>
                </a-input-group>
                <!-- 操作 -->
                <div class="pipeline-detail-handler">
                  <a-button-group v-if="record.details.length > 1">
                    <a-button title="移除" @click="removeOption(index)" icon="minus-circle"/>
                    <a-button title="上移" v-if="index !== 0" @click="swapOption(index, index - 1)" icon="arrow-up"/>
                    <a-button title="下移" v-if="index !== record.details.length - 1" @click="swapOption(index + 1, index )" icon="arrow-down"/>
                  </a-button-group>
                </div>
              </div>
            </template>
          </div>
          <!-- 添加 -->
          <a-button type="dashed" class="add-option-button" @click="addOption">
            添加应用操作
          </a-button>
          <!-- 保存 -->
          <a-button type="primary" class="save-button" @click="check">
            保存
          </a-button>
        </a-form-model-item>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
import { CONFIG_STATUS, STAGE_TYPE } from '@/lib/enum'

const layout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 }
}

export default {
  name: 'AddPipelineModal',
  data: function() {
    return {
      STAGE_TYPE,
      id: null,
      visible: false,
      title: null,
      loading: false,
      profileId: null,
      record: {},
      appList: [],
      layout
    }
  },
  methods: {
    add() {
      this.title = '新增流水线'
      this.initRecord({
        details: []
      })
    },
    update(id) {
      this.title = '配置流水线'
      this.$api.getAppPipelineDetail({
        id
      }).then(({ data }) => {
        this.initRecord(data)
      })
    },
    initRecord(row) {
      this.id = row.id
      this.record = row
      // 设置明细显示
      this.record.details.forEach(detail => {
        detail.visible = true
      })
      this.visible = true
      // 读取当前环境
      const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
      if (!activeProfile) {
        this.$message.warning('请先维护应用环境')
        return
      }
      this.profileId = JSON.parse(activeProfile).id
      // 加载应用列表
      this.loadAppList()
    },
    loadAppList() {
      this.loading = true
      this.app = null
      this.appList = []
      this.$api.getAppList({
        profileId: this.profileId,
        limit: 10000
      }).then(({ data }) => {
        this.loading = false
        if (data.rows && data.rows.length) {
          this.appList = data.rows.filter(s => s.isConfig === CONFIG_STATUS.CONFIGURED.value)
        }
      }).catch(() => {
        this.loading = false
      })
    },
    addOption() {
      this.record.details.push({
        visible: true,
        id: Date.now(),
        appId: undefined,
        stageType: STAGE_TYPE.BUILD.value
      })
    },
    removeOption(index) {
      this.record.details.visible = false
      this.$nextTick(() => {
        this.record.details.splice(index, 1)
      })
    },
    swapOption(index, target) {
      const temp = this.record.details[target]
      this.$set(this.record.details, target, this.record.details[index])
      this.$set(this.record.details, index, temp)
    },
    check() {
      if (!this.record.name || !this.record.name.trim().length) {
        this.$message.warning('请输入流水线名称')
        return
      }
      if (!this.record.details.length) {
        this.$message.warning('请设置流水线操作')
        return
      }
      for (let i = 0; i < this.record.details.length; i++) {
        const detail = this.record.details[i]
        if (!detail.stageType) {
          this.$message.warning(`请选择操作类型 [${i + 1}]`)
          return
        }
        if (!detail.appId) {
          this.$message.warning(`请选择操作应用 [${i + 1}]`)
          return
        }
      }
      this.loading = true
      // 设置数据
      const req = {
        id: this.id,
        profileId: this.profileId,
        name: this.record.name,
        description: this.record.description,
        details: []
      }
      // 设置详情
      this.record.details.forEach(({
        appId,
        stageType
      }) => {
        req.details.push({
          appId,
          stageType
        })
      })
      this.submit(req)
    },
    async submit(req) {
      let res
      try {
        if (!this.id) {
          // 添加
          res = await this.$api.addAppPipeline(req)
        } else {
          // 修改
          res = await this.$api.updateAppPipeline(req)
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

<style lang="less" scoped>
.name-form-item {
  margin-bottom: 18px;

  .name-input {
    width: 350px
  }
}

.description-form-item {
  margin-bottom: 4px;

  .description-input {
    width: 350px
  }
}

.detail-form-item {
  margin-bottom: 4px;

  .pipeline-detail {
    display: flex;
    align-items: center;
    height: 36px;
    margin: 2px 0 6px 0;
  }
}

.add-option-button {
  width: 350px;
}

.save-button {
  width: 350px;
}

</style>
