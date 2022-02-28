<template>
  <div class="app-env-container">
    <!-- 机器容器 -->
    <div class="app-env-app-container">
      <!-- 机器头 -->
      <div class="app-env-app-header">
        <a-page-header @back="() => {}">
          <template #title>
            <span class="ant-page-header-heading-title pointer" title="刷新" @click="getAppList">应用列表</span>
          </template>
          <template #backIcon>
            <a-icon type="appstore" title="刷新" @click="getAppList"/>
          </template>
        </a-page-header>
      </div>
      <!-- 机器菜单 -->
      <a-spin :spinning="appLoading">
        <div class="app-env-app-list">
          <a-menu mode="inline" :defaultSelectedKeys="defaultSelectedAppIds">
            <a-menu-item v-for="app in appList" :key="app.id" :title="app.host" @click="chooseApp(app.id)">
              <a-icon type="code-sandbox"/>
              {{ app.name }}
            </a-menu-item>
          </a-menu>
        </div>
      </a-spin>
    </div>
    <!-- 环境变量容器 -->
    <div class="app-env-app-env-container">
      <div class="app-env-wrapper">
        <!-- 环境变量筛选 -->
        <div class="table-search-columns">
          <a-form-model class="app-env-app-env-search-form" ref="query" :model="query">
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
        <!-- 工具栏 -->
        <div class="table-tools-bar">
          <!-- 左侧 -->
          <div class="tools-fixed-left">
            <span class="table-title">环境变量</span>
            <a-divider type="vertical"/>
            <!-- 视图 -->
            <div class="mx8 nowrap">
              <a-radio-group v-model="viewType" buttonStyle="solid">
                <a-radio-button v-for="view in $enum.VIEW_TYPE"
                                :key="view.value"
                                :value="view.value"
                                @click="changeView(view)">
                  {{ view.name }}
                </a-radio-button>
              </a-radio-group>
            </div>
            <a-divider v-show="selectedRowKeys.length" type="vertical"/>
            <!-- 删除 -->
            <a-button class="ml8" v-show="selectedRowKeys.length && viewType === $enum.VIEW_TYPE.TABLE.value"
                      type="danger"
                      icon="delete"
                      @click="batchRemove()">
              删除
            </a-button>
          </div>
          <!-- 右侧 -->
          <div class="tools-fixed-right">
            <a-button class="mx8" v-if="viewType !== $enum.VIEW_TYPE.TABLE.value"
                      type="primary"
                      icon="check"
                      :disabled="loading"
                      @click="save">
              保存
            </a-button>
            <a-divider v-if="viewType !== $enum.VIEW_TYPE.TABLE.value" type="vertical"/>
            <AppProfileChecker ref="profileChecker-1">
              <template #trigger>
                <a-button class="mx8" type="primary" icon="sync">同步</a-button>
              </template>
              <template #footer>
                <a-button type="primary" size="small" @click="syncEnv(-1)">确定</a-button>
              </template>
            </AppProfileChecker>
            <a-button class="mr8" type="primary" icon="plus" @click="add">添加</a-button>
            <a-divider type="vertical"/>
            <a-icon type="search" class="tools-icon" title="查询" @click="getAppEnv({})"/>
            <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
          </div>
        </div>
        <!-- 环境变量表格 -->
        <div class="table-main-container table-scroll-x-auto"
             style="border-radius: 4px 4px 0 0;"
             v-if="viewType === $enum.VIEW_TYPE.TABLE.value">
          <a-table :columns="columns"
                   :dataSource="rows"
                   :pagination="pagination"
                   :rowSelection="rowSelection"
                   rowKey="id"
                   @change="getAppEnv"
                   :scroll="{x: '100%'}"
                   :loading="loading"
                   size="middle">
            <!-- key -->
            <template v-slot:key="record">
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
            <template v-slot:value="record">
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
            <template v-slot:updateTime="record">
              {{ record.updateTime | formatDate }}
            </template>
            <!-- 操作 -->
            <template v-slot:action="record">
              <a @click="update(record.id)">修改</a>
              <a-divider type="vertical"/>
              <AppProfileChecker :ref="'profileChecker' + record.id">
                <template #trigger>
                  <span class="span-blue pointer">同步</span>
                </template>
                <template #footer>
                  <a-button type="primary" size="small" @click="syncEnv(record.id)">确定</a-button>
                </template>
              </AppProfileChecker>
              <a-divider type="vertical"/>
              <a @click="history(record)">历史</a>
              <a-divider type="vertical"/>
              <a-popconfirm :disabled="record.forbidDelete !== 1"
                            placement="topRight"
                            title="是否删除当前变量?"
                            ok-text="确定"
                            cancel-text="取消"
                            @confirm="remove(record.id)">
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
      </div>
      <!-- 环境变量视图 -->
      <div class="table-main-container env-editor-container" v-if="viewType !== $enum.VIEW_TYPE.TABLE.value">
        <a-spin class="editor-spin" style="height: 100%" :spinning="loading">
          <Editor ref="editor" :value="viewValue" :lang="viewLang"/>
        </a-spin>
      </div>
    </div>
    <!-- 事件 -->
    <div class="app-env-event-container">
      <!-- 添加模态框 -->
      <AddAppEnvModal ref="addModal" @added="getAppEnv({})" @updated="getAppEnv({})"/>
      <!-- 历史模态框 -->
      <EnvHistoryModal ref="historyModal" @rollback="getAppEnv()"/>
      <!-- 预览框 -->
      <TextPreview ref="preview"/>
    </div>
  </div>
</template>

<script>

import _$enum from '@/lib/enum'
import Editor from '@/components/editor/Editor'
import AddAppEnvModal from '@/components/app/AddAppEnvModal'
import EnvHistoryModal from '@/components/history/EnvHistoryModal'
import TextPreview from '@/components/preview/TextPreview'
import AppProfileChecker from '@/components/app/AppProfileChecker'
import _filters from '@/lib/filters'

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
    width: 150,
    align: 'center',
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
  name: 'AppEnv',
  components: {
    AddAppEnvModal,
    EnvHistoryModal,
    TextPreview,
    Editor,
    AppProfileChecker
  },
  data: function() {
    return {
      defaultSelectedAppIds: [],
      appLoading: false,
      appList: [],
      rows: [],
      query: {
        appId: null,
        profileId: null,
        key: null,
        value: null,
        description: null
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
      viewType: _$enum.VIEW_TYPE.TABLE.value,
      viewLang: null,
      viewValue: null
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
    async getAppList() {
      this.appLoading = true
      const apps = await this.$api.getAppList({ limit: 10000 })
      this.appList = apps.data.rows.map(i => {
        return {
          id: i.id,
          name: i.name
        }
      })
      this.appLoading = false
    },
    chooseApp(id) {
      this.query.appId = id
      this.pagination.current = 1
      this.getAppEnv()
    },
    chooseProfile({ id }) {
      this.query.profileId = id
      this.pagination.current = 1
      this.getAppEnv({})
    },
    changeView(view) {
      this.viewLang = view.lang
      this.viewType = view.value
      // 表格
      if (this.viewType === this.$enum.VIEW_TYPE.TABLE.value) {
        this.viewValue = null
      }
      this.getAppEnv({})
    },
    getAppEnv(page = this.pagination) {
      this.loading = true
      if (this.viewType === this.$enum.VIEW_TYPE.TABLE.value) {
        this.$api.getAppEnvList({
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
        // 其他视图
        this.$api.getAppEnvView({
          ...this.query,
          viewType: this.viewType
        }).then(({ data }) => {
          this.viewValue = data
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
      this.$api.saveAppEnvView({
        appId: this.query.appId,
        profileId: this.query.profileId,
        viewType: this.viewType,
        value
      }).then(({ data }) => {
        this.loading = false
        this.$message.info(`保存成功 ${data} 条数据`)
        this.getAppEnv({})
      }).catch(() => {
        this.loading = false
        this.$message.error('解析失败, 请检查内容')
      })
    },
    add() {
      this.$refs.addModal.add(this.query.appId, this.query.profileId)
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(id) {
      this.$api.deleteAppEnv({
        idList: [id]
      }).then(() => {
        this.$message.success('删除成功')
        this.getAppEnv({})
      })
    },
    batchRemove() {
      this.$confirm({
        title: '确认删除',
        content: '是否删除所选环境变量?',
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.deleteAppEnv({
            idList: this.selectedRowKeys
          }).then(() => {
            this.$message.success('删除成功')
            this.getAppEnv({})
          })
          this.selectedRowKeys = []
        }
      })
    },
    preview(value) {
      this.$refs.preview.preview(value)
    },
    history(record) {
      this.$refs.historyModal.open({
        key: record.key,
        valueId: record.id,
        valueType: this.$enum.HISTORY_VALUE_TYPE.APP_ENV.value
      })
    },
    syncEnv(id) {
      const ref = this.$refs['profileChecker' + id]
      if (!ref.checkedList.length) {
        this.$message.warning('请先选择同步的环境')
        return
      }
      const targetProfileIdList = ref.checkedList
      ref.clear()
      ref.hide()
      // 同步
      this.$api.syncAppEnv({
        id,
        appId: this.query.appId,
        profileId: this.query.profileId,
        targetProfileIdList
      }).then(() => {
        this.$message.success('同步成功')
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getAppEnv({})
    }
  },
  async mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
      return
    }
    this.query.profileId = JSON.parse(activeProfile).id
    // 加载应用列表
    await this.getAppList()
    let chooseId
    if (this.$route.params.id) {
      chooseId = parseInt(this.$route.params.id)
    } else if (this.appList.length) {
      chooseId = this.appList[0].id
    } else {
      this.$message.warning('请先维护应用')
      return
    }
    this.chooseApp(chooseId)
    this.defaultSelectedAppIds.push(chooseId)
  },
  filters: {
    ..._filters
  }
}
</script>

<style lang="less" scoped>

.app-env-container {
  display: flex;
  justify-content: flex-start;
  width: 100%;

  .app-env-app-container {
    width: 216px;
    padding: 0 8px 8px 8px;
    margin-right: 16px;
    background-color: #FFF;
    border-radius: 4px;
  }

  .app-env-app-env-container {
    width: calc(100% - 228px);
    min-height: calc(100vh - 84px);
    background: #FFF;
    border-radius: 4px;

    .app-env-wrapper {
      background: #F0F2F5;
    }
  }

  .env-editor-container {
    height: calc(100% - 150px);
    padding-bottom: 12px;
  }
}

.app-env-app-list {
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

.editor-spin /deep/ .ant-spin-container {
  height: 100%;
}

</style>
