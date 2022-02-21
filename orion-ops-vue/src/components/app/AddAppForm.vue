<template>
  <a-spin :spinning="loading">
    <a-form :form="form" v-bind="layout">
      <a-form-item label="名称" hasFeedback>
        <a-input v-decorator="decorators.name" allowClear/>
      </a-form-item>
      <a-form-item label="标签" hasFeedback>
        <a-input v-decorator="decorators.tag" allowClear/>
      </a-form-item>
      <a-form-item label="版本仓库">
        <a-select placeholder="请选择" v-decorator="decorators.vcsId" style="width: 85%" allowClear>
          <a-select-option v-for="vcs in vcsList" :key="vcs.id" :value="vcs.id">
            <span>{{ vcs.name }}</span>
          </a-select-option>
        </a-select>
        <a class="reload-vcs" title="刷新" @click="getVcsList">
          <a-icon type="reload"/>
        </a>
      </a-form-item>
      <a-form-item label="描述">
        <a-textarea v-decorator="decorators.description" allowClear/>
      </a-form-item>
    </a-form>
  </a-spin>
</template>

<script>
import { pick } from 'lodash'

function getDecorators() {
  return {
    name: ['name', {
      rules: [{
        required: true,
        message: '请输入名称'
      }, {
        max: 32,
        message: '名称长度不能大于32位'
      }]
    }],
    tag: ['tag', {
      rules: [{
        required: true,
        message: '请输入标签'
      }, {
        max: 32,
        message: '标签长度不能大于32位'
      }]
    }],
    vcsId: ['vcsId'],
    description: ['description', {
      rules: [{
        max: 64,
        message: '描述长度不能大于64位'
      }]
    }]
  }
}

export default {
  name: 'AddAppForm',
  props: {
    layout: {
      type: Object,
      default: () => {
        return {
          labelCol: { span: 5 },
          wrapperCol: { span: 16 }
        }
      }
    }
  },
  data: function() {
    return {
      id: null,
      loading: false,
      record: null,
      vcsList: [],
      decorators: getDecorators.call(this),
      form: this.$form.createForm(this)
    }
  },
  methods: {
    add() {
      this.initRecord({})
    },
    update(id) {
      this.$api.getAppDetail({ id })
        .then(({ data }) => {
          this.initRecord(data)
        })
    },
    initRecord(row) {
      this.form.resetFields()
      this.id = row.id
      if (!row.vcsId) {
        row.vcsId = undefined
      }
      this.record = pick(Object.assign({}, row), 'name', 'tag', 'vcsId', 'description')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    async getVcsList() {
      this.$api.getVcsList({
        limit: 10000,
        status: this.$enum.VCS_STATUS.OK.value
      }).then(({ data }) => {
        if (data && data.rows && data.rows.length) {
          this.vcsList = data.rows.map(s => {
            return {
              id: s.id,
              name: s.name
            }
          })
        }
      })
    },
    check() {
      this.loading = true
      this.$emit('loading', true)
      this.form.validateFields((err, values) => {
        if (err) {
          this.loading = false
          this.$emit('loading', false)
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
          res = await this.$api.addApp({ ...values })
        } else {
          // 修改
          res = await this.$api.updateApp({
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
      this.$emit('loading', false)
    },
    close() {
      this.loading = false
      this.$emit('loading', false)
      this.$emit('close')
    }
  },
  async mounted() {
    await this.getVcsList()
  }
}
</script>

<style scoped>
.reload-vcs {
  margin-left: 16px;
  font-size: 16px;
}
</style>
