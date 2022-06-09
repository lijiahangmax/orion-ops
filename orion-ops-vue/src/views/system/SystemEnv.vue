<template>
  <div class="system-env-container">
    <!-- 环境变量容器 -->
    <div class="system-env-wrapper">
      <!-- 环境变量筛选 -->
      <div class="table-search-columns">
        <a-form-model class="system-env-search-form" ref="query" :model="query">
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
          <a-button class="mr8" type="primary" icon="plus" @click="add">添加</a-button>
          <a-divider type="vertical"/>
          <a-icon type="search" class="tools-icon" title="查询" @click="getSystemEnv({})"/>
          <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
        </div>
      </div>
      <!-- 环境变量表格 -->
      <div class="table-main-container table-scroll-x-auto" v-if="viewType === VIEW_TYPE.TABLE.value">
        <a-table :columns="columns"
                 :dataSource="rows"
                 :pagination="pagination"
                 :rowSelection="rowSelection"
                 rowKey="id"
                 @change="getSystemEnv"
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
    </div>
    <!-- 环境变量视图 -->
    <div class="table-main-container env-editor-container" v-if="viewType !== VIEW_TYPE.TABLE.value">
      <a-spin class="editor-spin" style="height: 100%" :spinning="loading">
        <Editor ref="editor" :lang="viewLang"/>
      </a-spin>
    </div>
    <!-- 事件 -->
    <div class="system-env-event-container">
      <!-- 添加模态框 -->
      <AddSystemEnvModal ref="addModal" @added="getSystemEnv({})" @updated="getSystemEnv({})"/>
      <!-- 历史模态框 -->
      <EnvHistoryModal ref="historyModal" @rollback="getSystemEnv()"/>
      <!-- 预览框 -->
      <TextPreview ref="preview"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { HISTORY_VALUE_TYPE, VIEW_TYPE } from '@/lib/enum'
import Editor from '@/components/editor/Editor'
import AddSystemEnvModal from '@/components/system/AddSystemEnvModal'
import EnvHistoryModal from '@/components/history/EnvHistoryModal'
import TextPreview from '@/components/preview/TextPreview'

const columns = [
  {
    title: 'key',
    key: 'key',
    width: 240,
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
    ellipsis: true,
    width: 180
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
    width: 170,
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'SystemEnv',
  components: {
    AddSystemEnvModal,
    EnvHistoryModal,
    TextPreview,
    Editor
  },
  data: function() {
    return {
      VIEW_TYPE,
      rows: [],
      query: {
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
      viewType: VIEW_TYPE.TABLE.value,
      viewLang: null
    }
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        columnWidth: '75px',
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
    changeView(view) {
      this.viewLang = view.lang
      this.viewType = view.value
      this.getSystemEnv({})
    },
    getSystemEnv(page = this.pagination) {
      this.loading = true
      if (this.viewType === VIEW_TYPE.TABLE.value) {
        this.$api.getSystemEnvList({
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
        this.$api.getSystemEnvView({
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
      this.$api.saveSystemEnvView({
        viewType: this.viewType,
        value
      }).then(({ data }) => {
        this.loading = false
        this.$message.info(`保存成功 ${data} 条数据`)
        this.getSystemEnv({})
      }).catch(() => {
        this.loading = false
        this.$message.error('解析失败, 请检查内容')
      })
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    remove(idList) {
      this.$api.deleteSystemEnv({
        idList
      }).then(() => {
        this.$message.success('删除成功')
        this.getSystemEnv({})
      })
    },
    preview(value) {
      this.$refs.preview.preview(value)
    },
    history(record) {
      this.$refs.historyModal.open({
        key: record.key,
        valueId: record.id,
        valueType: HISTORY_VALUE_TYPE.SYSTEM_ENV.value
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getSystemEnv({})
    }
  },
  async mounted() {
    this.getSystemEnv({})
  },
  filters: {
    formatDate
  }
}
</script>

<style lang="less" scoped>

.system-env-container {
  display: flex;
  justify-content: flex-start;
  flex-direction: column;

  .env-editor-container {
    height: calc(100vh - 236px);
    padding-bottom: 12px;
  }
}

.editor-spin /deep/ .ant-spin-container {
  height: 100%;
}

</style>
