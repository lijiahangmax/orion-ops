<template>
  <div class="alarm-config-container">
    <a-spin :spinning="loading">
      <!-- 报警配置 -->
      <div class="alarm-config-wrapper">
        <!-- 报警配置 -->
        <a-tabs v-model="activeKey" tabPosition="left" size="small" :animated="false">
          <a-tab-pane class="alarm-tab-pane"
                      v-for="alarmType of MACHINE_ALARM_TYPE"
                      :key="alarmType.value"
                      :tab="`${alarmType.label}报警配置`">
            <h3>{{ alarmType.label }}报警配置</h3>
            <!-- 报警阈值 -->
            <div class="alarm-config-form-item">
              <div class="alarm-config-form-item-wrapper">
                <span class="alarm-config-form-label">报警阈值</span>
                <a-input-number
                  class="alarm-config-form-input"
                  v-model="config[alarmType.alarmProp].alarmThreshold"
                  :min="1"
                  :max="100"
                  :formatter="value => `${value}%`"
                  :parser="value => value.replace('%', '')"/>
              </div>
              <span class="help-text alarm-help-text">
                配置{{ alarmType.label }}报警阈值
              </span>
            </div>
            <!-- 通知阈值 -->
            <div class="alarm-config-form-item">
              <div class="alarm-config-form-item-wrapper">
                <span class="alarm-config-form-label">通知阈值 (次)</span>
                <a-input-number
                  class="alarm-config-form-input"
                  v-model="config[alarmType.alarmProp].triggerThreshold"
                  :min="1"/>
              </div>
              <span class="help-text alarm-help-text">
                当{{ alarmType.label }}连续达到报警阈值N次时则会通知报警联系组
              </span>
            </div>
            <!-- 沉默时间 -->
            <div class="alarm-config-form-item">
              <div class="alarm-config-form-item-wrapper">
                <span class="alarm-config-form-label">沉默时间 (分)</span>
                <a-input-number
                  class="alarm-config-form-input"
                  v-model="config[alarmType.alarmProp].notifySilence"
                  :min="0"/>
              </div>
              <span class="help-text alarm-help-text">
                当触发通知后的N分钟内, 再次达到报警阈值则不会触发通知
              </span>
            </div>
            <a-button type="primary" class="save-button" @click="saveAlarmConfig(alarmType)">保存</a-button>
          </a-tab-pane>
          <!-- 报警联系组 -->
          <a-tab-pane class="alarm-tab-pane" :key="1" tab="报警联系组">
            <h3>报警联系组配置</h3>
            <div class="alarm-group-transfer-wrapper">
              <!-- 报警联系组穿梭框 -->
              <a-transfer
                :dataSource="alarmGroups"
                :titles="['未选', '已选']"
                :targetKeys="selectAlarmGroup"
                :render="item => item.title"
                @change="selectedAlarmGroup"/>
            </div>
            <a-button type="primary" class="save-button" @click="saveAlarmGroup">保存</a-button>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-spin>
  </div>
</template>

<script>
import { MACHINE_ALARM_TYPE } from '@/lib/enum'

export default {
  name: 'MachineMonitorAlarmConfig',
  props: {
    machineId: Number
  },
  data() {
    return {
      MACHINE_ALARM_TYPE,
      activeKey: MACHINE_ALARM_TYPE.CPU_USAGE.value,
      loading: false,
      config: {
        cpuAlarm: {},
        memoryAlarm: {}
      },
      alarmGroups: [],
      selectAlarmGroup: []
    }
  },
  methods: {
    selectedAlarmGroup(v) {
      this.selectAlarmGroup = v
    },
    saveAlarmConfig(type) {
      const submit = this.config[type.alarmProp]
      if (!submit.alarmThreshold) {
        this.$message.warning('请输入报警阈值')
        return
      }
      if (!submit.triggerThreshold) {
        this.$message.warning('请输入通知阈值')
        return
      }
      if (!submit.notifySilence) {
        this.$message.warning('请输入沉默时间')
        return
      }
      this.loading = true
      this.$api.setMachineAlarmConfig({
        ...submit,
        machineId: this.machineId,
        type: type.value
      }).then(() => {
        this.$message.success('修改成功')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    saveAlarmGroup() {
      if (!this.selectAlarmGroup.length) {
        this.$message.warning('请选择报警联系组')
        return
      }
      this.loading = true
      this.$api.setMachineAlarmGroup({
        machineId: this.machineId,
        groupIdList: this.selectAlarmGroup
      }).then(() => {
        this.$message.success('修改成功')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  },
  async created() {
    this.loading = true
    // 加载报警组信息
    await this.$api.getAlarmGroupList({
      limit: 10000
    }).then(({ data }) => {
      this.alarmGroups = data.rows.map(row => {
        return {
          key: row.id.toString(),
          title: row.name
        }
      })
    })
    // 加载报警配置
    await this.$api.getMachineAlarmConfig({
      machineId: this.machineId
    }).then(({ data }) => {
      const cpuAlarm = data.config.filter(s => s.type === MACHINE_ALARM_TYPE.CPU_USAGE.value)
      const memoryAlarm = data.config.filter(s => s.type === MACHINE_ALARM_TYPE.MEMORY_USAGE.value)
      this.config.cpuAlarm = (cpuAlarm.length && cpuAlarm[0]) || {}
      this.config.memoryAlarm = (memoryAlarm.length && memoryAlarm[0]) || {}
      this.selectAlarmGroup = data.groupIdList.map(s => s.toString())
    })
    this.loading = false
  }
}
</script>

<style lang="less" scoped>
.alarm-config-container {
  padding: 18px;
  overflow: auto;
}

.alarm-config-wrapper {
  padding: 16px;
  background: #FFF;
  box-shadow: 0 1px 4px 0 #D9D9D9;
  border-radius: 2px;
}

.alarm-config-form-item-wrapper {
  display: flex;
  align-items: center;
  margin-top: 12px;

  .alarm-config-form-label {
    color: rgba(0, 0, 0, .9);
    width: 102px;
    text-align: end;
    margin-right: 8px;
  }

  .alarm-config-form-label::after {
    margin-left: 4px;
    content: ':';
  }

  .alarm-config-form-input {
    width: 200px;
  }
}

.alarm-help-text {
  display: block;
  margin: 4px 0 8px 109px;
}

.alarm-group-transfer-wrapper {
  margin: 11px 0 0 16px;
}

.save-button {
  margin: 16px 0 0 16px;
}

.alarm-tab-pane {
  height: 286px;
}
</style>
