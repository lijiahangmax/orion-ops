<template>
  <a-spin :spinning="loading">
    <a-form :form="form" v-bind="layout">
      <a-form-item label="名称" hasFeedback>
        <a-input v-decorator="decorators.name" allowClear/>
      </a-form-item>
      <a-form-item label="唯一标识" hasFeedback>
        <a-input v-decorator="decorators.tag" allowClear/>
      </a-form-item>
      <a-form-item label="版本仓库">
        <a-select placeholder="请选择" v-decorator="decorators.repoId" style="width: calc(100% - 45px)" allowClear>
          <a-select-option v-for="repo in repoList" :key="repo.id" :value="repo.id">
            <span>{{ repo.name }}</span>
          </a-select-option>
        </a-select>
        <a class="reload-repo" title="刷新" @click="getRepositoryList">
          <a-icon type="reload"/>
        </a>
      </a-form-item>
      <a-form-item label="描述" style="margin-bottom: 0;">
        <a-textarea v-decorator="decorators.description" allowClear/>
      </a-form-item>
    </a-form>
  </a-spin>
</template>

<script>
import { pick } from 'lodash'
import { REPOSITORY_STATUS } from '@/lib/enum'

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
        message: '请输入唯一标识'
      }, {
        max: 32,
        message: '唯一标识长度不能大于32位'
      }]
    }],
    repoId: ['repoId'],
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
          labelCol: { span: 4 },
          wrapperCol: { span: 17 }
        }
      }
    }
  },
  data: function() {
    return {
      id: null,
      loading: false,
      record: null,
      repoList: [],
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
      if (!row.repoId) {
        row.repoId = undefined
      }
      this.record = pick(Object.assign({}, row), 'name', 'tag', 'repoId', 'description')
      this.$nextTick(() => {
        this.form.setFieldsValue(this.record)
      })
    },
    async getRepositoryList() {
      this.$api.getRepositoryList({
        limit: 10000,
        status: REPOSITORY_STATUS.OK.value
      }).then(({ data }) => {
        if (data && data.rows && data.rows.length) {
          this.repoList = data.rows.map(s => {
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
    await this.getRepositoryList()
  }
}
</script>

<style scoped>
.reload-repo {
  margin-left: 16px;
  font-size: 16px;
}
</style>
