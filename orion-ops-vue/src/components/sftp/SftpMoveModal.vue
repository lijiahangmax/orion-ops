<template>
  <a-modal v-model="visible"
           v-drag-modal
           title="文件移动"
           :width="450"
           okText="移动"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @cancel="close"
           @ok="move">
    <a-spin :spinning="loading">
      <div class="origin-path-container">
        <span class="path-label span-blue normal-label" @click="$copy(filePath, '原始路径已复制')">原始路径</span>
        <span class="origin-path-text">{{ filePath }}</span>
      </div>
      <br>
      <div class="move-path-container">
        <span class="path-label span-blue normal-label" @click="$copy(movePath, '移动路径已复制')">移动路径</span>
        <a-input class="move-path-input" id="move-path" v-model="movePath"/>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  name: 'SftpMoveModal',
  props: {
    sessionToken: String,
    files: Array
  },
  data: function() {
    return {
      loading: false,
      visible: false,
      parentPath: null,
      filePath: null,
      movePath: null
    }
  },
  methods: {
    openMove(config) {
      this.visible = true
      this.parentPath = config.parentPath
      this.filePath = config.filePath
      this.movePath = config.filePath
    },
    async move() {
      this.loading = true
      try {
        const res = await this.$api.sftpMove({
          sessionToken: this.sessionToken,
          source: this.filePath,
          target: this.movePath
        })
        const movePath = res.data
        const parentPath = movePath.substring(0, movePath.lastIndexOf('/'))
        for (let i = 0; i < this.files.length; i++) {
          if (this.files[i].path === this.filePath) {
            if (parentPath === this.parentPath) {
              // 列表改名
              this.files[i].name = movePath.substring(movePath.lastIndexOf('/') + 1, movePath.length)
              this.files[i].path = movePath
            } else {
              // 从列表删除
              this.files.splice(i, 1)
            }
          }
        }
        this.$message.success(`已移动到 ${movePath}`)
        this.close()
      } catch (e) {
        this.$message.error('文件移动失败')
      }
      this.loading = false
    },
    close() {
      this.loading = false
      this.visible = false
      this.parentPath = null
      this.filePath = null
      this.movePath = null
    }
  }
}
</script>

<style scoped>

.origin-path-container {
  display: flex;
  align-items: flex-start;
}

.origin-path-text {
  max-width: 320px;
}

.path-label {
  margin-right: 10px;
  cursor: pointer;
}

.move-path-container {
  display: flex;
  align-items: center;
}

.move-path-input {
  width: 324px;
}

</style>
