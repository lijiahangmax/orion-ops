<template>
  <a-modal v-model="visible"
           title="设置发布定时"
           :width="330"
           :okButtonProps="{props: {disabled: !valid}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="submit"
           @cancel="close">
    <a-spin :spinning="loading">
      <div class="timed-release-picker-wrapper">
        <span class="normal-label mr8">调度时间 </span>
        <a-date-picker v-model="timedReleaseTime" :showTime="true" format="YYYY-MM-DD HH:mm:ss"/>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import { RELEASE_STATUS, TIMED_TYPE } from '@/lib/enum'
import moment from 'moment'

export default {
  name: 'AppReleaseTimedModal',
  data() {
    return {
      record: null,
      visible: false,
      loading: false,
      timedReleaseTime: undefined
    }
  },
  computed: {
    valid() {
      if (!this.timedReleaseTime) {
        return false
      }
      return Date.now() < this.timedReleaseTime
    }
  },
  methods: {
    open(record) {
      this.record = record
      this.timedReleaseTime = (record.timedReleaseTime && moment(record.timedReleaseTime)) || undefined
      this.visible = true
    },
    submit() {
      const time = this.timedReleaseTime.unix() * 1000
      this.loading = true
      this.$api.setAppTimedRelease({
        id: this.record.id,
        timedReleaseTime: time
      }).then(() => {
        this.loading = false
        this.visible = false
        this.record.timedReleaseTime = time
        this.record.status = RELEASE_STATUS.WAIT_SCHEDULE.value
        this.record.timedRelease = TIMED_TYPE.TIMED.value
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
