<template>
  <div class="app-vcs-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-vcs-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="url" prop="url">
              <a-input v-model="query.url" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="资源用户" prop="username">
              <a-input v-model="query.username" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="全部" allowClear>
                <a-select-option :value="type.value" v-for="type in $enum.VCS_STATUS" :key="type.value">
                  {{ type.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">仓库列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">新建</a-button>
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
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- url -->
        <template v-slot:url="record">
          <span class="span-blue pointer" title="预览" @click="$refs.preview.preview(record.url)">
            {{ record.url }}
          </span>
        </template>
        <!-- 资源用户 -->
        <template v-slot:username="record">
          {{ record.authType === $enum.VCS_AUTH_TYPE.PASSWORD.value ? record.username : '私人令牌' }}
        </template>
        <!-- 状态 -->
        <template v-slot:status="record">
          <a-tag :color="$enum.valueOf($enum.VCS_STATUS, record.status).color">
            {{ $enum.valueOf($enum.VCS_STATUS, record.status).label }}
          </a-tag>
        </template>
        <!-- 操作 -->
        <template v-slot:action="record">
          <!-- 初始化 -->
          <a v-if="$enum.VCS_STATUS.UNINITIALIZED.value === record.status || $enum.VCS_STATUS.ERROR.value === record.status" @click="init(record)">初始化</a>
          <a-popconfirm title="确定要重新初始化吗?"
                        v-else-if="$enum.VCS_STATUS.OK.value === record.status"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        :disabled="$enum.VCS_STATUS.INITIALIZING.value === record.status"
                        @confirm="reInit(record)">
            <span class="span-blue pointer">重新初始化</span>
          </a-popconfirm>
          <a-divider type="vertical" v-if="$enum.VCS_STATUS.UNINITIALIZED.value === record.status ||
                  $enum.VCS_STATUS.ERROR.value === record.status ||
                  $enum.VCS_STATUS.OK.value === record.status"/>
          <!-- 修改 -->
          <a-button class="p0"
                    type="link"
                    style="height: 22px"
                    :disabled="$enum.VCS_STATUS.INITIALIZING.value === record.status"
                    @click="update(record.id)">
            修改
          </a-button>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前仓库? 将会清空所有应用的关联!"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        :disabled="$enum.VCS_STATUS.INITIALIZING.value === record.status"
                        @confirm="remove(record.id)">
            <a-button class="p0"
                      type="link"
                      style="height: 22px"
                      :disabled="$enum.VCS_STATUS.INITIALIZING.value === record.status">
              删除
            </a-button>
          </a-popconfirm>
          <!--          <a-divider type="vertical"/>-->
          <!--          &lt;!&ndash; 清空 &ndash;&gt;-->
          <!--          <a-popconfirm title="确认要清空历史应用构建仓库?"-->
          <!--                        placement="topRight"-->
          <!--                        ok-text="确定"-->
          <!--                        cancel-text="取消"-->
          <!--                        :disabled="$enum.VCS_STATUS.INITIALIZING.value === record.status"-->
          <!--                        @confirm="clean(record.id)">-->
          <!--            <a-button class="p0"-->
          <!--                      type="link"-->
          <!--                      style="height: 22px"-->
          <!--                      :disabled="$enum.VCS_STATUS.INITIALIZING.value === record.status">-->
          <!--              清空-->
          <!--            </a-button>-->
          <!--          </a-popconfirm>-->
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-vcs-event">
      <!-- 添加模态框 -->
      <AddAppVcsModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 预览框 -->
      <TextPreview ref="preview"/>
      <!-- 导出模态框 -->
      <AppVcsExportModal ref="export"/>
    </div>
  </div>
</template>

<script>
import AddAppVcsModal from '@/components/app/AddAppVcsModal'
import TextPreview from '@/components/preview/TextPreview'
import AppVcsExportModal from '@/components/export/AppVcsExportModal'

/**
 * 列
 */
const columns = [
  {
    title: '序号',
    key: 'seq',
    width: 60,
    align: 'center',
    customRender: (text, record, index) => `${index + 1}`
  },
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    width: 160,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: 'url',
    key: 'url',
    width: 300,
    ellipsis: true,
    scopedSlots: { customRender: 'url' }
  },
  {
    title: '资源用户',
    key: 'username',
    ellipsis: true,
    width: 180,
    scopedSlots: { customRender: 'username' }
  },
  {
    title: '状态',
    key: 'status',
    width: 110,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 150,
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 240,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'AppVcs',
  components: {
    AppVcsExportModal,
    TextPreview,
    AddAppVcsModal
  },
  data: function() {
    return {
      query: {
        name: null,
        url: null,
        username: null,
        description: null,
        status: undefined
      },
      rows: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: function(total) {
          return `共 ${total} 条`
        }
      },
      loading: false,
      columns
    }
  },
  methods: {
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getVcsList({
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
      }).catch(() => {
        this.loading = false
      })
    },
    remove(id) {
      this.$api.deleteVcs({
        id
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    clean(id) {
      this.$api.cleanVcs({
        id
      }).then(() => {
        this.$message.success('清空完毕')
      })
    },
    init(record) {
      record.status = this.$enum.VCS_STATUS.INITIALIZING.value
      this.$api.initVcs({
        id: record.id
      }).then(() => {
        this.$message.success('正在初始化')
      })
    },
    reInit(record) {
      record.status = this.$enum.VCS_STATUS.INITIALIZING.value
      this.$api.reInitVcs({
        id: record.id
      }).then(() => {
        this.$message.success('正在重新初始化')
      })
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    openExport() {
      this.$refs.export.open()
    },
    openImport() {
      this.$refs.import.open()
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    }
  },
  mounted() {
    this.getList({})
  }
}
</script>

<style scoped>

</style>
