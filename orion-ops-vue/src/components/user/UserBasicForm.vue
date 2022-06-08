<template>
  <div class="basic-user-info-container">
    <a-spin :spinning="userLoading">
      <div class="basic-user-info">
        <a-upload accept=".jpg,.jpeg,.png"
                  :beforeUpload="selectAvatarFile"
                  :customRequest="() => {}"
                  :showUploadList="false">
          <a-avatar class="pointer" :src="user.avatar" :size="96" title="更换头像"/>
        </a-upload>
        <!-- 用户信息表单 -->
        <a-form class="basic-user-form" :form="userForm" v-bind="userFormLayout">
          <a-form-item label="用户名">
            <a-input :disabled="true" v-model="user.username"/>
          </a-form-item>
          <a-form-item label="昵称">
            <a-input v-decorator="userBasicDecorators.nickname"/>
          </a-form-item>
          <a-form-item label="角色">
            <a-select :disabled="true" :value="user.role">
              <a-select-option :value="user.role" v-if="user.role">
                {{ $enum.valueOf($enum.ROLE_TYPE, user.role).label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="手机">
            <a-input v-decorator="userBasicDecorators.phone"/>
          </a-form-item>
          <a-form-item label="邮箱">
            <a-input v-decorator="userBasicDecorators.email"/>
          </a-form-item>
        </a-form>
      </div>
      <!-- 按钮 -->
      <div class="user-basic-buttons">
        <a-button @click="updateUserInfo" type="primary" :disabled="userLoading || user.id === null">修改</a-button>
      </div>
    </a-spin>
  </div>
</template>

<script>

import { pick } from 'lodash'
import { readFileBase64 } from '@/lib/utils'

const userFormLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 }
}

function getUserBasicDecorators() {
  return {
    nickname: ['nickname', {
      rules: [{
        required: true,
        message: '请输入昵称'
      }, {
        max: 32,
        message: '昵称长度不能大于32位'
      }]
    }],
    phone: ['phone', {
      rules: [{
        required: true,
        message: '请输入联系电话'
      }, {
        min: 11,
        max: 16,
        message: '请输入正确的联系电话'
      }]
    }],
    email: ['email', {
      rules: [{
        min: 8,
        max: 128,
        message: '联系邮箱长度必须在8-128位之间'
      }]
    }]
  }
}

export default {
  name: 'UserBasicForm',
  data() {
    return {
      user: {},
      userLoading: false,
      userFormLayout,
      userBasicDecorators: getUserBasicDecorators.call(this),
      userForm: this.$form.createForm(this)
    }
  },
  methods: {
    getUserInfo() {
      this.userLoading = true
      this.$api.getUserDetail({ id: this.$getUserId() })
        .then(({ data }) => {
          this.user = data
          const formFields = pick(Object.assign({}, data), 'nickname', 'phone', 'email')
          this.$nextTick(() => {
            this.userForm.setFieldsValue(formFields)
          })
          this.userLoading = false
        })
        .catch(() => {
          this.userLoading = false
        })
    },
    updateUserInfo() {
      this.userLoading = true
      this.userForm.validateFields((err, values) => {
        if (err) {
          this.userLoading = false
          return
        }
        // 修改
        this.$api.updateUser({
          ...values,
          id: this.id
        }).then(() => {
          this.userLoading = false
          this.getUserInfo()
        }).catch(() => {
          this.userLoading = false
        })
      })
    },
    async selectAvatarFile(e) {
      const suffix = e.name.substring(e.name.lastIndexOf('.') + 1)
      if (suffix !== 'jpg' && suffix !== 'jpeg' && suffix !== 'png') {
        this.$message.error('请选择 jpg jpeg png 类型的图片')
        return false
      }
      if (e.size > 1024 * 1024) {
        this.$message.error('请选择小于1M的图片')
        return false
      }
      this.userLoading = true
      const fileBase64 = await readFileBase64(e)
      this.$api.updateAvatar({
        avatar: fileBase64
      }).then(() => {
        this.userLoading = false
        this.user.avatar = fileBase64
      }).catch(() => {
        this.userLoading = false
        this.$message.error('上传失败')
      })
      return false
    }
  },
  mounted() {
    this.getUserInfo()
  }
}
</script>

<style lang="less" scoped>

.basic-user-info-container {
  padding-bottom: 25px;
  width: 400px;

  .basic-user-info {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 400px;
    padding-top: 16px;

    .basic-user-form {
      width: 360px;
      padding: 16px 16px 0 0;

      /deep/ .ant-form-item {
        margin: 6px 0;
        display: flex;
      }

      /deep/ .ant-form-item-control-wrapper {
        flex: 0.9;
      }
    }
  }

  .user-basic-buttons {
    padding: 8px 8px 8px 77px;
  }
}

</style>
