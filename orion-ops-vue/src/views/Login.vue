<template>
  <div class="login-container">
    <div class="login-form-main">
      <div class="login-form-wrapper">
        <!-- 标题 -->
        <h3 class="login-label">用户登录</h3>
        <!-- 表单 -->
        <a-form-model class="login-form"
                      :model="form"
                      :rules="rules"
                      ref="form"
                      @submit="handleLoginSubmit">
          <!-- 用户名 -->
          <a-form-model-item prop="username">
            <a-input size="large"
                     placeholder="用户名"
                     v-decorator="rules.username"
                     v-model="form.username"
                     allowClear>
              <template #prefix>
                <a-icon type="user" style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input>
          </a-form-model-item>
          <!-- 密码 -->
          <a-form-model-item prop="password">
            <a-input size="large"
                     type="password"
                     placeholder="密码"
                     v-decorator="rules.password"
                     v-model="form.password"
                     allowClear>
              <template #prefix>
                <a-icon type="lock" style="color: rgba(0,0,0,.25)"/>
              </template>
            </a-input>
          </a-form-model-item>
          <!-- 按钮 -->
          <a-form-model-item>
            <a-button class="login-form-submit-button" type="primary" :disabled="isSubmit" html-type="submit">
              登录
            </a-button>
          </a-form-model-item>
        </a-form-model>
      </div>
    </div>
  </div>
</template>

<script>

const rules = {
  username: [{
    required: true,
    whitespace: true,
    message: '请输入用户名!'
  }],
  password: [{
    required: true,
    message: '请输入密码!'
  }]
}

export default {
  data: function() {
    return {
      rules,
      isSubmit: false,
      form: {
        username: null,
        password: null
      }
    }
  },
  methods: {
    async handleLoginSubmit(e) {
      e.preventDefault()
      this.isSubmit = true
      let valid = false
      try {
        valid = await this.$refs.form.validate()
      } catch (e) {
        // ignore
      }
      if (!valid) {
        this.isSubmit = false
        return
      }
      this.$api.login({
        username: this.form.username.trim(),
        password: this.$utils.md5(this.form.password.trim())
      }).then(({ data }) => {
        this.isSubmit = true
        const user = {
          userId: data.userId,
          username: data.username,
          nickname: data.nickname,
          roleType: data.roleType
        }
        this.$storage.set(this.$storage.keys.LOGIN_TOKEN, data.token)
        this.$storage.set(this.$storage.keys.USER, JSON.stringify(user))
        this.$router.push({ path: '/' })
      }).catch(() => {
        this.isSubmit = false
      })
    }
  }
}
</script>

<style lang="less" scoped>

.login-container {
  position: relative;
  height: 100vh;
  background: #F2F4F7;
  border-top: 1px solid #F2F4F7;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;

  .login-form-main {
    position: absolute;
    top: 50%;
    left: 70%;
    -webkit-transform: translate(-50%, -50%);
    -moz-transform: translate(-50%, -50%);
    -ms-transform: translate(-50%, -50%);
    -o-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    z-index: 5;
    margin: 0 auto;
    -webkit-border-radius: 8px;
    -moz-border-radius: 8px;
    border-radius: 8px;
  }
}

.login-form-wrapper {
  padding: 32px 64px;
  width: 450px;
  height: 330px;
  overflow: hidden;
  box-shadow: 0 0 20px 0 #F1F3F5;
  background: #FFFFFF;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-border-radius: 16px;
  -moz-border-radius: 16px;
  border-radius: 16px;
  position: relative;

  .login-label {
    color: #181E33;
    font-size: 20px;
    margin-bottom: 24px;
    font-weight: normal;
  }

  .login-form {
    .login-form-submit-button {
      margin-top: 12px;
      width: 100%;
      height: 48px;
      background-image: url('../assets/login-btn.png');
      background-repeat: no-repeat;
      background-position: 0 0;
      background-size: cover;
      font-size: 18px;
      border: none;
    }
  }
}

</style>
