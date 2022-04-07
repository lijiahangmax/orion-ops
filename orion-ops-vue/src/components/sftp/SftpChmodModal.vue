<template>
  <a-modal v-model="visible"
           v-drag-modal
           title="文件提权"
           :width="450"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @cancel="close"
           @ok="chmod">
    <a-spin :spinning="loading">
      <div class="file-path-container">
        <span class="file-path-label span-label span-blue normal-label" @click="$copy(filePath, '文件路径已复制')">文件路径</span>
        <span class="file-path-text">{{ filePath }}</span>
      </div>
      <br>
      <div class="file-permission-container">
        <div>
          <span class="file-permission-label span-label normal-label">权限</span>
          <a-input class="file-permission-input"
                   placeholder="权限"
                   v-model.number="permission"
                   v-limit-integer
                   @change="limitPermission"
                   @blur="padPermission"/>
        </div>
        <div>
          <span class="file-permission-string">{{ permissionString }}</span>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  name: 'SftpChmodModal',
  props: {
    sessionToken: String,
    files: Array
  },
  computed: {
    permissionString: function() {
      return this.$utils.permission10toString(this.permission).padEnd(9, '-').substring(0, 9)
    }
  },
  data: function() {
    return {
      visible: false,
      loading: false,
      filePath: null,
      permission: null
    }
  },
  methods: {
    limitPermission() {
      if ((this.permission + '').length > 3) {
        this.permission = (this.permission + '').substring(0, 3)
      }
    },
    padPermission() {
      if ((this.permission + '').length < 3) {
        this.permission = (this.permission + '').padEnd(3, '0')
      }
    },
    openChmod(config) {
      this.visible = true
      this.filePath = config.filePath
      this.permission = config.permission
    },
    async chmod() {
      if (parseInt(this.permission) + '' !== this.permission + '') {
        this.$message.error('文件权限不正确')
        return
      }
      if (parseInt(this.permission) > 777) {
        this.$message.error('文件权限不能大于777')
        return
      }
      this.loading = true
      try {
        await this.$api.sftpChmod({
          sessionToken: this.sessionToken,
          path: this.filePath,
          permission: this.permission
        })
        this.$message.success(`文件 ${this.filePath} 权限已修改为 ${this.permission}`)
        for (let i = 0; i < this.files.length; i++) {
          if (this.files[i].path === this.filePath) {
            this.files[i].attr = this.files[i].attr.charAt(0) + this.permissionString
            this.files[i].permission = this.permission
          }
        }
        this.close()
      } catch (e) {
        this.$message.error('文件权限修改失败')
      }
      this.loading = false
    },
    close() {
      this.visible = false
      this.loading = false
      this.filePath = null
      this.permission = null
    }
  }
}
</script>

<style scoped>

.file-path-container {
  display: flex;
  align-items: flex-start;
}

.file-permission-container {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.file-path-label {
  cursor: pointer;
}

.file-permission-input {
  width: 100px;
}

.span-label {
  margin-right: 10px;
  width: 74px;
}

.file-permission-string {
  margin-left: 20px;
  font-size: 18px;
}

.file-path-text {
  width: 320px;
}

</style>
