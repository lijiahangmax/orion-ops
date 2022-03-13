<template>
  <a-layout id="sftp-layout-container">
    <!-- 机器列表 -->
    <MachineListMenu ref="machineList"
                     theme="light"
                     :selectedMachine="selectedMachine"
                     :query="{status: $enum.ENABLE_STATUS.ENABLE.value}"
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
            <a-tag color="#5C7CFA" style="margin: 0">
              {{ currentMachine.tag }}
            </a-tag>
          </a-breadcrumb-item>
        </a-breadcrumb>
        <!-- sftp -->
        <MachineSftpMain ref="sftpMain"
                         :machineId="machineId"
                         :leftFolderDefaultVisible="true"
                         :visibleRightMenu="true"
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
      currentMachine: {},
      initMachineId: null,
      machineId: null
    }
  },
  methods: {
    changeSftpMain(id) {
      if (this.machineId === id) {
        return
      }
      const filterMachines = this.$refs.machineList.list.filter(m => m.id === id)
      if (filterMachines.length) {
        this.currentMachine = filterMachines[0]
      } else {
        this.$message.error('未查询到机器信息')
        return
      }
      this.machineId = id
      this.$nextTick(() => {
        const session = this.currentMachine.session
        if (session) {
          this.$refs.sftpMain.changeToken(session)
        } else {
          this.$refs.sftpMain.openSftp()
        }
      })
    },
    sftpOpened(machineId, session) {
      const filterMachines = this.$refs.machineList.list.filter(m => m.id === machineId)
      if (filterMachines.length) {
        filterMachines[0].session = session
      }
    }
  },
  created() {
    if (this.$route.params.id) {
      this.initMachineId = parseInt(this.$route.params.id)
    } else {
      this.initMachineId = 1
    }
    this.selectedMachine = [this.initMachineId]
  },
  async mounted() {
    await this.$refs.machineList.getMachineList()
    this.$utils.defineArrayKey(this.$refs.machineList.list, 'session')
    this.changeSftpMain(this.initMachineId)
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
    margin: 0 0 12px 0;
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
