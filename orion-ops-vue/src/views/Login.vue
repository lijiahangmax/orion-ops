<template>
  <div class="login-box">
    <a-form-model :model="form" :rules="rules" ref="form" class="login-form" @submit="handleLoginSubmit">
      <!-- 用户名 -->
      <a-form-model-item prop="username">
        <a-input v-decorator="rules.username" v-model="form.username" placeholder="用户名">
          <a-icon slot="prefix" type="user" style="color: rgba(0,0,0,.25)"/>
        </a-input>
      </a-form-model-item>
      <!-- 密码 -->
      <a-form-model-item prop="password">
        <a-input-password v-decorator="rules.password" v-model="form.password" placeholder="密码">
          <a-icon slot="prefix" type="lock" style="color: rgba(0,0,0,.25)"/>
        </a-input-password>
      </a-form-model-item>
      <!-- 按钮 -->
      <a-form-model-item>
        <a-button type="primary" :disabled="isSubmit" html-type="submit" class="login-form-submit-button">
          登陆
        </a-button>
      </a-form-model-item>
    </a-form-model>
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
@input_width: 320px;

.login-box {
  text-align: center !important;
  height: 100vh;
  width: 100%;
  background: -webkit-linear-gradient(#c3fae8, #e5dbff);
  background: -o-linear-gradient(#c3fae8, #e5dbff);
  background: -moz-linear-gradient(#c3fae8, #e5dbff);
  background: linear-gradient(#c3fae8, #e5dbff);
}

.login-form {
  text-align: left;
  display: inline-block;
  margin-top: 15%;
  width: @input_width;
}

.login-form-submit-button {
  width: @input_width;
}

</style>
