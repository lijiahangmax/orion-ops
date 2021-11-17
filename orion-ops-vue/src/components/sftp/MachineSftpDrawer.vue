<template>
  <a-drawer
    placement="right"
    :width="folderTreeVisible ? '80%' : '67%'"
    :closable="false"
    :visible="visible"
    :afterVisibleChange="changeVisibleAfter"
    :zIndex="800"
    :bodyStyle="{padding: 0}"
    @close="visible = !visible">
    <!-- 标题 -->
    <div class="sftp-drawer-title" slot="title">
      <span>文件管理器</span>
      <a-icon
        @click="changeFolderVisible"
        :title="folderTreeVisible ? '折叠文件夹' : '打开文件夹'"
        :type="folderTreeVisible ? 'menu-unfold' : 'menu-fold'"/>
    </div>
    <!-- sftp主体 -->
    <MachineSftpMain ref="sftpMain"
                     :machineId="machineId"
                     :leftFolderDefaultVisible="false"
                     @opened="() => init = true"/>
  </a-drawer>
</template>

<script>
import MachineSftpMain from './MachineSftpMain'

export default {
  name: 'MachineSftpDrawer',
  props: {
    machineId: Number
  },
  components: {
    MachineSftpMain
  },
  data: function() {
    return {
      visible: false,
      init: false,
      folderTreeVisible: false
    }
  },
  methods: {
    changeVisibleAfter(e) {
      if (this.init || !e) {
        return
      }
      // 初始化
      this.$refs.sftpMain.openSftp()
    },
    changeFolderVisible() {
      this.folderTreeVisible = !this.folderTreeVisible
      this.$refs.sftpMain.changeFolderVisible(this.folderTreeVisible)
    }
  }
}
</script>

<style lang="less" scoped>

.sftp-drawer-title {
  margin: 0;
  color: rgba(0, 0, 0, .85);
  font-weight: 50;
  font-size: 16px;
  line-height: 22px;
  display: flex;
  align-items: center;

  i {
    margin-left: 24px;
    font-size: 23px;
    cursor: pointer;
  }

  i:hover {
    color: #1890ff;
  }
}

/deep/ .ant-drawer-content-wrapper {
  height: 100%;
  transition: all 0.2s ease 0s;
}

</style>
