import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import MachineTerminal from '../views/machine/MachineTerminal.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'login',
    meta: {
      requireAuth: false,
      disableLayout: true,
      title: '登陆'
    },
    component: Login
  },
  {
    path: '/',
    name: 'index',
    meta: {
      requireAuth: true,
      title: '首页'
    },
    component: () => import('../views/Index.vue')
  },
  {
    path: '/console',
    name: 'console',
    meta: {
      requireAuth: true,
      title: '控制台'
    },
    component: () => import('../views/Console.vue')
  },
  {
    path: '/user/detail',
    name: 'userDetail',
    meta: {
      requireAuth: true,
      title: '用户详情'
    },
    component: () => import('../views/user/UserDetail.vue')
  },
  {
    path: '/machine/list',
    name: 'machineList',
    meta: {
      requireAuth: true,
      title: '机器列表'
    },
    component: () => import('../views/machine/MachineList.vue')
  },
  {
    path: '/machine/terminal',
    name: 'machineTerminal',
    meta: {
      requireAuth: true,
      disableLayout: true,
      title: 'terminal'
    },
    component: MachineTerminal
  },
  {
    path: '/machine/env',
    name: 'machineEnv',
    meta: {
      requireAuth: true,
      title: '机器环境变量'
    },
    component: () => import('../views/machine/MachineEnv.vue')
  }

]

const router = new VueRouter({
  routes
})

export default router
