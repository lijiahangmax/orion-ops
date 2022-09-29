<template>
  <div class="machine-group-view-container">
    <!-- 机器组树 -->
    <MachineEditableTree class="gray-box-shadow"
                         ref="tree"
                         :machines="machines"
                         :visibleEdit="true"
                         @reloadMachine="reloadMachine"/>
    <!-- 表格 -->
    <div class="machine-group-table table-wrapper gray-box-shadow">
      <!-- 工具栏 -->
      <div class="table-tools-bar">
        <!-- 左侧 -->
        <div class="tools-fixed-left">
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <a target="_blank" href="#/machine/terminal">
            <a-button type="primary" icon="desktop">Terminal</a-button>
          </a>
        </div>
      </div>
      <!-- 表格 -->
      <div class="table-main-container table-scroll-x-auto">
        <a-table :columns="columns"
                 :dataSource="filterMachines"
                 :pagination="pagination"
                 @change="changePage"
                 rowKey="id"
                 :scroll="{x: '100%'}"
                 :loading="loading"
                 size="middle">
          <!-- 名称 -->
          <template #info="record">
            <div class='machine-info-wrapper'>
              <!-- 机器名称 -->
              <div class="machine-info-name-wrapper">
                <span class="machine-info-label">名称: </span>
                <span class="machine-info-name-value">{{ record.name }}</span>
              </div>
              <!-- 唯一标识 -->
              <div class="machine-info-tag-wrapper">
                <span class="machine-info-label">标识: </span>
                <span class="machine-info-tag-value">{{ record.tag }}</span>
              </div>
            </div>
          </template>
          <!-- 主机 -->
          <template #host="record">
            <span class="span-blue pointer" title="复制主机" @click="$copy(record.host, true)">
              {{ record.host }}
            </span>
          </template>
          <!-- 操作 -->
          <template #action="record">
            <!-- 终端 -->
            <a-tooltip title="ctrl 点击新页面打开终端">
              <a target="_blank" :href="`#/machine/terminal/${record.id}`" @click="$emit('openTerminal', $event, record)">
                Terminal
              </a>
            </a-tooltip>
            <a-divider type="vertical"/>
            <!-- sftp -->
            <a :href="`#/machine/sftp/${record.id}`" title="打开sftp">sftp</a>
            <a-divider type="vertical"/>
            <!-- 移出 -->
            <span class="span-light-red pointer" title="移出分组" @click="removeToGroup(record)">移出</span>
            <a-divider type="vertical"/>
            <a-dropdown>
              <a class="ant-dropdown-link">
                更多
                <a-icon type="down"/>
              </a>
              <template #overlay>
                <a-menu @click="menuHandler($event, record)">
                  <a-menu-item key="ping">
                    ping
                  </a-menu-item>
                  <a-menu-item key="connect">
                    测试连接
                  </a-menu-item>
                  <a-menu-item key="openEnv">
                    <a :href="`#/machine/env/${record.id}`">环境变量</a>
                  </a-menu-item>
                  <a-menu-item key="openMonitor">
                    <a :href="`#/machine/monitor/metrics/${record.id}`">机器监控</a>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </a-table>
      </div>
    </div>
  </div>
</template>

<script>
import { defineArrayKey } from '@/lib/utils'
import { ENABLE_STATUS } from '@/lib/enum'

import MachineEditableTree from '@/components/machine/MachineEditableTree'

const columns = [
  {
    title: '机器信息',
    key: 'name',
    ellipsis: true,
    scopedSlots: { customRender: 'info' }
  },
  {
    title: '机器主机',
    key: 'host',
    width: 200,
    scopedSlots: { customRender: 'host' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    align: 'center',
    width: 240,
    scopedSlots: { customRender: 'action' }
  }
]

const moreMenuHandler = {
  ping(record) {
    const ping = this.$message.loading(`ping ${record.host}`)
    this.$api.machineTestPing({
      id: record.id
    }).then(() => {
      ping()
      this.$message.success('ok')
    }).catch(({ msg }) => {
      ping()
      this.$message.error(msg)
    })
  },
  connect(record) {
    const ssh = `${record.username}@${record.host}:${record.sshPort}`
    const connecting = this.$message.loading(`connecting ${ssh}`)
    this.$api.machineTestConnect({
      id: record.id
    }).then(() => {
      connecting()
      this.$message.success('ok')
    }).catch(({ msg }) => {
      connecting()
      this.$message.error(msg)
    })
  }
}

export default {
  name: 'MachineGroupView',
  components: { MachineEditableTree },
  data() {
    return {
      loading: false,
      machines: [],
      filterMachines: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      columns
    }
  },
  methods: {
    initData() {
      this.loading = true
      this.$refs.tree.loading = true
      this.$api.getMachineList({
        limit: 10000,
        status: ENABLE_STATUS.ENABLE.value,
        queryGroup: true
      }).then(({ data }) => {
        this.loading = false
        const rows = data.rows || []
        defineArrayKey(rows, 'visible', true)
        this.machines = rows
      }).then(() => {
        this.$refs.tree.initData()
      }).catch(() => {
        this.loading = false
        this.$refs.tree.loading = false
      })
    },
    reloadMachine(machines) {
      this.filterMachines = machines
    },
    changePage(page) {
      const pagination = { ...this.pagination }
      pagination.current = page.current
      this.pagination = pagination
    },
    menuHandler({ key }, record) {
      const handler = moreMenuHandler[key]
      handler && handler.call(this, record)
    },
    removeToGroup(record) {
      const nodeKeys = this.$refs.tree.findCurrentNodeAndChildrenKeys()
      if (!nodeKeys.length) {
        return
      }
      this.loading = true
      this.$api.deleteMachineGroupMachine({
        groupIdList: nodeKeys,
        machineId: record.id
      }).then(() => {
        // 移出分组
        for (const nodeKey of nodeKeys) {
          for (let i = 0; i < record.groupIdList.length; i++) {
            if (nodeKey === record.groupIdList[i]) {
              record.groupIdList.splice(i, 1)
            }
          }
        }
        this.loading = false
        // 计算数量
        this.$refs.tree.reloadMachineCount()
        // 重新加载机器
        this.$refs.tree.loadMachines(this.$refs.tree.selectedTreeNode[0])
      }).catch(() => {
        this.loading = false
      })
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

.machine-group-table {
  background-color: #FFF;
  border-radius: 2px;
  margin-left: 8px;
  width: 75%;
}

.machine-info-wrapper {
  display: flex;
  flex-direction: column;

  .machine-info-label {
    user-select: none;
    display: inline-block;
    color: rgba(0, 0, 0.9);
    margin-right: 4px;
  }

  .machine-info-name-wrapper {
    margin-bottom: 2px;
  }

  .machine-info-tag-wrapper {
    margin-top: 2px;
  }

  .machine-info-name-value, .machine-info-tag-value {
    color: rgba(0, 0, 0, .7);
  }
}

::v-deep .ant-table-row-cell-ellipsis {
  padding: 8px !important;
}
</style>
