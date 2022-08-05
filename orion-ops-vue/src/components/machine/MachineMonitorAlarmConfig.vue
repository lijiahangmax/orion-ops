<template>
  <div>
    报警配置 {{ machineId }}
  </div>
</template>

<script>
export default {
  name: 'MachineMonitorAlarmConfig',
  props: {
    machineId: Number
  },
  created() {
    console.log('config')
  }
}
</script>

<style scoped>

</style>
