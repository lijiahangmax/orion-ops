<template>
  <a-modal v-model="visible"
           :title="importType.title"
           :okText="dataCheckPage ? '开始导入' : '导入'"
           :width="500"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="clickImport"
           @cancel="close">
    <a-spin :spinning="loading">
      <!-- 导入页面 -->
      <div class="data-import-container" v-if="!dataCheckPage">
        <!-- 导入提示 -->
        <a-alert class="import-alert-message" :message="importType.tips" type="info"/>
        <!-- 上传框 -->
        <div class="file-select-container">
          <!-- 文件选择 -->
          <div class="file-select-wrapper">
            <a-upload accept=".xlsx"
                      :beforeUpload="selectFile"
                      :customRequest="() => {}"
                      :showUploadList="false">
              <a-button type="link">选择文件</a-button>
            </a-upload>
            <!-- 已选择的文件 -->
            <span class="selected-file-name" v-if="uploadFile" :title="uploadFile.name">
              {{ uploadFile.name }}
            </span>
          </div>
          <!-- 下载模板 -->
          <a-button type="link" @click="downloadTemplate">下载模板</a-button>
        </div>
        <!-- 参数 -->
        <div class="data-import-params">
          <!-- 文档密码 -->
          <div class="data-import-param">
            <span class="normal-label import-label">文档密码</span>
            <a-input class="param-input"
                     v-model="protectPassword"
                     :maxLength="10"
                     placeholder="导入文档的密码"/>
          </div>
        </div>
      </div>
      <!-- 检查页面 -->
      <div class="data-check-container" v-else>
        <!-- 非法数据 -->
        <div class="check-data-container" v-if="checkData.illegalRows.length">
          <span class="normal-label check-label span-red">非法数据 (不会导入)</span>
          <div class="check-data-wrapper" v-for="row of checkData.illegalRows" :key="row.index">
            第 <span class="span-red">{{ row.row }}</span> 行, <span v-if="row.symbol" class="ml4 mr8">{{ row.symbol }}</span>
            <span class="span-red">{{ row.illegalMessage }}</span>
          </div>
        </div>
        <!-- 新增数据 -->
        <div class="check-data-container" v-if="checkData.insertRows.length">
          <span class="normal-label check-label span-blue">新增数据</span>
          <div class="check-data-wrapper" v-for="row of checkData.insertRows" :key="row.index">
            第 <span class="span-blue">{{ row.row }}</span> 行, <span class="ml4">{{ row.symbol }}</span>
          </div>
        </div>
        <!-- 修改数据 -->
        <div class="check-data-container" v-if="checkData.updateRows.length">
          <span class="normal-label check-label span-blue">修改数据</span>
          <div class="check-data-wrapper" v-for="row of checkData.updateRows" :key="row.index">
            第 <span class="span-blue">{{ row.row }}</span> 行, <span class="ml4">{{ row.symbol }}</span>
          </div>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  name: 'DataImportModal',
  props: {
    importType: Object
  },
  data: function() {
    return {
      visible: false,
      loading: false,
      dataCheckPage: false,
      protectPassword: undefined,
      uploadFile: null,
      checkData: null
    }
  },
  methods: {
    open() {
      this.dataCheckPage = false
      this.protectPassword = undefined
      this.uploadFile = null
      this.checkData = null
      this.loading = false
      this.visible = true
    },
    selectFile(e) {
      const suffix = e.name.substring(e.name.lastIndexOf('.') + 1)
      if (suffix !== 'xlsx') {
        this.$message.error('请选择 xlsx 表格进行导入')
        return false
      }
      this.uploadFile = e
    },
    clickImport() {
      if (this.dataCheckPage) {
        this.asyncImportData()
      } else {
        this.checkImportData()
      }
    },
    checkImportData() {
      if (!this.uploadFile) {
        this.$message.warning('请选择导入文件')
        return
      }
      this.loading = true
      const formData = new FormData()
      formData.append('file', this.uploadFile)
      formData.append('type', this.importType.value)
      formData.append('protectPassword', this.protectPassword)
      this.$api.checkImportData(formData).then(({ data }) => {
        this.loading = false
        this.dataCheckPage = true
        this.checkData = data
      }).catch(() => {
        this.loading = false
      })
    },
    asyncImportData() {
      if (this.checkData.insertRows.length + this.checkData.updateRows.length === 0) {
        this.$message.warning('无可用导入数据, 无法导入')
        return
      }
      this.loading = true
      this.$api[this.importType.api]({
        importToken: this.checkData.importToken
      }).then(() => {
        this.loading = false
        this.visible = false
        this.$message.success('已开始导入')
      }).catch(() => {
        this.loading = false
      })
    },
    downloadTemplate() {
      this.$api.getImportTemplate({
        type: this.importType.value
      }).then((e) => {
        this.$utils.downloadFile(e)
      }).catch(() => {
        this.$message.error('下载失败')
      })
    },
    close() {
      if (this.dataCheckPage) {
        this.$api.cancelImportData({
          importToken: this.checkData.importToken
        })
      }
      this.visible = false
      this.loading = false
      this.uploadFile = null
    }
  }
}
</script>

<style lang="less" scoped>

.data-import-container {
  width: 100%;
}

.import-alert-message {
  margin-bottom: 8px;
}

.file-select-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;

  button {
    padding: 0;
  }

  .selected-file-name {
    white-space: nowrap;
    overflow: hidden;
    width: 294px;
    display: inline-flex;
    margin-left: 8px;
  }

}

.import-label {
  width: 64px;
  text-align: end;
}

.data-import-param {
  width: 100%;
  display: flex;
  align-items: center;
}

.param-input {
  margin-left: 8px;
  width: 236px;
}

.data-check-container {
  margin: -12px 0;

  .check-data-container {
    margin: 8px 0;
  }

  .check-label {
    margin-bottom: 4px;
    font-weight: 600;
    font-size: 18px;
  }

  .check-data-wrapper {
    line-height: 1.6;
    font-size: 15px;
    padding-left: 16px;
    color: rgba(0, 0, 0, .85);
  }
}

</style>
