<template>
  <a-modal v-model="visible"
           :title="title"
           :width="700"
           :dialogStyle="{top: '32px'}"
           :okButtonProps="{props: {disabled: loading}}"
           :bodyStyle="{padding: '24px 0 0 0'}"
           :maskClosable="false"
           :destroyOnClose="true"
           @ok="check"
           @cancel="close">
    <!-- 表单 -->
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="layout">
        <!-- 任务名称 -->
        <a-form-item label="任务名称" hasFeedback>
          <a-input v-decorator="decorators.name" allowClear/>
        </a-form-item>
        <!-- 表达式 -->
        <a-form-item class="expression-item" label="cron表达式">
          <!-- 表达式 -->
          <a-input class="expression-input" v-decorator="decorators.expression" allowClear/>
          <!-- 最近执行时间 -->
          <a-popover title="最近执行时间"
                     placement="right"
                     v-if="cron.valid === true && cron.next">
            <template #content>
              <span class="popover-next-time span-blue" v-for="next of cron.nextArr" :key="next">
                {{ next | formatDate }}<br/>
              </span>
            </template>
            <span class="expression-extra span-blue">
              {{ cron.next | formatDate }}
            </span>
          </a-popover>
          <span class="expression-extra span-red" v-if="cron.valid === true && !cron.next">无下次执行时间</span>
          <span class="expression-extra span-red" v-if="cron.valid === false">无效的表达式</span>
        </a-form-item>
        <!-- 执行序列 -->
        <a-form-item class="no-extra-form-item" label="执行序列">
          <a-radio-group v-decorator="decorators.serializeType">
            <a-radio v-for="type of SERIAL_TYPE" :key="type.value" :value="type.value">
              {{ type.label }}
            </a-radio>
          </a-radio-group>
        </a-form-item>
        <!-- 异常处理 -->
        <a-form-item v-if="form.getFieldValue('serializeType') === SERIAL_TYPE.SERIAL.value"
                     class="no-extra-form-item"
                     label="异常处理">
          <a-radio-group v-decorator="decorators.exceptionHandler">
            <a-tooltip v-for="type of EXCEPTION_HANDLER_TYPE" :key="type.value" :title="type.title">
              <a-radio :value="type.value">
                {{ type.label }}
              </a-radio>
            </a-tooltip>
          </a-radio-group>
        </a-form-item>
        <!-- 执行机器 -->
        <a-form-item label="执行机器">
          <MachineMultiSelector v-decorator="decorators.machineIdList"/>
        </a-form-item>
        <!-- 执行命令 -->
        <a-form-item label="执行命令">
          <Editor :height="350" v-decorator="decorators.command"/>
          <template #extra>
            <a @click="selectTemplate">从模板选择</a>
          </template>
        </a-form-item>
        <!-- 描述 -->
        <a-form-item label="描述">
          <a-textarea v-decorator="decorators.description" allowClear/>
        </a-form-item>
      </a-form>
    </a-spin>
    <!-- 事件 -->
    <div class="scheduler-event-container">
      <TemplateSelector ref="templateSelector" @selected="selectedTemplate"/>
    </div>
  </a-modal>
</template>

<script>
import { pick } from 'lodash'
import { formatDate } from '@/lib/filters'
import { EXCEPTION_HANDLER_TYPE, SERIAL_TYPE } from '@/lib/enum'
import Editor from '@/components/editor/Editor'
import MachineMultiSelector from '@/components/machine/MachineMultiSelector'
import TemplateSelector from '@/components/content/TemplateSelector'

const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入任务名称'
      }, {
        max: 128,
        message: '任务名称不能大于128位'
      }]
    }],
    expression: ['expression', {
      validateTrigger: 'blur',
      validateFirst: true,
      rules: [{
        required: true,
        message: '请输入表达式'
      }, {
        max: 512,
        message: '表达式不能大于512位'
      }, {
        validator: this.validateExpression
      }]
    }],
    command: ['command', {
      initialValue: undefined,
      rules: [{
        required: true,
        message: '请输入执行命令'
      }, {
        max: 1024,
        message: '执行命令不能大于2048位'
      }]
    }],
    machineIdList: ['machineIdList', {
      rules: [{
        required: true,
        message: '请选择执行机器'
      }]
    }],
    type: ['type', {
      rules: [{
        required: true,
        message: '请选择类型'
      }]
    }],
    serializeType: ['serializeType', {
      initialValue: SERIAL_TYPE.PARALLEL.value
    }],
    exceptionHandler: ['exceptionHandler', {
      initialValue: EXCEPTION_HANDLER_TYPE.SKIP_ALL.value
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
  name: 'AddSchedulerTask',
  components: {
    TemplateSelector,
    MachineMultiSelector,
    Editor
  },
  data: function() {
    return {
      SERIAL_TYPE,
      EXCEPTION_HANDLER_TYPE,
      id: null,
      visible: false,
      title: null,
      loading: false,
      record: null,
      layout,
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this),
      cron: {
        next: null,
        nextArr: null,
        valid: undefined
      }
    }
  },
  methods: {
    add() {
      this.title = '新增任务'
      this.initRecord({})
    },
    update(id) {
      this.title = '修改任务'
      this.$api.getSchedulerTask({ id })
        .then(({ data }) => {
          this.initRecord(data)
          this.cron.nextArr = data.nextTime
          this.cron.next = data.nextTime && data.nextTime.length && data.nextTime[0]
          this.cron.valid = true
        })
    },
    initRecord(row) {
      this.cron.next = null
      this.cron.nextArr = null
      this.cron.valid = undefined
      this.form.resetFields()
      this.visible = true
      this.id = row.id
      this.record = pick(Object.assign({}, row), 'name', 'expression', 'command', 'description', 'machineIdList', 'serializeType')
      // 设置数据
      new Promise((resolve) => {
        // 加载数据
        this.$nextTick(() => {
          this.form.setFieldsValue(this.record)
        })
        resolve()
      }).then(() => {
        // 设置异常处理
        if (this.record.serializeType === SERIAL_TYPE.SERIAL.value) {
          this.$nextTick(() => {
            const exceptionHandler = row.exceptionHandler
            this.record.exceptionHandler = exceptionHandler
            this.form.setFieldsValue({ exceptionHandler })
          })
        }
      })
    },
    selectTemplate() {
      this.$refs.templateSelector.open()
    },
    selectedTemplate(command) {
      this.form.setFieldsValue({ command })
      this.$refs.templateSelector.close()
    },
    validateExpression(rule, expression, callback) {
      this.$api.getCronNextTime({
        expression,
        times: 5
      }).then(({ data }) => {
        this.cron.valid = data.valid
        if (data.next && data.next.length) {
          this.cron.next = data.next[0]
          this.cron.nextArr = data.next
        }
        if (data.valid) {
          callback()
        } else {
          callback(new Error('无效表达式'))
        }
      }).catch(() => {
        this.cron.next = null
        this.cron.nextArr = []
        this.cron.valid = false
        callback(new Error('无效表达式'))
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
          res = await this.$api.addSchedulerTask({ ...values })
        } else {
          // 修改
          res = await this.$api.updateSchedulerTask({
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
    }
  },
  filters: {
    formatDate
  }
}
</script>

<style lang="less" scoped>
.expression-item {
  margin-bottom: 12px;

  ::v-deep .ant-form-explain {
    display: none;
    font-size: 0;
  }

  .expression-input {
    width: calc(100% - 148px);
  }

  .expression-extra {
    width: 140px;
    text-align: right;
    display: inline-block;
    font-size: 13px;
  }

  .popover-next-time {
    display: block;
    font-size: 13px;
  }
}

.no-extra-form-item {
  margin-bottom: 12px;
}

</style>
