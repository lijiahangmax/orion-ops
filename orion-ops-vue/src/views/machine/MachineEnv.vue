<template>
  <div class="machine-env-container">
    <!-- 机器容器 -->
    <div class="machine-list-container gray-box-shadow">
      <!-- 机器头 -->
      <div class="machine-list-header">
        <a-page-header @back="() => {}">
          <template #title>
            <span class="ant-page-header-heading-title pointer" title="刷新" @click="getMachines">机器列表</span>
          </template>
          <template #backIcon>
            <a-icon type="desktop" title="刷新" @click="getMachines"/>
          </template>
        </a-page-header>
      </div>
      <!-- 机器菜单 -->
      <a-spin :spinning="machineLoading">
        <div class="machine-list-wrapper">
          <a-menu mode="inline" :defaultSelectedKeys="defaultSelectedMachineIds">
            <a-menu-item v-for="machine in machineList" :key="machine.id" :title="machine.host" @click="chooseMachine(machine.id)">
              <a-icon type="desktop"/>
              {{ machine.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 环境变量容器 -->
    <div class="env-container">
      <!-- 环境变量筛选 -->
      <div class="search-columns-wrapper">
        <div class="table-search-columns">

          <a-form-model class="env-search-form" ref="query" :model="query">
            <a-row>
              <a-col :span="6">
                <a-form-model-item label="key" prop="key">
                  <a-input v-model="query.key" allowClear/>
                </a-form-model-item>
              </a-col>
              <a-col :span="6">
                <a-form-model-item label="value" prop="value">
                  <a-input v-model="query.value" allowClear/>
                </a-form-model-item>
              </a-col>
              <a-col :span="6">
                <a-form-model-item label="描述" prop="description">
                  <a-input v-model="query.description" allowClear/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </div>
      </div>
      <!-- 表格 -->
      <div class="table-wrapper">
        <!-- 工具栏 -->
        <div class="table-tools-bar">
          <!-- 左侧 -->
          <div class="tools-fixed-left">
            <span class="table-title">环境变量</span>
            <a-divider type="vertical"/>
            <!-- 视图 -->
            <div class="mx8 nowrap">
              <a-radio-group v-model="viewType" buttonStyle="solid">
                <a-radio-button v-for="view in VIEW_TYPE"
                                :key="view.value"
                                :value="view.value"
                                @click="changeView(view)">
                  {{ view.name }}
                </a-radio-button>
              </a-radio-group>
            </div>
            <a-divider v-show="selectedRowKeys.length" type="vertical"/>
            <!-- 删除 -->
            <a-popconfirm v-show="selectedRowKeys.length && viewType === VIEW_TYPE.TABLE.value"
                          placement="topRight"
                          title="是否删除选中环境变量?"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="remove(selectedRowKeys)">
              <a-button class="ml8" type="danger" icon="delete">删除</a-button>
            </a-popconfirm>
          </div>
          <!-- 右侧 -->
          <div class="tools-fixed-right">
            <a-button class="mx8" v-if="viewType !== VIEW_TYPE.TABLE.value"
                      type="primary"
                      icon="check"
                      :disabled="loading"
                      @click="save">
              保存
            </a-button>
            <a-divider v-if="viewType !== VIEW_TYPE.TABLE.value" type="vertical"/>
            <MachineChecker ref="machineChecker-1" :disableValue="[query.machineId]" placement="bottomRight">
              <template #trigger>
                <a-button class="mx8" type="primary" icon="sync">同步</a-button>
              </template>
              <template #footer>
                <a-button type="primary" size="small" @click="syncEnv(-1)">确定</a-button>
              </template>
            </MachineChecker>
            <a-button class="mr8" type="primary" icon="plus" @click="add">添加</a-button>
            <a-divider type="vertical"/>
            <a-icon type="search" class="tools-icon" title="查询" @click="getMachineEnv({})"/>
            <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
          </div>
        </div>
        <!-- 环境变量表格 -->
        <div class="table-main-container table-scroll-x-auto"
             style="border-radius: 4px 4px 0 0;"
             v-if="viewType === VIEW_TYPE.TABLE.value">
          <a-table :columns="columns"
                   :dataSource="rows"
                   :pagination="pagination"
                   :rowSelection="rowSelection"
                   rowKey="id"
                   @change="getMachineEnv"
                   :scroll="{x: '100%'}"
                   :loading="loading"
                   size="middle">
            <!-- key -->
            <template #key="record">
              <div class="auto-ellipsis">
                <a class="copy-icon-left" @click="$copy(record.key)">
                  <a-icon type="copy"/>
                </a>
                <span class="pointer auto-ellipsis-item" title="预览" @click="preview(record.key)">
                  {{ record.key }}
                </span>
              </div>
            </template>
            <!-- value -->
            <template #value="record">
              <div class="auto-ellipsis">
                <a class="copy-icon-left" @click="$copy(record.value)">
                  <a-icon type="copy"/>
                </a>
                <span class="pointer auto-ellipsis-item" title="预览" @click="preview(record.value)">
                  {{ record.value }}
                </span>
              </div>
            </template>
            <!-- 修改时间 -->
            <template #updateTime="record">
              {{ record.updateTime | formatDate }}
            </template>
            <!-- 操作 -->
            <template #action="record">
              <a @click="update(record.id)">修改</a>
              <a-divider type="vertical"/>
              <MachineChecker :ref="'machineChecker' + record.id" :disableValue="[query.machineId]" placement="bottomRight">
                <template #trigger>
                  <span class="span-blue pointer">同步</span>
                </template>
                <template #footer>
                  <a-button type="primary" size="small" @click="syncEnv(record.id)">确定</a-button>
                </template>
              </MachineChecker>
              <a-divider type="vertical"/>
              <a @click="history(record)">历史</a>
              <a-divider type="vertical"/>
              <a-popconfirm :disabled="record.forbidDelete !== 1"
                            placement="topRight"
                            title="是否删除当前变量?"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="remove([record.id])">
                <a-button class="p0"
                          type="link"
                          style="height: 22px"
                          :disabled="record.forbidDelete !== 1">
                  删除
                </a-button>
              </a-popconfirm>
            </template>
          </a-table>
        </div>
        <!-- 环境变量视图 -->
        <div class="table-main-container env-editor-container" v-if="viewType !== VIEW_TYPE.TABLE.value">
          <a-spin class="editor-spin" style="height: 100%" :spinning="loading">
            <Editor ref="editor" :lang="viewLang"/>
          </a-spin>
        </div>
      </div>
    </div>
    <!-- 事件 -->
    <div class="machine-env-event-container">
      <!-- 添加模态框 -->
      <AddMachineEnvModal ref="addModal" @added="getMachineEnv({})" @updated="getMachineEnv({})"/>
      <!-- 历史模态框 -->
      <EnvHistoryModal ref="historyModal" @rollback="getMachineEnv()"/>
      <!-- 预览框 -->
      <TextPreview ref="preview"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { HISTORY_VALUE_TYPE, VIEW_TYPE } from '@/lib/enum'
import Editor from '@/components/editor/Editor'
import AddMachineEnvModal from '@/components/machine/AddMachineEnvModal'
import EnvHistoryModal from '@/components/content/EnvHistoryModal'
import TextPreview from '@/components/preview/TextPreview'
import MachineChecker from '@/components/machine/MachineChecker'

const columns = [
  {
    title: 'key',
    key: 'key',
    width: 220,
    ellipsis: true,
    sorter: (a, b) => a.key.localeCompare(b.key),
    scopedSlots: { customRender: 'key' }
  },
  {
    title: 'value',
    key: 'value',
    ellipsis: true,
    sorter: (a, b) => a.value.localeCompare(b.value),
    scopedSlots: { customRender: 'value' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  },
  {
    title: '修改时间',
    key: 'updateTime',
    align: 'center',
    width: 150,
    sorter: (a, b) => a.updateTime - b.updateTime,
    scopedSlots: { customRender: 'updateTime' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    align: 'center',
    width: 200,
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'MachineEnv',
  components: {
    MachineChecker,
    AddMachineEnvModal,
    EnvHistoryModal,
    TextPreview,
    Editor
  },
  data: function() {
    return {
      VIEW_TYPE,
      defaultSelectedMachineIds: [],
      machineLoading: false,
      machineList: [],
      rows: [],
      query: {
        machineId: undefined,
        key: undefined,
        value: undefined,
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
      loading: false,
      selectedRowKeys: [],
      columns,
      viewType: VIEW_TYPE.TABLE.value,
      viewLang: null
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
            disabled: record.forbidDelete !== 1
          }
        })
      }
    }
  },
  methods: {
    async getMachines() {
      this.machineLoading = true
      const machines = await this.$api.getMachineList({ limit: 10000 })
      this.machineList = machines.data.rows.map(i => {
        return {
          id: i.id,
          name: i.name,
          host: i.host
        }
      })
      this.machineLoading = false
    },
    chooseMachine(id) {
      this.query.machineId = id
      this.pagination.current = 1
      this.getMachineEnv()
    },
    changeView(view) {
      this.viewLang = view.lang
      this.viewType = view.value
      this.getMachineEnv({})
    },
    getMachineEnv(page = this.pagination) {
      this.loading = true
      if (this.viewType === VIEW_TYPE.TABLE.value) {
        this.$api.getMachineEnvList({
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
      } else {
        // 其他
        this.$api.getMachineEnvView({
          ...this.query,
          viewType: this.viewType
        }).then(({ data }) => {
          this.$refs.editor.setValue(data)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    },
    save() {
      const value = this.$refs.editor.getValue()
      if (!value || !value.trim().length) {
        this.$message.warn('请输入内容')
        return
      }
      this.loading = true
      this.$api.saveMachineEnvView({
        machineId: this.query.machineId,
        viewType: this.viewType,
        value
      }).then(({ data }) => {
        this.loading = false
        this.$message.info(`保存成功 ${data} 条数据`)
        this.getMachineEnv({})
      }).catch(() => {
        this.loading = false
        this.$message.error('解析失败, 请检查内容')
      })
    },
    add() {
      this.$refs.addModal.add(this.query.machineId)
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(idList) {
      this.$api.deleteMachineEnv({
        idList
      }).then(() => {
        this.$message.success('删除成功')
        this.getMachineEnv({})
      })
    },
    preview(value) {
      this.$refs.preview.preview(value)
    },
    history(record) {
      this.$refs.historyModal.open({
        key: record.key,
        valueId: record.id,
        valueType: HISTORY_VALUE_TYPE.MACHINE_ENV.value
      })
    },
    syncEnv(id) {
      const ref = this.$refs['machineChecker' + id]
      if (!ref.checkedList.length) {
        this.$message.warning('请先选择同步的机器')
        return
      }
      const targetMachineIdList = ref.checkedList
      ref.clear()
      ref.hide()
      // 同步
      this.$api.syncMachineEnv({
        id,
        machineId: this.query.machineId,
        targetMachineIdList
      }).then(() => {
        this.$message.success('同步成功')
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getMachineEnv({})
    }
  },
  async mounted() {
    await this.getMachines()
    let chooseId
    if (this.$route.params.id) {
      chooseId = parseInt(this.$route.params.id)
    } else if (this.machineList.length) {
      chooseId = this.machineList[0].id
    } else {
      this.$message.warning('请先维护机器')
      return
    }
    this.chooseMachine(chooseId)
    this.defaultSelectedMachineIds.push(chooseId)
  },
  filters: {
    formatDate
  }
}
</script>

<style lang="less" scoped>

.machine-env-container {
  display: flex;
  justify-content: flex-start;
  width: 100%;

  .machine-list-container {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 2px;
  }

  .env-container {
    width: calc(100% - 228px);
    min-height: calc(100vh - 84px);
    background: #FFF;
    border-radius: 2px;

    .search-columns-wrapper {
      background: #F0F2F5;
      padding-bottom: 18px;

      .table-search-columns {
        margin-bottom: 0;
      }
    }

    .table-wrapper {
      height: calc(100% - 94px);
    }

    .env-editor-container {
      height: calc(100% - 64px);
      padding-bottom: 6px;
    }
  }
}

.machine-list-wrapper {
  width: 200px;
  height: calc(100vh - 162px);
  border-radius: 5px;
  overflow-y: auto;
  margin-right: 20px;

  ul {
    background-color: #FFF;
  }

  ::-webkit-scrollbar-track {
    background: #FFF;
  }
}

::v-deep .editor-spin .ant-spin-container {
  height: 100%;
}

</style>
