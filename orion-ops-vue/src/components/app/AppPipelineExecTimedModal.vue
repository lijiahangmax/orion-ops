<template>
  <a-modal v-model="visible"
           v-drag-modal
           title="设置定时执行"
           :width="330"
           :okButtonProps="{props: {disabled: !valid}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="submit"
           @cancel="close">
    <a-spin :spinning="loading">
      <div class="">
        <span class="normal-label mr8">调度时间 </span>
        <a-date-picker v-model="timedExecTime" :showTime="true" format="YYYY-MM-DD HH:mm:ss"/>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from 'moment'

export default {
  name: 'AppPipelineExecTimedModal',
  data() {
    return {
      record: null,
      visible: false,
      loading: false,
      timedExecTime: undefined
    }
  },
  computed: {
    valid() {
      if (!this.timedExecTime) {
        return false
      }
      return Date.now() < this.timedExecTime
    }
  },
  methods: {
    open(record) {
      this.record = record
      this.timedExecTime = (record.timedExecTime && moment(record.timedExecTime)) || undefined
      this.visible = true
    },
    submit() {
      const time = this.timedExecTime.unix() * 1000
      this.loading = true
      this.$api.setAppPipelineRecordTimedExec({
        id: this.record.id,
        timedExecTime: time
      }).then(() => {
        this.loading = false
        this.visible = false
        this.record.timedExecTime = time
        this.record.status = this.$enum.PIPELINE_STATUS.WAIT_SCHEDULE.value
        this.record.timedExec = this.$enum.TIMED_TYPE.TIMED.value
        this.$emit('updated')
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      this.visible = false
      this.loading = false
    }
  }
}
</script>

<style scoped>

</style>
