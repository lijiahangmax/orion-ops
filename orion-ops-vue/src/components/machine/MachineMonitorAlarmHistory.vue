<template>
  <div>
    报警历史 {{ machineId }}
  </div>
</template>

<script>
export default {
  name: 'MachineMonitorAlarmHistory',
  props: {
    machineId: Number
  },
  created() {
    console.log('his')
  }
}
</script>

<style scoped>

</style>
