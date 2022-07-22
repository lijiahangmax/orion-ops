<template>
  <a-spin :spinning="loading">
    <div class="upload-container">
      <!-- 路径 -->
      <div class="upload-path-container">
        <span class="upload-path-label normal-label mr4">文件夹</span>
        <a-input v-model="path" placeholder="上传文件夹"/>
      </div>
      <!-- 上传文件触发器 -->
      <div class="upload-event-container">
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
          <a-button type="primary" class="button-upload" :disabled="fileList.length === 0" @click="check">上传</a-button>
          <a-button class="button-clear" :disabled="fileList.length === 0" @click="clear">清空</a-button>
        </div>
      </div>
      <!-- 文件列表 -->
      <div class="upload-file-list">
        <a-upload :fileList="fileList" :remove="removeFile"/>
      </div>
    </div>
  </a-spin>
</template>

<script>
export default {
  name: 'SftpUpload',
  props: {
    sessionToken: String,
    currentPath: String
  },
  watch: {
    currentPath(e) {
      if (!this.loading) {
        this.path = e
      }
    }
  },
  data() {
    return {
      loading: false,
      fileList: [],
      path: ''
    }
  },
  methods: {
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
    async check() {
      if (!this.path.trim()) {
        this.$message.error('上传文件夹不能为空')
        return
      }
      const size = this.fileList.map(s => s.size).reduce((t, v) => t + v, 0)
      this.loading = true
      var presentFiles
      try {
        const checkPresentRes = await this.$api.sftpCheckFilePresent({
          sessionToken: this.sessionToken,
          path: this.path,
          names: this.fileList.map(s => s.name),
          size
        })
        presentFiles = checkPresentRes.data
      } catch (e) {
        this.loading = false
        return
      }
      if (presentFiles && presentFiles.length) {
        var confirmMessage
        if (presentFiles.length > 3) {
          confirmMessage = `文件 ${presentFiles.slice(0, 3).join(', ')} 等${presentFiles.length}个文件已存在, 是否继续上传?`
        } else {
          confirmMessage = `文件 ${presentFiles.join(', ')} 已存在, 是否继续上传?`
        }
        this.$confirm({
          title: '文件已存在',
          content: confirmMessage,
          okText: '确认',
          cancelText: '取消',
          mask: false,
          zIndex: 1035,
          onOk: () => {
            this.uploadFile()
          },
          onCancel: () => {
            this.loading = false
            this.$emit('changeVisible', true)
          }
        })
      } else {
        await this.uploadFile()
      }
    },
    async uploadFile() {
      this.loading = true
      let accessToken
      try {
        // 获取上传token
        const { data } = await this.$api.getSftpUploadToken({
          remotePath: this.path,
          sessionToken: this.sessionToken
        })
        accessToken = data
      } catch {
        this.loading = false
        return
      }
      this.$emit('changeVisible', false)
      this.$emit('uploading', true)
      const formData = new FormData()
      formData.append('accessToken', accessToken)
      this.fileList.forEach(file => {
        formData.append('files', file)
      })
      this.$api.sftpUploadExec(formData).then(() => {
        this.$message.success('已添加至传输列表')
        this.loading = false
        this.fileList = []
        this.$emit('uploading', false)
      }).catch(() => {
        this.loading = false
        this.$emit('changeVisible', true)
        this.$emit('uploading', false)
      })
    }
  },
  mounted() {
    this.path = this.currentPath
  }
}
</script>

<style lang="less" scoped>

.upload-path-container {
  display: flex;
  margin: 0 8px 8px 0;
  align-items: center;
  justify-content: center;

  .upload-path-label {
    width: 60px;
  }
}

.upload-container {
  width: 350px;
}

.upload-event-container {
  display: flex;

  .upload-event-trigger {
    width: 275px;
    height: 75px;
  }

  .upload-buttons {
    height: 30px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    flex-wrap: wrap;
    width: 60px;
    margin-left: 8px;

    .button-upload {
      margin-bottom: 9px;
    }
  }

}

.upload-file-list {
  margin-top: 6px;
  min-height: 90px;
  max-height: 205px;
  overflow-y: auto;
}

#upload-trigger-icon {
  margin: 0;
}

.upload-drag /deep/ .ant-upload span {
  padding: 0;
}

</style>
