<template>
  <div class="app-info-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-info-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="query.name" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item label="标签" prop="tag">
              <a-input v-model="query.tag" allowClear/>
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
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
        <span class="table-title">应用列表</span>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button class="ml16 mr8" type="primary" icon="plus" @click="add">新建</a-button>
        <a-divider type="vertical"/>
        <a-icon type="search" class="tools-icon" title="查询" @click="getList({})"/>
        <a-icon type="reload" class="tools-icon" title="重置" @click="resetForm"/>
      </div>
    </div>
    <!-- 表格 -->
    <div class="table-main-container">
      <a-table :columns="columns"
               :dataSource="rows"
               :pagination="pagination"
               rowKey="id"
               @change="getList"
               :loading="loading"
               size="middle">
        <!-- 展开的机器列表 -->
        <a-table
          slot="expandedRowRender"
          slot-scope="record"
          v-if="record.machines"
          :rowKey="(record, index) => index"
          :columns="innerColumns"
          :dataSource="record.machines"
          :pagination="false"
          size="middle">
          <!-- 名称 -->
          <span slot="name" slot-scope="machine">
            {{ machine.name }}
            <a-tag v-if="machine.id === 1" color="#5C7CFA">
              宿主机
            </a-tag>
          </span>
          <!-- tag -->
          <span slot="tag" slot-scope="machine">
            <a-tag v-if="machine.tag" color="#20C997">
              {{ machine.tag }}
            </a-tag>
          </span>
          <!-- 状态 -->
          <span slot="status" slot-scope="machine">
            <a-badge
              v-if="machine.status"
              :status='$enum.valueOf($enum.ENABLE_STATUS, machine.status)["badge-status"]'
              :text="$enum.valueOf($enum.ENABLE_STATUS, machine.status).label"/>
          </span>
          <div slot="action" slot-scope="machine">
            <a @click="removeAppMachine(record.id, machine.id)">删除</a>
          </div>
        </a-table>
        <!-- sort -->
        <div slot="sort" slot-scope="record" class="sort-column">
          <a @click="adjustSort(record.id, 1)" title="上调排序">
            <a-icon type="up-square"/>
          </a>
          <a-divider type="vertical"/>
          <a @click="adjustSort(record.id, 2)" title="下调排序">
            <a-icon type="down-square"/>
          </a>
        </div>
        <!-- tag -->
        <span slot="tag" slot-scope="record" class="span-blue pointer">
          <a-tag color="#5C7CFA">
          {{ record.tag }}
          </a-tag>
        </span>
        <!-- 状态 -->
        <a-tag slot="config" slot-scope="record" :color="$enum.valueOf($enum.CONFIG_STATUS, record.isConfig).color">
          {{ $enum.valueOf($enum.CONFIG_STATUS, record.isConfig).label }}
        </a-tag>
        <!-- 操作 -->
        <div slot="action" slot-scope="record">
          <!-- 配置 -->
          <span class="span-blue pointer" @click="toConfig(record.id)">
            配置
          </span>
          <a-divider type="vertical"/>
          <!-- 构建 -->
          <a-button class="p0"
                    type="link"
                    :disabled="record.isConfig !== $enum.CONFIG_STATUS.CONFIGURED.value"
                    @click="buildApp(record.id)">
            构建
          </a-button>
          <a-divider type="vertical"/>
          <!-- 发布 -->
          <a-button class="p0"
                    type="link"
                    :disabled="record.isConfig !== $enum.CONFIG_STATUS.CONFIGURED.value"
                    @click="releaseApp(record.id)">
            发布
          </a-button>
          <a-divider type="vertical"/>
          <!-- 同步 -->
          <AppProfileChecker :ref="'profileChecker' + record.id">
            <a slot="trigger">
              同步
            </a>
            <a-button type="primary" size="small" slot="footer" @click="sync(record.id)">确定</a-button>
          </AppProfileChecker>
          <a-divider type="vertical"/>
          <!-- 下拉菜单 -->
          <a-dropdown>
            <a class="ant-dropdown-link">
              更多
              <a-icon type="down"/>
            </a>
            <a-menu slot="overlay" @click="menuHandler($event, record)">
              <a-menu-item key="update">
                修改
              </a-menu-item>
              <a-menu-item key="remove">
                删除
              </a-menu-item>
              <a-menu-item key="copy">
                复制
              </a-menu-item>
              <a-menu-item key="openEnv">
                环境变量
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-info-event">
      <!-- 添加模态框 -->
      <AddAppModal ref="addModal" @added="getList({})" @updated="getList({})"/>
      <!-- 构建模态框 -->
      <AppBuildModal ref="buildModal"/>
      <!-- 构建模态框 -->
      <AppReleaseModal ref="releaseModal"/>
    </div>
  </div>
</template>

<script>
import AddAppModal from '@/components/app/AddAppModal'
import AppProfileChecker from '@/components/app/AppProfileChecker'
import AppBuildModal from '@/components/app/AppBuildModal'
import AppReleaseModal from '@/components/app/AppReleaseModal'

/**
 * 列
 */
const columns = [
  {
    title: '排序',
    key: 'sort',
    width: 70,
    align: 'center',
    scopedSlots: { customRender: 'sort' }
  },
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '标签',
    key: 'tag',
    scopedSlots: { customRender: 'tag' },
    sorter: (a, b) => a.tag.localeCompare(b.tag)
  },
  {
    title: '是否已配置',
    key: 'config',
    align: 'center',
    sorter: (a, b) => a.isConfig - b.isConfig,
    scopedSlots: { customRender: 'config' }
  },
  {
    title: '仓库名称',
    key: 'vcsName',
    dataIndex: 'vcsName',
    ellipsis: true
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
    width: 200
  },
  {
    title: '操作',
    key: 'action',
    align: 'center',
    width: 270,
    scopedSlots: { customRender: 'action' }
  }
]

/**
 * 展开的机器列
 */
const innerColumns = [
  {
    title: '机器名称',
    key: 'name',
    ellipsis: true,
    sorter: (a, b) => a.name.localeCompare(b.name),
    scopedSlots: { customRender: 'name' }
  },
  {
    title: '标签',
    key: 'tag',
    sorter: (a, b) => a.tag.localeCompare(b.tag),
    scopedSlots: { customRender: 'tag' }
  },
  {
    title: '机器主机',
    key: 'host',
    dataIndex: 'host',
    ellipsis: true,
    sorter: (a, b) => a.host.localeCompare(b.host)
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
    title: '操作',
    key: 'action',
    width: 120,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

/**
 * 菜单操作
 */
const moreMenuHandler = {
  update(record) {
    this.$refs.addModal.update(record.id)
  },
  remove(record) {
    this.$confirm({
      title: '确认删除',
      content: '是否删除当前应用? 将会删除应用相关联的所有数据!',
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        this.$api.deleteApp({
          id: record.id
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
      content: `是否复制应用 ${record.name}?`,
      okText: '复制',
      cancelText: '取消',
      onOk: () => {
        const copyLoading = this.$message.loading('正在提交复制请求', 3)
        this.$api.copyApp({
          id: record.id
        }).then(() => {
          copyLoading()
          this.$message.success('复制成功')
          this.getList()
        }).catch(() => {
          copyLoading()
        })
      }
    })
  },
  openEnv(record) {
    this.$router.push({ path: `/app/env/${record.id}` })
  }
}

export default {
  name: 'AppList',
  components: {
    AppReleaseModal,
    AppBuildModal,
    AddAppModal,
    AppProfileChecker
  },
  data: function() {
    return {
      query: {
        profileId: null,
        name: null,
        tag: null,
        username: null,
        description: null,
        queryMachine: 1
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
      columns,
      innerColumns
    }
  },
  methods: {
    chooseProfile(profile) {
      this.query.profileId = profile.id
      this.getList({})
    },
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getAppList({
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
      }).catch(() => {
        this.loading = false
      })
    },
    adjustSort(id, sortAdjust) {
      this.$api.adjustAppSort({
        id,
        sortAdjust
      }).then(() => {
        this.getList()
      })
    },
    remove(id) {
      this.$api.deleteApp({
        id
      }).then(() => {
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    removeAppMachine(id, machineId) {
      this.$api.deleteAppMachine({
        id,
        profileId: this.query.profileId,
        machineId
      }).then(() => {
        this.$message.success('已删除')
      })
      this.getList()
    },
    add() {
      this.$refs.addModal.add()
    },
    toConfig(id) {
      this.$router.push({
        path: `/app/conf/${id}`
      })
    },
    buildApp(id) {
      this.$refs.buildModal.openBuild(this.query.profileId, id)
    },
    releaseApp(id) {
      this.$refs.releaseModal.openRelease(this.query.profileId, id)
    },
    sync(id) {
      const ref = this.$refs['profileChecker' + id]
      if (!ref.checkedList.length) {
        this.$message.warning('请先选择同步的环境')
        return
      }
      const targetProfileIdList = ref.checkedList
      ref.clear()
      ref.hide()
      // 同步
      this.$message.success('已提交同步请求')
      this.$api.syncApp({
        appId: id,
        profileId: this.query.profileId,
        targetProfileIdList
      }).then(() => {
        this.$message.success('应用已同步成功')
      })
    },
    menuHandler({ key }, record) {
      moreMenuHandler[key].call(this, record)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.getList({})
    }
  },
  mounted() {
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (activeProfile) {
      this.query.profileId = JSON.parse(activeProfile).id
    }
    this.getList({})
  }
}
</script>

<style scoped>

.sort-column {
  font-size: 16px;
}

</style>
