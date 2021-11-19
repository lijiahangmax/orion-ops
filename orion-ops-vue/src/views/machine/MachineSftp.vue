<template>
  <a-layout id="sftp-layout-container">
    <!-- 机器列表 -->
    <MachineListMenu ref="machineList"
                     :selectedMachine="selectedMachine"
                     :query="{status: $enum.MACHINE_STATUS.ENABLE.value}"
                     @chooseMachine="changeSftpMain"/>
    <!-- main -->
    <a-layout>
      <a-layout-content id="sftp-content-fixed-right">
        <!-- 面包屑 -->
        <a-breadcrumb id="sftp-breadcrumb">
          <a-breadcrumb-item>
            <a-tag color="#7950F2" style="margin: 0">
              {{ currentMachine.name }}
            </a-tag>
          </a-breadcrumb-item>
          <a-breadcrumb-item>
            <a-tag color="#5C7CFA" style="margin: 0">
              {{ currentMachine.host }}
            </a-tag>
          </a-breadcrumb-item>
          <a-breadcrumb-item>
            <a-tag color="#20C997" style="margin: 0">
              {{ currentMachine.tag }}
            </a-tag>
          </a-breadcrumb-item>
        </a-breadcrumb>
        <!-- sftp -->
        <MachineSftpMain ref="sftpMain"
                         :machineId="machineId"
                         :leftFolderDefaultVisible="true"
                         @opened="sftpOpened"/>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
import MachineListMenu from '@/components/machine/MachineListMenu'
import MachineSftpMain from '@/components/sftp/MachineSftpMain'

export default {
  name: 'MachineSftp',
  components: {
    MachineSftpMain,
    MachineListMenu
  },
  data() {
    return {
      selectedMachine: [],
      currentMachine: {
        id: null,
        tag: null,
        name: null,
        host: null
      },
      machineSessionToken: [],
      machineId: null
    }
  },
  methods: {
    changeSftpMain(id) {
      const filterMachines = this.$refs.machineList.list.filter(m => m.id === id)
      if (filterMachines.length) {
        const filterMachine = filterMachines[0]
        this.currentMachine.id = filterMachine.id
        this.currentMachine.tag = filterMachine.tag
        this.currentMachine.name = filterMachine.name
        this.currentMachine.host = filterMachine.host
      }
      this.machineId = id
      const filterSessionList = this.machineSessionToken.filter(m => m.machineId === id)
      if (filterSessionList.length) {
        this.$refs.sftpMain.changeToken(filterSessionList[0].session)
      } else {
        this.$refs.sftpMain.openSftp()
      }
      this.$refs.sftpMain.cleanChooseTree()
    },
    sftpOpened(machineId, session) {
      this.machineSessionToken.push({
        machineId,
        session
      })
    }
  },
  created() {
    if (this.$route.params.id) {
      this.machineId = parseInt(this.$route.params.id)
    } else {
      this.machineId = 1
    }
    this.selectedMachine = [this.machineId]
  },
  async mounted() {
    await this.$refs.machineList.getMachineList()
    this.changeSftpMain(this.machineId)
  }
}
</script>

<style lang="less" scoped>

#sftp-layout-container {
  height: 100vh;
}

#sftp-content-fixed-right {
  overflow: auto;
  padding: 18px;

  #sftp-breadcrumb {
    margin: 0 0 16px 0;
    background-color: #FFF;
    padding: 8px;
    border-radius: 4px;
  }
}

/deep/ .sftp-folder-left-fixed {
  height: calc(100vh - 122px);
  max-height: calc(100vh - 122px);
}

</style>
