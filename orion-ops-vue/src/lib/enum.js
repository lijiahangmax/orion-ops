/**
 * 用 value 获取枚举值
 */
export function enumValueOf(e, value) {
  for (const key in e) {
    const val = e[key]
    if (val && val.value === value) {
      return val
    }
  }
  return {}
}

/**
 * 终端操作
 */
export const TERMINAL_OPERATOR = {
  KEY: {
    value: '0'
  },
  CONNECT: {
    value: '1'
  },
  PING: {
    value: '2'
  },
  RESIZE: {
    value: '3'
  },
  COMMAND: {
    value: '4'
  },
  CLEAR: {
    value: '5'
  }
}

/**
 * 终端状态
 */
export const TERMINAL_STATUS = {
  NOT_CONNECT: {
    value: 0,
    label: '未连接',
    color: '#FFD43B'
  },
  CONNECTED: {
    value: 20,
    label: '已连接',
    color: '#4DABF7'
  },
  DISCONNECTED: {
    value: 30,
    label: '已断开',
    color: '#ADB5BD'
  },
  ERROR: {
    value: 40,
    label: '错误',
    color: '#E03131'
  }
}

/**
 * WS protocol
 */
export const WS_PROTOCOL = {
  OK: {
    value: '0'
  },
  CONNECTED: {
    value: '1'
  },
  PONG: {
    value: '2'
  },
  ERROR: {
    value: '3'
  }
}

/**
 * 文件类型
 */
export const FILE_TYPE = {
  NORMAL_FILE: {
    value: '-',
    label: '普通文件',
    icon: 'file'
  },
  DIRECTORY: {
    value: 'd',
    label: '目录',
    icon: 'folder'
  },
  LINK_FILE: {
    value: 'l',
    label: '链接文件',
    icon: 'link'
  },
  MANAGE_FILE: {
    value: 'p',
    label: '管理文件',
    icon: 'dhh'
  },
  BLOCK_DEVICE_FILE: {
    value: 'b',
    label: '块设备文件',
    icon: 'dhh'
  },
  CHARACTER_DEVICE_FILE: {
    value: 'c',
    label: '字符设备文件',
    icon: 'dhh'
  },
  SOCKET_FILE: {
    value: 's',
    label: '套接字文件',
    icon: 'dhh'
  }
}

/**
 * 启用状态
 */
export const ENABLE_STATUS = {
  ENABLE: {
    value: 1,
    label: '启用',
    status: 'processing'
  },
  DISABLE: {
    value: 2,
    label: '停用',
    status: 'default'
  }
}

/**
 * 启用状态
 */
export const AUDIT_STATUS = {
  RESOLVE: {
    value: 10,
    label: '通过'
  },
  REJECT: {
    value: 20,
    label: '驳回'
  }
}

/**
 * 应用环境审核状态
 */
export const PROFILE_AUDIT_STATUS = {
  ENABLE: {
    value: 1,
    label: '需要审核',
    color: 'blue'
  },
  DISABLE: {
    value: 2,
    label: '无需审核',
    color: 'green'
  }
}

/**
 * 机器认证类型
 */
export const MACHINE_AUTH_TYPE = {
  PASSWORD: {
    value: 1,
    label: '密码认证',
    color: '#845EF7'
  },
  KEY: {
    value: 2,
    label: '秘钥认证',
    color: '#5C7CFA'
  }
}

/**
 * 机器代理方式
 */
export const MACHINE_PROXY_TYPE = {
  HTTP: {
    value: 1,
    label: 'http',
    color: '#228BE6'
  },
  SOCKS4: {
    value: 2,
    label: 'socks4',
    color: '#1098AD'
  },
  SOCKS5: {
    value: 3,
    label: 'socks5',
    color: '#0CA678'
  }
}

/**
 * 秘钥挂载状态
 */
export const MACHINE_KEY_MOUNT_STATUS = {
  NOT_FOUND: {
    value: 1,
    label: '未找到',
    color: '#DEE2E6'
  },
  MOUNTED: {
    value: 2,
    label: '已挂载',
    color: '#4C6EF5'
  },
  C: {
    value: 3,
    label: '未挂载',
    color: '#FF922B'
  }
}

/**
 * 历史值类型
 */
export const HISTORY_VALUE_TYPE = {
  MACHINE_ENV: {
    value: 10,
    label: '机器环境变量'
  },
  APP_ENV: {
    value: 20,
    label: '应用环境变量'
  },
  SYSTEM_ENV: {
    value: 30,
    label: '系统环境变量'
  }
}

/**
 * 历史值操作类型
 */
export const HISTORY_VALUE_OPTION_TYPE = {
  INSERT: {
    value: 1,
    label: '新增',
    color: '#94D82D'
  },
  UPDATE: {
    value: 2,
    label: '修改',
    color: '#4C6EF5'
  },
  DELETE: {
    value: 3,
    label: '删除',
    color: '#FF922B'
  }
}

/**
 * sftp 传输类型
 */
export const SFTP_TRANSFER_TYPE = {
  UPLOAD: {
    value: 10,
    label: '上传'
  },
  DOWNLOAD: {
    value: 20,
    label: '下载'
  },
  TRANSFER: {
    value: 30,
    label: '传输'
  },
  PACKAGE: {
    value: 40,
    label: '打包'
  }
}

/**
 * sftp 传输状态
 */
export const SFTP_TRANSFER_STATUS = {
  WAIT: {
    value: 10,
    label: '等待中',
    color: '#FF922B'
  },
  RUNNABLE: {
    value: 20,
    label: '进行中',
    color: '#5C7CFA'
  },
  PAUSE: {
    value: 30,
    label: '已暂停',
    color: '#CED4DA'
  },
  FINISH: {
    value: 40,
    label: '已完成',
    color: '#51CF66'
  },
  CANCEL: {
    value: 50,
    label: '已取消',
    color: '#868E96'
  },
  ERROR: {
    value: 60,
    label: '失败',
    color: '#F03E3E'
  }
}

/**
 * 批量执行
 */
export const BATCH_EXEC_STATUS = {
  WAITING: {
    value: 10,
    label: '未开始',
    color: ''
  },
  RUNNABLE: {
    value: 20,
    label: '执行中',
    color: 'green'
  },
  COMPLETE: {
    value: 30,
    label: '已完成',
    color: 'blue'
  },
  EXCEPTION: {
    value: 40,
    label: '异常',
    color: 'red'
  },
  TERMINATED: {
    value: 50,
    label: '已停止',
    color: 'orange'
  }
}

/**
 * 文件 tail 模式
 */
export const FILE_TAIL_MODE = {
  TRACKER: {
    value: 'tracker',
    label: 'tracker',
    tips: '本地文件推荐选择 (IO)'
  },
  TAIL: {
    value: 'tail',
    label: 'tail',
    tips: '远程文件必须选择 (命令)'
  }
}

/**
 * 文件 tail 类型
 */
export const FILE_TAIL_TYPE = {
  /**
   * 命令执行日志
   */
  EXEC_LOG: {
    value: 10
  },
  /**
   * tail列表
   */
  TAIL_LIST: {
    value: 20
  },
  /**
   * 应用构建日志
   */
  APP_BUILD_LOG: {
    value: 30
  },
  /**
   * 应用发布日志
   */
  APP_RELEASE_LOG: {
    value: 40
  },
  /**
   * 调度任务机器日志
   */
  SCHEDULER_TASK_MACHINE_LOG: {
    value: 50
  },
  /**
   * 应用操作日志
   */
  APP_ACTION_LOG: {
    value: 60
  }
}

/**
 * 日志 tail 状态
 */
export const LOG_TAIL_STATUS = {
  WAITING: {
    value: 10,
    label: '等待',
    status: 'warning'
  },
  RUNNABLE: {
    value: 20,
    label: '正常',
    status: 'processing'
  },
  ERROR: {
    value: 30,
    label: '错误',
    status: 'error'
  },
  CLOSE: {
    value: 40,
    label: '关闭',
    status: 'default'
  }
}

/**
 * 文件下载类型
 */
export const FILE_DOWNLOAD_TYPE = {
  /**
   * 秘钥
   */
  SECRET_KEY: {
    value: 10
  },
  /**
   * 终端录屏
   */
  TERMINAL_SCREEN: {
    value: 20
  },
  /**
   * 命令执行日志
   */
  EXEC_LOG: {
    value: 30
  },
  /**
   * sftp下载文件
   */
  SFTP_DOWNLOAD: {
    value: 40
  },
  /**
   * tail列表文件
   */
  TAIL_LIST_FILE: {
    value: 50
  },
  /**
   * 应用构建日志
   */
  APP_BUILD_LOG: {
    value: 60
  },
  /**
   * 应用操作日志
   */
  APP_ACTION_LOG: {
    value: 70
  },
  /**
   * 应用构建产物文件
   */
  APP_BUILD_BUNDLE: {
    value: 80
  },
  /**
   * 应用发布机器日志
   */
  APP_RELEASE_MACHINE_LOG: {
    value: 90
  },
  /**
   * 调度任务机器日志
   */
  SCHEDULER_TASK_MACHINE_LOG: {
    value: 110
  }
}

/**
 * 角色类型
 */
export const ROLE_TYPE = {
  ADMINISTRATOR: {
    value: 10,
    label: '管理员'
  },
  DEVELOPER: {
    value: 20,
    label: '开发'
  },
  OPERATION: {
    value: 30,
    label: '运维'
  }
}

/**
 * 视图类型
 */
export const VIEW_TYPE = {
  TABLE: {
    value: 0,
    name: '表格',
    lang: null
  },
  JSON: {
    value: 10,
    name: 'json',
    lang: 'json'
  },
  XML: {
    value: 20,
    name: 'xml',
    lang: 'xml'
  },
  YML: {
    value: 30,
    name: 'yml',
    lang: 'yaml'
  },
  PROPERTIES: {
    value: 40,
    name: 'properties',
    lang: 'properties'
  }
}

/**
 * 仓库状态
 */
export const REPOSITORY_STATUS = {
  UNINITIALIZED: {
    value: 10,
    label: '未初始化',
    color: ''
  },
  INITIALIZING: {
    value: 20,
    label: '未初始中',
    color: 'green'
  },
  OK: {
    value: 30,
    label: '正常',
    color: 'blue'
  },
  ERROR: {
    value: 40,
    label: '失败',
    color: 'red'
  }
}

/**
 * 仓库认证方式
 */
export const REPOSITORY_AUTH_TYPE = {
  PASSWORD: {
    value: 10,
    label: '密码认证'
  },
  TOKEN: {
    value: 20,
    label: '私人令牌'
  }
}

/**
 * 仓库令牌类型
 */
export const REPOSITORY_TOKEN_TYPE = {
  GITHUB: {
    value: 10,
    label: 'github',
    description: 'Settings -> Developer settings -> Personal access tokens'
  },
  GITEE: {
    value: 20,
    label: 'gitee',
    description: '设置 -> 私人令牌'
  },
  GITLAB: {
    value: 30,
    label: 'gitlab',
    description: 'User Settings -> Access Tokens'
  }
}

/**
 * 配置类型
 */
export const CONFIG_STATUS = {
  CONFIGURED: {
    value: 1,
    label: '已配置',
    color: 'blue'
  },
  NOT_CONFIGURED: {
    value: 2,
    label: '未配置',
    color: 'green'
  }
}

/**
 * 构建操作类型
 */
export const BUILD_ACTION_TYPE = {
  CHECKOUT: {
    value: 110,
    label: '检出'
  },
  COMMAND: {
    value: 120,
    label: '主机命令'
  }
}

/**
 * 发布操作类型
 */
export const RELEASE_ACTION_TYPE = {
  TRANSFER: {
    value: 210,
    label: '传输'
  },
  COMMAND: {
    value: 220,
    label: '目标主机命令'
  }
}

/**
 * 序列类型
 */
export const SERIAL_TYPE = {
  SERIAL: {
    value: 10,
    label: '串行'
  },
  PARALLEL: {
    value: 20,
    label: '并行'
  }
}

/**
 * 发布产物传输方式
 */
export const RELEASE_TRANSFER_MODE = {
  SCP: {
    value: 'scp',
    label: 'SCP'
  },
  SFTP: {
    value: 'sftp',
    label: 'SFTP'
  }
}

/**
 * 发布产物传输类型
 */
export const RELEASE_TRANSFER_FILE_TYPE = {
  NORMAL: {
    value: 'normal',
    label: '文件 / 文件夹'
  },
  ZIP: {
    value: 'zip',
    label: '文件夹zip'
  }
}

/**
 * 构建状态
 */
export const BUILD_STATUS = {
  WAIT: {
    value: 10,
    color: '',
    label: '未开始',
    stepStatus: 'wait'
  },
  RUNNABLE: {
    value: 20,
    color: 'green',
    label: '进行中',
    stepStatus: 'process'
  },
  FINISH: {
    value: 30,
    color: 'blue',
    label: '已完成',
    stepStatus: 'finish'
  },
  FAILURE: {
    value: 40,
    color: 'red',
    label: '已失败',
    stepStatus: 'error'
  },
  TERMINATED: {
    value: 50,
    color: 'orange',
    label: '已停止',
    stepStatus: 'finish'
  }
}

/**
 * 操作状态
 */
export const ACTION_STATUS = {
  WAIT: {
    value: 10,
    color: '',
    label: '未开始',
    stepStatus: 'wait',
    log: false,
    actionStyle: {
      background: '#CED4DA'
    },
    actionValue() {
      return '未开始'
    }
  },
  RUNNABLE: {
    value: 20,
    color: 'green',
    label: '进行中',
    stepStatus: 'process',
    log: true,
    actionStyle: {
      background: '#94D82D'
    },
    actionValue() {
      return '进行中'
    }
  },
  FINISH: {
    value: 30,
    color: 'blue',
    label: '已完成',
    stepStatus: 'finish',
    log: true,
    actionStyle: {
      background: '#74C0FC'
    },
    actionValue(log) {
      return log.usedInterval || '已完成'
    }
  },
  FAILURE: {
    value: 40,
    color: 'red',
    label: '已失败',
    stepStatus: 'error',
    log: true,
    actionStyle: {
      background: '#F03E3E'
    },
    actionValue(log) {
      return log.usedInterval || '已失败'
    }
  },
  SKIPPED: {
    value: 50,
    color: 'orange',
    label: '已跳过',
    stepStatus: 'wait',
    log: false,
    actionStyle: {
      background: '#FFD43B'
    },
    actionValue() {
      return '已跳过'
    }
  },
  TERMINATED: {
    value: 60,
    color: 'orange',
    label: '已停止',
    stepStatus: 'wait',
    log: true,
    actionStyle: {
      background: '#FFA94D'
    },
    actionValue() {
      return '已停止'
    }
  }
}

/**
 * 发布状态
 */
export const RELEASE_STATUS = {
  WAIT_AUDIT: {
    value: 10,
    color: '',
    label: '待审核'
  },
  AUDIT_REJECT: {
    value: 20,
    color: 'orange',
    label: '已驳回'
  },
  WAIT_RUNNABLE: {
    value: 30,
    color: 'cyan',
    label: '待发布'
  },
  WAIT_SCHEDULE: {
    value: 35,
    color: 'cyan',
    label: '待调度'
  },
  RUNNABLE: {
    value: 40,
    color: 'green',
    label: '发布中'
  },
  FINISH: {
    value: 50,
    color: 'blue',
    label: '已完成'
  },
  TERMINATED: {
    value: 60,
    color: 'orange',
    label: '已停止'
  },
  FAILURE: {
    value: 70,
    color: 'red',
    label: '已失败'
  }
}

/**
 * 发布类型
 */
export const RELEASE_TYPE = {
  NORMAL: {
    value: 10,
    label: '正常发布'
  },
  ROLLBACK: {
    value: 20,
    label: '回滚发布'
  }
}

/**
 * 发布状态
 */
export const TIMED_TYPE = {
  NORMAL: {
    value: 10,
    releaseLabel: '普通发布',
    execLabel: '普通执行'
  },
  TIMED: {
    value: 20,
    releaseLabel: '定时发布',
    execLabel: '定时执行'
  }
}

/**
 * 操作阶段类型
 */
export const STAGE_TYPE = {
  BUILD: {
    value: 10,
    symbol: 'build',
    label: '构建'
  },
  RELEASE: {
    value: 20,
    symbol: 'release',
    label: '发布'
  }
}

/**
 * 操作日志分类
 */
export const EVENT_CLASSIFY = {
  AUTHENTICATION: {
    value: 5,
    label: '认证操作'
  },
  MACHINE: {
    value: 10,
    label: '机器操作'
  },
  MACHINE_ENV: {
    value: 15,
    label: '机器变量操作'
  },
  MACHINE_KEY: {
    value: 20,
    label: '秘钥操作'
  },
  MACHINE_PROXY: {
    value: 25,
    label: '代理操作'
  },
  TERMINAL: {
    value: 30,
    label: '终端操作'
  },
  SFTP: {
    value: 35,
    label: '远程文件操作'
  },
  EXEC: {
    value: 40,
    label: '批量执行操作'
  },
  TAIL: {
    value: 45,
    label: '日志文件操作'
  },
  TEMPLATE: {
    value: 50,
    label: '模板操作'
  },
  USER: {
    value: 55,
    label: '用户操作'
  },
  APP: {
    value: 60,
    label: '应用操作'
  },
  PROFILE: {
    value: 65,
    label: '环境操作'
  },
  APP_ENV: {
    value: 70,
    label: '应用变量操作'
  },
  REPOSITORY: {
    value: 75,
    label: '应用仓库操作'
  },
  BUILD: {
    value: 80,
    label: '应用构建操作'
  },
  RELEASE: {
    value: 85,
    label: '应用发布操作'
  },
  APP_PIPELINE: {
    value: 88,
    label: '应用流水线'
  },
  SYSTEM_ENV: {
    value: 90,
    label: '系统变量操作'
  },
  SYSTEM: {
    value: 95,
    label: '系统操作'
  },
  SCHEDULER: {
    value: 100,
    label: '调度任务操作'
  },
  DATA_CLEAR: {
    value: 110,
    label: '数据清理'
  },
  DATA_IMPORT: {
    value: 120,
    label: '数据导入'
  },
  DATA_EXPORT: {
    value: 130,
    label: '数据导出'
  }
}

/**
 * 操作日志类型
 */
export const EVENT_TYPE = {
  LOGIN: {
    value: 1005,
    label: '登陆',
    classify: 5
  },
  LOGOUT: {
    value: 1010,
    label: '登出',
    classify: 5
  },
  RESET_PASSWORD: {
    value: 1015,
    label: '重置密码',
    classify: 5
  },
  ADD_MACHINE: {
    value: 2005,
    label: '添加机器',
    classify: 10
  },
  UPDATE_MACHINE: {
    value: 2010,
    label: '修改机器',
    classify: 10
  },
  DELETE_MACHINE: {
    value: 2015,
    label: '删除机器',
    classify: 10
  },
  CHANGE_MACHINE_STATUS: {
    value: 2020,
    label: '修改状态',
    classify: 10
  },
  COPY_MACHINE: {
    value: 2025,
    label: '复制机器',
    classify: 10
  },
  DELETE_MACHINE_ENV: {
    value: 2105,
    label: '删除变量',
    classify: 15
  },
  SYNC_MACHINE_ENV: {
    value: 2110,
    label: '同步变量',
    classify: 15
  },
  ADD_MACHINE_KEY: {
    value: 2205,
    label: '新增秘钥',
    classify: 20
  },
  UPDATE_MACHINE_KEY: {
    value: 2210,
    label: '修改秘钥',
    classify: 20
  },
  DELETE_MACHINE_KEY: {
    value: 2215,
    label: '删除秘钥',
    classify: 20
  },
  MOUNT_MACHINE_KEY: {
    value: 2220,
    label: '挂载秘钥',
    classify: 20
  },
  DUMP_MACHINE_KEY: {
    value: 2225,
    label: '卸载秘钥'
  },
  MOUNT_ALL_MACHINE_KEY: {
    value: 2230,
    label: '挂载全部秘钥',
    classify: 20
  },
  DUMP_ALL_MACHINE_KEY: {
    value: 2235,
    label: '卸载全部秘钥',
    classify: 20
  },
  TEMP_MOUNT_MACHINE_KEY: {
    value: 2240,
    label: '临时挂载秘钥',
    classify: 20
  },
  ADD_MACHINE_PROXY: {
    value: 2305,
    label: '新增代理',
    classify: 25
  },
  UPDATE_MACHINE_PROXY: {
    value: 2310,
    label: '修改代理',
    classify: 25
  },
  DELETE_MACHINE_PROXY: {
    value: 2315,
    label: '删除代理',
    classify: 25
  },
  OPEN_TERMINAL: {
    value: 2400,
    label: '打开机器终端',
    classify: 30
  },
  FORCE_OFFLINE_TERMINAL: {
    value: 2405,
    label: '强制下线终端',
    classify: 30
  },
  UPDATE_TERMINAL_CONFIG: {
    value: 2410,
    label: '修改终端配置',
    classify: 30
  },
  DELETE_TERMINAL_LOG: {
    value: 2415,
    label: '删除终端日志',
    classify: 30
  },
  OPEN_SFTP: {
    value: 2500,
    label: '打开机器SFTP',
    classify: 35
  },
  SFTP_MKDIR: {
    value: 2505,
    label: '创建文件夹',
    classify: 35
  },
  SFTP_TOUCH: {
    value: 2510,
    label: '创建文件',
    classify: 35
  },
  SFTP_TRUNCATE: {
    value: 2515,
    label: '截断文件',
    classify: 35
  },
  SFTP_MOVE: {
    value: 2520,
    label: '移动文件',
    classify: 35
  },
  SFTP_REMOVE: {
    value: 2525,
    label: '删除文件',
    classify: 35
  },
  SFTP_CHMOD: {
    value: 2530,
    label: '修改权限',
    classify: 35
  },
  SFTP_CHOWN: {
    value: 2535,
    label: '修改所有者',
    classify: 35
  },
  SFTP_CHGRP: {
    value: 2540,
    label: '修改所有组',
    classify: 35
  },
  SFTP_UPLOAD: {
    value: 2545,
    label: '上传文件',
    classify: 35
  },
  SFTP_DOWNLOAD: {
    value: 2550,
    label: '下载文件',
    classify: 35
  },
  SFTP_PACKAGE: {
    value: 2555,
    label: '打包文件',
    classify: 35
  },
  EXEC_SUBMIT: {
    value: 2605,
    label: '批量执行',
    classify: 40
  },
  EXEC_DELETE: {
    value: 2610,
    label: '删除执行',
    classify: 40
  },
  EXEC_TERMINATE: {
    value: 2615,
    label: '停止执行',
    classify: 40
  },
  ADD_TAIL_FILE: {
    value: 2705,
    label: '添加文件',
    classify: 45
  },
  UPDATE_TAIL_FILE: {
    value: 2710,
    label: '修改文件',
    classify: 45
  },
  DELETE_TAIL_FILE: {
    value: 2715,
    label: '删除文件',
    classify: 45
  },
  UPLOAD_TAIL_FILE: {
    value: 2720,
    label: '上传文件',
    classify: 45
  },
  ADD_TEMPLATE: {
    value: 2805,
    label: '添加模板',
    classify: 50
  },
  UPDATE_TEMPLATE: {
    value: 2810,
    label: '修改模板',
    classify: 50
  },
  DELETE_TEMPLATE: {
    value: 2815,
    label: '删除模板',
    classify: 50
  },
  ADD_USER: {
    value: 1105,
    label: '添加用户',
    classify: 55
  },
  UPDATE_USER: {
    value: 1110,
    label: '修改信息',
    classify: 55
  },
  DELETE_USER: {
    value: 1115,
    label: '删除用户',
    classify: 55
  },
  CHANGE_USER_STATUS: {
    value: 1120,
    label: '修改状态',
    classify: 55
  },
  UNLOCK_USER: {
    value: 1125,
    label: '解锁用户',
    classify: 55
  },
  ADD_APP: {
    value: 3005,
    label: '添加应用',
    classify: 60
  },
  UPDATE_APP: {
    value: 3010,
    label: '修改应用',
    classify: 60
  },
  DELETE_APP: {
    value: 3015,
    label: '删除应用',
    classify: 60
  },
  CONFIG_APP: {
    value: 3020,
    label: '配置应用',
    classify: 60
  },
  SYNC_APP: {
    value: 3025,
    label: '同步应用',
    classify: 60
  },
  COPY_APP: {
    value: 3030,
    label: '复制应用',
    classify: 60
  },
  ADD_PROFILE: {
    value: 3105,
    label: '添加环境',
    classify: 65
  },
  UPDATE_PROFILE: {
    value: 3110,
    label: '修改环境',
    classify: 65
  },
  DELETE_PROFILE: {
    value: 3115,
    label: '删除环境',
    classify: 65
  },
  DELETE_APP_ENV: {
    value: 3205,
    label: '删除变量',
    classify: 70
  },
  SYNC_APP_ENV: {
    value: 3210,
    label: '同步变量',
    classify: 70
  },
  ADD_REPOSITORY: {
    value: 3305,
    label: '添加仓库',
    classify: 75
  },
  INIT_REPOSITORY: {
    value: 3310,
    label: '初始化仓库',
    classify: 75
  },
  RE_INIT_REPOSITORY: {
    value: 3315,
    label: '重新初始化',
    classify: 75
  },
  UPDATE_REPOSITORY: {
    value: 3320,
    label: '更新仓库',
    classify: 75
  },
  DELETE_REPOSITORY: {
    value: 3325,
    label: '删除仓库',
    classify: 75
  },
  CLEAN_REPOSITORY: {
    value: 3330,
    label: '清空仓库',
    classify: 75
  },
  SUBMIT_BUILD: {
    value: 4005,
    label: '提交构建',
    classify: 80
  },
  BUILD_TERMINATE: {
    value: 4010,
    label: '停止构建',
    classify: 80
  },
  DELETE_BUILD: {
    value: 4015,
    label: '删除构建',
    classify: 80
  },
  SUBMIT_REBUILD: {
    value: 4020,
    label: '重新构建',
    classify: 80
  },
  SUBMIT_RELEASE: {
    value: 5005,
    label: '提交发布',
    classify: 85
  },
  AUDIT_RELEASE: {
    value: 5010,
    label: '发布审核',
    classify: 85
  },
  RUNNABLE_RELEASE: {
    value: 5015,
    label: '执行发布',
    classify: 85
  },
  ROLLBACK_RELEASE: {
    value: 5020,
    label: '回滚发布',
    classify: 85
  },
  TERMINATE_RELEASE: {
    value: 5025,
    label: '停止发布',
    classify: 85
  },
  DELETE_RELEASE: {
    value: 5030,
    label: '删除发布',
    classify: 85
  },
  COPY_RELEASE: {
    value: 5035,
    label: '复制发布',
    classify: 85
  },
  CANCEL_TIMED_RELEASE: {
    value: 5040,
    label: '取消定时发布',
    classify: 85
  },
  SET_TIMED_RELEASE: {
    value: 5045,
    label: '设置定时发布',
    classify: 85
  },
  TERMINATE_MACHINE_RELEASE: {
    value: 5050,
    label: '停止机器操作',
    classify: 85
  },
  SKIP_MACHINE_RELEASE: {
    value: 5055,
    label: '跳过机器操作',
    classify: 85
  },
  ADD_PIPELINE: {
    value: 5505,
    label: '添加流水线',
    classify: 88
  },
  UPDATE_PIPELINE: {
    value: 5510,
    label: '修改流水线',
    classify: 88
  },
  DELETE_PIPELINE: {
    value: 5515,
    label: '删除流水线',
    classify: 88
  },
  SUBMIT_PIPELINE_TASK: {
    value: 5605,
    label: '提交执行任务',
    classify: 88
  },
  AUDIT_PIPELINE_TASK: {
    value: 5610,
    label: '审核任务',
    classify: 88
  },
  COPY_PIPELINE_TASK: {
    value: 5615,
    label: '复制任务',
    classify: 88
  },
  EXEC_PIPELINE_TASK: {
    value: 5620,
    label: '执行任务',
    classify: 88
  },
  DELETE_PIPELINE_TASK: {
    value: 5625,
    label: '删除任务',
    classify: 88
  },
  SET_PIPELINE_TIMED_TASK: {
    value: 5630,
    label: '设置定时执行',
    classify: 88
  },
  CANCEL_PIPELINE_TIMED_TASK: {
    value: 5635,
    label: '取消定时执行',
    classify: 88
  },
  TERMINATE_PIPELINE_TASK: {
    value: 5640,
    label: '停止执行任务',
    classify: 88
  },
  TERMINATE_PIPELINE_TASK_DETAIL: {
    value: 5645,
    label: '停止执行操作',
    classify: 88
  },
  SKIP_PIPELINE_TASK_DETAIL: {
    value: 5650,
    label: '跳过执行操作',
    classify: 88
  },
  ADD_SYSTEM_ENV: {
    value: 6005,
    label: '添加变量',
    classify: 90
  },
  UPDATE_SYSTEM_ENV: {
    value: 6010,
    label: '更新变量',
    classify: 90
  },
  DELETE_SYSTEM_ENV: {
    value: 6015,
    label: '删除变量',
    classify: 90
  },
  SAVE_SYSTEM_ENV: {
    value: 6020,
    label: '保存变量',
    classify: 90
  },
  CONFIG_IP_LIST: {
    value: 6105,
    label: '配置IP过滤器',
    classify: 95
  },
  RE_ANALYSIS_SYSTEM: {
    value: 6110,
    label: '系统统计分析',
    classify: 95
  },
  CLEAN_SYSTEM_FILE: {
    value: 6115,
    label: '清理系统文件',
    classify: 95
  },
  UPDATE_SYSTEM_CONFIG: {
    value: 6120,
    label: '修改系统配置',
    classify: 95
  },
  ADD_SCHEDULER_TASK: {
    value: 7105,
    label: '添加调度任务',
    classify: 100
  },
  UPDATE_SCHEDULER_TASK: {
    value: 7110,
    label: '修改调度任务',
    classify: 100
  },
  UPDATE_SCHEDULER_TASK_STATUS: {
    value: 7115,
    label: '更新任务状态',
    classify: 100
  },
  DELETE_SCHEDULER_TASK: {
    value: 7120,
    label: '删除调度任务',
    classify: 100
  },
  MANUAL_TRIGGER_SCHEDULER_TASK: {
    value: 7125,
    label: '手动触发任务',
    classify: 100
  },
  TERMINATE_ALL_SCHEDULER_TASK: {
    value: 7130,
    label: '停止任务',
    classify: 100
  },
  TERMINATE_SCHEDULER_TASK_MACHINE: {
    value: 7135,
    label: '停止机器操作',
    classify: 100
  },
  SKIP_SCHEDULER_TASK_MACHINE: {
    value: 7140,
    label: '跳过机器操作',
    classify: 100
  },
  DELETE_TASK_RECORD: {
    value: 7145,
    label: '删除调度明细',
    classify: 100
  },
  DATA_CLEAR_BATCH_EXEC: {
    value: 8105,
    label: '批量执行',
    classify: 110
  },
  DATA_CLEAR_TERMINAL_LOG: {
    value: 8110,
    label: '终端日志',
    classify: 110
  },
  DATA_CLEAR_SCHEDULER_RECORD: {
    value: 8115,
    label: '任务调度',
    classify: 110
  },
  DATA_CLEAR_APP_BUILD: {
    value: 8120,
    label: '应用构建',
    classify: 110
  },
  DATA_CLEAR_APP_RELEASE: {
    value: 8125,
    label: '应用发布',
    classify: 110
  },
  DATA_CLEAR_APP_PIPELINE: {
    value: 8130,
    label: '流水线任务',
    classify: 110
  },
  DATA_CLEAR_EVENT_LOG: {
    value: 8140,
    label: '操作日志',
    classify: 110
  },
  DATA_IMPORT_MACHINE: {
    value: 8305,
    label: '机器信息',
    classify: 120
  },
  DATA_IMPORT_MACHINE_PROXY: {
    value: 8310,
    label: '导入机器代理',
    classify: 120
  },
  DATA_IMPORT_TAIL_FILE: {
    value: 8315,
    label: '导入日志文件',
    classify: 120
  },
  DATA_IMPORT_PROFILE: {
    value: 8320,
    label: '导入应用环境',
    classify: 120
  },
  DATA_IMPORT_APPLICATION: {
    value: 8325,
    label: '导入应用信息',
    classify: 120
  },
  DATA_IMPORT_REPOSITORY: {
    value: 8330,
    label: '导入版本仓库',
    classify: 120
  },
  DATA_IMPORT_COMMAND_TEMPLATE: {
    value: 8335,
    label: '导入命令模板',
    classify: 120
  },
  DATA_EXPORT_MACHINE: {
    value: 8505,
    label: '机器信息',
    classify: 130
  },
  DATA_EXPORT_MACHINE_PROXY: {
    value: 8510,
    label: '机器代理',
    classify: 130
  },
  DATA_EXPORT_TERMINAL_LOG: {
    value: 8515,
    label: '终端日志',
    classify: 130
  },
  DATA_EXPORT_TAIL_FILE: {
    value: 8520,
    label: '日志文件',
    classify: 130
  },
  DATA_EXPORT_PROFILE: {
    value: 8550,
    label: '应用环境',
    classify: 130
  },
  DATA_EXPORT_APPLICATION: {
    value: 8555,
    label: '应用信息',
    classify: 130
  },
  DATA_EXPORT_REPOSITORY: {
    value: 8560,
    label: '应用仓库',
    classify: 130
  },
  DATA_EXPORT_COMMAND_TEMPLATE: {
    value: 8605,
    label: '命令模板',
    classify: 130
  },
  DATA_EXPORT_EVENT_LOG: {
    value: 8615,
    label: '操作日志',
    classify: 130
  }
}

/**
 * 系统清理类型
 */
export const SYSTEM_CLEAR_TYPE = {
  TEMP_FILE: {
    key: 'tempFile',
    value: 10,
    label: '临时文件'
  },
  LOG_FILE: {
    key: 'logFile',
    value: 20,
    label: '日志文件'
  },
  SWAP_FILE: {
    key: 'swapFile',
    value: 30,
    label: '交换区文件'
  },
  DIST_FILE: {
    key: 'distFile',
    value: 40,
    label: '旧版本构建产物'
  },
  REPO_FILE: {
    key: 'repoFile',
    value: 50,
    label: '旧版本应用仓库'
  },
  SCREEN_FILE: {
    key: 'screenFile',
    value: 60,
    label: '录屏文件'
  }
}

/**
 * 系统配置项
 */
export const SYSTEM_OPTION_KEY = {
  FILE_CLEAN_THRESHOLD: {
    key: 'fileCleanThreshold',
    value: 10
  },
  ENABLE_AUTO_CLEAN_FILE: {
    key: 'autoCleanFile',
    value: 20
  },
  ALLOW_MULTIPLE_LOGIN: {
    key: 'allowMultipleLogin',
    value: 30
  },
  LOGIN_FAILURE_LOCK: {
    key: 'loginFailureLock',
    value: 40
  },
  LOGIN_IP_BIND: {
    key: 'loginIpBind',
    value: 50
  },
  LOGIN_TOKEN_AUTO_RENEW: {
    key: 'loginTokenAutoRenew',
    value: 60
  },
  LOGIN_TOKEN_EXPIRE: {
    key: 'loginTokenExpire',
    value: 70
  },
  LOGIN_FAILURE_LOCK_THRESHOLD: {
    key: 'loginFailureLockThreshold',
    value: 80
  },
  LOGIN_TOKEN_AUTO_RENEW_THRESHOLD: {
    key: 'loginTokenAutoRenewThreshold',
    value: 90
  },
  RESUME_ENABLE_SCHEDULER_TASK: {
    key: 'resumeEnableSchedulerTask',
    value: 100
  },
  SFTP_UPLOAD_THRESHOLD: {
    key: 'sftpUploadThreshold',
    value: 110
  },
  STATISTICS_CACHE_EXPIRE: {
    key: 'statisticsCacheExpire',
    value: 120
  }
}

/**
 * 调度任务执行状态
 */
export const SCHEDULER_TASK_STATUS = {
  WAIT: {
    value: 10,
    label: '待调度',
    color: ''
  },
  RUNNABLE: {
    value: 20,
    label: '执行中',
    color: 'green'
  },
  SUCCESS: {
    value: 30,
    label: '成功',
    color: 'blue'
  },
  FAILURE: {
    value: 40,
    label: '失败',
    color: 'red'
  },
  TERMINATED: {
    value: 50,
    label: '已停止',
    color: 'orange'
  }
}

/**
 * 调度任务执行机器状态
 */
export const SCHEDULER_TASK_MACHINE_STATUS = {
  WAIT: {
    value: 10,
    label: '待调度',
    color: ''
  },
  RUNNABLE: {
    value: 20,
    label: '执行中',
    color: 'green'
  },
  SUCCESS: {
    value: 30,
    label: '成功',
    color: 'blue'
  },
  FAILURE: {
    value: 40,
    label: '失败',
    color: 'red'
  },
  SKIPPED: {
    value: 50,
    label: '已跳过',
    color: 'orange'
  },
  TERMINATED: {
    value: 60,
    label: '已停止',
    color: 'orange'
  }
}

/**
 * 异常处理类型
 */
export const EXCEPTION_HANDLER_TYPE = {
  SKIP_ALL: {
    value: 10,
    label: '跳过所有',
    title: '跳过所有项, 中断执行'
  },
  SKIP_ERROR: {
    value: 20,
    label: '跳过错误',
    title: '跳过错误项, 继续执行'
  }
}

/**
 * 线程池指标类型
 */
export const THREAD_POOL_METRICS_TYPE = {
  TERMINAL: {
    value: 10,
    label: '远程终端线程池'
  },
  TERMINAL_WATCHER: {
    value: 15,
    label: '终端监视线程池'
  },
  EXEC: {
    value: 20,
    label: '批量执行线程池'
  },
  TAIL: {
    value: 30,
    label: '文件追踪线程池'
  },
  SFTP_TRANSFER_RATE: {
    value: 40,
    label: '传输进度线程池'
  },
  SFTP_UPLOAD: {
    value: 50,
    label: '文件上传线程池'
  },
  SFTP_DOWNLOAD: {
    value: 60,
    label: '文件下载线程池'
  },
  SFTP_PACKAGE: {
    value: 70,
    label: '文件打包线程池'
  },
  APP_BUILD: {
    value: 80,
    label: '应用构建线程池'
  },
  RELEASE_MAIN: {
    value: 90,
    label: '应用发布主线程池'
  },
  RELEASE_MACHINE: {
    value: 100,
    label: '应用发布机器线程池'
  },
  SCHEDULER_TASK_MAIN: {
    value: 110,
    label: '调度任务主线程池'
  },
  SCHEDULER_TASK_MACHINE: {
    value: 120,
    label: '调度任务机器线程池'
  },
  PIPELINE: {
    value: 130,
    label: '应用流水线线程池'
  }
}

/**
 * 阅读状态
 */
export const READ_STATUS = {
  UNREAD: {
    value: 1,
    label: '未读'
  },
  READ: {
    value: 2,
    label: '已读'
  }
}

/**
 * 消息分类
 */
export const MESSAGE_CLASSIFY = {
  SYSTEM: {
    value: 10,
    label: '系统消息',
    icon: 'setting'
  },
  IMPORT: {
    value: 20,
    label: '数据导入',
    icon: 'import'
  }
}

/**
 * 消息类型
 */
export const MESSAGE_TYPE = {
  EXEC_SUCCESS: {
    classify: 10,
    value: 1010,
    label: '命令执行完成',
    notify: 'success',
    redirect: '/batch/exec/list'
  },
  EXEC_FAILURE: {
    classify: 10,
    value: 1020,
    label: '命令执行失败',
    notify: 'error',
    redirect: '/batch/exec/list'
  },
  REPOSITORY_INIT_SUCCESS: {
    classify: 10,
    value: 1030,
    label: '版本仓库初始化成功',
    notify: 'success',
    redirect: '/app/repo'
  },
  REPOSITORY_INIT_FAILURE: {
    classify: 10,
    value: 1040,
    label: '版本仓库初始化失败',
    notify: 'error',
    redirect: '/app/repo'
  },
  BUILD_SUCCESS: {
    classify: 10,
    value: 1050,
    label: '构建执行成功',
    notify: 'success',
    redirect: '/app/build/list'
  },
  BUILD_FAILURE: {
    classify: 10,
    value: 1060,
    label: '构建执行失败',
    notify: 'error',
    redirect: '/app/build/list'
  },
  RELEASE_AUDIT_RESOLVE: {
    classify: 10,
    value: 1070,
    label: '发布审批通过',
    notify: 'success',
    redirect: '/app/release/list'
  },
  RELEASE_AUDIT_REJECT: {
    classify: 10,
    value: 1080,
    label: '发布审批驳回',
    notify: 'warning',
    redirect: '/app/release/list'
  },
  RELEASE_SUCCESS: {
    classify: 10,
    value: 1090,
    label: '发布执行成功',
    notify: 'success',
    redirect: '/app/release/list'
  },
  RELEASE_FAILURE: {
    classify: 10,
    value: 1100,
    label: '发布执行失败',
    notify: 'error',
    redirect: '/app/release/list'
  },
  PIPELINE_AUDIT_RESOLVE: {
    classify: 10,
    value: 1110,
    label: '应用流水线审批通过',
    notify: 'success',
    redirect: '/app/pipeline/record'
  },
  PIPELINE_AUDIT_REJECT: {
    classify: 10,
    value: 1120,
    label: '应用流水线审批驳回',
    notify: 'warning',
    redirect: '/app/pipeline/record'
  },
  PIPELINE_EXEC_SUCCESS: {
    classify: 10,
    value: 1130,
    label: '流水线执行成功',
    notify: 'success',
    redirect: '/app/pipeline/record'
  },
  PIPELINE_EXEC_FAILURE: {
    classify: 10,
    value: 1140,
    label: '流水线执行失败',
    notify: 'error',
    redirect: '/app/pipeline/record'
  },
  MACHINE_AGENT_INSTALL_SUCCESS: {
    classify: 10,
    value: 1150,
    label: '机器监控插件安装成功',
    notify: 'success',
    redirect: '/machine/monitor/list'
  },
  MACHINE_AGENT_INSTALL_FAILURE: {
    classify: 10,
    value: 1160,
    label: '机器监控插件安装失败',
    notify: 'error',
    redirect: '/machine/monitor/list'
  },
  MACHINE_IMPORT_SUCCESS: {
    classify: 20,
    value: 2010,
    label: '机器信息导入成功',
    notify: 'success',
    redirect: '/machine/list'
  },
  MACHINE_IMPORT_FAILURE: {
    classify: 20,
    value: 2020,
    label: '机器信息导入失败',
    notify: 'error',
    redirect: '/machine/list'
  },
  MACHINE_PROXY_IMPORT_SUCCESS: {
    classify: 20,
    value: 2030,
    label: '机器代理导入成功',
    notify: 'success',
    redirect: '/machine/proxy'
  },
  MACHINE_PROXY_IMPORT_FAILURE: {
    classify: 20,
    value: 2040,
    label: '机器代理导入失败',
    notify: 'error',
    redirect: '/machine/proxy'
  },
  MACHINE_TAIL_FILE_IMPORT_SUCCESS: {
    classify: 20,
    value: 2050,
    label: '日志文件导入成功',
    notify: 'success',
    redirect: '/log/list'
  },
  MACHINE_TAIL_FILE_IMPORT_FAILURE: {
    classify: 20,
    value: 2060,
    label: '日志文件导入失败',
    notify: 'error',
    redirect: '/log/list'
  },
  PROFILE_IMPORT_SUCCESS: {
    classify: 20,
    value: 2070,
    label: '应用环境导入成功',
    notify: 'success',
    redirect: '/app/profile'
  },
  PROFILE_IMPORT_FAILURE: {
    classify: 20,
    value: 2080,
    label: '应用环境导入失败',
    notify: 'error',
    redirect: '/app/profile'
  },
  APPLICATION_IMPORT_SUCCESS: {
    classify: 20,
    value: 2090,
    label: '应用信息导入成功',
    notify: 'success',
    redirect: '/app/list'
  },
  APPLICATION_IMPORT_FAILURE: {
    classify: 20,
    value: 2100,
    label: '应用信息导入失败',
    notify: 'error',
    redirect: '/app/list'
  },
  REPOSITORY_IMPORT_SUCCESS: {
    classify: 20,
    value: 2110,
    label: '版本仓库导入成功',
    notify: 'success',
    redirect: '/app/repo'
  },
  REPOSITORY_IMPORT_FAILURE: {
    classify: 20,
    value: 2120,
    label: '版本仓库导入失败',
    notify: 'error',
    redirect: '/app/repo'
  },
  COMMAND_TEMPLATE_IMPORT_SUCCESS: {
    classify: 20,
    value: 2130,
    label: '命令模板导入成功',
    notify: 'success',
    redirect: '/template/list'
  },
  COMMAND_TEMPLATE_IMPORT_FAILURE: {
    classify: 20,
    value: 2140,
    label: '命令模板导入失败',
    notify: 'error',
    redirect: '/template/list'
  }
}

/**
 * 流水线状态
 */
export const PIPELINE_STATUS = {
  WAIT_AUDIT: {
    value: 10,
    color: '',
    label: '待审核'
  },
  AUDIT_REJECT: {
    value: 20,
    color: 'orange',
    label: '已驳回'
  },
  WAIT_RUNNABLE: {
    value: 30,
    color: 'cyan',
    label: '待执行'
  },
  WAIT_SCHEDULE: {
    value: 35,
    color: 'cyan',
    label: '待调度'
  },
  RUNNABLE: {
    value: 40,
    color: 'green',
    label: '执行中'
  },
  FINISH: {
    value: 50,
    color: 'blue',
    label: '已完成'
  },
  TERMINATED: {
    value: 60,
    color: 'orange',
    label: '已停止'
  },
  FAILURE: {
    value: 70,
    color: 'red',
    label: '已失败'
  }
}

/**
 * 流水线操作状态
 */
export const PIPELINE_DETAIL_STATUS = {
  WAIT: {
    value: 10,
    color: '',
    label: '未开始',
    log: false,
    actionStyle: {
      background: '#CED4DA'
    },
    actionValue() {
      return '未开始'
    }
  },
  RUNNABLE: {
    value: 20,
    color: 'green',
    label: '进行中',
    log: true,
    actionStyle: {
      background: '#94D82D'
    },
    actionValue() {
      return '进行中'
    }
  },
  FINISH: {
    value: 30,
    color: 'blue',
    label: '已完成',
    log: true,
    actionStyle: {
      background: '#74C0FC'
    },
    actionValue(log) {
      return log.usedInterval || '已完成'
    }
  },
  FAILURE: {
    value: 40,
    color: 'red',
    label: '已失败',
    log: true,
    actionStyle: {
      background: '#F03E3E'
    },
    actionValue(log) {
      return log.usedInterval || '已失败'
    }
  },
  SKIPPED: {
    value: 50,
    color: 'orange',
    label: '已跳过',
    log: false,
    actionStyle: {
      background: '#FFD43B'
    },
    actionValue() {
      return '已跳过'
    }
  },
  TERMINATED: {
    value: 60,
    color: 'orange',
    label: '已停止',
    log: true,
    actionStyle: {
      background: '#FFA94D'
    },
    actionValue() {
      return '已停止'
    }
  }
}

/**
 * 流水线日志状态
 */
export const PIPELINE_LOG_STATUS = {
  CREATE: {
    value: 10,
    label: '创建'
  },
  EXEC: {
    value: 20,
    label: '执行'
  },
  SUCCESS: {
    value: 30,
    label: '成功'
  },
  FAILURE: {
    value: 40,
    label: '失败'
  },
  TERMINATED: {
    value: 50,
    label: '停止'
  },
  SKIPPED: {
    value: 60,
    label: '跳过'
  }
}

/**
 * 批量上传状态
 */
export const BATCH_UPLOAD_STATUS = {
  WAIT: {
    value: 10,
    loading: false,
    mask: false,
    checked: false,
    visibleTransfer: false,
    message: '点击上传按钮开始上传',
    type: 'info'
  },
  CHECKING: {
    value: 20,
    loading: true,
    mask: true,
    checked: false,
    visibleTransfer: false,
    message: '正在检查文件...',
    type: 'info'
  },
  REQUESTING: {
    value: 30,
    loading: true,
    mask: true,
    checked: false,
    visibleTransfer: false,
    message: '正在请求上传...',
    type: 'info'
  },
  WAIT_UPLOAD: {
    value: 40,
    loading: true,
    mask: false,
    checked: true,
    visibleTransfer: false,
    message: '等待上传中...',
    type: 'info'
  },
  UPLOADING: {
    value: 50,
    loading: true,
    mask: false,
    checked: false,
    visibleTransfer: true,
    message: '文件上传中... 清空或页面关闭则在后台继续上传',
    type: 'info'
  },
  ERROR: {
    value: 60,
    loading: false,
    mask: false,
    checked: false,
    visibleTransfer: false,
    message: '文件上传失败, 请重试',
    type: 'error'
  },
  NO_AVAILABLE: {
    value: 70,
    loading: false,
    mask: false,
    checked: false,
    visibleTransfer: false,
    message: '无可用上传机器, 请重新选择后重试',
    type: 'error'
  }
}

/**
 * 数据清理区间
 */
export const DATA_CLEAR_RANGE = {
  DAY: {
    value: 10
  },
  TOTAL: {
    value: 20
  },
  REL_ID: {
    value: 30
  }
}

/**
 * 数据导入类型
 */
export const IMPORT_TYPE = {
  MACHINE: {
    value: 100,
    tips: '使用唯一标识来区分数据, 存在更新不存在新增, 优先使用导入密码',
    title: '机器信息 导入',
    api: 'importMachineData'
  },
  MACHINE_PROXY: {
    value: 110,
    tips: '导入时优先使用导入密码',
    title: '机器代理 导入',
    api: 'importMachineProxy'
  },
  TAIL_FILE: {
    value: 130,
    tips: '通过机器标识来区分机器, 机器名称无需填写',
    title: '日志文件 导入',
    api: 'importTailFile'
  },
  PROFILE: {
    value: 200,
    tips: '使用唯一标识来区分数据, 存在更新不存在新增',
    title: '应用环境 导入',
    api: 'importAppProfile'
  },
  APPLICATION: {
    value: 210,
    tips: '使用唯一标识来区分数据, 存在更新不存在新增',
    title: '应用信息 导入',
    api: 'importApplication'
  },
  REPOSITORY: {
    value: 220,
    tips: '使用名称来区分数据, 存在更新不存在新增, 优先使用导入密码',
    title: '版本仓库 导入',
    api: 'importRepository'
  },
  COMMAND_TEMPLATE: {
    value: 310,
    tips: '使用模板名称来区分数据, 存在更新不存在新增',
    title: '命令模板 导入',
    api: 'importCommandTemplate'
  }
}

/**
 * 监控状态
 */
export const MONITOR_STATUS = {
  NOT_START: {
    value: 1,
    label: '未启动',
    status: 'default'
  },
  STARTING: {
    value: 2,
    label: '启动中',
    status: 'success'
  },
  RUNNING: {
    value: 3,
    label: '运行中',
    status: 'processing'
  }
}

/**
 * 监控数据区间
 */
export const MONITOR_DATA_RANGE = {
  HOUR: {
    value: 1,
    label: '实时',
    rangeGetter: () => {
      const end = ~~(Date.now() / 1000)
      const start = end - (60 * 60)
      return [start, end]
    }
  },
  DAY: {
    value: 2,
    label: '近24时',
    rangeGetter: () => {
      const end = ~~(Date.now() / 1000)
      const start = end - (60 * 60 * 24)
      return [start, end]
    }
  },
  WEEK: {
    value: 3,
    label: '近7天',
    rangeGetter: () => {
      const end = ~~(Date.now() / 1000)
      const start = end - (60 * 60 * 24 * 7)
      return [start, end]
    }
  }
}

/**
 * 监控数据粒度
 */
export const MONITOR_DATA_GRANULARITY = {
  SECOND_30: {
    value: 12,
    label: '30秒',
    check: (start, end) => {
      return end - start <= 60 * 60
    }
  },
  MINUTE_1: {
    value: 20,
    label: '1分',
    min: 0,
    max: 60 * 60 * 24,
    default: true,
    check: (start, end) => {
      return end - start <= 60 * 60
    }
  },
  MINUTE_5: {
    value: 22,
    label: '5分',
    default: true,
    check: (start, end) => {
      return end - start <= 60 * 60 * 24
    }
  },
  MINUTE_10: {
    value: 24,
    label: '10分',
    check: (start, end) => {
      const e = end - start
      return 60 * 60 < e && e <= 60 * 60 * 24
    }
  },
  MINUTE_30: {
    value: 26,
    label: '30分',
    check: (start, end) => {
      const e = end - start
      return 60 * 60 < e && e <= 60 * 60 * 24
    }
  },
  HOUR_1: {
    value: 30,
    label: '1时',
    default: true,
    check: (start, end) => {
      const e = end - start
      return 60 * 60 * 24 <= e && e <= 60 * 60 * 24 * 7
    }
  },
  HOUR_6: {
    value: 32,
    label: '6时',
    check: (start, end) => {
      const e = end - start
      return 60 * 60 * 24 < e && e <= 60 * 60 * 24 * 7 * 2
    }
  },
  HOUR_12: {
    value: 34,
    label: '12时',
    check: (start, end) => {
      const e = end - start
      return 60 * 60 * 24 < e && e <= 60 * 60 * 24 * 7 * 2
    }
  },
  DAY: {
    value: 40,
    label: '1天',
    default: true,
    check: (start, end) => {
      const e = end - start
      return 60 * 60 * 24 * 7 <= e
    }
  }
}
