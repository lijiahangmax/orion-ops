<template>
  <a-modal v-model="visible"
           :title="title"
           :width="650"
           :dialogStyle="{top: '64px', padding: 0}"
           :okButtonProps="{props: {disabled: loading}}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <a-form-item label="机器">
          <MachineSelector ref="machineSelector"
                           placeholder="请选择"
                           @change="setDefaultConfig"
                           :query="machineQuery"
                           v-decorator="decorators.machineId"/>
        </a-form-item>
        <!-- 追踪模式 -->
        <a-form-item v-if="form.getFieldValue('machineId') === 1" label="追踪模式">
          <a-radio-group v-decorator="decorators.tailMode">
            <a-tooltip v-for="type of FILE_TAIL_MODE" :key="type.value" :title="type.tips">
              <a-radio :value="type.value">
                {{ type.label }}
              </a-radio>
            </a-tooltip>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <a-form-item label="文件路径" hasFeedback>
          <a-input v-decorator="decorators.path" allowClear/>
        </a-form-item>
        <a-form-item label="命令" style="margin-bottom: 12px">
          <a-textarea v-decorator="decorators.command"
                      :disabled="form.getFieldValue('machineId') === 1 &&
                       form.getFieldValue('tailMode') === FILE_TAIL_MODE.TRACKER.value"
                      allowClear/>
        </a-form-item>
        <a-form-item label="文件偏移量(行)" hasFeedback>
          <a-input v-decorator="decorators.offset" allowClear/>
        </a-form-item>
        <a-form-item label="文件编码">
          <a-input v-decorator="decorators.charset" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { pick } from 'lodash'
import { ENABLE_STATUS, FILE_TAIL_MODE } from '@/lib/enum'
import MachineSelector from '@/components/machine/MachineSelector'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入名称'
      }, {
        max: 64,
        message: '名称长度不能大于64位'
      }]
    }],
    machineId: ['machineId', {
      rules: [{
        required: true,
        message: '请选择机器'
      }]
    }],
    tailMode: ['tailMode', {
      rules: [{
        required: true,
        message: '请选择追踪模式'
      }],
      initialValue: FILE_TAIL_MODE.TRACKER.value
    }],
    path: ['path', {
      rules: [{
        required: true,
        message: '请输入文件路径'
      }, {
        max: 1024,
        message: '文件路径长度不能大于1024位'
      }]
    }],
    command: ['command', {
      rules: [{
        required: true,
        message: '请输入命令'
      }, {
        max: 1024,
        message: '命令长度不能大于1024位'
      }]
    }],
    offset: ['offset', {
      rules: [{
        required: true,
        message: '请输入文件偏移量'
      }, {
        validator: this.validateOffset
      }]
    }],
    charset: ['charset', {
      rules: [{
        required: true,
        message: '请输入文件编码'
      }, {
        max: 16,
        message: '文件编码长度不能大于16位'
      }]
    }]
  }
}

export default {
  name: 'AddLogFileModal',
  components: {
    MachineSelector
  },
  data: function() {
    return {
      FILE_TAIL_MODE,
      id: null,
      visible: false,
      title: null,
      loading: false,
      record: null,
      updateConfig: true,
      layout,
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this),
      machineQuery: { status: ENABLE_STATUS.ENABLE.value }
    }
  },
  methods: {
    validateOffset(rule, value, callback) {
      if (value === '') {
        callback(new Error('请输入文件偏移量'))
      } else if (parseFloat(value) !== parseInt(value)) {
        callback(new Error('请输入文件偏移量必须为整数'))
      } else {
        if (value < 0 || value > 10000) {
          callback(new Error('文件偏移量必须在0-10000之间'))
        } else {
          callback()
        }
      }
    },
    setDefaultConfig(machineId) {
      if (!this.updateConfig) {
        return
      }
      if (!machineId) {
        return
      }
      this.$api.getTailConfig({ machineId })
        .then(({ data }) => {
          const config = pick(Object.assign({}, data), 'command', 'offset', 'charset')
          this.$nextTick(() => {
            this.form.setFieldsValue(config)
          })
        })
    },
    add() {
      this.title = '新增日志文件'
      this.updateConfig = true
      this.initRecord({})
    },
    update(id) {
      this.title = '修改日志文件'
      this.updateConfig = false
      this.$api.getTailDetail({
        id
      }).then(({ data }) => {
        this.initRecord(data)
      })
    },
    initRecord(row) {
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'machineId', 'name', 'path', 'command', 'offset', 'charset')
      // 设置数据
      new Promise((resolve) => {
        // 加载数据
        this.$nextTick(() => {
          this.form.setFieldsValue(this.record)
        })
        resolve()
      }).then(() => {
        // 设置追踪类型
        if (this.record.machineId === 1) {
          this.$nextTick(() => {
            const tailMode = row.tailMode
            this.record.tailMode = tailMode
            this.form.setFieldsValue({ tailMode })
          })
        }
      })
      setTimeout(() => {
        this.updateConfig = true
      })
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
      let res
      try {
        if (!this.id) {
          // 添加
          res = await this.$api.addTailFile({
            ...values
          })
        } else {
          // 修改
          res = await this.$api.updateTailFile({
            ...values,
            id: this.id
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
      this.$refs.machineSelector.reset()
    }
  }
}
</script>

<style scoped>

</style>
