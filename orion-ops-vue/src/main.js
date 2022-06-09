import Vue from 'vue'
import App from './App'
import router from './router'
import $api from './lib/api'
import $storage from './lib/storage'
import { copyToClipboard } from '@/lib/utils'
import './lib/directive'

import ant from 'ant-design-vue'
import $message from 'ant-design-vue/lib/message'
import 'ant-design-vue/dist/antd.min.css'

Vue.use(ant)

Vue.prototype.$api = $api
Vue.prototype.$storage = $storage

Vue.config.productionTip = false

$message.config({
  maxCount: 1
})

router.beforeEach((to, from, next) => {
  // 校验登陆
  if (to.meta.requireAuth !== false && !$storage.get($storage.keys.LOGIN_TOKEN)) {
    router.push({ path: '/login' })
    return
  }
  // 校验管理员
  if (to.meta.requireAdmin && !Vue.prototype.$isAdmin()) {
    router.push({ path: '/404' })
    return
  }
  // 设置标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

/**
 * 复制
 */
Vue.prototype.$copy = function(value, tips = '已复制') {
  if (copyToClipboard(value) && tips) {
    if (tips === true) {
      this.$message.success(value + ' 已复制')
    } else {
      this.$message.success(tips)
    }
  }
}

/**
 * 是否是管理员
 */
Vue.prototype.$isAdmin = function() {
  try {
    return JSON.parse($storage.get($storage.keys.CURRENT_USER)).roleType === 10
  } catch (e) {
    return false
  }
}

/**
 * 获取当前userId
 */
Vue.prototype.$getUserId = function() {
  try {
    return JSON.parse($storage.get($storage.keys.CURRENT_USER)).userId
  } catch (e) {
    return false
  }
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
