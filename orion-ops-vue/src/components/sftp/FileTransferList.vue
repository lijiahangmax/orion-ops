<template>
  <div class="transfer-list-container">
    <!-- 工具栏 -->
    <div class="transfer-list-bar">
      <a-icon type="caret-right" title="开始所有" @click="resumeAll"/>
      <a-icon type="pause" title="暂停所有" @click="pauseAll"/>
      <a-icon type="reload" title="刷新" @click="getTransferList"/>
      <a-icon type="file-zip" title="打包下载" @click="getTransferList"/>
      <a-icon type="delete" title="清空" @click="clearAll"/>
    </div>
    <div class="transfer-list-items" v-if="transferList.length === 0">
      <!-- 空数据 -->
      <a-empty style="margin-top: 35px"/>
    </div>
    <div class="transfer-list-items" v-else>
      <!-- 文件行 -->
      <div v-for="item in transferList" :key="item.fileToken" class="transfer-list-item">
        <!-- 文件名称 -->
        <div class="transfer-list-item-name">
          <span :title="item.remoteFile">{{ item.remoteFile }}</span>
        </div>
        <div class="transfer-list-item-body">
          <!-- 类型 -->
          <div class="transfer-list-item-type">
            <a-tag
              :color="$enum.valueOf($enum.SFTP_TRANSFER_STATUS,item.status).color"
              :title="$enum.valueOf($enum.SFTP_TRANSFER_STATUS,item.status).label">
              {{ $enum.valueOf($enum.SFTP_TRANSFER_TYPE, item.type).label }}
            </a-tag>
          </div>
          <!-- 10未开始 -->
          <div class="transfer-list-item-status" v-if="item.status === 10">
            <a-tooltip :title="`等待 0KB / ${item.size}`">
              <a-progress :percent="item.progress"/>
            </a-tooltip>
          </div>
          <!-- 20进行中 -->
          <div class="transfer-list-item-status" v-if="item.status === 20">
            <a-tooltip :title="`${item.rate || '0KB'}/s | ${item.current} / ${item.size}`">
              <a-progress :percent="item.progress" status="active"/>
            </a-tooltip>
          </div>
          <!-- 30已暂停 -->
          <div class="transfer-list-item-status" v-if="item.status === 30">
            <a-tooltip :title="`暂停 ${item.current} / ${item.size}`">
              <a-progress :percent="item.progress" :showInfo="false"/>
            </a-tooltip>
          </div>
          <!-- 40已完成 -->
          <div class="transfer-list-item-status" v-if="item.status === 40">
            <a-tooltip :title="`已完成 ${item.size}`">
              <a-progress :percent="item.progress" :showInfo="false"/>
            </a-tooltip>
          </div>
          <!-- 50传输异常 -->
          <div class="transfer-list-item-status" v-if="item.status === 50">
            <a-tooltip :title="`失败 ${item.current}/ ${item.size}`">
              <a-progress :percent="item.progress" status="exception" :showInfo="false"/>
            </a-tooltip>
          </div>
          <!-- 按钮 -->
          <div class="transfer-list-item-action">
            <a-icon title="暂停" v-if="item.status === 20" type="pause-circle" @click="pause(item.fileToken)"/>
            <a-icon title="开始" v-if="item.status === 30" type="play-circle" @click="resume(item.fileToken)"/>
            <a-icon title="获取下载链接" v-if="item.status === 40 && !item.downloadUrl" type="link" @click="loadDownload(item)"/>
            <a v-if="item.status === 40 && item.downloadUrl" @click="clearDownloadUrl(item)" :href="item.downloadUrl">
              <a-icon title="下载" type="download"/>
            </a>
            <a-icon title="删除" type="close-circle" @click="remove(item.fileToken)"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

/**
 * 打开sftp通知
 */
function openSftpNotify() {
  this.notifyClient = new WebSocket(this.$api.sftpNotify(this.sessionToken))
  this.notifyClient.onopen = () => {
    // 打开连接发送当前登录token
    setTimeout(() => {
      this.notifyClient.send(this.$storage.get(this.$storage.keys.LOGIN_TOKEN))
    }, 500)
  }
  this.notifyClient.onclose = e => {
    console.log('sftpNotify已断开', e.code, e.reason)
  }
  this.notifyClient.onmessage = msg => {
    const body = JSON.parse(msg.data)
    if (body.type === 10) {
      // 添加
      const file = JSON.parse(body.body)
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

export default {
  name: 'FileTransferList',
  props: {
    sessionToken: String
  },
  data: function() {
    return {
      notifyClient: null,
      transferList: []
    }
  },
  methods: {
    getTransferList() {
      this.$api.sftpTransferList({ sessionToken: this.sessionToken })
        .then(({ data }) => {
          this.transferList = data
        })
    },
    clearAll() {
      this.$api.sftpTransferClear({ sessionToken: this.sessionToken })
        .then(() => {
          this.getTransferList()
        })
    },
    pause(fileToken) {
      this.$api.sftpTransferPause({ fileToken })
    },
    resume(fileToken) {
      this.$api.sftpTransferResume({ fileToken })
    },
    pauseAll() {
      this.$api.sftpTransferPauseAll({ sessionToken: this.sessionToken })
    },
    resumeAll() {
      this.$api.sftpTransferResumeAll({ sessionToken: this.sessionToken })
    },
    remove(fileToken) {
      this.$api.sftpTransferRemove({ fileToken })
        .then(() => {
          for (let i = 0; i < this.transferList.length; i++) {
            if (this.transferList[i].fileToken === fileToken) {
              this.transferList.splice(i, 1)
            }
          }
        })
    },
    async loadDownload(item) {
      const downloadUrl = await this.$api.getFileDownloadToken({
        type: 40,
        id: item.id
      })
      item.downloadUrl = this.$api.fileDownloadExec({ token: downloadUrl.data })
      this.$forceUpdate()
    },
    clearDownloadUrl(item) {
      item.downloadUrl = null
      this.$forceUpdate()
    }
  },
  mounted() {
    // 加载传输列表
    this.getTransferList()
    // 打开sftp通知
    openSftpNotify.call(this)
  }
}
</script>

<style scoped>

.transfer-list-bar {
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
  background-color: #f8f9fa;
  padding: 5px;
  border-radius: 5px;
}

.transfer-list-bar i {
  font-size: 20px;
  margin-left: 5px;
  cursor: pointer;
  color: #1C7ED6;
}

.transfer-list-items {
  width: 350px;
  height: 250px;
  overflow-y: auto;
}

.transfer-list-item {
  display: flex;
  word-break: break-all;
  flex-wrap: wrap;
  flex-direction: column;
  padding: 0 0 10px 5px;
  box-shadow: 0 8px 8px #F8F9FA;
}

.transfer-list-item:hover {
  background-color: #D0EBFF;
  border-radius: 5px;
}

.transfer-list-item-body {
  display: flex;
  align-items: center;
  font-size: 18px;
  height: 30px;
}

.transfer-list-item-body i {
  cursor: pointer;
  margin-left: 3px;
  color: #5C7CFA;
}

.transfer-list-item-body i:hover {
  color: #1C7ED6;
}

.transfer-list-item-type {
  margin-top: 5px;
}

.transfer-list-item-status {
  width: 67%;
}

.transfer-list-item-action {
  margin: 5px 0 0 15px;
}

</style>
