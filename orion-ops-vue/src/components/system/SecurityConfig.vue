<template>
  <div class="security-container">
    <div class="security-title normal-title">
      安全配置
    </div>
    <div class="option-form-wrapper">
      <a-form-model v-bind="layout">
        <!-- 允许多端登陆 -->
        <a-form-model-item label="允许多端登陆">
          <a-switch v-model="option.allowMultipleLogin"
                    :loading="loading.allowMultipleLogin"
                    checkedChildren="启用"
                    unCheckedChildren="停用"
                    @change="changeOption($enum.SYSTEM_OPTION_KEY.ALLOW_MULTIPLE_LOGIN)"/>
          <template #extra>
            <span class="help-text">
              开启后一个账号可以多个设备同时登陆, 其他设备强制登陆后不会强制下线
            </span>
          </template>
        </a-form-model-item>
        <!-- 登陆失败锁定 -->
        <a-form-model-item label="登陆失败锁定">
          <a-switch v-model="option.loginFailureLock"
                    :loading="loading.loginFailureLock"
                    checkedChildren="启用"
                    unCheckedChildren="停用"
                    @change="changeOption($enum.SYSTEM_OPTION_KEY.LOGIN_FAILURE_LOCK)"/>
          <template #extra>
            <span class="help-text">
              开启后同个账号多次登陆失败后将会锁定账号无法继续登陆
            </span>
          </template>
        </a-form-model-item>
        <!-- 登陆IP绑定 -->
        <a-form-model-item label="登陆IP绑定">
          <a-switch v-model="option.loginIpBind"
                    :loading="loading.loginIpBind"
                    checkedChildren="启用"
                    unCheckedChildren="停用"
                    @change="changeOption($enum.SYSTEM_OPTION_KEY.LOGIN_IP_BIND)"/>
          <template #extra>
            <span class="help-text">
              开启后会将登陆凭证和IP进行绑定, 其他IP使用此登陆凭证则无法访问
            </span>
          </template>
        </a-form-model-item>
        <!-- 凭证自动续签 -->
        <a-form-model-item label="登陆凭证自动续签">
          <a-switch v-model="option.loginTokenAutoRenew"
                    :loading="loading.loginTokenAutoRenew"
                    checkedChildren="启用"
                    unCheckedChildren="停用"
                    @change="changeOption($enum.SYSTEM_OPTION_KEY.LOGIN_TOKEN_AUTO_RENEW)"/>
          <template #extra>
            <span class="help-text">
              开启后当操作时间间隔超过自动续签阈值时, 登陆凭证将会自动续签
            </span>
          </template>
        </a-form-model-item>
        <!-- 凭证有效期 -->
        <a-form-model-item label="凭证有效期(时)">
          <a-input-search class="option-input"
                          v-model="option.loginTokenExpire"
                          v-limit-integer
                          placeholder="登陆凭证有效时长"
                          :disabled="loading.loginTokenExpire"
                          :loading="loading.loginTokenExpire"
                          @search="changeOption($enum.SYSTEM_OPTION_KEY.LOGIN_TOKEN_EXPIRE)">
            <template #enterButton>
              <a-icon type="check"/>
            </template>
          </a-input-search>
          <template #extra>
            <span class="help-text">
              设置登陆凭证有效期时长(时), 设置后下次登陆生效
            </span>
          </template>
        </a-form-model-item>
        <!-- 登陆失败锁定阈值 -->
        <a-form-model-item label="登陆失败锁定阈值">
          <a-input-search class="option-input"
                          v-model="option.loginFailureLockThreshold"
                          v-limit-integer
                          placeholder="锁定阈值(次)"
                          :disabled="loading.loginFailureLockThreshold"
                          :loading="loading.loginFailureLockThreshold"
                          @search="changeOption($enum.SYSTEM_OPTION_KEY.LOGIN_FAILURE_LOCK_THRESHOLD)">
            <template #enterButton>
              <a-icon type="check"/>
            </template>
          </a-input-search>
          <template #extra>
            <span class="help-text">
              开启登陆失败锁定后, 登陆失败次数到达该值时账号会自动锁定
            </span>
          </template>
        </a-form-model-item>
        <!-- 登陆自动续签阈值 -->
        <a-form-model-item label="登陆自动续签阈值">
          <a-input-search class="option-input"
                          v-model="option.loginTokenAutoRenewThreshold"
                          v-limit-integer
                          placeholder="续签阈值(时)"
                          :disabled="loading.loginTokenAutoRenewThreshold"
                          :loading="loading.loginTokenAutoRenewThreshold"
                          @search="changeOption($enum.SYSTEM_OPTION_KEY.LOGIN_TOKEN_AUTO_RENEW_THRESHOLD)">
            <template #enterButton>
              <a-icon type="check"/>
            </template>
          </a-input-search>
          <template #extra>
            <span class="help-text">
              登陆凭证自动续签间隔(时)
            </span>
          </template>
        </a-form-model-item>
      </a-form-model>
    </div>
  </div>
</template>

<script>
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 }
}

export default {
  name: 'SecurityConfig',
  data() {
    return {
      layout,
      option: {
        allowMultipleLogin: false,
        loginFailureLock: false,
        loginIpBind: false,
        loginTokenAutoRenew: false,
        loginTokenExpire: null,
        loginFailureLockThreshold: null,
        loginTokenAutoRenewThreshold: null
      },
      loading: {
        allowMultipleLogin: false,
        loginFailureLock: false,
        loginIpBind: false,
        loginTokenAutoRenew: false,
        loginTokenExpire: null,
        loginFailureLockThreshold: null,
        loginTokenAutoRenewThreshold: null
      }
    }
  },
  methods: {
    changeOption(option) {
      this.loading[option.key] = true
      this.$api.updateSystemOption({
        option: option.value,
        value: this.option[option.key]
      }).then(() => {
        this.loading[option.key] = false
        this.$message.success('已保存')
      }).catch(() => {
        this.loading[option.key] = false
      })
    }
  },
  mounted() {
    this.$api.getSystemOptions().then(({ data }) => {
      this.option = data
    })
  }
}
</script>

<style lang="less" scoped>

.security-title {
  margin: 16px 0 0 16px;
}

.option-form-wrapper {
  width: 590px;
  margin: 24px;

  /deep/ .ant-form-item {
    margin: 0 0 18px 0;
  }

  .option-input {
    width: 200px;
  }
}

</style>
