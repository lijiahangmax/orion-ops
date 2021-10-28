import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login'
import MachineTerminal from '../views/machine/MachineTerminal'

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
    component: () => import('../views/Index')
  },
  {
    path: '/console',
    name: 'console',
    meta: {
      requireAuth: true,
      title: '控制台'
    },
    component: () => import('../views/Console')
  },
  {
    path: '/user/detail',
    name: 'userDetail',
    meta: {
      requireAuth: true,
      title: '用户详情'
    },
    component: () => import('../views/user/UserDetail')
  },
  {
    path: '/machine/list',
    name: 'machineList',
    meta: {
      requireAuth: true,
      title: '机器列表'
    },
    component: () => import('../views/machine/MachineList')
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
      title: '环境变量'
    },
    component: () => import('../views/machine/MachineEnv')
  },
  {
    path: '/machine/key',
    name: 'MachineKey',
    meta: {
      requireAuth: true,
      title: '机器秘钥'
    },
    component: () => import('../views/machine/MachineKey')
  },
  {
    path: '/machine/proxy',
    name: 'MachineProxy',
    meta: {
      requireAuth: true,
      title: '机器代理'
    },
    component: () => import('../views/machine/MachineProxy')
  }

]

const router = new VueRouter({
  routes
})

export default router
