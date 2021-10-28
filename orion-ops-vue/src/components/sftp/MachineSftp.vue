<template>
  <div class="machine-sftp-container">
    <!-- sftp主体 -->
    <div class="machine-sftp-main-container">
      <a-drawer
        title="文件管理器"
        placement="right"
        :closable="false"
        :visible="visible"
        :after-visible-change="changeVisibleAfter"
        :width="'65%'"
        :zIndex="800"
        @close="visible = !visible">
        <div class="sftp-container">
          <!-- 头部 -->
          <div class="sftp-header">
            <div class="sftp-bar-left">
              <!-- home -->
              <div class="sftp-home">
                <a-icon type="home" @click="listFiles(home)"/>
              </div>
              <!-- 路径 -->
              <div class="sftp-navigator-paths">
                <a-breadcrumb v-for="(pathItem,index) in pathAnalysis" :key="pathItem.path">
                  <a-breadcrumb-item>
                    <div class="path-item">
                      <span v-if="index !== 0">/</span>
                      <a class="sftp-navigator-paths-item" @click="listFiles(pathItem.path)">{{pathItem.name}}</a>
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
                  <a-button :disabled="!sessionToken" @click="$copy(path)" title="复制路径" icon="link"/>
                  <a-button :disabled="!sessionToken" @click="listFiles()" title="刷新" icon="reload"/>
                  <a-button :disabled="!sessionToken" @click="openTouch(true)" title="创建文件" icon="file-add"/>
                  <a-button :disabled="!sessionToken" @click="openTouch(false)" title="创建目录" icon="folder-add"/>
                  <a-button :disabled="!sessionToken" @click="selectUploadFile" title="上传文件" :icon="canUpload ? 'cloud-upload' : 'loading'"/>
                  <a-popover :disabled="!sessionToken" title="传输列表" trigger="click" placement="bottomRight" :overlayClassName="'transfer-list-popover'">
                    <FileTransferList slot="content" :sessionToken="sessionToken"/>
                    <a-button title="传输列表" icon="unordered-list"/>
                  </a-popover>
                </a-button-group>
                <a-input id="upload-file-input" type="file" style="display: none" @change="checkUploadFile($event)"/>
              </div>
            </div>
          </div>
        </div>
        <!-- 文件列表 -->
        <div class="sft-file-list-container">
          <a-table :columns="fileListColumns"
                   :data-source="files"
                   :rowKey="'name'"
                   :pagination="{pageSize: 12}"
                   size="small">
            <!-- 名称 -->
            <div slot="name" slot-scope="record" class="file-name-cols">
              <!-- 图标 -->
              <a-icon :type="getFileIcon(record.attr)" class="file-name-cols-icon"/>
              <!-- 名称 -->
              <a v-if="record.isDir" @click="listFiles(record.path)">{{record.name}}</a>
              <span v-else>{{record.name}}</span>
            </div>
            <!-- 修改时间 -->
            <span slot="modifyTime" slot-scope="record">
              {{record.modifyTime | formatDate({date: record.modifyTime, pattern: 'yyyy-MM-dd HH:mm:ss'})}}
            </span>
            <!-- 操作 -->
            <div slot="action" slot-scope="record">
              <!-- 复制路径 -->
              <a @click="$copy(record.path)" title="复制路径">
                <a-icon type="copy"/>
              </a>
              <a-divider type="vertical"/>
              <!-- 下载 -->
              <a v-if="!record.isDir" @click="download(record)" title="下载">
                <a-icon type="cloud-download"/>
              </a>
              <a-divider v-if="!record.isDir" type="vertical"/>
              <!-- 删除 -->
              <a @click="remove(record)" title="删除">
                <a-icon type="delete"/>
              </a>
              <a-divider type="vertical"/>
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
      </a-drawer>
    </div>
    <!-- sftp事件 -->
    <div class="machine-sftp-event-container">
      <!-- 创建文件模态框 -->
      <SftpTouchModal :detail="touch" @listFiles="listFiles"/>
      <!-- 移动文件模态框 -->
      <SftpMoveModal :detail="move" :files="files"/>
      <!-- 文件提权模态框 -->
      <SftpChmodModal :detail="chmod" :files="files"/>
    </div>
  </div>
</template>

<script>
  import _utils from '../../lib/utils'
  import SftpTouchModal from './SftpTouchModal'
  import SftpMoveModal from './SftpMoveModal'
  import SftpChmodModal from './SftpChmodModal'
  import FileTransferList from './FileTransferList'

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
  const fileListColumns = [
    {
      title: '名称',
      key: 'name',
      width: 330,
      scopedSlots: { customRender: 'name' },
      sorter: (a, b) => a.name.localeCompare(b.name)
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

  export default {
    name: 'SftpMainBody',
    props: {
      machineId: Number
    },
    components: {
      SftpTouchModal,
      SftpMoveModal,
      SftpChmodModal,
      FileTransferList
    },
    data: function() {
      return {
        visible: false,
        init: false,
        showHideFile: false,
        canUpload: true,
        sessionToken: '',
        home: '',
        path: '',
        files: [],
        fileListColumns,
        touch: {
          visible: false,
          sessionToken: null,
          homePath: '',
          parentPath: '',
          isTouchFile: true
        },
        move: {
          visible: false,
          sessionToken: null,
          parentPath: '',
          filePath: '',
          movePath: ''
        },
        chmod: {
          visible: false,
          sessionToken: '',
          filePath: '',
          permission: ''
        }
      }
    },
    computed: {
      pathAnalysis: function() {
        return getPathAnalysis(this.path)
      }
    },
    methods: {
      changeVisibleAfter(e) {
        if (this.init || !e) {
          return
        }
        // 初始化
        this.$api.sftpOpen({ machineId: this.machineId })
          .then(({ data }) => {
            this.home = data.home
            this.path = data.path
            this.sessionToken = data.sessionToken
            this.files = data.files
            this.init = true
          })
      },
      changeShowHideFile(e) {
        this.showHideFile = e
        this.listFiles()
      },
      listFiles(path = this.path) {
        this.$api.sftpList({
          sessionToken: this.sessionToken,
          all: this.showHideFile,
          path
        }).then(({ data }) => {
          this.path = data.path
          this.files = data.files
        })
      },
      getFileIcon(attr) {
        return this.$enum.FILE_ICON_TYPE[attr.charAt(0)]
      },
      openTouch(touch) {
        this.touch.visible = true
        this.touch.sessionToken = this.sessionToken
        this.touch.homePath = this.home
        this.touch.parentPath = this.path
        this.touch.isTouchFile = touch
      },
      download(record) {
        const loading = this.$message.loading('正在请求下载文件', 5)
        this.$api.sftpDownloadExec({
          path: record.path,
          sessionToken: this.sessionToken
        }).then(e => {
          loading()
          this.$message.success('已添加至传输列表')
        }).catch(e => {
          loading()
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
              path: record.path
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
      openMove(record) {
        this.move.visible = true
        this.move.sessionToken = this.sessionToken
        this.move.parentPath = this.path
        this.move.filePath = record.path
        this.move.movePath = record.path
      },
      openChmod(record) {
        this.chmod.visible = true
        this.chmod.sessionToken = this.sessionToken
        this.chmod.filePath = record.path
        this.chmod.permission = record.permission
      },
      selectUploadFile() {
        if (!this.canUpload) {
          this.$message.warning('请等待当前文件完毕后再次上传!')
        } else {
          document.getElementById('upload-file-input').click()
        }
      },
      async checkUploadFile(e) {
        if (e.target.files.length === 0) {
          return
        }
        const file = e.target.files[0]
        // 检查文件是否存在
        const checkPresentRes = await this.$api.sftpCheckFilePresent({
          sessionToken: this.sessionToken,
          path: this.path,
          name: file.name
        })
        if (checkPresentRes.data) {
          this.$confirm({
            title: '文件已存在',
            content: '文件已存在, 是否继续上传?',
            okText: '确认',
            cancelText: '取消',
            onOk: () => {
              this.uploadFile(file)
            }
          })
        } else {
          this.uploadFile(file)
        }
      },
      async uploadFile(file) {
        this.canUpload = false
        // 获取上传token
        const uploadToken = await this.$api.getSftpUploadToken({
          sessionToken: this.sessionToken
        })
        // 提示
        this.$notification.open({
          message: '文件开始上传',
          description: `开始上传 ${file.name} 至 ${this.path}`,
          duration: 3
        })
        const formData = new FormData()
        formData.append('fileToken', uploadToken.data)
        formData.append('remotePath', this.path)
        formData.append('file', file)
        this.$api.sftpUploadExec(formData).then(() => {
          this.$message.success('文件上传请求提交成功')
          this.canUpload = true
        }).catch(e => {
          this.$message.error('文件上传请求提交失败')
          this.canUpload = true
        })
      }
    },
    filters: {
      formatDate(origin, { date, pattern }) {
        return _utils.dateFormat(new Date(date), pattern)
      }
    }
  }
</script>

<style scoped>

  .sftp-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .sftp-bar-left {
    display: flex;
    align-items: center;
  }

  .sftp-navigator-paths {
    display: flex;
    flex-wrap: wrap;
  }

  .sftp-navigator-paths .sftp-navigator-paths-item {
    margin: 0 5px;
    color: #1C7ED6;
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

  .sftp-home {
    margin-right: 15px;
    font-size: 22px;
    cursor: pointer;
  }

  .sftp-home :hover {
    color: #1C7ED6;
  }

  .path-item {
    display: flex;
  }

  .transfer-list-popover /deep/ .ant-popover-inner-content {
    padding: 8px 2px 8px 8px;
  }

</style>
