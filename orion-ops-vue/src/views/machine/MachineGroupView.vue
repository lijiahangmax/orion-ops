<template>
  <div class="machine-group-view-container">
    <!-- 机器组树 -->
    <div class="machine-group-tree gray-box-shadow">
      <!-- 标题 -->
      <div class="machine-group-tree-title-wrapper">
        <span class="machine-group-tree-title-text">分组列表</span>
        <a-switch v-model="editable" checkedChildren="编辑" unCheckedChildren="浏览"/>
      </div>
      <a-divider class="m0"/>
      <!-- 树 -->
      <a-spin wrapperClassName="machine-group-tree-wrapper" :spinning="treeLoading">
        <!-- 加载完成有数据 -->
        <a-tree v-if="treeData.length"
                :treeData="treeData"
                :draggable="editable"
                :blockNode="true"
                :showIcon="true"
                :selectedKeys="selectedTreeNode"
                :expandedKeys="expandedTreeNode"
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
            <a-dropdown :trigger="['contextmenu']" v-if="!row.editable">
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
        <div v-if="!treeLoading && !treeData.length">
          <a v-if="$isAdmin()" @click="clickNodeMenu(null,'addRoot')">点击这里添加分组</a>
          <p v-else>您还没有可访问的主机分组, 请联系管理员添加权限</p>
        </div>
        <!-- 加载中 -->
        <div v-else/>
      </a-spin>
    </div>
    <!-- 表格 -->
    <div class="machine-group-table gray-box-shadow">
      {{ groupMachines }}
    </div>
  </div>
</template>

<script>
import { getUUID, defineArrayKey } from '@/lib/utils'
import { findNode, findParentNode, getChildNodeKeys, setTreeDataProps, getDepthKeys } from '@/lib/tree'

function fillTreeNodeProps(node) {
  node.scopedSlots = {
    icon: 'icon'
  }
  node.editable = false
  node.rename = false
  node.add = false
  node.machineCount = 0
  node.machines = []
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
    const keys = getChildNodeKeys(node, [node.key])
    this.treeLoading = true
    this.$api.deleteMachineGroup({
      idList: keys
    }).then(() => {
      // 删除node
      this.deleteNode(node)
      // 检查删除的node是否包含选中的
      if (keys.includes(this.selectedTreeNode[0])) {
        this.selectedTreeNode = []
      }
      this.treeLoading = false
    }).catch(() => {
      this.treeLoading = false
    })
  }
}

export default {
  name: 'MachineGroupView',
  data() {
    return {
      editable: false,
      treeLoading: false,
      machineLoading: false,
      selectedTreeNode: [],
      expandedTreeNode: [],
      machines: [],
      filterMachines: [],
      groupMachines: [],
      treeData: []
    }
  },
  watch: {
    selectedTreeNode(e) {
      this.loadMachines(e[0])
    }
  },
  methods: {
    initData() {
      this.machineLoading = true
      this.treeLoading = true
      this.$api.getMachineList({
        limit: 10000,
        queryGroup: true
      }).then(({ data }) => {
        this.machineLoading = false
        const rows = data.rows || []
        defineArrayKey(rows, 'visible', true)
        this.machines = rows
      })
      .then(this.$api.getMachineGroupTree)
      .then(({ data }) => {
        this.treeLoading = false
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
        this.machineLoading = false
        this.treeLoading = false
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
    deleteNode(row) {
      let nodes
      if (row.parentId === -1) {
        nodes = this.treeData
      } else {
        nodes = findParentNode(this.treeData, row.key).children
      }
      if (!nodes) {
        return
      }
      // 移除
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].key === row.key) {
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
    loadMachines(id) {
      if (id) {
        const node = findNode(this.treeData, id)
        if (node) {
          this.groupMachines = Array.from(computeNodeMachine(node, this.machines))
        } else {
          this.groupMachines = []
        }
      } else {
        this.groupMachines = []
      }
    },
    saveGroup(e, row) {
      const value = e.target.value
      if (row.add) {
        // 添加
        if (value) {
          this.treeLoading = true
          this.$api.addMachineGroup({
            parentId: row.parentId,
            name: value
          }).then(({ data }) => {
            row.title = value
            row.key = data
            fillTreeNodeProps(row)
            this.treeLoading = false
          }).catch(() => {
            this.treeLoading = false
          })
        } else {
          // 删除node
          this.deleteNode(row)
        }
      } else {
        // 重命名
        if (value) {
          this.treeLoading = true
          this.$api.renameMachineGroup({
            id: row.key,
            name: value
          }).then(() => {
            row.title = value
            row.editable = false
            row.rename = false
            this.treeLoading = false
          }).catch(() => {
            this.treeLoading = false
          })
        }
      }
    }
  },
  mounted() {
    this.initData()
  }
}
</script>

<style lang="less" scoped>
.machine-group-view-container {
  display: flex;
}

.machine-group-tree {
  width: 25%;
  background-color: #FFF;
  border-radius: 2px;
  margin-right: 8px;
  overflow-x: hidden;

  .machine-group-tree-title-wrapper {
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

}

.machine-group-tree-wrapper {
  padding: 0 8px;
  min-height: 220px;

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

.machine-group-table {
  background-color: #FFF;
  border-radius: 2px;
  margin-left: 8px;
  padding: 8px;
  width: 75%;
}

::v-deep .ant-tree li .ant-tree-node-content-wrapper.ant-tree-node-selected {
  background-color: #1890FF;
  color: #FFF;
}

::v-deep .ant-dropdown-menu-item {
  user-select: none;
}
</style>
