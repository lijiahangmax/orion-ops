<template>
  <a-modal v-model="visible"
           title="上传日志文件"
           okText="上传"
           :width="400"
           :dialogStyle="{top: '64px', padding: 0}"
           :bodyStyle="{padding: '16px'}"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="upload"
           @cancel="close">
    <a-spin :spinning="loading">
      <!-- 提示 -->
      <a-alert message="通常用于下载的日志文件着色查看"/>
      <!-- 文件上传拖拽框 -->
      <div class="upload-event-trigger mt8">
        <a-upload-dragger class="upload-drag"
                          accept=".log,.txt"
                          :beforeUpload="selectFile"
                          :multiple="true"
                          :fileList="fileList"
                          :showUploadList="true">
          <p id="upload-trigger-icon" class="ant-upload-drag-icon">
            <a-icon type="inbox"/>
          </p>
          <p class="ant-upload-text">单击或拖动文件到此区域进行上传</p>
        </a-upload-dragger>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  name: 'UploadLogFileModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      fileList: []
    }
  },
  methods: {
    open() {
      this.visible = true
    },
    selectFile(e) {
      this.fileList.push(e)
      if (this.fileList.length > 10) {
        this.fileList.splice(0, this.fileList.length - 10)
      }
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
    upload() {
      if (!this.fileList.length) {
        this.$message.warning('请先选择文件')
        return
      }
      this.loading = true
      const formData = new FormData()
      this.fileList.forEach(file => {
        formData.append('files', file)
      })
      // 上传
      this.$api.uploadTailFile(formData).then(() => {
        this.$message.success('上传成功')
        this.$emit('uploaded')
        this.close()
      }).catch(() => {
        this.loading = false
        this.$message.error('上传失败')
      })
    },
    close() {
      this.visible = false
      this.loading = false
      this.fileList = []
    }
  }
}
</script>

<style lang="less" scoped>
#upload-trigger-icon {
  margin: 0;
}

.upload-drag /deep/ .ant-upload span {
  padding: 8px;
}
</style>
