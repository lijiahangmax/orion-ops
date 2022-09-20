<template>
  <div class="machine-list-container">
    <!-- 条件列 -->
    <div class="table-search-columns">
      <a-form-model class="machine-info-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <MachineAutoComplete ref="machineSelector" @change="selectedMachine" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="标识" prop="tag">
              <a-input v-model="query.tag" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="主机" prop="host">
              <a-input v-model="query.host" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" @change="getList({})" allowClear>
                <a-select-option :value="status.value" v-for="status in ENABLE_STATUS" :key="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 表格 -->
    <div class="table-wrapper">
      <!-- 工具栏 -->
      <div class="table-tools-bar">
        <!-- 左侧 -->
        <div class="tools-fixed-left">
          <span class="table-title">机器列表</span>
          <a-divider v-show="selectedRowKeys.length" type="vertical"/>
          <div v-show="selectedRowKeys.length">
            <a-popconfirm title="是否要启用选中机器?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="batchStatus(1)">
              <a-button class="ml8" type="primary" icon="build">启用</a-button>
            </a-popconfirm>
            <a-popconfirm title="是否要停用选中机器?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="batchStatus(2)">
              <a-button class="ml8" type="primary" icon="fork">停用</a-button>
            </a-popconfirm>
            <a-popconfirm title="是否删除选择中机器? 将会删除机器相关联的所有数据!?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="batchRemove()">
              <a-button class="ml8" type="danger" icon="delete">删除</a-button>
            </a-popconfirm>
          </div>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <a target="_blank" href="#/machine/terminal">
            <a-button class="mr8" type="primary" icon="desktop">Terminal</a-button>
          </a>
          <a href="#/machine/add">
            <a-button class="mr8" type="primary" icon="plus">新建</a-button>
          </a>
          <a-divider type="vertical"/>
          <a-icon type="export" class="tools-icon" title="导出数据" @click="openExport"/>
          <a-icon type="import" class="tools-icon" title="导入数据" @click="openImport"/>
          <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
          <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
        </div>
      </div>
      <!-- 表格 -->
      <div class="table-main-container table-scroll-x-auto">
        <a-table :columns="columns"
                 :dataSource="rows"
                 :pagination="pagination"
                 :rowSelection="rowSelection"
                 rowKey="id"
                 @change="getList"
                 :scroll="{x: '100%'}"
                 :loading="loading"
                 size="middle">
          <!-- 名称 -->
          <template #name="record">
            <span :class="[ENABLE_STATUS.ENABLE.value === record.status ? 'machine-name-enabled' : 'machine-name-disabled']"
                  :title="ENABLE_STATUS.ENABLE.value === record.status ? record.name : '机器已停用'">
              {{ record.name }}
            </span>
            <span v-if="record.id === 1" class="host-machine-label usn">#宿主机</span>
          </template>
          <!-- 唯一标识 -->
          <template #tag="record">
          <span class="span-blue">
            {{ record.tag }}
          </span>
          </template>
          <!-- 主机 -->
          <template #host="record">
          <span class="span-blue pointer" title="复制主机" @click="$copy(record.host, true)">
            {{ record.host }}
          </span>
          </template>
          <!-- 操作 -->
          <template #action="record">
            <!-- 启用 -->
            <a-popconfirm :title="`确认${record.status === 1 ? '停用' : '启用'}当前机器?`"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="changeStatus(record)"
                          :disabled="record.id === 1">
              <a-button class="p0"
                        type="link"
                        style="height: 22px"
                        :disabled="record.id === 1">
                {{ (record.status === 1 ? 2 : 1) | formatEnableStatus('label') }}
              </a-button>
            </a-popconfirm>
            <a-divider type="vertical"/>
            <!-- 终端 -->
            <a-button class="p0"
                      type="link"
                      style="height: 22px"
                      :disabled="record.status !== 1">
              <a-tooltip title="ctrl 点击新页面打开终端">
                <a target="_blank" :href="`#/machine/terminal/${record.id}`" @click="openTerminal($event, record)">
                  Terminal
                </a>
              </a-tooltip>
            </a-button>
            <a-divider type="vertical"/>
            <!-- sftp -->
            <a :href="`#/machine/sftp/${record.id}`">
              <a-button title="打开sftp"
                        class="p0"
                        type="link"
                        style="height: 22px"
                        :disabled="record.status !== 1">
                sftp
              </a-button>
            </a>
            <a-divider type="vertical"/>
            <a-dropdown>
              <a class="ant-dropdown-link">
                更多
                <a-icon type="down"/>
              </a>
              <template #overlay>
                <a-menu @click="menuHandler($event, record)">
                  <a-menu-item key="detail">
                    详情
                  </a-menu-item>
                  <a-menu-item key="update">
                    <a :href="`#/machine/update/${record.id}`">编辑</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.id !== 1" key="delete">
                    删除
                  </a-menu-item>
                  <a-menu-item key="copy">
                    复制
                  </a-menu-item>
                  <a-menu-item key="bindKey" v-if="record.authType === MACHINE_AUTH_TYPE.SECRET_KEY.value">
                    绑定秘钥
                  </a-menu-item>
                  <a-menu-item key="ping" v-if="record.status === 1">
                    ping
                  </a-menu-item>
                  <a-menu-item key="connect" v-if="record.status === 1">
                    测试连接
                  </a-menu-item>
                  <a-menu-item key="openEnv">
                    <a :href="`#/machine/env/${record.id}`">环境变量</a>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </a-table>
      </div>
    </div>
    <!-- 事件 -->
    <div class="machine-event-container">
      <!-- 终端模态框 -->
      <div v-if="openTerminalArr.length">
        <TerminalModal v-for="openTerminal of openTerminalArr"
                       :key="openTerminal.terminalId"
                       :ref='`terminalModal${openTerminal.terminalId}`'
                       :visibleMinimize="true"
                       @close="closedTerminal"
                       @minimize="minimizeTerminal"/>
      </div>
      <!-- 终端最小化 -->
      <div class="terminal-minimize-container">
        <a-card v-for="minimizeTerminal of minimizeTerminalArr"
                :key="minimizeTerminal.terminalId"
                :title="`${minimizeTerminal.name} (${minimizeTerminal.host})`"
                class="terminal-minimize-item pointer"
                size="small"
                @click="maximizeTerminal(minimizeTerminal.terminalId)">
          <!-- 关闭按钮 -->
          <template #extra>
            <a-icon class="ml4 pointer"
                    type="close"
                    title="关闭"
                    @click.stop="closeMinimizeTerminal(minimizeTerminal.terminalId)"/>
          </template>
        </a-card>
      </div>
      <!-- 详情模态框 -->
      <MachineDetailModal ref="detailModal"/>
      <!-- 详情模态框 -->
      <MachineKeyBindModal ref="keyBindModal" @bindSuccess="bindKeySuccess"/>
      <!-- 导出模态框 -->
      <MachineExportModal ref="export"/>
      <!-- 导入模态框 -->
      <DataImportModal ref="import" :importType="importType"/>
    </div>
  </div>
</template>

<script>
import { enumValueOf, ENABLE_STATUS, MACHINE_AUTH_TYPE, IMPORT_TYPE } from '@/lib/enum'
import MachineDetailModal from '@/components/machine/MachineDetailModal'
import TerminalModal from '@/components/terminal/TerminalModal'
import MachineExportModal from '@/components/export/MachineExportModal'
import DataImportModal from '@/components/import/DataImportModal'
import MachineAutoComplete from '@/components/machine/MachineAutoComplete'
import MachineKeyBindModal from '@/components/machine/MachineKeyBindModal'

const columns = [
  {
    title: '名称',
    key: 'name',
    ellipsis: true,
    scopedSlots: { customRender: 'name' }
  },
  {
    title: '唯一标识',
    key: 'tag',
    width: 150,
    scopedSlots: { customRender: 'tag' }
  },
  {
    title: '主机',
    key: 'host',
    width: 180,
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
  detail(record) {
    this.$refs.detailModal.open(record.id)
  },
  delete(record) {
    this.$confirm({
      title: '确认删除',
      content: '是否删除当前机器? 将会删除机器相关联的所有数据!',
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        this.$api.deleteMachine({
          idList: [record.id]
        }).then(() => {
          this.$message.success('删除成功')
          this.getList({})
        })
      }
    })
  },
  copy(record) {
    this.$confirm({
      title: '确认复制',
      content: `是否复制机器 ${record.name}?`,
      okText: '复制',
      cancelText: '取消',
      onOk: () => {
        this.$api.copyMachine({
          id: record.id
        }).then(() => {
          this.$message.success('复制成功')
          this.getList()
        })
      }
    })
  },
  bindKey(record) {
    this.$refs.keyBindModal.open(record.id, record.keyId, record.name)
  },
  ping(record) {
    const ping = this.$message.loading(`ping ${record.host}`)
    this.$api.machineTestPing({
      id: record.id
    }).then(e => {
      ping()
      if (e.data === 1) {
        this.$message.success('ok')
      } else {
        this.$message.error(`无法访问 ${record.host}`)
      }
    }).catch(() => {
      ping()
      this.$message.error(`无法访问 ${record.host}`)
    })
  },
  connect(record) {
    const ssh = `${record.username}@${record.host}:${record.sshPort}`
    const connecting = this.$message.loading(`connecting ${ssh}`)
    this.$api.machineTestConnect({
      id: record.id
    }).then(e => {
      connecting()
      if (e.data === 1) {
        this.$message.success('ok')
      } else {
        this.$message.error(`无法连接 ${ssh}`)
      }
    }).catch(() => {
      connecting()
      this.$message.error(`无法连接 ${ssh}`)
    })
  }
}

export default {
  name: 'MachineList',
  components: {
    MachineKeyBindModal,
    MachineAutoComplete,
    DataImportModal,
    MachineExportModal,
    TerminalModal,
    MachineDetailModal
  },
  data() {
    return {
      ENABLE_STATUS,
      MACHINE_AUTH_TYPE,
      rows: [],
      query: {
        id: undefined,
        name: undefined,
        tag: undefined,
        host: undefined,
        status: undefined,
        description: undefined
      },
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      columns,
      loading: false,
      selectedRowKeys: [],
      openTerminalArr: [],
      minimizeTerminalArr: [],
      importType: IMPORT_TYPE.MACHINE_INFO
    }
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: e => {
          this.selectedRowKeys = e
        },
        getCheckboxProps: record => ({
          props: {
            disabled: record.id === 1
          }
        })
      }
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getMachineList({
        ...this.query,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.pagination = pagination
        this.loading = false
        this.selectedRowKeys = []
      }).catch(() => {
        this.loading = false
      })
    },
    selectedMachine(id, name) {
      if (id) {
        this.query.id = id
        this.query.name = undefined
      } else {
        this.query.id = undefined
        this.query.name = name
      }
      if (id === undefined && name === undefined) {
        this.getList({})
      }
    },
    openExport() {
      this.$refs.export.open()
    },
    openImport() {
      this.$refs.import.open()
    },
    menuHandler({ key }, record) {
      const handler = moreMenuHandler[key]
      handler && handler.call(this, record)
    },
    changeStatus(record) {
      if (record.id === 1) {
        this.$message.error('宿主机不支持该操作')
      } else {
        const label = record.status === 1 ? '停用' : '启用'
        const pending = this.$message.loading(`正在${label}...`)
        this.$api.updateMachineStatus({
          idList: [record.id],
          status: record.status === 1 ? 2 : 1
        }).then(() => {
          pending()
          this.$message.success(`已${label}`)
          this.getList()
        }).catch(() => {
          pending()
        })
      }
    },
    batchStatus(status) {
      const label = status === 1 ? '启用' : '停用'
      const pending = this.$message.loading(`正在${label}...`)
      this.$api.updateMachineStatus({
        idList: this.selectedRowKeys,
        status
      }).then(() => {
        pending()
        this.$message.success(`已${label}`)
        this.selectedRowKeys = []
        this.getList()
      }).catch(() => {
        pending()
      })
    },
    batchRemove() {
      this.$api.deleteMachine({
        idList: this.selectedRowKeys
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    bindKeySuccess(id, keyId) {
      this.rows.forEach(row => {
        if (row.id === id) {
          row.keyId = keyId
        }
      })
    },
    openTerminal(e, record) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        const now = Date.now()
        this.openTerminalArr.push({
          name: record.name,
          terminalId: now
        })
        this.$nextTick(() => {
          this.$refs[`terminalModal${now}`][0].open(record, now)
        })
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    closedTerminal(terminalId) {
      for (let i = 0; i < this.openTerminalArr.length; i++) {
        if (this.openTerminalArr[i].terminalId === terminalId) {
          this.openTerminalArr.splice(i, 1)
        }
      }
    },
    minimizeTerminal(m) {
      this.minimizeTerminalArr.push(m)
    },
    maximizeTerminal(terminalId) {
      const refs = this.$refs[`terminalModal${terminalId}`]
      for (let i = 0; i < this.minimizeTerminalArr.length; i++) {
        if (this.minimizeTerminalArr[i].terminalId === terminalId) {
          this.minimizeTerminalArr.splice(i, 1)
        }
      }
      refs && refs[0] && refs[0].maximize()
    },
    closeMinimizeTerminal(terminalId) {
      const refs = this.$refs[`terminalModal${terminalId}`]
      for (let i = 0; i < this.minimizeTerminalArr.length; i++) {
        if (this.minimizeTerminalArr[i].terminalId === terminalId) {
          this.minimizeTerminalArr.splice(i, 1)
        }
      }
      refs && refs[0] && refs[0].close()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.machineSelector.reset()
      this.query.id = undefined
      this.query.name = undefined
      this.getList({})
    }
  },
  filters: {
    formatEnableStatus(status, f) {
      return enumValueOf(ENABLE_STATUS, status)[f]
    }
  },
  mounted() {
    this.getList({})
  },
  beforeDestroy() {
    // 关闭所有最小化的终端
    for (const { terminalId } of this.minimizeTerminalArr) {
      const refs = this.$refs[`terminalModal${terminalId}`]
      refs && refs[0] && refs[0].close()
    }
  }
}
</script>

<style lang="less" scoped>

.machine-name-enabled {
  color: rgba(0, 0, 0, .7);
}

.machine-name-disabled {
  color: rgba(0, 0, 0, .25);
  background-color: transparent;
  border-color: transparent;
  text-shadow: none;
  box-shadow: none;
  cursor: not-allowed;
}

.host-machine-label {
  color: #5C7CFA;
  margin-left: 2px;
}

.terminal-minimize-container {
  position: absolute;
  right: 16px;
  bottom: 16px;
  z-index: 10;
  display: flex;
  flex-wrap: wrap-reverse;
  justify-content: flex-end;

  .terminal-minimize-item {
    width: 200px;
    box-shadow: 0 3px 4px #DEE2E6;
    border-radius: 4px;
    margin: 1px 0.5px;
  }

}

</style>
