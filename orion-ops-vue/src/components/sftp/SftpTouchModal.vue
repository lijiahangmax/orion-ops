<template>
  <a-modal v-model="visible"
           title="创建"
           width="450px"
           :maskClosable="false"
           :destroyOnClose="true">
    <!-- 输入框 -->
    <a-spin :spinning="loading">
      <a-input-group compact>
        <a-select v-model="parentType">
          <a-select-option value="current">
            当前目录
          </a-select-option>
          <a-select-option value="home">
            home目录
          </a-select-option>
          <a-select-option value="root">
            根目录
          </a-select-option>
        </a-select>
        <a-auto-complete v-model="path" style="width: 300px" placeholder="路径"/>
      </a-input-group>
    </a-spin>
    <!-- 底部按钮 -->
    <template #footer>
      <a-button @click="close()">取消</a-button>
      <a-button type="primary"
                :disabled="loading"
                :loading="loading && isTouchFile"
                @click="handleTouch(true)">
        创建文件
      </a-button>
      <a-button type="primary"
                :disabled="loading"
                :loading="loading && !isTouchFile"
                @click="handleTouch(false)">
        创建文件夹
      </a-button>
    </template>
  </a-modal>
</template>
<script>
import { getPath, isEmptyStr } from '@/lib/utils'

export default {
  name: 'SftpTouchModal',
  props: {
    sessionToken: String
  },
  data() {
    return {
      visible: false,
      loading: false,
      currentPath: null,
      homePath: null,
      parentType: 'current',
      path: null,
      isTouchFile: true
    }
  },
  methods: {
    openTouch(config) {
      this.currentPath = config.currentPath
      this.homePath = config.homePath
      this.visible = true
    },
    async handleTouch(isTouchFile) {
      if (isEmptyStr(this.path)) {
        this.$message.warning('路径不能为空')
        return
      }
      this.isTouchFile = isTouchFile
      this.loading = true
      // 父目录
      let parentPath
      switch (this.parentType) {
        case 'current':
          parentPath = this.currentPath
          break
        case 'home':
          parentPath = this.homePath
          break
        default:
          parentPath = ''
      }
      this.realPath = getPath(parentPath + '/' + this.path)
      // 创建
      const data = {
        sessionToken: this.sessionToken,
        path: this.realPath
      }
      try {
        let res
        if (isTouchFile) {
          res = await this.$api.sftpTouch(data)
        } else {
          res = await this.$api.sftpMkdir(data)
        }
        this.$message.success(`已创建${isTouchFile ? '文件' : '目录'} ${res.data}`)
        if (this.parentPath === parentPath) {
          this.$emit('listFiles')
        }
        this.close()
      } catch (e) {
        this.$message.error(`创建${isTouchFile ? '文件' : '目录'}失败`)
      }
      this.loading = false
    },
    close() {
      this.visible = false
      this.loading = false
      this.currentPath = null
      this.homePath = null
      this.parentType = 'current'
      this.path = null
    }
  }
}
</script>

<style scoped>

</style>
