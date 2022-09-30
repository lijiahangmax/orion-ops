<template>
  <!-- 机器组树 -->
  <div class="machine-group-tree">
    <!-- 标题 -->
    <div v-if="visibleEdit" class="machine-group-tree-editable-title-wrapper">
      <span class="machine-group-tree-title-text">分组列表</span>
      <a-switch v-model="editable" checkedChildren="编辑" unCheckedChildren="浏览"/>
    </div>
    <div v-else class="machine-group-tree-uneditable-title-wrapper">
      分组列表
    </div>
    <a-divider v-if="visibleEdit" class="m0"/>
    <!-- 树 -->
    <a-spin wrapperClassName="machine-group-tree-wrapper" :style="treeStyle" :spinning="loading">
      <!-- 加载完成有数据 -->
      <a-tree v-if="treeData.length"
              :treeData="treeData"
              :draggable="editable"
              :blockNode="true"
              :showIcon="true"
              :selectedKeys="selectedTreeNode"
              :expandedKeys="expandedTreeNode"
              @dragenter="moveOnDragEnter"
              @drop="moveOnDrop"
              @select="e => chooseTreeNode(e[0])"
              @expand="(e, { node }) => changeExpandTreeNode(node.eventKey)"
              @dblclick="changeExpandTreeNode(selectedTreeNode[0])">
        <!-- 图标 -->
        <template #icon="{ expanded, editable }">
          <a-icon v-if="!editable" :type="expanded ? 'folder-open' : 'folder'"/>
        </template>
        <!-- 名称 -->
        <template #title="row">
          <!--展示 -->
          <a-dropdown :trigger="['contextmenu']" v-if="!row.editable" :disabled="!visibleEdit">
            <div class="machine-group-tree-node-title-container">
              <div class="machine-group-tree-node-title-wrapper">
                <!-- 组名称 -->
                <div class="machine-group-tree-node-title-value">{{ row.title }}</div>
                <!-- 机器数量 -->
                <div class="machine-group-tree-node-title-count">{{ row.machineCount }}</div>
              </div>
            </div>
            <!-- 右键菜单-->
            <template #overlay>
              <a-menu @click="({ key: menuKey }) => clickNodeMenu(row, menuKey)">
                <a-menu-item key="addRoot">添加根目录</a-menu-item>
                <a-menu-item key="addChild">添加子目录</a-menu-item>
                <a-menu-item key="rename">重命名目录</a-menu-item>
                <a-menu-item key="delete">删除目录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
          <!-- 新增 -->
          <div class="init-group-wrapper" v-else>
            <a-input size="small"
                     placeholder="请输入"
                     :ref="`groupNodeInput${row.key}`"
                     :value="row.title"
                     :maxLength="32"
                     @pressEnter="$event => $event.target.blur()"
                     @blur="saveGroup($event, row.dataRef)"/>
          </div>
        </template>
      </a-tree>
      <!-- 加载完成无数据 -->
      <div v-if="!loading && !treeData.length" class="m8">
        <a v-if="$isAdmin() && visibleEdit" @click="clickNodeMenu(null,'addRoot')">点击这里添加分组</a>
        <p v-else-if="$isAdmin() && !visibleEdit">请先添加机器分组</p>
        <p v-else>您还没有可访问的主机分组, 请联系管理员添加权限</p>
      </div>
      <!-- 加载中 -->
      <div v-else/>
    </a-spin>
  </div>
</template>

<script>
import { TREE_MOVE_TYPE } from '@/lib/enum'
import { getUUID } from '@/lib/utils'
import { findNode, findParentNode, getChildNodeKeys, setTreeDataProps, getDepthKeys } from '@/lib/tree'

function fillTreeNodeProps(node) {
  node.scopedSlots = {
    icon: 'icon'
  }
  node.editable = false
  node.rename = false
  node.add = false
  node.machineCount = 0
  node.parentId = undefined
}

function computeNodeMachineCount(node, machines) {
  // 计算当前节点机器数量
  const nodeMachineIds = machines.filter(s => s.visible)
  .filter(s => s.groupIdList && s.groupIdList.includes(node.key))
  .map(s => s.id)
  const childMachines = new Set(nodeMachineIds)
  // 计算叶子节点数量
  if (node.children && node.children.length) {
    for (const child of node.children) {
      computeNodeMachineCount(child, machines).forEach(s => childMachines.add(s))
    }
  }
  node.machineCount = childMachines.size
  return childMachines
}

function computeNodeMachine(node, machines) {
  // 计算当前节点机器
  const nodeMachines = machines.filter(s => s.visible)
  .filter(s => s.groupIdList && s.groupIdList.includes(node.key))
  const childMachines = new Set(nodeMachines)
  // 计算叶子节点数量
  if (node.children && node.children.length) {
    for (const child of node.children) {
      computeNodeMachine(child, machines).forEach(s => childMachines.add(s))
    }
  }
  return childMachines
}

const nodeRightMenuHandler = {
  addRoot() {
    const key = getUUID()
    this.treeData.unshift({
      editable: true,
      rename: false,
      add: true,
      title: undefined,
      parentId: -1,
      children: [],
      key
    })
    // 聚焦
    this.$nextTick(() => {
      const ref = this.$refs[`groupNodeInput${key}`]
      ref && ref.focus()
    })
    // 选择
    this.chooseTreeNode(key)
  },
  addChild(node) {
    const key = getUUID()
    let children
    if (node.children) {
      children = node.children
    } else {
      children = node.children = []
    }
    children.unshift({
      editable: true,
      rename: false,
      add: true,
      title: undefined,
      parentId: node.key,
      children: [],
      key
    })
    // 聚焦
    this.$nextTick(() => {
      const ref = this.$refs[`groupNodeInput${key}`]
      ref && ref.focus()
    })
    // 展开
    this.expandTreeNode(node.key)
    // 选择
    this.chooseTreeNode(key)
  },
  rename(node) {
    node.dataRef.editable = true
    node.dataRef.rename = true
    // 聚焦
    this.$nextTick(() => {
      const ref = this.$refs[`groupNodeInput${node.eventKey}`]
      ref && ref.focus()
    })
    // 选择
    this.chooseTreeNode(node.eventKey)
  },
  delete(node) {
    // 获取层内的key
    this.$confirm({
      title: `确认删除 ${node.title}`,
      content: '删除后数据将无法恢复, 确定要删除吗?',
      mask: false,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        const keys = getChildNodeKeys(node, [node.key])
        this.loading = true
        this.$api.deleteMachineGroup({
          idList: keys
        }).then(() => {
          // 删除node
          this.deleteNode(node)
          // 检查删除的node是否包含选中的
          if (keys.includes(this.selectedTreeNode[0])) {
            this.selectedTreeNode = []
          }
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    })
  }
}

export default {
  name: 'MachineEditableTree',
  props: {
    machines: Array,
    visibleEdit: Boolean,
    treeStyle: Object
  },
  data() {
    return {
      editable: false,
      loading: false,
      selectedTreeNode: [],
      expandedTreeNode: [],
      treeData: [],
      curr: null
    }
  },
  watch: {
    selectedTreeNode(e) {
      this.loadMachines(e[0])
    }
  },
  methods: {
    initData() {
      this.loading = true
      this.$api.getMachineGroupTree()
      .then(({ data }) => {
        this.loading = false
        // 填充属性
        this.treeData = setTreeDataProps(data, fillTreeNodeProps)
        // 展开前三层
        this.expandedTreeNode = getDepthKeys(this.treeData, 3)
        // 加载分组机器数量
        this.reloadMachineCount()
        // 加载第一个分组数据
        if (this.treeData.length) {
          this.selectedTreeNode = [this.treeData[0].key]
        }
      }).catch(() => {
        this.loading = false
      })
    },
    clickNodeMenu(node, key) {
      nodeRightMenuHandler[key].call(this, node)
    },
    chooseTreeNode(key) {
      if (key) {
        this.selectedTreeNode = [key]
      }
    },
    expandTreeNode(curr) {
      if (!this.expandedTreeNode.includes(curr)) {
        this.expandedTreeNode.push(curr)
      }
    },
    changeExpandTreeNode(curr) {
      if (this.expandedTreeNode.includes(curr)) {
        for (let i = 0; i < this.expandedTreeNode.length; i++) {
          if (this.expandedTreeNode[i] === curr) {
            this.expandedTreeNode.splice(i, 1)
            return
          }
        }
      } else {
        this.expandedTreeNode.push(curr)
      }
    },
    deleteNode({ key }) {
      // 检查是否为root层
      let nodes
      for (const treeNode of this.treeData) {
        if (treeNode.key === key) {
          nodes = this.treeData
          break
        }
      }
      // 没有则查询tree子节点
      if (nodes) {
        const findParent = findParentNode(this.treeData, key)
        if (findParent) {
          nodes = findParent.children
        }
      }
      if (!nodes) {
        return
      }
      // 移除
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].key === key) {
          nodes.splice(i, 1)
          return
        }
      }
    },
    reloadMachineCount() {
      for (const treeNode of this.treeData) {
        computeNodeMachineCount(treeNode, this.machines)
      }
    },
    moveOnDragEnter(info) {
      this.expandedTreeNode = info.expandedKeys
    },
    moveOnDrop(info) {
      const dragKey = info.dragNode.eventKey
      const dropKey = info.node.eventKey
      const dropPos = info.node.pos.split('-')
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1])
      const loop = (data, key, callback) => {
        data.forEach((item, index, arr) => {
          if (item.key === key) {
            return callback(item, index, arr)
          }
          if (item.children) {
            return loop(item.children, key, callback)
          }
        })
      }
      const data = [...this.treeData]
      let dragObj
      loop(data, dragKey, (item, index, arr) => {
        arr.splice(index, 1)
        dragObj = item
      })
      let moveType
      if (!info.dropToGap) {
        // 挪到里面下
        loop(data, dropKey, item => {
          item.children = item.children || []
          item.children.push(dragObj)
        })
        moveType = TREE_MOVE_TYPE.IN_BOTTOM.value
      } else if ((info.node.children || []).length > 0 && info.node.expanded && dropPosition === 1) {
        // 挪到里面上
        loop(data, dropKey, item => {
          item.children = item.children || []
          item.children.unshift(dragObj)
        })
        moveType = TREE_MOVE_TYPE.IN_TOP.value
      } else {
        let ar
        let i
        loop(data, dropKey, (item, index, arr) => {
          ar = arr
          i = index
        })
        if (dropPosition === -1) {
          // 挪到上面
          ar.splice(i, 0, dragObj)
          moveType = TREE_MOVE_TYPE.PREV.value
        } else {
          // 挪到下面
          ar.splice(i + 1, 0, dragObj)
          moveType = TREE_MOVE_TYPE.NEXT.value
        }
      }
      // 移动
      this.loading = true
      this.$api.moveMachineGroup({
        id: dragKey,
        targetId: dropKey,
        moveType
      }).then(() => {
        this.loading = false
        this.treeData = data
        this.reloadMachineCount()
      }).catch(() => {
        this.loading = false
      })
    },
    saveGroup(e, row) {
      const value = e.target.value
      if (row.add) {
        // 添加
        if (value) {
          this.loading = true
          this.$api.addMachineGroup({
            parentId: row.parentId,
            name: value
          }).then(({ data }) => {
            row.title = value
            row.key = data
            fillTreeNodeProps(row)
            this.loading = false
          }).catch(() => {
            e.target.focus()
            e.target.value = value
            this.loading = false
          })
        } else {
          // 删除node
          this.deleteNode(row)
        }
      } else {
        // 重命名
        if (value) {
          const call = () => {
            row.title = value
            row.editable = false
            row.rename = false
          }
          if (value === row.title) {
            call()
            return
          }
          this.loading = true
          this.$api.renameMachineGroup({
            id: row.key,
            name: value
          }).then(() => {
            call()
            this.loading = false
          }).catch(() => {
            e.target.focus()
            e.target.value = value
            this.loading = false
          })
        }
      }
    },
    loadMachines(id) {
      this.machineLoading = true
      if (id) {
        this.curr = findNode(this.treeData, id)
        if (this.curr) {
          this.$emit('reloadMachine', Array.from(computeNodeMachine(this.curr, this.machines)))
        } else {
          this.$emit('reloadMachine', [])
        }
      } else {
        this.curr = null
        this.$emit('reloadMachine', [])
      }
      this.machineLoading = false
    },
    findCurrentNodeAndChildrenKeys() {
      if (!this.curr) {
        return []
      }
      return getChildNodeKeys(this.curr, [this.curr.key])
    }
  }
}
</script>

<style lang="less" scoped>

.machine-group-tree {
  background-color: #FFF;
  border-radius: 2px;
  overflow-x: hidden;

  .machine-group-tree-editable-title-wrapper {
    height: 50px;
    padding: 8px 12px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .machine-group-tree-title-text {
      font-size: 17px;
      color: rgba(0, 0, 0, .75);
      font-weight: 600;
    }
  }

  .machine-group-tree-uneditable-title-wrapper {
    height: 42px;
    line-height: 42px;
    padding-left: 12px;
    font-weight: 600;
    margin-bottom: 12px;
    background: #F0F0F0;
  }

}

.machine-group-tree-wrapper {
  ::v-deep .ant-tree-title {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    width: 100%;
  }

  ::v-deep .ant-tree-node-content-wrapper {
    padding: 0;
  }

  ::v-deep .ant-tree-node-content-wrapper:not(.ant-tree-node-selected) > span {
    color: rgba(0, 0, 0, .9) !important;
  }

  .init-group-wrapper {
    background: #1890FF;
    padding: 0 32px;
    border-radius: 2px;
  }

  .machine-group-tree-node-title-container {
    user-select: none;
    display: inline-block;
    width: calc(100% - 24px);
    padding: 0 4px;

    .machine-group-tree-node-title-wrapper {
      display: flex;
      justify-content: space-between;
    }

    .machine-group-tree-node-title-value {
      margin-right: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .machine-group-tree-node-title-count {
      width: 22px;
      text-align: end;
    }
  }
}

::-webkit-scrollbar {
  display: none;
}

::v-deep .ant-tree li .ant-tree-node-content-wrapper.ant-tree-node-selected {
  background-color: #1890FF;
  color: #FFF;
}

::v-deep .ant-dropdown-menu-item {
  user-select: none;
}
</style>
