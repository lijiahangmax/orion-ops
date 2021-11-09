<template>
  <div class="machine-sftp-container">
    <a-drawer
      placement="right"
      width="77%"
      :closable="false"
      :visible="visible"
      :afterVisibleChange="changeVisibleAfter"
      :zIndex="800"
      @close="visible = !visible">
      <!-- sftp主体 -->
      <div class="sftp-drawer-title" slot="title">
        <span>文件管理器</span>
        <a-icon type="menu-unfold"/>
      </div>
      <!-- sftp主体 -->
      <div class="machine-sftp-main-container">
        <div class="sftp-folder-left-fixed">
          <SftpDirectoryTree></SftpDirectoryTree>
        </div>
        <div class="sftp-body-container">
          <div class="sftp-container">
            <!-- 头部 -->
            <div class="sftp-header">
              <div class="sftp-bar-left">
                <!-- home -->
                <div class="sftp-home">
                  <a-icon type="home" :title="home" @click="listFiles(home)"/>
                  <a-popconfirm placement="bottomLeft"
                                ok-text="跳转"
                                cancel-text="取消"
                                :visible="redirectVisible"
                                @confirm="redirectDirectory"
                                @cancel="redirectVisible = false">
                    <div slot="icon">
                      <a-input ref="redirectPathInput" v-model="redirectPath" placeholder="路径" @pressEnter="redirectDirectory"/>
                    </div>
                    <a-icon class="path-input-trigger" type="folder-open" title="路径" @click="openRedirect"/>
                  </a-popconfirm>
                </div>
                <!-- 路径 -->
                <div class="sftp-navigator-paths">
                  <a-breadcrumb v-for="(pathItem,index) in pathAnalysis" :key="pathItem.path">
                    <a-breadcrumb-item>
                      <div class="path-item">
                        <span v-if="index !== 0">/</span>
                        <a class="sftp-navigator-paths-item" @click="listFiles(pathItem.path)">{{ pathItem.name }}</a>
                      </div>
                    </a-breadcrumb-item>
                  </a-breadcrumb>
                </div>
              </div>
              <div class="sftp-tools-bar">
                <!-- 显示隐藏 -->
                <div class="sftp-tools-hide-switch">
                  <span class="text">显示隐藏文件: </span>
                  <a-switch :disabled="!sessionToken" v-model="showHideFile" @change="changeShowHideFile"/>
                </div>
                <!-- 工具栏 -->
                <div class="sftp-file-exec-buttons">
                  <a-button-group>
                    <a-button :disabled="!sessionToken || selectedRowKeys.length === 0"
                              @click="batchRemove" title="批量删除" icon="delete"/>
                    <a-popconfirm :disabled="!sessionToken || selectedRowKeys.length === 0"
                                  placement="bottomRight"
                                  ok-text="确定" cancel-text="取消"
                                  @confirm="batchDownload">
                      <template slot="title">
                        是否下载当前选中的 {{ selectedRowKeys.length }} 个文件?
                      </template>
                      <a-button :disabled="!sessionToken || selectedRowKeys.length === 0" title="批量下载" icon="cloud-download"/>
                    </a-popconfirm>
                    <a-button :disabled="!sessionToken" @click="$copy(path)" title="复制路径" icon="link"/>
                    <a-button :disabled="!sessionToken" @click="listFiles()" title="刷新" icon="reload"/>
                    <a-button :disabled="!sessionToken" @click="openTouch" title="创建" icon="file-add"/>
                    <a-popover v-model="uploadVisible" trigger="click" placement="bottomRight" overlayClassName="sftp-upload-list-popover">
                      <SftpUpload ref="upload"
                                  slot="content"
                                  @changeVisible="changeUploadVisible"
                                  :currentPath="path"
                                  :sessionToken="sessionToken"/>
                      <a-button :disabled="!sessionToken" title="上传" icon="cloud-upload"/>
                    </a-popover>
                    <a-popover trigger="click" placement="bottomRight" overlayClassName="sftp-transfer-list-popover">
                      <FileTransferList slot="content" :sessionToken="sessionToken"/>
                      <a-button :disabled="!sessionToken" title="传输列表" icon="unordered-list"/>
                    </a-popover>
                  </a-button-group>
                  <a-input id="upload-file-input" type="file" style="display: none" @change="checkUploadFile($event)"/>
                </div>
              </div>
            </div>
          </div>
          <!-- 文件列表 -->
          <div class="sftp-file-list-container">
            <a-table :columns="columns"
                     :dataSource="files"
                     :pagination="pagination"
                     :rowSelection="rowSelection"
                     :loading="loading"
                     @change="changePage"
                     rowKey="path"
                     size="small">
              <!-- 名称 -->
              <div slot="name" slot-scope="record" class="file-name-cols">
                <!-- 图标 -->
                <a-icon :type="$enum.valueOf($enum.FILE_TYPE, record.attr.charAt(0)).icon"
                        :title="$enum.valueOf($enum.FILE_TYPE, record.attr.charAt(0)).title"
                        class="file-name-cols-icon"/>
                <!-- 名称 -->
                <a v-if="record.isDir" @click="listFiles(record.path)">{{ record.name }}</a>
                <span v-else>{{ record.name }}</span>
              </div>
              <!-- 名称筛选图标 -->
              <a-icon
                slot="nameFilterIcon"
                slot-scope="filtered"
                type="search"
                :style="{ color: filtered ? '#108ee9' : undefined }"
              />
              <!-- 名称筛选输入框 -->
              <div slot="nameFilterDropdown"
                   slot-scope="{setSelectedKeys, selectedKeys, confirm, clearFilters}"
                   style="padding: 8px">
                <a-input v-ant-ref="c => (nameSearchInput = c)"
                         placeholder="名称"
                         :value="selectedKeys[0]"
                         style="width: 188px; margin-bottom: 8px; display: block;"
                         @change="e => setSelectedKeys(e.target.value ? [e.target.value] : [])"
                         @pressEnter="() => confirm()"/>
                <a-button type="primary" icon="search" size="small"
                          style="width: 90px; margin-right: 8px"
                          @click="() => confirm()">
                  搜索
                </a-button>
                <a-button size="small" style="width: 90px" @click="() => resetFileName(selectedKeys, clearFilters)">
                  重置
                </a-button>
              </div>
              <!-- 修改时间 -->
              <span slot="modifyTime" slot-scope="record">
              {{
                  record.modifyTime | formatDate({
                    date: record.modifyTime,
                    pattern: 'yyyy-MM-dd HH:mm:ss'
                  })
                }}
            </span>
              <!-- 操作 -->
              <div slot="action" slot-scope="record">
                <!-- 复制路径 -->
                <a @click="$copy(record.path)" title="复制路径">
                  <a-icon type="copy"/>
                </a>
                <a-divider type="vertical"/>
                <!-- 下载 -->
                <a-popconfirm v-if="record.isDir" placement="bottomRight" ok-text="确定" cancel-text="取消" @confirm="download">
                  <template slot="title">
                    确定要下载当前文件夹?
                  </template>
                  <a title="下载">
                    <a-icon type="cloud-download"/>
                  </a>
                </a-popconfirm>
                <a @click="download(record)" title="下载" v-else>
                  <a-icon type="cloud-download"/>
                </a>
                <a-divider type="vertical"/>
                <!-- 删除 -->
                <a v-if="record.isSafe" @click="remove(record)" title="删除">
                  <a-icon type="delete"/>
                </a>
                <a-divider v-if="record.isSafe" type="vertical"/>
                <!-- 移动 -->
                <a @click="openMove(record)" title="移动">
                  <a-icon type="block"/>
                </a>
                <a-divider type="vertical"/>
                <!-- 提权 -->
                <a @click="openChmod(record)" title="提权">
                  <a-icon type="team"/>
                </a>
              </div>
            </a-table>
          </div>
        </div>
      </div>
    </a-drawer>
    <!-- sftp事件 -->
    <div class="machine-sftp-event-container">
      <!-- 创建文件模态框 -->
      <SftpTouchModal ref="touchModal" :sessionToken="sessionToken" @listFiles="listFiles()"/>
      <!-- 移动文件模态框 -->
      <SftpMoveModal ref="moveModal" :sessionToken="sessionToken" :files="files"/>
      <!-- 文件提权模态框 -->
      <SftpChmodModal ref="chmodModal" :sessionToken="sessionToken" :files="files"/>
    </div>
  </div>
</template>

<script>
import _utils from '@/lib/utils'
import SftpDirectoryTree from './SftpDirectoryTree'
import SftpTouchModal from './SftpTouchModal'
import SftpMoveModal from './SftpMoveModal'
import SftpChmodModal from './SftpChmodModal'
import FileTransferList from './FileTransferList'
import SftpUpload from './SftpUpload'

/**
 * 获取解析路径
 */
function getPathAnalysis(analysisPath, paths = []) {
  const lastSymbol = analysisPath.lastIndexOf('/')
  if (lastSymbol === -1) {
    paths.unshift({
      name: '/',
      path: '/'
    })
    return paths
  }
  const name = analysisPath.substring(lastSymbol, analysisPath.length)
  if (!_utils.isEmptyStr(name) && name !== '/') {
    paths.unshift({
      name: name.substring(1, name.length),
      path: analysisPath
    })
  }
  return getPathAnalysis(analysisPath.substring(0, lastSymbol), paths)
}

/**
 * 表格列
 */
const fileListColumns = function() {
  return [
    {
      title: '名称',
      key: 'name',
      width: 330,
      scopedSlots: {
        customRender: 'name',
        filterDropdown: 'nameFilterDropdown',
        filterIcon: 'nameFilterIcon'
      },
      sorter: (a, b) => a.name.localeCompare(b.name),
      onFilter: (value, record) => record.name.toString().toLowerCase().includes(value.toLowerCase()),
      onFilterDropdownVisibleChange: visible => {
        if (visible) {
          setTimeout(() => {
            this.nameSearchInput.focus()
          }, 0)
        }
      }
    },
    {
      title: '大小',
      dataIndex: 'size',
      key: 'size',
      width: 60,
      sorter: (a, b) => a.sizeByte - b.sizeByte
    },
    {
      title: '属性',
      dataIndex: 'attr',
      key: 'attr',
      width: 70
    },
    {
      title: '修改时间',
      key: 'modifyTime',
      width: 110,
      scopedSlots: { customRender: 'modifyTime' },
      sorter: (a, b) => a.modifyTime - b.modifyTime
    },
    {
      title: '操作',
      key: 'operation',
      width: 120,
      scopedSlots: { customRender: 'action' }
    }
  ]
}

export default {
  name: 'SftpMainBody',
  props: {
    machineId: Number
  },
  components: {
    SftpDirectoryTree,
    SftpTouchModal,
    SftpMoveModal,
    SftpChmodModal,
    FileTransferList,
    SftpUpload
  },
  data: function() {
    return {
      visible: false,
      init: false,
      showHideFile: false,
      sessionToken: '',
      home: '',
      path: '',
      files: [],
      redirectPath: '',
      redirectVisible: false,
      selectedRowKeys: [],
      selectedRows: [],
      columns: fileListColumns.call(this),
      loading: false,
      nameSearchInput: null,
      uploadVisible: false,
      pagination: {
        current: 1,
        pageSize: 12,
        showTotal: function(total) {
          return `共 ${total}条`
        }
      }
    }
  },
  computed: {
    pathAnalysis() {
      return getPathAnalysis(this.path)
    },
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        columnWidth: '25px',
        onChange: (keys, rows) => {
          this.selectedRowKeys = keys
          this.selectedRows = rows
        }
      }
    }
  },
  watch: {
    path(e) {
      this.redirectPath = e
    }
  },
  methods: {
    changeVisibleAfter(e) {
      if (this.init || !e) {
        return
      }
      // 初始化
      this.loading = true
      this.$api.sftpOpen({ machineId: this.machineId })
        .then(({ data }) => {
          this.home = data.home
          this.path = data.path
          this.sessionToken = data.sessionToken
          this.files = data.files
          this.init = true
          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.$message.error('加载sftp失败')
        })
    },
    changeShowHideFile(e) {
      this.showHideFile = e
      this.listFiles()
    },
    listFiles(path = this.path, toPage = 1) {
      this.loading = true
      this.$api.sftpList({
        sessionToken: this.sessionToken,
        all: this.showHideFile,
        path
      }).then(({ data }) => {
        this.path = data.path
        const pagination = { ...this.pagination }
        pagination.total = data.files.length
        pagination.current = toPage
        this.pagination = pagination
        this.files = data.files
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    changePage(page) {
      this.selectedRowKeys = []
      this.selectedRows = []
      const pagination = { ...this.pagination }
      pagination.current = page.current
      this.pagination = pagination
    },
    openRedirect() {
      this.redirectVisible = true
      setTimeout(() => {
        this.$refs.redirectPathInput.focus()
      }, 50)
    },
    redirectDirectory() {
      if (!this.redirectVisible) {
        this.$message.error('路径不能为空')
        return
      }
      this.redirectVisible = false
      this.listFiles(this.redirectPath)
    },
    resetFileName(selectedKeys, clearFilters) {
      clearFilters()
      selectedKeys = []
    },
    download(record) {
      const downloadLoading = this.$message.loading('正在请求下载文件...', 3)
      this.$api.sftpDownloadExec({
        paths: [record.path],
        sessionToken: this.sessionToken
      }).then(e => {
        downloadLoading()
        this.$message.success('已添加至传输列表')
      }).catch(e => {
        downloadLoading()
      })
    },
    batchDownload() {
      const downloadLoading = this.$message.loading('正在请求下载文件...', 3)
      this.$api.sftpDownloadExec({
        paths: this.selectedRowKeys,
        sessionToken: this.sessionToken
      }).then(e => {
        downloadLoading()
        this.$message.success('已添加至传输列表')
        this.selectedRowKeys = []
        this.selectedRows = []
      }).catch(e => {
        downloadLoading()
      })
    },
    remove(record) {
      this.$confirm({
        title: '确认删除',
        content: `是否删除 ${record.isDir ? '文件夹' : '文件'} ${record.path}?`,
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.sftpRemove({
            sessionToken: this.sessionToken,
            paths: [record.path]
          }).then(e => {
            this.$message.success(`${record.isDir ? '文件夹' : '文件'} ${record.path} 删除成功`)
            for (let i = 0; i < this.files.length; i++) {
              if (this.files[i].path === record.path) {
                this.files.splice(i, 1)
              }
            }
          })
        }
      })
    },
    batchRemove() {
      for (const selectedRow of this.selectedRows) {
        if (!selectedRow.isSafe) {
          this.$message.warn('删除的文件包含不安全项')
          this.selectedRows = this.selectedRows.filter(s => s.isSafe)
          this.selectedRowKeys = this.selectedRows.map(s => s.path)
          return
        }
      }
      this.$confirm({
        title: '确认删除',
        content: `是否删除选中的 ${this.selectedRowKeys.length}个 文件?`,
        okType: 'danger',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$api.sftpRemove({
            sessionToken: this.sessionToken,
            paths: this.selectedRowKeys
          }).then(e => {
            this.$message.success('批量删除成功')
            for (const path of this.selectedRowKeys) {
              for (let i = 0; i < this.files.length; i++) {
                if (this.files[i].path === path) {
                  this.files.splice(i, 1)
                }
              }
            }
            this.selectedRowKeys = []
            this.selectedRows = []
          })
        }
      })
    },
    changeUploadVisible(visible) {
      this.uploadVisible = visible
    },
    openTouch(touch) {
      this.$refs.touchModal.openTouch({
        homePath: this.home,
        currentPath: this.path
      })
    },
    openMove(record) {
      this.$refs.moveModal.openMove({
        parentPath: this.path,
        filePath: record.path
      })
    },
    openChmod(record) {
      this.$refs.chmodModal.openChmod({
        filePath: record.path,
        permission: record.permission
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
  }
}
</script>

<style lang="less" scoped>

.sftp-drawer-title {
  margin: 0;
  color: rgba(0, 0, 0, .85);
  font-weight: 500;
  font-size: 16px;
  line-height: 22px;
  display: flex;
  align-items: center;

  i {
    margin-left: 24px;
    font-size: 23px;
    cursor: pointer;
  }

  i:hover {
    color: #1890ff;
  }
}

.machine-sftp-main-container {
  display: flex;

  .sftp-folder-left-fixed {
    width: 14.9%;
    margin-top: 45px;
  }

  .sftp-body-container {
    width: 85.1%;
  }

}

.sftp-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sftp-bar-left {
  display: flex;
  align-items: flex-start;

  .sftp-home {
    width: 50px;
    display: flex;
    align-items: center;
    margin-right: 8px;
    font-size: 22px;
    cursor: pointer;

    .path-input-trigger {
      color: #1C7ED6;
      margin-left: 12px;
      font-size: 20px;
    }
  }

  .sftp-navigator-paths {
    display: flex;
    flex-wrap: wrap;
  }

  .sftp-navigator-paths .sftp-navigator-paths-item {
    margin: 0 5px;
    color: #1C7ED6;
  }
}

.sftp-tools-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-name-cols {
  display: flex;
  align-items: center;
}

.file-name-cols-icon {
  font-size: 16px;
  padding-right: 8px;
}

.sftp-tools-bar .text {
  color: #868e96;
  font-size: 13px;
}

.sftp-tools-hide-switch {
  margin-right: 12px;
  width: 140px;
}

.sftp-home :hover {
  color: #1C7ED6;
}

.path-item {
  display: flex;
}

</style>
