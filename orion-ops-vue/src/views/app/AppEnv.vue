<template>
  <div class="app-env-container">
    <!-- 机器容器 -->
    <div class="app-env-app-container">
      <!-- 机器头 -->
      <div class="app-env-app-header">
        <a-page-header @back="() => {}">
          <span slot="title"
                class="ant-page-header-heading-title pointer"
                title="刷新"
                @click="getAppList">应用列表</span>
          <a-icon v-if="!redirectAppId"
                  slot="backIcon"
                  type="appstore"
                  title="刷新"
                  @click="getAppList"/>
          <a-icon v-else
                  slot="backIcon"
                  type="arrow-left"
                  title="返回"
                  @click="backHistory"/>
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
      <!-- 环境变量筛选 -->
      <div class="table-search-columns">
        <a-form-model class="app-env-app-env-search-form" ref="query" :model="query">
          <a-row>
            <a-col :span="6">
              <a-form-model-item label="key" prop="key">
                <a-input v-model="query.key"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="value" prop="value">
                <a-input v-model="query.value"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="6">
              <a-form-model-item label="描述" prop="description">
                <a-input v-model="query.description"/>
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
          <div class="mx8">
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
          <a-button class="ml8" v-show="selectedRowKeys.length"
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
            <a-button slot="trigger" class="mx8" type="primary" icon="sync">同步</a-button>
            <a-button type="primary" size="small" slot="footer" @click="syncEnv(-1)">确定</a-button>
          </AppProfileChecker>
          <a-button class="mr8" type="primary" icon="plus" @click="add">添加</a-button>
          <a-divider type="vertical"/>
          <a-icon type="search" class="tools-icon" title="查询" @click="getAppEnv({})"/>
          <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
        </div>
      </div>
      <!-- 环境变量表格 -->
      <div class="table-main-container table-scroll-x-auto" v-if="viewType === $enum.VIEW_TYPE.TABLE.value">
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
          <div slot="key" slot-scope="record" class="auto-ellipsis">
            <a class="copy-icon-left" @click="$copy(record.key)">
              <a-icon type="copy"/>
            </a>
            <span class="pointer auto-ellipsis-item" title="预览" @click="preview(record.key)">
              {{ record.key }}</span>
          </div>
          <!-- value -->
          <div slot="value" slot-scope="record" class="auto-ellipsis">
            <a class="copy-icon-left" @click="$copy(record.value)">
              <a-icon type="copy"/>
            </a>
            <span class="pointer auto-ellipsis-item" title="预览" @click="preview(record.value)">
              {{ record.value }}
            </span>
          </div>
          <!-- 修改时间 -->
          <span slot="updateTime" slot-scope="record">
              {{
              record.updateTime | formatDate({
                date: record.updateTime,
                pattern: 'yyyy-MM-dd HH:mm:ss'
              })
            }}
          </span>
          <!-- 操作 -->
          <div slot="action" slot-scope="record">
            <a @click="update(record.id)">修改</a>
            <a-divider type="vertical"/>
            <AppProfileChecker :ref="'profileChecker' + record.id">
              <a slot="trigger">同步</a>
              <a-button type="primary" size="small" slot="footer" @click="syncEnv(record.id)">确定</a-button>
            </AppProfileChecker>
            <a-divider type="vertical"/>
            <a @click="history(record)">历史</a>
            <a-divider v-if="record.forbidDelete === 1" type="vertical"/>
            <a-popconfirm v-if="record.forbidDelete === 1"
                          placement="topRight"
                          title="是否删除当前变量?"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="remove(record.id)">
              <span class="span-blue pointer">删除</span>
            </a-popconfirm>
          </div>
        </a-table>
      </div>
      <!-- 环境变量视图 -->
      <div class="table-main-container env-editor-container" v-else>
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

import _utils from '@/lib/utils'
import _$enum from '@/lib/enum'
import Editor from '@/components/editor/Editor'
import AddAppEnvModal from '@/components/app/AddAppEnvModal'
import EnvHistoryModal from '@/components/history/EnvHistoryModal'
import TextPreview from '@/components/preview/TextPreview'
import AppProfileChecker from '@/components/app/AppProfileChecker'

const columns = [
  {
    title: '序号',
    key: 'seq',
    width: 60,
    align: 'center',
    customRender: (text, record, index) => `${index + 1}`
  },
  {
    title: 'key',
    key: 'key',
    width: 180,
    ellipsis: true,
    sorter: (a, b) => a.key.localeCompare(b.key),
    scopedSlots: { customRender: 'key' }
  },
  {
    title: 'value',
    key: 'value',
    width: 220,
    ellipsis: true,
    sorter: (a, b) => a.value.localeCompare(b.value),
    scopedSlots: { customRender: 'value' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
    width: 180
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
    width: 180,
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
      redirectAppId: null,
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
          this.rows = data.rows
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
      if (id === -1) {
        this.$message.success('已提交同步请求')
      }
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
    },
    backHistory() {
      this.$router.back(-1)
    }
  },
  created() {
    if (this.$route.params.id) {
      this.redirectAppId = parseInt(this.$route.params.id)
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
    if (this.redirectAppId) {
      chooseId = this.redirectAppId
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
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
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
    width: calc(100% - 222px)
  }

  .env-editor-container {
    height: calc(100% - 150px);
    padding-bottom: 8px;
  }

}

.app-env-app-list {
  width: 200px;
  min-height: 25vh;
  max-height: 740px;
  border-radius: 5px;
  overflow-y: auto;
  margin-right: 20px;
}

.app-env-app-list ul {
  background-color: #FFF;
}

.app-env-app-list::-webkit-scrollbar-track {
  background: #FFF;
}

.editor-spin /deep/ .ant-spin-container {
  height: 100%;
}

</style>
