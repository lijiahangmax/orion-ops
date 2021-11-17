<template>
  <a-spin :spinning="reloading">
    <a-tree
      :show-line="false"
      :show-icon="false"
      :tree-data="treeData"
      :expandedKeys.sync="expandedKeys"
      :load-data="onLoadData"
      :selectedKeys="selectedKeys"
      @select="redirect">
      <!-- 右键菜单 -->
      <template #title="{ key: treeKey, title }">
        <a-dropdown :trigger="['contextmenu']">
          <span>{{ title }}</span>
          <template #overlay>
            <a-menu @click="({ key: menuKey }) => handlerTreeMenu(treeKey, menuKey)">
              <a-menu-item key="reload">刷新</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
    </a-tree>
  </a-spin>
</template>

<script>

/**
 * 文件夹转树node
 */
function filesToNodes(data) {
  const children = []
  if (data && data.files && data.files.length) {
    for (const file of data.files) {
      children.push({
        title: file.name,
        key: file.path,
        children: []
      })
    }
  }
  return children
}

export default {
  name: 'SftpFolderTree',
  props: {
    sessionToken: String
  },
  data() {
    return {
      reloading: false,
      selectedKeys: [],
      treeData: [
        {
          title: '/',
          key: '/',
          children: null
        }
      ],
      expandedKeys: ['/']
    }
  },
  watch: {
    sessionToken(e) {
      this.handlerTreeMenu('/', 'reload')
    }
  },
  methods: {
    handlerTreeMenu(treeKey, menuKey) {
      if (menuKey === 'reload') {
        // 刷新
        this.reloadNode(treeKey)
      }
    },
    reloadNode(key) {
      this.reloading = true
      var brothers = this.treeData
      var curr = this.treeData
      const pathAnalysis = this.$utils.getPathAnalysis(key)
      for (let i = 0; i < pathAnalysis.length; i++) {
        var pathItem = pathAnalysis[i]
        for (const treeNode of brothers) {
          if (treeNode.key === pathItem.path) {
            brothers = treeNode.children
            curr = treeNode
            break
          }
        }
      }
      this.$api.sftpListDir({
        sessionToken: this.sessionToken,
        path: curr.key
      }).then(({ data }) => {
        curr.children = filesToNodes(data)
        this.reloading = false
      }).catch(() => {
        this.reloading = false
      })
    },
    redirect(selectedKeys, info) {
      this.selectedKeys = selectedKeys
      if (selectedKeys.length && selectedKeys[0]) {
        this.$emit('redirect', selectedKeys[0])
      }
    },
    async onLoadData(treeNode) {
      return new Promise((resolve, reject) => {
        this.$api.sftpListDir({
          sessionToken: this.sessionToken,
          path: treeNode.eventKey
        }).then(({ data }) => {
          resolve(data)
        }).catch(e => {
          reject(e)
        })
      }).then(data => {
        treeNode.dataRef.children = filesToNodes(data)
        this.treeData = [...this.treeData]
      })
    }
  }
}
</script>

<style scoped>

</style>
