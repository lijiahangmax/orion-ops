<template>
  <a-modal v-model="visible"
           v-drag-modal
           title="临时挂载"
           :width="450"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="秘钥文件">
          <a-upload v-decorator="decorators.file"
                    :beforeUpload="selectFile"
                    :fileList="fileList"
                    :remove="() => fileList = []">
            <a-button icon="upload">选择文件</a-button>
          </a-upload>
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-decorator="decorators.password" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    file: ['file', {
      rules: [{
        validator: this.validateFile
      }]
    }],
    password: ['password', {
      rules: [{
        required: true,
        max: 128,
        message: '密码长度不能大于128位'
      }]
    }]
  }
}

export default {
  name: 'TempMountMachineKeyModal',
  data: function() {
    return {
      visible: false,
      loading: false,
      layout,
      fileList: [],
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    add() {
      this.form.resetFields()
      this.visible = true
    },
    selectFile(e) {
      this.fileList = [e]
      return false
    },
    validateFile(rule, value, callback) {
      if (!this.fileList.length) {
        callback(new Error('请选择秘钥文件'))
      } else {
        callback()
      }
    },
    check() {
      this.loading = true
      this.form.validateFields((err, values) => {
        if (err) {
          this.loading = false
          return
        }
        this.submit(values)
      })
    },
    async submit(values) {
      // 读取文件
      let fileBase64
      if (this.fileList.length) {
        fileBase64 = await this.$utils.readFileBase64(this.fileList[0])
        fileBase64 = this.$utils.getBase64Data(fileBase64)
      }
      let res
      try {
        res = await this.$api.tempMountMachineKey({
          ...values,
          file: fileBase64
        })
        this.$message.success('挂载成功')
        this.$emit('mounted', res.data)
        this.close()
      } catch (e) {
        // ignore
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

<style scoped>

</style>
