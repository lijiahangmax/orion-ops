import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import $api from './lib/api'
import $http from './lib/http'
import $utils from './lib/utils'
import $storage from './lib/storage'
import $enum from './lib/enum'
import './lib/directive'

import ant from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'

Vue.use(ant)
Vue.prototype.$api = $api
Vue.prototype.$http = $http
Vue.prototype.$utils = $utils
Vue.prototype.$storage = $storage
Vue.prototype.$enum = $enum

Vue.config.productionTip = false

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  if (to.meta.requireAuth === false) {
    // 无需登陆
    next()
  } else {
    if ($storage.get($storage.keys.LOGIN_TOKEN)) {
      next()
    } else {
      router.push({ path: '/login' })
    }
  }
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
