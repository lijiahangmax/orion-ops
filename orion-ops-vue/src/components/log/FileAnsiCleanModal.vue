<template>
  <a-modal v-model="visible"
           title="清除文件 ASNI 码"
           okText="清除"
           :width="400"
           :dialogStyle="{top: '64px', padding: 0}"
           :bodyStyle="{padding: '16px'}"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="clean"
           @cancel="close">
    <a-spin :spinning="loading">
      <!-- 提示 -->
      <a-alert message="清除日志文件的 ANSI 着色码, 恢复为普通的日志文件"/>
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
          <p class="ant-upload-text">单击或拖动文件到此区域</p>
        </a-upload-dragger>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import { downloadFile } from '@/lib/utils'

export default {
  name: 'FileAnsiCleanModal',
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
      return false
    },
    removeFile(e) {
      for (let i = 0; i < this.fileList.length; i++) {
        if (this.fileList[i] === e) {
          this.fileList.splice(i, 1)
        }
      }
    },
    async clean() {
      if (!this.fileList.length) {
        this.$message.warning('请先选择文件')
        return
      }
      this.loading = true
      for (const file of this.fileList) {
        const formData = new FormData()
        formData.append('file', file)
        this.$message.info(`开始处理 ${file.name}`)
        await this.$api.cleanFileAnsiCode(formData).then((e) => {
          this.$message.success(`${file.name} 处理完成, 片刻后自动下载`)
          downloadFile(e, file.name)
          this.fileList.splice(0, 1)
        }).catch(() => {
          this.$message.error(`${file.name} 处理失败`)
        })
      }
      this.loading = false
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

::v-deep .upload-drag .ant-upload span {
  padding: 8px;
}
</style>
