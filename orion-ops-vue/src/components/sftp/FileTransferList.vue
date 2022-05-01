<template>
  <div ref="transferMain" @contextmenu.prevent>
    <div class="transfer-list-container">
      <!-- 工具栏 -->
      <div class="transfer-list-bar" ref="transferListBar">
        <!-- 刷新 -->
        <div class="transfer-list-action" @click="getTransferList" title="刷新">
          <a-icon type="sync"/>
        </div>
        <!-- 开始所有 -->
        <div class="transfer-list-action" @click="resumeAll" title="开始所有">
          <a-icon type="caret-right"/>
        </div>
        <!-- 暂停所有 -->
        <div class="transfer-list-action" @click="pauseAll" title="暂停所有">
          <a-icon type="pause"/>
        </div>
        <!-- 重试所有 -->
        <div class="transfer-list-action" @click="retryAll" title="重试所有">
          <a-icon type="issues-close"/>
        </div>
        <!-- 打包 -->
        <a-popconfirm placement="bottomRight"
                      v-model="packageVisible"
                      :getPopupContainer="() => $refs.transferListBar">
          <template #title>
            <!-- 标题 -->
            <div class="transfer-popover-title-wrapper">
              <span>是否要打包传输文件?</span>
            </div>
            <!-- 按钮 -->
            <div class="transfer-popover-buttons-wrapper">
              <a-button class="transfer-popover-button span-blue" size="small" @click="() => packageVisible = false">取消</a-button>
              <a-button class="transfer-popover-button" size="small" type="primary" @click="transferPackage(1)">仅上传</a-button>
              <a-button class="transfer-popover-button" size="small" type="primary" @click="transferPackage(2)">仅下载</a-button>
              <a-button class="transfer-popover-button" size="small" type="primary" @click="transferPackage(3)">所有</a-button>
            </div>
          </template>
          <!-- 触发器 -->
          <div class="transfer-list-action" @click="() => packageVisible = true" title="打包传输">
            <a-icon type="file-zip"/>
          </div>
        </a-popconfirm>
        <!-- 清空 -->
        <a-popconfirm title="确定要清空传输记录吗?"
                      placement="bottomRight"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="clearAll">
          <div class="transfer-list-action" title="清空">
            <a-icon type="delete"/>
          </div>
        </a-popconfirm>
      </div>
      <!-- 传输列表 -->
      <div class="transfer-list-items" v-if="transferList.length === 0">
        <!-- 空数据 -->
        <a-empty style="margin-top: 35px"/>
      </div>
      <div class="transfer-list-items" v-else>
        <!-- 文件行 -->
        <div class="transfer-list-item"
             v-for="item in transferList"
             :key="item.fileToken"
             @contextmenu.prevent="openTransferRightMenu($event, item)">
          <!-- 文件名称 -->
          <div class="transfer-list-item-name auto-ellipsis-item">
            <span :title="item.remoteFile">{{ item.remoteFile }}</span>
          </div>
          <!-- 传输主体 -->
          <div class="transfer-list-item-body">
            <!-- 类型 -->
            <div class="transfer-list-item-type">
              <a-tag :color="$enum.valueOf($enum.SFTP_TRANSFER_STATUS, item.status).color"
                     :title="$enum.valueOf($enum.SFTP_TRANSFER_STATUS, item.status).label">
                {{ $enum.valueOf($enum.SFTP_TRANSFER_TYPE, item.type).label }}
              </a-tag>
            </div>
            <!-- 状态进度条 -->
            <div class="transfer-list-item-status">
              <!-- 10未开始 -->
              <template v-if="item.status === 10">
                <a-tooltip :title="`等待 0KB / ${item.size}`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" :showInfo="false"/>
                </a-tooltip>
              </template>
              <!-- 20进行中 -->
              <template v-if="item.status === 20 && item.type === 40">
                <a-tooltip :title="`${item.current} / ${item.size}`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" status="active"/>
                </a-tooltip>
              </template>
              <template v-if="item.status === 20 && item.type !== 40">
                <a-tooltip :title="`${item.rate || '0KB'}/s | ${item.current} / ${item.size}`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" status="active"/>
                </a-tooltip>
              </template>
              <!-- 30已暂停 -->
              <template v-if="item.status === 30">
                <a-tooltip :title="`暂停 ${item.current} / ${item.size}`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" :showInfo="false"/>
                </a-tooltip>
              </template>
              <!-- 40已完成 -->
              <template class="transfer-list-item-status" v-if="item.status === 40">
                <a-tooltip :title="`已完成 ${item.size}`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" :showInfo="false"/>
                </a-tooltip>
              </template>
              <!-- 50已取消 -->
              <template class="transfer-list-item-status" v-if="item.status === 50">
                <a-tooltip :title="`已取消`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" :showInfo="false"/>
                </a-tooltip>
              </template>
              <!-- 60传输异常 -->
              <template class="transfer-list-item-status" v-if="item.status === 60">
                <a-tooltip :title="`失败 ${item.current}/ ${item.size}`" :getPopupContainer="() => $refs.transferMain">
                  <a-progress :percent="item.progress" status="exception" :showInfo="false"/>
                </a-tooltip>
              </template>
            </div>
            <!-- 按钮 -->
            <div class="transfer-list-item-action">
              <a-icon title="获取下载链接" v-if="item.status === 40 && !item.downloadUrl" type="link" @click="loadDownload(item)"/>
              <a v-if="item.status === 40 && item.downloadUrl" @click="clearDownloadUrl(item)" target="_blank" :href="item.downloadUrl">
                <a-icon title="下载" type="download"/>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 事件 -->
    <div class="transfer-event-container">
      <!-- 右键菜单 -->
      <RightClickMenu ref="rightMenu"
                      :x="e => e.offsetX + 20"
                      :y="e => e.y - 110"
                      @clickRight="clickTransferRightMenuItem">
        <template #items v-if="curr">
          <a-menu-item key="resumeFile" v-if="curr.status === 30">
            <span class="right-menu-item"><a-icon type="play-circle"/>开始</span>
          </a-menu-item>
          <a-menu-item key="pauseFile" v-if="(curr.status === 10 || curr.status === 20) && curr.type !== 40">
            <span class="right-menu-item"><a-icon type="pause-circle"/>暂停</span>
          </a-menu-item>
          <a-menu-item key="pauseFile" v-if="(curr.status === 10 || curr.status === 20) && curr.type === 40">
            <span class="right-menu-item"><a-icon type="stop"/>取消</span>
          </a-menu-item>
          <a-menu-item key="retryFile" v-if="curr.status === 60 && curr.type !== 40">
            <span class="right-menu-item"><a-icon type="sync"/>重试</span>
          </a-menu-item>
          <a-menu-item key="removeFile">
            <span class="right-menu-item"><a-icon type="close-circle"/>删除</span>
          </a-menu-item>
          <a-menu-item key="reUploadFile" v-if="curr.type === 10">
            <span class="right-menu-item"><a-icon type="redo"/>重新上传</span>
          </a-menu-item>
          <a-menu-item key="reDownloadFile" v-if="curr.type === 20">
            <span class="right-menu-item"><a-icon type="redo"/>重新下载</span>
          </a-menu-item>
          <a-menu-item key="clearAllFile">
            <span class="right-menu-item"><a-icon type="stop"/>清空所有</span>
          </a-menu-item>
        </template>
      </RightClickMenu>
    </div>
  </div>
</template>

<script>

import RightClickMenu from '@/components/common/RightClickMenu'

/**
 * 打开sftp通知
 */
function openSftpNotify() {
  this.notifyClient = new WebSocket(this.$api.sftpNotify({ token: this.sessionToken }))
  this.notifyClient.onopen = () => {
    // 打开连接发送当前登录token
    setTimeout(() => {
      this.notifyClient.send(this.$storage.get(this.$storage.keys.LOGIN_TOKEN))
    }, 500)
  }
  this.notifyClient.onclose = e => {
    console.log('sftp notify close', e.code, e.reason)
  }
  this.notifyClient.onmessage = msg => {
    const body = JSON.parse(msg.data)
    if (body.type === 10) {
      // 添加
      const file = JSON.parse(body.body)
      file.downloadUrl = null
      this.transferList.unshift(file)
    } else if (body.type === 20) {
      // 速率
      const rate = JSON.parse(body.body)
      for (let i = 0; i < this.transferList.length; i++) {
        if (this.transferList[i].fileToken === body.fileToken) {
          this.transferList[i].progress = parseFloat(rate.progress)
          this.transferList[i].rate = rate.rate
          this.transferList[i].current = rate.current
        }
      }
    } else if (body.type === 30) {
      // 状态
      const status = body.body
      for (let i = 0; i < this.transferList.length; i++) {
        if (this.transferList[i].fileToken === body.fileToken) {
          this.transferList[i].status = status
        }
      }
    }
  }
}

/**
 * 右键菜单操作
 */
const transferRightMenuHandler = {
  resumeFile() {
    this.$api.sftpTransferResume({ fileToken: this.curr.fileToken })
  },
  pauseFile() {
    this.$api.sftpTransferPause({ fileToken: this.curr.fileToken })
  },
  retryFile() {
    this.$api.sftpTransferRetry({ fileToken: this.curr.fileToken })
  },
  reUploadFile() {
    this.$api.sftpTransferReUpload({ fileToken: this.curr.fileToken })
  },
  reDownloadFile() {
    this.$api.sftpTransferReDownload({ fileToken: this.curr.fileToken })
  },
  removeFile() {
    for (let i = 0; i < this.transferList.length; i++) {
      if (this.transferList[i].fileToken === this.curr.fileToken) {
        this.transferList.splice(i, 1)
      }
    }
    this.$api.sftpTransferRemove({ fileToken: this.curr.fileToken })
  },
  clearAllFile() {
    this.clearAll()
  }
}

export default {
  name: 'FileTransferList',
  components: { RightClickMenu },
  props: {
    sessionToken: String
  },
  data: function() {
    return {
      init: false,
      notifyClient: null,
      transferList: [],
      curr: null,
      packageVisible: false
    }
  },
  watch: {
    sessionToken(e) {
      this.close()
    }
  },
  methods: {
    open() {
      !this.init && this.initData()
    },
    initData() {
      this.init = true
      // 加载传输列表
      this.getTransferList()
      // 打开sftp通知
      openSftpNotify.call(this)
    },
    close() {
      this.init = false
      this.curr = null
      this.transferList = []
      this.notifyClient && this.notifyClient.close()
      this.notifyClient = null
    },
    getTransferList() {
      this.$api.sftpTransferList({
        sessionToken: this.sessionToken
      }).then(({ data }) => {
        this.$utils.defineArrayKey(data, 'downloadUrl')
        this.transferList = data
      })
    },
    clearAll() {
      this.transferList = this.transferList.filter(s => s.status === 20)
      this.$api.sftpTransferClear({
        sessionToken: this.sessionToken
      }).then(() => {
        this.getTransferList()
      })
    },
    pauseAll() {
      this.$api.sftpTransferPauseAll({ sessionToken: this.sessionToken })
    },
    resumeAll() {
      this.$api.sftpTransferResumeAll({ sessionToken: this.sessionToken })
    },
    retryAll() {
      this.$api.sftpTransferRetryAll({ sessionToken: this.sessionToken })
    },
    transferPackage(packageType) {
      this.packageVisible = false
      this.$api.sftpTransferPackage({
        sessionToken: this.sessionToken,
        packageType
      }).then(() => {
        this.$message.success('正在打包...')
      })
    },
    async loadDownload(item) {
      const downloadUrl = await this.$api.getFileDownloadToken({
        type: this.$enum.FILE_DOWNLOAD_TYPE.SFTP_DOWNLOAD.value,
        id: item.id
      })
      item.downloadUrl = this.$api.fileDownloadExec({ token: downloadUrl.data })
    },
    clearDownloadUrl(item) {
      setTimeout(() => {
        item.downloadUrl = null
      })
    },
    openTransferRightMenu(e, item) {
      if (e.button === 2) {
        this.curr = item
        this.$refs.rightMenu.openRightMenu(e)
      }
    },
    clickTransferRightMenuItem(key) {
      transferRightMenuHandler[key].call(this)
    }
  }
}
</script>

<style lang="less" scoped>

.transfer-list-bar {
  display: flex;
  justify-content: flex-end;
  margin: 0 6px 6px 0;
  background-color: #F8F9FA;
  padding: 4px;
  border-radius: 4px;

  .transfer-list-action {
    display: flex;
    transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
    border-radius: 6px;
    cursor: pointer;
    margin: 0 2px;
    padding: 4px;
    font-size: 20px;
    color: #1C7ED6;
  }

  .transfer-list-action:hover {
    color: hsla(0, 0%, 100%, .2);
    background-color: #1C7ED6;
    color: #FFF;
  }
}

.transfer-list-items {
  width: 350px;
  min-height: 245px;
  max-height: 284px;
  overflow-y: auto;

  .transfer-list-item {
    padding: 0 0 10px 5px;
    box-shadow: 0 8px 8px #F8F9FA;
    margin: 2px 0;
  }

  .transfer-list-item:hover {
    background-color: #D0EBFF;
    border-radius: 5px;
  }

  .transfer-list-item-name {
    width: 100%;
    padding: 4px 0;
  }

  .transfer-list-item-body {
    display: flex;
    align-items: center;
    font-size: 18px;
    height: 30px;

    i {
      cursor: pointer;
      margin-left: 3px;
      color: #5C7CFA;
    }

    i:hover {
      color: #4263EB;
    }

    .transfer-list-item-type {
      margin-top: 5px;
    }

    .transfer-list-item-status {
      width: 75%;
    }

    .transfer-list-item-action {
      margin: 5px 0 0 8px;
    }
  }
}

.transfer-list-bar {
  /deep/ .ant-popover-message {
    padding: 0 !important;
  }

  /deep/ .ant-popover-buttons {
    display: none !important;
  }

  /deep/ .ant-popover-message-title {
    padding-right: 8px;
    margin-bottom: 4px;
  }

  .transfer-popover-title-wrapper {
    display: block;
    padding-top: 4px;
    margin-bottom: 16px;
  }

  .transfer-popover-button {
    height: 24px;
    padding: 0 7px;
    font-size: 14px;
    border-radius: 4px;
    margin: 0 2.5px;
  }
}

</style>
