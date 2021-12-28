<template>
  <div class="app-info-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-info-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="应用" prop="appId">
              <AppSelector ref="appSelector" @change="appId => query.appId = appId"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="构建序列" prop="seq">
              <a-input v-limit-integer v-model="query.seq"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="状态" prop="status">
              <a-select v-model="query.status" placeholder="请选择" allowClear>
                <a-select-option v-for="status of $enum.BUILD_STATUS" :key="status.value" :value="status.value">
                  {{ status.label }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="描述" prop="description">
              <a-input v-model="query.description"/>
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
    <!-- 工具栏 -->
    <div class="table-tools-bar">
      <!-- 左侧 -->
      <div class="tools-fixed-left">
        <span class="table-title">构建列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="build" @click="openBuild">构建应用</a-button>
        <a-divider type="vertical"/>
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
        <!-- 构建序列 -->
        <a-tag slot="seq" slot-scope="record" color="#5C7CFA">
          #{{ record.seq }}
        </a-tag>
        <!-- 版本 -->
        <span slot="version" slot-scope="record" v-if="record.vcsId">
          <!-- 仓库 -->
          <span class="mr4" title="仓库">{{ record.vcsName }}</span>
          <!-- 分支 -->
          <span class="mr4" v-if="record.branchName" style="white-space: nowrap;" title="分支"><a-icon type="branches"/>{{ record.branchName }}</span>
          <!-- commitId -->
          <a-tooltip v-if="record.commitId">
            <div slot="title">
              <span style="display: block; word-break: break-all;">commitId: {{ record.commitId }}</span>
            </div>
            <span class="span-blue">
               #{{ record.commitId.substring(0, 7) }}
            </span>
          </a-tooltip>
        </span>
        <!-- 状态 -->
        <a-tag class="m0" slot="status" slot-scope="record" :color="$enum.valueOf($enum.BUILD_STATUS, record.status).color">
          {{ $enum.valueOf($enum.BUILD_STATUS, record.status).label }}
        </a-tag>
        <!-- 创建时间 -->
        <span slot="createTime" slot-scope="record">
          {{
            record.createTime | formatDate({
              date: record.createTime,
              pattern: 'yyyy-MM-dd HH:mm:ss'
            })
          }}
        </span>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 详情 -->
          <a @click="openDetail(record.id)">详情</a>
          <a-divider type="vertical"/>
          <!-- 日志 -->
          <a target="_blank"
             title="ctrl 打开新页面"
             :href="`#/app/build/log/view/${record.id}`"
             @click="openLogView($event, record.id)">日志</a>
          <a-divider type="vertical"/>
          <!-- 重新构建 -->
          <a-popconfirm title="是否要重新构建?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="rebuild(record.id)">
            <span class="span-blue pointer">重新构建</span>
          </a-popconfirm>
          <a-divider type="vertical" v-if="record.status === $enum.BUILD_STATUS.RUNNABLE.value"/>
          <!-- 停止 -->
          <a-popconfirm title="是否要停止构建?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="terminated(record)">
            <span class="span-blue pointer" v-if="record.status === $enum.BUILD_STATUS.RUNNABLE.value">停止</span>
          </a-popconfirm>
        </div>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-info-event">
      <!-- 添加模态框 -->
      <AppBuildModal ref="addModal" @submit="getList({})"/>
      <!-- 详情模态框 -->
      <AppBuildDetailDrawer ref="detail"/>
      <!-- 日志模态框 -->
      <AppBuildLogAppenderModal ref="logView"/>
    </div>
  </div>
</template>

<script>
import _utils from '@/lib/utils'
import AppSelector from '@/components/app/AppSelector'
import AppBuildDetailDrawer from '@/components/app/AppBuildDetailDrawer'
import AppBuildModal from '@/components/app/AppBuildModal'
import AppBuildLogAppenderModal from '@/components/log/AppBuildLogAppenderMadal'

/**
 * 列
 */
const columns = [
  {
    title: '序列',
    key: 'seq',
    width: 90,
    align: 'center',
    sorter: (a, b) => a.seq - b.seq,
    scopedSlots: { customRender: 'seq' }
  },
  {
    title: '构建应用',
    key: 'appName',
    dataIndex: 'appName',
    ellipsis: true,
    width: 200,
    sorter: (a, b) => a.appName.localeCompare(b.appName)
  },
  {
    title: '仓库 / 版本',
    key: 'version',
    width: 260,
    scopedSlots: { customRender: 'version' }
  },
  {
    title: '状态',
    key: 'status',
    align: 'center',
    width: 120,
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
    title: '构建人',
    key: 'createUserName',
    dataIndex: 'createUserName',
    ellipsis: true,
    width: 130,
    sorter: (a, b) => a.createUserName.localeCompare(b.createUserName)
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
    ellipsis: true,
    width: 170
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
    AppBuildLogAppenderModal,
    AppBuildModal,
    AppSelector,
    AppBuildDetailDrawer
  },
  data: function() {
    return {
      query: {
        profileId: null,
        seq: null,
        appId: null,
        status: undefined,
        onlyMyself: false,
        description: null
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
        this.rows = data.rows
        this.pagination = pagination
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    openBuild() {
      this.$refs.addModal.openBuild(this.query.profileId)
    },
    openDetail(id) {
      this.$refs.detail.open(id)
    },
    rebuild(id) {
      const rebuilding = this.$message.loading('正在提交构建请求...', 5)
      this.$api.rebuildApp({
        id
      }).then(() => {
        rebuilding()
        this.$message.success('已开始重新构建')
        this.getList({})
      }).catch(() => {
        rebuilding()
      })
    },
    terminated(record) {
      const terminating = this.$message.loading('正在提交停止请求...', 5)
      this.$api.terminatedAppBuild({
        id: record.id
      }).then(() => {
        terminating()
        this.$message.success('已提交停止请求')
      }).catch(() => {
        terminating()
      })
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.appSelector.reset()
      this.query.appId = undefined
      this.query.status = undefined
      this.query.onlyMyself = false
      this.getList({})
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
    pollStatus() {
      if (!this.rows || !this.rows.length) {
        return
      }
      const idList = this.rows.filter(r => r.status === this.$enum.BUILD_STATUS.WAIT.value ||
        r.status === this.$enum.BUILD_STATUS.RUNNABLE.value)
        .map(s => s.id)
      if (!idList.length) {
        return
      }
      this.$api.getAppBuildStatusList({
        idList
      }).then(({ data }) => {
        for (const build of data) {
          this.rows.filter(s => s.id === parseInt(build.id)).forEach(s => {
            s.status = build.status
            s.keepTime = build.keepTime
            s.used = build.used
          })
        }
      })
    }
  },
  filters: {
    formatDate(origin, {
      date,
      pattern
    }) {
      return _utils.dateFormat(new Date(date), pattern)
    }
  },
  mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
    }
    this.query.profileId = JSON.parse(activeProfile).id
    // 设置轮询
    setInterval(this.pollStatus, 5000)
    // 查询列表
    this.getList({})
  }
}
</script>

<style scoped>

</style>
