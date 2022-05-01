<template>
  <a-spin :spinning="reloading">
    <a-tree :show-line="false"
            :show-icon="false"
            :tree-data="treeData"
            :expandedKeys.sync="expandedKeys"
            :load-data="onLoadData"
            :selectedKeys="selectedKeys"
            @select="redirect">
      <!-- 右键菜单 -->
      <template #title="{ key: treeKey, title, isSafe }">
        <a-dropdown :trigger="['contextmenu']">
          <span style="padding: 2px 6px">{{ title }}</span>
          <template #overlay>
            <a-menu @click="({ key: menuKey }) => handlerTreeMenu(treeKey, menuKey)" @contextmenu.prevent>
              <a-menu-item class="folder-right-menu" key="reload">
                <a-icon type="sync" class="mr4"/>
                刷新
              </a-menu-item>
              <a-menu-item class="folder-right-menu" key="copyPath">
                <a-icon type="copy" class="mr4"/>
                复制路径
              </a-menu-item>
              <a-menu-item class="folder-right-menu" key="remove" v-if="isSafe">
                <a-icon type="delete" class="mr4"/>
                删除目录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
    </a-tree>
  </a-spin>
</template>

<script>
import _utils from '@/lib/utils'

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
        isSafe: file.isSafe,
        children: []
      })
    }
  }
  return children
}

/**
 * 菜单操作
 */
const folderRightMenuHandler = {
  reload(path) {
    // 刷新
    this.reloadNode(path)
  },
  copyPath(path) {
    // 复制路径
    this.$copy(path)
  },
  remove(path) {
    const last = path.lastIndexOf('/')
    const parentPath = last === 0 ? '/' : path.substring(0, last)
    this.$confirm({
      title: '确认删除',
      content: `是否删除文件夹 ${path}`,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        // 删除
        this.$api.sftpRemove({
          sessionToken: this.sessionToken,
          paths: [path]
        }).then(() => {
          this.$message.success('删除成功')
          const parentNodeChildren = findNode(this.treeData, parentPath).children
          for (let i = 0; i < parentNodeChildren.length; i++) {
            if (parentNodeChildren[i].key === path) {
              parentNodeChildren.splice(i, 1)
            }
          }
        })
      }
    })
  }
}

/**
 * 查找节点
 */
function findNode(node, key) {
  const keys = _utils.getPathAnalysis(key)
  const length = keys.length
  var curr = node[0]
  for (let i = 1; i < length; i++) {
    for (const child of curr.children) {
      if (child.key === keys[i].path) {
        curr = child
        break
      }
    }
  }
  return curr
}

export default {
  name: 'SftpFolderTree',
  props: {
    sessionToken: String
  },
  watch: {
    sessionToken(e) {
      e && this.handlerTreeMenu('/', 'reload')
    }
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
  methods: {
    handlerTreeMenu(treeKey, menuKey) {
      folderRightMenuHandler[menuKey].call(this, treeKey)
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
      }).catch((data) => {
        // ignore
      })
    },
    clean() {
      this.reloading = false
      this.selectedKeys = []
      this.treeData = [{
        title: '/',
        key: '/',
        children: null
      }]
      this.expandedKeys = ['/']
    }
  }
}
</script>

<style scoped>
.folder-right-menu {
  padding: 6px 18px;
}

/deep/ .ant-tree-node-content-wrapper {
  padding: 0 !important;
}

</style>
