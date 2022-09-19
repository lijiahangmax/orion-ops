<template>
  <a-modal v-model="visible"
           :title="title"
           :width="450"
           :okButtonProps="{props: {disabled: loading}}"
           :bodyStyle="{padding: '16px 24px 0 24px'}"
           :mask="mask"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-alert class="mb16" message="请使用 ssh-keygen -m PEM -t rsa 生成秘钥"/>
      <a-form :form="form" v-bind="layout">
        <a-form-item label="秘钥名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
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
        <a-form-item label="描述">
          <a-textarea v-decorator="decorators.description" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { pick } from 'lodash'
import { getBase64Data, readFileBase64 } from '@/lib/utils'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入秘钥名称'
      }, {
        max: 32,
        message: '秘钥名称长度不能大于32位'
      }]
    }],
    file: ['file', {
      rules: [{
        validator: this.validateFile
      }]
    }],
    password: ['password', {
      rules: [{
        max: 128,
        message: '密码长度不能大于128位'
      }, {
        validator: this.validatePassword
      }]
    }],
    description: ['description', {
      rules: [{
        max: 64,
        message: '描述长度不能大于64位'
      }]
    }]
  }
}

export default {
  name: 'AddMachineKeyModal',
  data: function() {
    return {
      id: null,
      visible: false,
      title: null,
      loading: false,
      mask: true,
      record: null,
      layout,
      fileList: [],
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    setMask(mask) {
      this.mask = mask
    },
    add() {
      this.title = '新增秘钥'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改秘钥'
      this.$api.getMachineKeyDetail({
        id
      }).then(({ data }) => {
        this.initRecord(data)
      })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'name', 'description')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    selectFile(e) {
      this.fileList = [e]
      return false
    },
    validateFile(rule, value, callback) {
      if (!this.id && !this.fileList.length) {
        callback(new Error('请选择秘钥文件'))
      } else {
        callback()
      }
    },
    validatePassword(rule, value, callback) {
      if (!this.id && !value) {
        callback(new Error('请输入密码'))
      } else if (this.id && !value && this.fileList.length) {
        callback(new Error('请输入密码'))
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
        fileBase64 = await readFileBase64(this.fileList[0])
        fileBase64 = getBase64Data(fileBase64)
      }
      let res
      try {
        if (!this.id) {
          // 添加
          res = await this.$api.addMachineKey({
            ...values,
            file: fileBase64
          })
        } else {
          // 修改
          res = await this.$api.updateMachineKey({
            ...values,
            id: this.id,
            file: fileBase64
          })
        }
        if (!this.id) {
          this.$message.success('添加成功')
          this.$emit('added', res.data)
        } else {
          this.$message.success('修改成功')
          this.$emit('updated', res.data)
        }
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
