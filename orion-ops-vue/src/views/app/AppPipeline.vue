<template>
  <div class="app-pipeline-container">
    <!-- 搜索列 -->
    <div class="table-search-columns">
      <a-form-model class="app-pipeline-search-form" ref="query" :model="query">
        <a-row>
          <a-col :span="5">
            <a-form-model-item label="名称" prop="name">
              <PipelineAutoComplete ref="pipelineSelector" @change="selectedPipeline" @choose="getList({})"/>
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
        <span class="table-title">流水线列表</span>
        <a-divider v-show="selectedRowKeys.length" type="vertical"/>
        <a-popconfirm v-show="selectedRowKeys.length"
                      placement="topRight"
                      title="是否删除选中流水线?"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="remove(selectedRowKeys)">
          <a-button class="ml8" type="danger" icon="delete">删除</a-button>
        </a-popconfirm>
      </div>
      <!-- 右侧 -->
      <div class="tools-fixed-right">
        <a-button v-if="query.profileId" class="ml16 mr8" type="primary" icon="plus" @click="add">新建</a-button>
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
               :rowSelection="{selectedRowKeys, onChange: e => selectedRowKeys = e}"
               rowKey="id"
               @change="getList"
               :scroll="{x: '100%'}"
               :loading="loading"
               size="middle">
        <!-- 流水线操作 -->
        <template #detail="record">
          <div class="pipeline-stage-wrapper">
            <span v-for="(detail, index) of record.details" :key="detail.id">
              {{ detail.stageType | formatStageType('label') }}
              {{ detail.appName }}
              <a-icon class="span-blue" v-if="index !== record.details.length - 1" type="swap-right"/>
            </span>
          </div>
        </template>
        <!-- 修改时间 -->
        <template #updateTime="record">
          {{ record.updateTime | formatDate }}
        </template>
        <!-- 操作 -->
        <template #action="record">
          <!-- 执行 -->
          <span class="span-blue pointer" @click="execPipeline(record.id)">执行</span>
          <a-divider type="vertical"/>
          <!-- 详情 -->
          <span class="span-blue pointer" @click="openDetail(record.id)">详情</span>
          <a-divider type="vertical"/>
          <!-- 配置 -->
          <span class="span-blue pointer" @click="update(record.id)">配置</span>
          <a-divider type="vertical"/>
          <!-- 删除 -->
          <a-popconfirm title="确认删除当前流水线?"
                        placement="topRight"
                        ok-text="确定"
                        cancel-text="取消"
                        @confirm="remove([record.id])">
            <span class="span-blue pointer">删除</span>
          </a-popconfirm>
        </template>
      </a-table>
    </div>
    <!-- 事件 -->
    <div class="app-pipeline-event">
      <!-- 流水线操作抽屉 -->
      <AppPipelineDetailViewDrawer ref="detailViewDrawer"/>
      <!-- 添加/修改模态框 -->
      <AddPipelineModal ref="addModal" @added="getList({})" @updated="getList"/>
      <!-- 流水线执行模态框 -->
      <AppPipelineExecModal ref="execModal" :visibleReselect="false"/>
    </div>
  </div>
</template>

<script>
import { formatDate } from '@/lib/filters'
import { enumValueOf, STAGE_TYPE } from '@/lib/enum'
import AppPipelineDetailViewDrawer from '@/components/app/AppPipelineDetailViewDrawer'
import AddPipelineModal from '@/components/app/AddPipelineModal'
import AppPipelineExecModal from '@/components/app/AppPipelineExecModal'
import PipelineAutoComplete from '@/components/app/PipelineAutoComplete'

/**
 * 列
 */
const columns = [
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name',
    ellipsis: true,
    width: 200,
    sorter: (a, b) => a.name.localeCompare(b.name)
  },
  {
    title: '操作',
    key: 'detail',
    ellipsis: true,
    scopedSlots: { customRender: 'detail' }
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 200,
    ellipsis: true
  },
  {
    title: '修改时间',
    key: 'updateTime',
    width: 150,
    sorter: (a, b) => a.updateTime - b.updateTime,
    scopedSlots: { customRender: 'updateTime' }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 220,
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'AppPipeline',
  components: {
    PipelineAutoComplete,
    AppPipelineExecModal,
    AddPipelineModal,
    AppPipelineDetailViewDrawer
  },
  watch: {
    'query.profileId'(e) {
      this.$refs.pipelineSelector.loadData(e)
    }
  },
  data: function() {
    return {
      query: {
        id: undefined,
        profileId: undefined,
        name: undefined,
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
      selectedRowKeys: [],
      loading: false,
      columns
    }
  },
  methods: {
    chooseProfile({ id }) {
      this.query.profileId = id
      this.resetForm()
    },
    getList(page = this.pagination) {
      this.loading = true
      this.$api.getAppPipelineList({
        ...this.query,
        queryDetail: 1,
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
    selectedPipeline(id, name) {
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
    remove(idList) {
      this.$api.deleteAppPipeline({
        idList
      }).then(() => {
        this.selectedRowKeys = []
        this.$message.success('删除成功')
        this.getList({})
      })
    },
    add() {
      this.$refs.addModal.add()
    },
    update(id) {
      this.$refs.addModal.update(id)
    },
    execPipeline(id) {
      this.$refs.execModal.openPipeline(this.query.profileId, id)
    },
    openDetail(id) {
      this.$refs.detailViewDrawer.open(id)
    },
    resetForm() {
      this.$refs.query.resetFields()
      this.$refs.pipelineSelector.reset()
      this.query.id = undefined
      this.query.name = undefined
      this.getList({})
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
    this.getList({})
  },
  filters: {
    formatDate,
    formatStageType(type, f) {
      return enumValueOf(STAGE_TYPE, type)[f]
    }
  }
}
</script>

<style scoped>
.pipeline-stage-wrapper {
  display: contents;
}
</style>
