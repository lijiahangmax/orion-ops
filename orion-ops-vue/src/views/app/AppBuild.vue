<template>
  <div class="app-info-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-info-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="应用" prop="appId">
              <AppAutoComplete ref="appSelector" @change="selectedApp" @choose="getList({})"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="构建序列" prop="seq">
              <a-input v-limit-integer v-model="query.seq" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="请选择" @change="getList({})" allowClear>
                <a-select-option v-for="status of BUILD_STATUS" :key="status.value" :value="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item label="只看自己" prop="onlyMyself">
              <a-checkbox v-model="query.onlyMyself" @change="getList({})"/>
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
          <span class="table-title">构建列表</span>
          <a-divider v-show="selectedRowKeys.length" type="vertical"/>
          <div v-show="selectedRowKeys.length">
            <a-popconfirm title="确认删除所选中的构建记录吗?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="deleteBuild(selectedRowKeys)">
              <a-button class="ml8" type="danger" icon="delete">删除</a-button>
            </a-popconfirm>
          </div>
        </div>
        <!-- 右侧 -->
        <div class="tools-fixed-right">
          <a-button v-if="query.profileId" class="ml16 mr8" type="primary" icon="build" @click="openBuild">构建应用</a-button>
          <a-divider type="vertical"/>
          <a-icon type="delete" class="tools-icon" title="清理" @click="openClear"/>
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
          <!-- 构建序列 -->
          <template #seq="record">
            <span class="span-blue">#{{ record.seq }}</span>
          </template>
          <!-- 版本 -->
          <template #version="record">
          <span v-if="record.repoId">
            <!-- 分支 -->
            <span class="mr4 nowrap" v-if="record.branchName" title="分支">
              <a-icon type="branches"/>{{ record.branchName }}
            </span>
            <!-- commitId -->
            <a-tooltip v-if="record.commitId">
              <template #title>
                {{ record.commitId }}
              </template>
              <span class="span-blue">
                 #{{ record.commitId.substring(0, 7) }}
              </span>
            </a-tooltip>
          </span>
          </template>
          <!-- 状态 -->
          <template #status="record">
            <a-tag class="m0" :color="record.status | formatBuildStatus('color')">
              {{ record.status | formatBuildStatus('label') }}
            </a-tag>
          </template>
          <!-- 创建时间 -->
          <template #createTime="record">
            {{ record.createTime | formatDate }}
          </template>
          <!-- 操作 -->
          <template #action="record">
            <!-- 详情 -->
            <a @click="openDetail(record.id)">详情</a>
            <a-divider type="vertical"/>
            <!-- 日志 -->
            <a-tooltip title="ctrl 点击打开新页面">
              <a target="_blank"
                 :href="`#/app/build/log/view/${record.id}`"
                 @click="openLogView($event, record.id)">日志</a>
            </a-tooltip>
            <a-divider type="vertical"/>
            <!-- 重新构建 -->
            <a-popconfirm title="是否要重新构建?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="rebuild(record.id)">
              <span class="span-blue pointer">重新构建</span>
            </a-popconfirm>
            <a-divider type="vertical" v-if="visibleHolder.visibleTerminate(record.status)"/>
            <!-- 停止 -->
            <a-popconfirm v-if="visibleHolder.visibleTerminate(record.status)"
                          title="是否要停止构建?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="terminate(record.id)">
              <span class="span-blue pointer">停止</span>
            </a-popconfirm>
            <a-divider type="vertical" v-if="visibleHolder.visibleDelete(record.status)"/>
            <!-- 删除 -->
            <a-popconfirm v-if="visibleHolder.visibleDelete(record.status)"
                          title="确认删除当前构建记录吗?"
                          placement="topRight"
                          ok-text="确定"
                          cancel-text="取消"
                          @confirm="deleteBuild([record.id])">
              <span class="span-blue pointer">删除</span>
            </a-popconfirm>
          </template>
        </a-table>
      </div>
    </div>
    <!-- 事件 -->
    <div class="app-info-event">
      <!-- 添加模态框 -->
      <AppBuildModal ref="addModal" :visibleReselect="true" @submit="getList({})"/>
      <!-- 详情模态框 -->
      <AppBuildDetailDrawer ref="detail"/>
      <!-- 日志模态框 -->
      <AppBuildLogAppenderModal ref="logView"/>
      <!-- 清理模态框 -->
      <AppBuildClearModal ref="clear" @clear="getList({})"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { BUILD_STATUS, enumValueOf } from '@/lib/enum'
import AppBuildDetailDrawer from '@/components/app/AppBuildDetailDrawer'
import AppBuildModal from '@/components/app/AppBuildModal'
import AppBuildLogAppenderModal from '@/components/log/AppBuildLogAppenderModal'
import AppBuildClearModal from '@/components/clear/AppBuildClearModal'
import AppAutoComplete from '@/components/app/AppAutoComplete'

/**
 * 状态判断
 */
const visibleHolder = {
  visibleTerminate(status) {
    return status === BUILD_STATUS.RUNNABLE.value
  },
  visibleDelete(status) {
    return status !== BUILD_STATUS.WAIT.value && status !== BUILD_STATUS.RUNNABLE.value
  }
}

/**
 * 列
 */
const columns = [
  {
    title: '序列',
    key: 'seq',
    width: 90,
    sorter: (a, b) => a.seq - b.seq,
    scopedSlots: { customRender: 'seq' }
  },
  {
    title: '构建应用',
    key: 'appName',
    dataIndex: 'appName',
    ellipsis: true,
    width: 200
  },
  {
    title: '版本',
    key: 'version',
    ellipsis: true,
    scopedSlots: { customRender: 'version' }
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
    align: 'center',
    sorter: (a, b) => a.status - b.status,
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '持续时间',
    key: 'keepTime',
    dataIndex: 'keepTime',
    width: 120,
    sorter: (a, b) => (a.used || 0) - (b.used || 0)
  },
  {
    title: '构建时间',
    key: 'createTime',
    align: 'center',
    ellipsis: true,
    width: 150,
    sorter: (a, b) => a.createTime - b.createTime,
    scopedSlots: { customRender: 'createTime' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 230,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'AppBuild',
  components: {
    AppAutoComplete,
    AppBuildClearModal,
    AppBuildLogAppenderModal,
    AppBuildModal,
    AppBuildDetailDrawer
  },
  data: function() {
    return {
      BUILD_STATUS,
      query: {
        profileId: undefined,
        id: undefined,
        seq: undefined,
        appId: undefined,
        appName: undefined,
        status: undefined,
        onlyMyself: false,
        description: undefined
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
      pollId: null,
      columns,
      selectedRowKeys: [],
      visibleHolder
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
            disabled: record.status === BUILD_STATUS.WAIT.value ||
              record.status === BUILD_STATUS.RUNNABLE.value
          }
        })
      }
    }
  },
  methods: {
    chooseProfile(profile) {
      this.query.profileId = profile.id
      this.getList({})
    },
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getAppBuildList({
        ...this.query,
        onlyMyself: this.query.onlyMyself ? 1 : 2,
        page: page.current,
        limit: page.pageSize
      }).then(({ data }) => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.page
        this.rows = data.rows || []
        this.pagination = pagination
        this.selectedRowKeys = []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    selectedApp(id, name) {
      if (id) {
        this.query.appId = id
        this.query.appName = undefined
      } else {
        this.query.appId = undefined
        this.query.appName = name
      }
      if (id === undefined && name === undefined) {
        this.getList({})
      }
    },
    openBuild() {
      this.$refs.addModal.openBuild(this.query.profileId)
    },
    openDetail(id) {
      this.$refs.detail.open(id)
    },
    rebuild(id) {
      this.$api.rebuildApp({
        id
      }).then(() => {
        this.$message.success('已开始重新构建')
        this.getList({})
      })
    },
    terminate(id) {
      this.$api.terminateAppBuild({
        id
      }).then(() => {
        this.$message.success('已停止')
      })
    },
    deleteBuild(idList) {
      this.$api.deleteAppBuild({
        idList
      }).then(() => {
        this.$message.success('已删除')
        this.getList({})
      })
    },
    openLogView(e, id) {
      if (!e.ctrlKey) {
        e.preventDefault()
        // 打开模态框
        this.$refs.logView.open(id)
        return false
      } else {
        // 跳转页面
        return true
      }
    },
    openClear() {
      this.$refs.clear.open(this.query.profileId)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.appSelector.reset()
      this.query.id = undefined
      this.query.appId = undefined
      this.query.appName = undefined
      this.query.status = undefined
      this.query.onlyMyself = false
      this.getList({})
    },
    pollStatus() {
      if (!this.rows || !this.rows.length) {
        return
      }
      const idList = this.rows.filter(r => r.status === BUILD_STATUS.WAIT.value ||
        r.status === BUILD_STATUS.RUNNABLE.value).map(s => s.id)
      if (!idList.length) {
        return
      }
      this.$api.getAppBuildStatusList({
        idList
      }).then(({ data }) => {
        if (!data || !data.length) {
          return
        }
        for (const build of data) {
          this.rows.filter(s => s.id === parseInt(build.id)).forEach(s => {
            s.status = build.status
            s.keepTime = build.keepTime
            s.used = build.used
          })
        }
        // 强制刷新状态
        this.$set(this.rows, 0, this.rows[0])
      })
    }
  },
  filters: {
    formatDate,
    formatBuildStatus(status, f) {
      return enumValueOf(BUILD_STATUS, status)[f]
    }
  },
  mounted() {
    this.query.id = this.$route.query.id
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
      return
    }
    this.query.profileId = JSON.parse(activeProfile).id
    // 设置轮询
    this.pollId = setInterval(this.pollStatus, 5000)
    // 查询列表
    this.getList({})
  },
  beforeDestroy() {
    this.pollId !== null && clearInterval(this.pollId)
    this.pollId = null
  }
}
</script>

<style scoped>

</style>
