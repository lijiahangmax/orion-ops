<template>
  <div class="batch-upload-container">
    <a-spin :spinning="status.loading">
      <template #indicator>
        <a-icon v-if="status.mask" type="loading" style="font-size: 24px"/>
        <span v-else/>
      </template>
      <!-- 上传容器  -->
      <div class="upload-list-container gray-box-shadow">
        <!-- 上传机器 -->
        <div class="upload-machine-checker-container">
          <span class="normal-label upload-label">上传机器</span>
          <div class="machine-checker-wrapper">
            <MachineChecker ref="machineChecker" :query="machineQuery">
              <template #trigger>
                <span class="span-blue pointer">已选择 {{ machineIdList.length }} 台机器</span>
              </template>
              <template #footer>
                <a-button type="primary" size="small" @click="chooseMachines">确定</a-button>
              </template>
            </MachineChecker>
          </div>
        </div>
        <!-- 上传路径 -->
        <div class="upload-remote-path-container">
          <span class="normal-label upload-label">上传路径</span>
          <div class="upload-remote-path-input-wrapper">
            <a-input v-model="remotePath" placeholder="远程机器文件夹" allowClear/>
          </div>
        </div>
        <!-- 上传文件触发器 -->
        <div class="upload-trigger-container">
          <!-- 文件上传拖拽框 -->
          <div class="upload-event-trigger">
            <a-upload-dragger class="upload-drag"
                              :beforeUpload="selectFile"
                              :multiple="true"
                              :fileList="fileList"
                              :showUploadList="false">
              <p id="upload-trigger-icon" class="ant-upload-drag-icon">
                <a-icon type="inbox"/>
              </p>
              <p class="ant-upload-text">单击或拖动文件到此区域进行上传</p>
            </a-upload-dragger>
          </div>
          <!-- 按钮 -->
          <div class="upload-buttons">
            <a-button type="primary" class="button-upload" :disabled="fileList.length === 0" @click="checkFile">上传</a-button>
            <a-button class="button-clear" :disabled="fileList.length === 0" @click="clear">清空</a-button>
          </div>
        </div>
        <!-- 文件列表 -->
        <div class="upload-file-list">
          <a-upload :fileList="fileList" :remove="removeFile"/>
        </div>
      </div>
    </a-spin>
    <!-- 传输容器 -->
    <div class="transfer-list-container gray-box-shadow">
      <!-- 提示 -->
      <div class="transfer-clear-wrapper">
        <a-alert :type="status.type" style="width: 100%">
          <template #message>
            <span>{{ status.message }}</span>
          </template>
        </a-alert>
        <a-button class="ml8" v-if="status.visibleTransfer" @click="abortUpload">清空</a-button>
      </div>
      <!-- 检查信息  -->
      <div class="upload-check-container" v-if="status.checked && checkData">
        <!-- 无法连接机器 -->
        <div class="upload-check-wrapper" v-if="checkData.notConnectedMachines.length">
          <p class="check-label normal-label span-red">无法连接的机器</p>
          <p class="check-machine-wrapper span-blue"
             v-for="notConnectedMachine of checkData.notConnectedMachines"
             :key=notConnectedMachine.id>
            <!-- 机器信息 -->
            <span class="check-machine-name">{{ notConnectedMachine.name }}</span>
            <span class="check-machine-host">{{ `(${notConnectedMachine.host})` }}</span>
          </p>
        </div>
        <!-- 重复文件 -->
        <div class="upload-check-wrapper" v-if="checkData.machinePresentFiles.length">
          <p class="check-label normal-label span-red">重复文件的机器</p>
          <div class="check-machine-wrapper"
               v-for="machinePresentFile of checkData.machinePresentFiles"
               :key=machinePresentFile.id>
            <!-- 重复机器信息 -->
            <div class="check-machine-line span-blue">
              <span class="check-machine-name">{{ machinePresentFile.name }}</span>
              <span class="check-machine-host">{{ `(${machinePresentFile.host})` }}</span>
            </div>
            <!-- 重复的文件 -->
            <div class="check-machine-present-files">
              <p class="check-machine-present-file" v-for="file of machinePresentFile.presentFiles" :key="file">
                {{ file }}
              </p>
            </div>
          </div>
        </div>
        <!-- 操作 -->
        <div class="upload-check-buttons">
          <!-- 放弃上传 -->
          <a-button class="mr8" @click="abortUpload">放弃上传</a-button>
          <!-- 继续上传 -->
          <a-button v-if="checkData.connectedMachineIdList.length"
                    type="primary"
                    @click="startUpload">
            继续上传
          </a-button>
          <a-tooltip v-else title="无可连接机器, 无法继续上传">
            <a-button type="primary" :disabled="true">继续上传</a-button>
          </a-tooltip>
        </div>
      </div>
      <!-- 传输列表 -->
      <div class="upload-transfer-list-container" v-if="status.visibleTransfer">
        <!-- 传输列表 -->
        <div class="transfer-list-body-wrapper">
          <div class="transfer-file-item-wrapper" v-for="item of transferList" :key="item.id">
            <!-- 传输文件体 头部 -->
            <div class="transfer-file-item-header">
              <!-- 文件路径 -->
              <span class="transfer-file-item-path auto-ellipsis-item" :title="item.remoteFile">
                {{ item.remoteFile }}
              </span>
              <!-- 机器名称 -->
              <span class="transfer-file-item-machine auto-ellipsis-item">
                {{ formatMachine(item.machineId) }}
              </span>
            </div>
            <!-- 传输文件体 底部 -->
            <div class="transfer-file-item-bottom">
              <!-- 状态进度条 -->
              <div class="transfer-file-item-status">
                <!-- 10未开始 -->
                <template v-if="item.status === 10">
                  <a-tooltip :title="`等待 0KB / ${item.size}`">
                    <a-progress :percent="item.progress" :showInfo="false"/>
                  </a-tooltip>
                </template>
                <!-- 20进行中 -->
                <template v-if="item.status === 20 && item.type === 40">
                  <a-tooltip :title="`${item.current} / ${item.size}`">
                    <a-progress :percent="item.progress" status="active"/>
                  </a-tooltip>
                </template>
                <template v-if="item.status === 20 && item.type !== 40">
                  <a-tooltip :title="`${item.rate || '0KB'}/s | ${item.current} / ${item.size}`">
                    <a-progress :percent="item.progress" status="active"/>
                  </a-tooltip>
                </template>
                <!-- 30已暂停 -->
                <template v-if="item.status === 30">
                  <a-tooltip :title="`暂停 ${item.current} / ${item.size}`">
                    <a-progress :percent="item.progress" :showInfo="false"/>
                  </a-tooltip>
                </template>
                <!-- 40已完成 -->
                <template class="transfer-file-item-status" v-if="item.status === 40">
                  <a-tooltip :title="`已完成 ${item.size}`">
                    <a-progress :percent="item.progress" :showInfo="false"/>
                  </a-tooltip>
                </template>
                <!-- 50已取消 -->
                <template class="transfer-file-item-status" v-if="item.status === 50">
                  <a-tooltip :title="`已取消`">
                    <a-progress :percent="item.progress" :showInfo="false"/>
                  </a-tooltip>
                </template>
                <!-- 60传输异常 -->
                <template class="transfer-file-item-status" v-if="item.status === 60">
                  <a-tooltip :title="`失败 ${item.current} / ${item.size}`">
                    <a-progress :percent="item.progress" status="exception" :showInfo="false"/>
                  </a-tooltip>
                </template>
              </div>
              <!-- 操作 -->
              <div class="transfer-file-item-actions">
                <span class="span-blue transfer-file-item-action"
                      v-if="item.status === 30"
                      title="开始"
                      @click="resumeFile(item.fileToken)">
                  开始
                </span>
                <span class="span-blue transfer-file-item-action"
                      v-if="item.status === 10 || item.status === 20"
                      title="暂停"
                      @click="pauseFile(item.fileToken)">
                  暂停
                </span>
                <span class="span-blue transfer-file-item-action"
                      v-if="item.status === 60"
                      title="重试"
                      @click="retryFile(item.fileToken)">
                  重试
                </span>
                <span class="span-blue transfer-file-item-action"
                      title="删除"
                      @click="deleteFile(item.fileToken)">
                  删除
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { BATCH_UPLOAD_STATUS, ENABLE_STATUS } from '@/lib/enum'
import MachineChecker from '@/components/machine/MachineChecker'

export default {
  name: 'BatchUploadFile',
  components: { MachineChecker },
  data() {
    return {
      machineIdList: [],
      remotePath: '',
      fileList: [],
      status: BATCH_UPLOAD_STATUS.WAIT,
      checkData: null,
      notifyClient: null,
      token: null,
      fileTokenList: [],
      transferList: [],
      machineQuery: {
        status: ENABLE_STATUS.ENABLE.value
      }
    }
  },
  methods: {
    chooseMachines() {
      const ref = this.$refs.machineChecker
      if (!ref.checkedList.length) {
        this.$message.warning('请选择上传的机器')
        return
      }
      this.machineIdList = ref.checkedList
      ref.hide()
    },
    selectFile(e) {
      this.fileList.push(e)
      return false
    },
    removeFile(e) {
      for (let i = 0; i < this.fileList.length; i++) {
        if (this.fileList[i] === e) {
          this.fileList.splice(i, 1)
        }
      }
    },
    clear() {
      this.fileList = []
    },
    async checkFile() {
      // 检查文件
      if (!this.machineIdList.length) {
        this.$message.error('请选择上传机器')
        return
      }
      if (!this.remotePath.trim()) {
        this.$message.error('上传路径不能为空')
        return
      }
      if (!this.fileList.length) {
        this.$message.error('请选择上传文件')
        return
      }
      const size = this.fileList.map(s => s.size).reduce((t, v) => t + v, 0)
      this.status = BATCH_UPLOAD_STATUS.CHECKING

      try {
        const { data } = await this.$api.checkBatchUploadFiles({
          remotePath: this.remotePath,
          machineIds: this.machineIdList,
          names: this.fileList.map(s => s.name),
          size
        })
        this.checkData = data
        if (!this.checkData.connectedMachineIdList.length) {
          this.status = BATCH_UPLOAD_STATUS.NO_AVAILABLE
          return
        }
        if (this.checkData.notConnectedMachines.length || this.checkData.machinePresentFiles.length) {
          this.status = BATCH_UPLOAD_STATUS.WAIT_UPLOAD
          return
        }
      } catch {
        this.status = BATCH_UPLOAD_STATUS.ERROR
        return
      }
      // 开始上传
      await this.startUpload()
    },
    async startUpload() {
      this.status = BATCH_UPLOAD_STATUS.REQUESTING
      try {
        this.notifyClient = null
        this.token = null
        this.fileTokenList = []
        this.transferList = []
        // 获取token
        await this.getUploadToken()
        // 打开传输通知
        await this.openTransferNotify()
        // 执行上传
        setTimeout(() => {
          this.execUploadFile()
        }, 600)
      } catch {
        this.status = BATCH_UPLOAD_STATUS.ERROR
      }
    },
    async getUploadToken() {
      // 获取token
      const { data } = await this.$api.getBatchUploadToken({
        remotePath: this.remotePath,
        machineIds: this.checkData.connectedMachineIdList
      })
      this.token = data
    },
    async openTransferNotify() {
      this.notifyClient = await new WebSocket(this.$api.sftpNotify({ token: this.token.notifyToken }))
      this.notifyClient.onopen = () => {
        // 打开连接发送当前登录token
        setTimeout(() => {
          this.notifyClient.send(this.$storage.get(this.$storage.keys.LOGIN_TOKEN))
        }, 500)
      }
      this.notifyClient.onclose = e => {
        console.log('transfer notify close', e.code, e.reason)
      }
      this.notifyClient.onmessage = msg => {
        const body = JSON.parse(msg.data)
        // 判断是否在 fileTokenList
        if (this.fileTokenList.length) {
          const inCurrentFile = this.fileTokenList.filter(s => s === body.fileToken).length
          if (!inCurrentFile) {
            return
          }
        }
        // 处理消息
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
    },
    async execUploadFile() {
      // form
      const formData = new FormData()
      formData.append('accessToken', this.token.accessToken)
      this.fileList.forEach(file => {
        formData.append('files', file)
      })
      // 上传
      this.$api.execBatchUpload(formData).then(({ data }) => {
        this.$message.success('已添加至传输列表')
        this.fileTokenList = data
        this.status = BATCH_UPLOAD_STATUS.UPLOADING
      }).catch(() => {
        this.$message.error('上传失败')
        this.status = BATCH_UPLOAD_STATUS.ERROR
      })
    },
    abortUpload() {
      this.checkData = null
      this.notifyClient && this.notifyClient.close()
      this.notifyClient = null
      this.token = null
      this.fileTokenList = []
      this.transferList = []
      this.status = BATCH_UPLOAD_STATUS.WAIT
    },
    formatMachine(machineId) {
      const filterMachines = this.checkData.connectedMachines.filter(s => s.id === machineId)
      if (filterMachines && filterMachines.length) {
        const filterMachine = filterMachines[0]
        return `${filterMachine.name} (${filterMachine.host})`
      } else {
        return ''
      }
    },
    resumeFile(fileToken) {
      this.$api.sftpTransferResume({ fileToken: fileToken })
    },
    pauseFile(fileToken) {
      this.$api.sftpTransferPause({ fileToken: fileToken })
    },
    retryFile(fileToken) {
      this.$api.sftpTransferRetry({ fileToken: fileToken })
    },
    deleteFile(fileToken) {
      for (let i = 0; i < this.transferList.length; i++) {
        if (this.transferList[i].fileToken === fileToken) {
          this.transferList.splice(i, 1)
        }
      }
      this.$api.sftpTransferRemove({ fileToken: fileToken })
    }
  }
}
</script>

<style lang="less" scoped>
.batch-upload-container {
  display: flex;
  height: calc(100vh - 84px);
  border-radius: 2px;
}

.upload-list-container {
  width: 500px;
  background: #FFFFFF;
  border-radius: 2px;
  padding: 16px;
  margin-right: 16px;

  .upload-machine-checker-container, .upload-remote-path-container {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
  }

  .upload-remote-path-input-wrapper {
    width: 388px;
  }

  .upload-trigger-container {
    display: flex;

    .upload-event-trigger {
      width: 394px;
      height: 75px;
    }

    .upload-buttons {
      height: 30px;
      display: flex;
      justify-content: flex-end;
      align-items: center;
      flex-wrap: wrap;
      width: 60px;
      margin-left: 12px;

      .button-upload {
        margin-bottom: 9px;
      }
    }
  }

  .upload-file-list {
    overflow-y: auto;
    margin-top: 6px;
    height: calc(100vh - 274px);
  }

  #upload-trigger-icon {
    margin: 0;
  }

  ::v-deep .upload-drag .ant-upload span {
    padding: 0;
  }
}

.transfer-list-container {
  width: calc(100% - 516px);
  border-radius: 2px;
  background: #FFFFFF;
  padding: 16px;
  overflow-y: auto;

  .transfer-clear-wrapper {
    display: flex;
    align-items: center;
  }

  .upload-check-wrapper {
    margin-top: 16px;

    .check-label {
      margin-bottom: 8px;
      font-size: 15px;
      font-weight: 600;
    }

    .check-machine-wrapper {
      margin: 0 0 8px 16px;
      color: rgba(0, 0, 0, .85);
    }

    .check-machine-name {
      margin-right: 8px;
    }

    .check-machine-present-files {
      margin: 0 0 12px 16px;
    }

    .check-machine-present-file {
      margin: 2px 0;
    }
  }

  .upload-check-buttons {
    margin-top: 16px;
  }

  .transfer-list-body-wrapper {
    margin-top: 8px;
  }

  .transfer-file-item-wrapper {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 56px;
    padding: 4px;
    margin: 2px 0;
    transition: .3s;
    box-shadow: 0 4px 8px 2px #F8F9FA;

    .transfer-file-item-header {
      display: flex;
      justify-content: space-between;

      .transfer-file-item-machine {
        margin-left: 32px;
        width: 240px;
        text-align: end;
        color: rgba(0, 0, 0, 0.85);
      }
    }

    .transfer-file-item-bottom {
      display: flex;

      .transfer-file-item-status {
        width: calc(100% - 240px);
      }

      .transfer-file-item-actions {
        margin-left: 32px;
        width: 240px;
        text-align: end;

        .transfer-file-item-action {
          display: inline-block;
          margin-left: 8px;
          cursor: pointer;
        }
      }
    }
  }

  .transfer-file-item-wrapper:hover {
    background-color: #D0EBFF;
    border-radius: 5px;
  }
}

.upload-label {
  margin-right: 8px;
  font-size: 16px;
  font-weight: 600;
}
</style>
