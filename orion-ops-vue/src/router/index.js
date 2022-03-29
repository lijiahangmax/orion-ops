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
    path: '/',
    redirect: '/console'
  },
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
    path: '/machine/terminal/:id?',
    name: 'terminal',
    meta: {
      requireAuth: true,
      title: '终端'
    },
    component: () => import('../views/machine/MachineTerminal')
  },
  {
    path: '/machine/sftp/:id?',
    name: 'sftp',
    meta: {
      requireAuth: true,
      title: '文件管理器'
    },
    component: () => import('../views/machine/MachineSftp')
  },
  {
    path: '/batch/exec/log/view/:id',
    name: 'ExecLoggerView',
    meta: {
      requireAuth: true,
      title: '执行日志'
    },
    component: () => import('../views/exec/ExecLoggerView')
  },
  {
    path: '/log/view/:id?',
    name: 'loggerView',
    meta: {
      requireAuth: true,
      title: '日志面板'
    },
    component: () => import('../views/log/LoggerView')
  },
  {
    path: '/app/build/log/view/:id',
    name: 'AppBuildLogView',
    meta: {
      requireAuth: true,
      title: '构建日志'
    },
    component: () => import('../views/app/AppBuildLogView')
  },
  {
    path: '/app/release/log/view/:id',
    name: 'AppReleaseLogView',
    meta: {
      requireAuth: true,
      title: '发布日志'
    },
    component: () => import('../views/app/AppReleaseLogView')
  },
  {
    path: '/app/release/machine/log/view/:id',
    name: 'AppReleaseMachineLogView',
    meta: {
      requireAuth: true,
      title: '发布日志'
    },
    component: () => import('../views/app/AppReleaseMachineLogView')
  },
  {
    path: '/task/machine/log/view/:id',
    name: 'SchedulerMachineLogView',
    meta: {
      requireAuth: true,
      title: '调度日志'
    },
    component: () => import('../views/scheduler/SchedulerMachineLogView')
  },
  {
    path: '/task/log/view/:id',
    name: 'SchedulerTaskLogView',
    meta: {
      requireAuth: true,
      title: '调度日志'
    },
    component: () => import('../views/scheduler/SchedulerTaskLogView')
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
          title: '控制台',
          visibleProfile: true
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
        path: '/machine/env/:id?',
        name: 'machineEnv',
        meta: {
          requireAuth: true,
          title: '环境变量',
          leftProps: [{
            icon: 'arrow-left',
            title: '返回',
            call: 'back'
          }]
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
        path: '/terminal/session',
        name: 'terminalSession',
        meta: {
          requireAuth: true,
          requireAdmin: true,
          title: '终端控制'
        },
        component: () => import('../views/machine/MachineTerminalSession')
      },
      {
        path: '/terminal/logs',
        name: 'terminalLogs',
        meta: {
          requireAuth: true,
          title: '终端日志'
        },
        component: () => import('../views/machine/MachineTerminalLogs')
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
        path: '/batch/exec/list',
        name: 'BatchExecList',
        meta: {
          requireAuth: true,
          title: '批量执行'
        },
        component: () => import('../views/exec/BatchExecList')
      },
      {
        path: '/batch/exec/add',
        name: 'BatchExecAdd',
        component: () => import('../views/exec/AddBatchExecTask')
      },
      {
        path: '/log/list',
        name: 'loggerList',
        meta: {
          requireAuth: true,
          title: '日志列表'
        },
        component: () => import('../views/log/LoggerList')
      },
      {
        path: '/app/list',
        name: 'appList',
        meta: {
          requireAuth: true,
          title: '应用列表',
          visibleProfile: true
        },
        component: () => import('../views/app/AppList')
      },
      {
        path: '/app/conf/:appId',
        name: 'appConfig',
        meta: {
          requireAuth: true,
          title: '应用配置',
          visibleProfile: true,
          leftProps: [{
            icon: 'arrow-left',
            title: '返回',
            call: 'back'
          }, {
            icon: 'unordered-list',
            title: '选择模板',
            event: 'openTemplate'
          }]
        },
        component: () => import('../views/app/AppConfig')
      },
      {
        path: '/app/profile',
        name: 'appProfile',
        meta: {
          requireAuth: true,
          title: '环境管理'
        },
        component: () => import('../views/app/AppProfile')
      },
      {
        path: '/app/env/:id?',
        name: 'appEnv',
        meta: {
          requireAuth: true,
          title: '环境变量',
          visibleProfile: true,
          leftProps: [{
            icon: 'arrow-left',
            title: '返回',
            call: 'back'
          }]
        },
        component: () => import('../views/app/AppEnv')
      },
      {
        path: '/app/vcs',
        name: 'appVcs',
        meta: {
          requireAuth: true,
          title: '版本仓库'
        },
        component: () => import('../views/app/AppVcs')
      },
      {
        path: '/app/build',
        name: 'appBuild',
        meta: {
          requireAuth: true,
          title: '应用构建',
          visibleProfile: true
        },
        component: () => import('../views/app/AppBuild')
      },
      {
        path: '/app/release',
        name: 'appRelease',
        meta: {
          requireAuth: true,
          title: '应用发布',
          visibleProfile: true
        },
        component: () => import('../views/app/AppRelease')
      },
      {
        path: '/user/list',
        name: 'userList',
        meta: {
          requireAuth: true,
          title: '用户列表'
        },
        component: () => import('../views/user/UserList')
      },
      {
        path: '/user/detail',
        name: 'userDetail',
        meta: {
          requireAuth: true,
          title: '基本信息'
        },
        component: () => import('../views/user/UserDetail')
      },
      {
        path: '/user/event/log/:id',
        name: 'simpleUserEventLog',
        meta: {
          requireAuth: true,
          title: '操作日志',
          leftProps: [{
            icon: 'arrow-left',
            title: '返回',
            call: 'back'
          }]
        },
        component: () => import('../views/user/SimpleUserEventLogList')
      },
      {
        path: '/user/event/logs',
        name: 'UserEventLogList',
        meta: {
          requireAuth: true,
          title: '操作日志'
        },
        component: () => import('../views/user/UserEventLogList')
      },
      {
        path: '/template/list',
        name: 'templateList',
        meta: {
          requireAuth: true,
          title: '模板配置'
        },
        component: () => import('../views/template/TemplateList')
      },
      {
        path: '/system/env',
        name: 'systemEnv',
        meta: {
          requireAuth: true,
          title: '环境变量'
        },
        component: () => import('../views/system/SystemEnv')
      },
      {
        path: '/system/setting',
        name: 'systemSetting',
        meta: {
          requireAuth: true,
          title: '系统设置'
        },
        component: () => import('../views/system/SystemSetting')
      },
      {
        path: '/scheduler/list',
        name: 'schedulerList',
        meta: {
          requireAuth: true,
          title: '调度任务'
        },
        component: () => import('../views/scheduler/SchedulerList')
      },
      {
        path: '/scheduler/record/:id?',
        name: 'schedulerRecord',
        meta: {
          requireAuth: true,
          title: '调度历史'
        },
        component: () => import('../views/scheduler/SchedulerRecord')
      },
      {
        path: '/scheduler/statistics/:id?',
        name: 'schedulerStatistic',
        meta: {
          requireAuth: true,
          title: '调度统计'
        },
        component: () => import('../views/scheduler/SchedulerStatistic')
      },
      {
        path: '*',
        name: '404',
        meta: {
          requireAuth: true,
          title: '404',
          leftProps: [{
            icon: 'arrow-left',
            title: '返回',
            call: 'back'
          }]
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
