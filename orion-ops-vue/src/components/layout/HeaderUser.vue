<template>
  <a-dropdown>
    <a class="ant-dropdown-link" @click="e => e.preventDefault()">
      <a-avatar :src="user.avatar" :size="36"/>
    </a>
    <a-menu slot="overlay" @click="chooseMenu">
      <a-menu-item key="nickname" id="user-nickname">
        <a-icon type="smile"/>
        {{user.nickname}}
      </a-menu-item>
      <a-menu-item key="userInfo">
        <a-icon type="user"/>
        个人中心
      </a-menu-item>
      <a-menu-item key="resetPassword">
        <a-icon type="safety-certificate"/>
        修改密码
        <ResetPassword ref="resetModel" @resetSuccess="resetSuccess"/>
      </a-menu-item>
      <a-menu-item key="logout">
        <a-icon type="export"/>
        退出登录
      </a-menu-item>
    </a-menu>
  </a-dropdown>
</template>

<script>
  import ResetPassword from '../user/ResetPassword'

  const menuItemHandler = {
    nickname() {
    },
    userInfo() {
      this.$router.push({ path: '/user/detail' })
    },
    resetPassword() {
      this.openResetModel()
    },
    logout() {
      this.$api.logout()
      this.$storage.clear()
      this.$router.push({ path: '/login' })
    }
  }

  export default {
    name: 'HeaderUser',
    components: { ResetPassword },
    data: function() {
      return {
        user: {
          nickname: '',
          avatar: ''
        }
      }
    },
    methods: {
      chooseMenu({ key }) {
        menuItemHandler[key].call(this)
      },
      openResetModel() {
        this.$refs.resetModel.showModal()
      },
      resetSuccess() {
        this.$storage.clear()
        this.$router.push('/login')
      }
    },
    mounted() {
      this.$api.getUserDetail()
        .then(({ data }) => {
          this.user.nickname = data.nickname
          this.user.avatar = data.avatar
        })
    }
  }
</script>

<style scoped>
  #user-nickname {
    color: #495057;
    padding-left: 12px;
    cursor: default;
  }
</style>
