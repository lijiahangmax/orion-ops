import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login'
import Layout from '../components/layout/Layout'

Vue.use(VueRouter)

// 重复点击路由不抛异常
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/login',
    name: 'login',
    meta: {
      requireAuth: false,
      title: '登陆'
    },
    component: Login
  },
  {
    path: '/machine/terminal/:id',
    name: 'terminal',
    meta: {
      requireAuth: true,
      title: 'terminal'
    },
    component: () => import('../views/machine/MachineTerminal')
  },
  {
    path: '/',
    redirect: '/console'
  },
  {
    path: '',
    name: 'layout',
    component: Layout,
    children: [
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
        path: '/machine/list',
        name: 'machineList',
        meta: {
          requireAuth: true,
          title: '机器列表'
        },
        component: () => import('../views/machine/MachineList')
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
        path: '*',
        name: '404',
        meta: {
          requireAuth: true,
          title: '404'
        },
        component: () => import('../views/404')
      }
    ]
  }

]

const router = new VueRouter({
  routes
})

export default router
