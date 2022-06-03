<template>
  <a-modal v-model="visible"
           title="站内信 清理"
           okText="清理"
           :width="400"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="clear"
           @cancel="close">
    <a-spin :spinning="loading">
      <div class="data-clear-container">
        <!-- 清理区间 -->
        <div class="data-clear-range">
          <a-radio-group class="nowrap" v-model="submit.range">
            <a-radio-button :value="$enum.DATA_CLEAR_RANGE.DAY.value">保留天数</a-radio-button>
            <a-radio-button :value="$enum.DATA_CLEAR_RANGE.TOTAL.value">保留条数</a-radio-button>
            <a-radio-button :value="$enum.DATA_CLEAR_RANGE.REL_ID.value">清理分类</a-radio-button>
          </a-radio-group>
        </div>
        <!-- 清理参数 -->
        <div class="data-clear-params">
          <div class="data-clear-param" v-if="$enum.DATA_CLEAR_RANGE.DAY.value === submit.range">
            <span class="normal-label clear-label">保留天数</span>
            <a-input-number class="param-input"
                            v-model="submit.reserveDay"
                            :min="0"
                            :max="9999"
                            placeholder="清理后数据所保留的天数"/>
          </div>
          <div class="data-clear-param" v-if="$enum.DATA_CLEAR_RANGE.TOTAL.value === submit.range">
            <span class="normal-label clear-label">保留条数</span>
            <a-input-number class="param-input"
                            v-model="submit.reserveTotal"
                            :min="0"
                            :max="9999"
                            placeholder="清理后数据所保留的条数"/>
          </div>
          <div class="data-clear-param" v-if="$enum.DATA_CLEAR_RANGE.REL_ID.value === submit.range">
            <span class="normal-label clear-label">清理分类</span>
            <a-select class="param-input"
                      placeholder="请选择需要清理的消息分类"
                      @change="(e) => submit.relIdList[0] = e">
              <a-select-option v-for="classify in $enum.MESSAGE_CLASSIFY" :key="classify.value" :value="classify.value">
                {{ classify.label }}
              </a-select-option>
            </a-select>
          </div>
        </div>
        <!-- 清理状态 -->
        <div class="read-status-wrapper">
          <span class="normal-label clear-label">状态区间</span>
          <a-checkbox class="param-input" v-model="onlyRead">只清理已读的消息</a-checkbox>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import _enum from '@/lib/enum'

export default {
  name: 'WebSideMessageClearModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      onlyRead: false,
      submit: {
        reserveDay: null,
        reserveTotal: null,
        relIdList: [],
        range: _enum.DATA_CLEAR_RANGE.DAY.value
      }
    }
  },
  methods: {
    open() {
      this.onlyRead = false
      this.submit.reserveDay = null
      this.submit.reserveTotal = null
      this.submit.relIdList = []
      this.submit.range = this.$enum.DATA_CLEAR_RANGE.DAY.value
      this.loading = false
      this.visible = true
    },
    clear() {
      if (this.submit.range === this.$enum.DATA_CLEAR_RANGE.DAY.value) {
        if (this.submit.reserveDay === null) {
          this.$message.warning('请输入需要保留的天数')
          return
        }
      } else if (this.submit.range === this.$enum.DATA_CLEAR_RANGE.TOTAL.value) {
        if (this.submit.reserveTotal === null) {
          this.$message.warning('请输入需要保留的条数')
          return
        }
      } else if (this.submit.range === this.$enum.DATA_CLEAR_RANGE.REL_ID.value) {
        if (!this.submit.relIdList.length) {
          this.$message.warning('请选择需要清理的分类')
          return
        }
      } else {
        return
      }
      this.$confirm({
        title: '确认清理',
        content: '清理后数据将无法恢复, 确定要清理吗?',
        mask: false,
        okText: '确认',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.doClear()
        }
      })
    },
    doClear() {
      this.loading = true
      this.$api.clearWebSideMessage({
        ...this.submit,
        onlyRead: this.onlyRead ? 1 : 2
      }).then(({ data }) => {
        this.loading = false
        this.visible = false
        this.$emit('clear')
        this.$message.info(`共清理 ${data}条数据`)
      }).catch(() => {
        this.loading = false
        this.$message.error('清理失败')
      })
    },
    close() {
      this.visible = false
      this.loading = false
    }
  }
}
</script>

<style lang="less" scoped>

.data-clear-container {
  width: 100%;
}

.data-clear-range {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

.clear-label {
  width: 64px;
  text-align: end;
}

.data-clear-param {
  width: 100%;
  display: flex;
  align-items: center;
}

.param-input {
  margin-left: 8px;
  width: 236px;
}

.read-status-wrapper {
  margin-top: 12px;
}

</style>
