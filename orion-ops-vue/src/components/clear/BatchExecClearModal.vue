<template>
  <a-modal v-model="visible"
           title="批量执行 清理"
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
            <a-radio-button :value="$enum.DATA_CLEAR_RANGE.REL_ID.value">清理机器</a-radio-button>
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
            <span class="normal-label clear-label">清理机器</span>
            <MachineSelector class="param-input"
                             placeholder="请选择清理的机器"
                             @change="(e) => submit.relIdList[0] = e"/>
          </div>
        </div>
        <!-- 管理员 -->
        <div class="all-user-wrapper" v-if="$isAdmin()">
          <span class="normal-label clear-label">执行用户</span>
          <a-checkbox class="param-input" v-model="iCreated">只清理我执行的</a-checkbox>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import _enum from '@/lib/enum'
import MachineSelector from '@/components/machine/MachineSelector'

export default {
  name: 'BatchExecClearModal',
  components: { MachineSelector },
  data: function() {
    return {
      visible: false,
      loading: false,
      iCreated: true,
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
      this.iCreated = true
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
          this.$message.warning('请选择需要清理的机器')
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
      this.$api.clearBatchExec({
        ...this.submit,
        iCreated: this.iCreated ? 1 : 2
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

.all-user-wrapper {
  margin-top: 12px;
}

</style>
