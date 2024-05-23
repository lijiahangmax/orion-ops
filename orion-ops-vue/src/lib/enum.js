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
 * 终端操作 client 端
 */
export const TERMINAL_CLIENT_OPERATOR = {
  KEY: {
    value: '0'
  },
  CONNECT: {
    value: '1'
  },
  PING: {
    value: '2'
  },
  PONG: {
    value: '3'
  },
  RESIZE: {
    value: '4'
  },
  COMMAND: {
    value: '5'
  },
  CLEAR: {
    value: '6'
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
  PING: {
    value: '2'
  },
  PONG: {
    value: '3'
  },
  ERROR: {
    value: '4'
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
    label: '需要审核'
  },
  DISABLE: {
    value: 2,
    label: '无需审核'
  }
}

/**
 * 机器认证类型
 */
export const MACHINE_AUTH_TYPE = {
  PASSWORD: {
    value: 1,
    label: '密码认证'
  },
  SECRET_KEY: {
    value: 2,
    label: '独立密钥'
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
   * 密钥
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
    value: 100,
    label: '认证操作'
  },
  USER: {
    value: 110,
    label: '用户操作'
  },
  ALARM_GROUP: {
    value: 120,
    label: '报警组操作'
  },
  MACHINE: {
    value: 200,
    label: '机器操作'
  },
  MACHINE_ENV: {
    value: 210,
    label: '机器环境变量操作'
  },
  MACHINE_KEY: {
    value: 220,
    label: '密钥操作'
  },
  MACHINE_PROXY: {
    value: 230,
    label: '代理操作'
  },
  MACHINE_MONITOR: {
    value: 240,
    label: '机器监控'
  },
  MACHINE_ALARM: {
    value: 250,
    label: '机器报警'
  },
  TERMINAL: {
    value: 260,
    label: '终端操作'
  },
  SFTP: {
    value: 270,
    label: 'sftp 操作'
  },
  EXEC: {
    value: 300,
    label: '批量执行操作'
  },
  TAIL: {
    value: 310,
    label: '日志追踪操作'
  },
  SCHEDULER: {
    value: 320,
    label: '调度操作'
  },
  APP: {
    value: 400,
    label: '应用操作'
  },
  PROFILE: {
    value: 410,
    label: '环境操作'
  },
  APP_ENV: {
    value: 420,
    label: '应用环境变量操作'
  },
  REPOSITORY: {
    value: 430,
    label: '应用仓库操作'
  },
  BUILD: {
    value: 440,
    label: '应用构建操作'
  },
  RELEASE: {
    value: 450,
    label: '应用发布操作'
  },
  PIPELINE: {
    value: 460,
    label: '应用流水线'
  },
  TEMPLATE: {
    value: 500,
    label: '模板操作'
  },
  WEBHOOK: {
    value: 510,
    label: 'webhook操作'
  },
  SYSTEM: {
    value: 600,
    label: '系统操作'
  },
  SYSTEM_ENV: {
    value: 610,
    label: '系统环境变量操作'
  },
  DATA_CLEAR: {
    value: 620,
    label: '数据清理'
  },
  DATA_IMPORT: {
    value: 630,
    label: '数据导入'
  },
  DATA_EXPORT: {
    value: 640,
    label: '数据导出'
  }
}

/**
 * 操作日志类型
 */
export const EVENT_TYPE = {
  LOGIN: {
    value: 100010,
    label: '登陆系统',
    classify: 100
  },
  LOGOUT: {
    value: 100020,
    label: '退出系统',
    classify: 100
  },
  RESET_PASSWORD: {
    value: 100030,
    label: '重置密码',
    classify: 100
  },
  ADD_USER: {
    value: 110010,
    label: '添加用户',
    classify: 110
  },
  UPDATE_USER: {
    value: 110020,
    label: '修改用户信息',
    classify: 110
  },
  DELETE_USER: {
    value: 110030,
    label: '删除用户',
    classify: 110
  },
  CHANGE_USER_STATUS: {
    value: 110040,
    label: '修改用户状态',
    classify: 110
  },
  UNLOCK_USER: {
    value: 110050,
    label: '解锁用户',
    classify: 110
  },
  ADD_ALARM_GROUP: {
    value: 120010,
    label: '添加报警组',
    classify: 120
  },
  UPDATE_ALARM_GROUP: {
    value: 120020,
    label: '修改报警组',
    classify: 120
  },
  DELETE_ALARM_GROUP: {
    value: 120030,
    label: '删除报警组',
    classify: 120
  },
  ADD_MACHINE: {
    value: 200010,
    label: '添加机器',
    classify: 200
  },
  UPDATE_MACHINE: {
    value: 200020,
    label: '修改机器',
    classify: 200
  },
  DELETE_MACHINE: {
    value: 200030,
    label: '删除机器',
    classify: 200
  },
  CHANGE_MACHINE_STATUS: {
    value: 200040,
    label: '修改机器状态',
    classify: 200
  },
  COPY_MACHINE: {
    value: 200050,
    label: '复制机器',
    classify: 200
  },
  DELETE_MACHINE_ENV: {
    value: 210010,
    label: '删除机器环境变量',
    classify: 210
  },
  SYNC_MACHINE_ENV: {
    value: 210020,
    label: '同步机器环境变量',
    classify: 210
  },
  ADD_MACHINE_KEY: {
    value: 220010,
    label: '新增密钥',
    classify: 220
  },
  UPDATE_MACHINE_KEY: {
    value: 220020,
    label: '修改密钥',
    classify: 220
  },
  DELETE_MACHINE_KEY: {
    value: 220030,
    label: '删除密钥',
    classify: 220
  },
  BIND_MACHINE_KEY: {
    value: 220040,
    label: '绑定密钥',
    classify: 220
  },
  ADD_MACHINE_PROXY: {
    value: 230010,
    label: '新建代理',
    classify: 230
  },
  UPDATE_MACHINE_PROXY: {
    value: 230020,
    label: '修改代理',
    classify: 230
  },
  DELETE_MACHINE_PROXY: {
    value: 230030,
    label: '删除代理',
    classify: 230
  },
  UPDATE_MACHINE_MONITOR_CONFIG: {
    value: 240010,
    label: '修改配置',
    classify: 240
  },
  INSTALL_UPGRADE_MACHINE_MONITOR: {
    value: 240020,
    label: '安装/升级插件',
    classify: 240
  },
  SET_MACHINE_ALARM_CONFIG: {
    value: 250010,
    label: '修改报警配置',
    classify: 250
  },
  SET_MACHINE_ALARM_GROUP: {
    value: 250020,
    label: '修改报警联系组',
    classify: 250
  },
  RENOTIFY_MACHINE_ALARM_GROUP: {
    value: 250030,
    label: '重新发送报警通知',
    classify: 250
  },
  OPEN_TERMINAL: {
    value: 260010,
    label: '打开机器终端',
    classify: 260
  },
  FORCE_OFFLINE_TERMINAL: {
    value: 260020,
    label: '强制下线终端',
    classify: 260
  },
  UPDATE_TERMINAL_CONFIG: {
    value: 260030,
    label: '修改终端配置',
    classify: 260
  },
  DELETE_TERMINAL_LOG: {
    value: 260040,
    label: '删除终端日志',
    classify: 260
  },
  OPEN_SFTP: {
    value: 270010,
    label: '打开机器 SFTP',
    classify: 270
  },
  SFTP_MKDIR: {
    value: 270020,
    label: '创建文件夹',
    classify: 270
  },
  SFTP_TOUCH: {
    value: 270030,
    label: '创建文件',
    classify: 270
  },
  SFTP_TRUNCATE: {
    value: 270040,
    label: '截断文件',
    classify: 270
  },
  SFTP_MOVE: {
    value: 270050,
    label: '移动文件',
    classify: 270
  },
  SFTP_REMOVE: {
    value: 270060,
    label: '删除文件',
    classify: 270
  },
  SFTP_CHMOD: {
    value: 270070,
    label: '修改文件权限',
    classify: 270
  },
  SFTP_CHOWN: {
    value: 270080,
    label: '修改文件所有者',
    classify: 270
  },
  SFTP_CHGRP: {
    value: 270090,
    label: '修改文件所有组',
    classify: 270
  },
  SFTP_UPLOAD: {
    value: 270100,
    label: '上传文件',
    classify: 270
  },
  SFTP_DOWNLOAD: {
    value: 270110,
    label: '下载文件',
    classify: 270
  },
  SFTP_PACKAGE: {
    value: 270120,
    label: '打包文件',
    classify: 270
  },
  EXEC_SUBMIT: {
    value: 300010,
    label: '批量执行',
    classify: 300
  },
  EXEC_DELETE: {
    value: 300020,
    label: '删除执行',
    classify: 300
  },
  EXEC_TERMINATE: {
    value: 300030,
    label: '终止执行',
    classify: 300
  },
  ADD_TAIL_FILE: {
    value: 310010,
    label: '添加日志文件',
    classify: 310
  },
  UPDATE_TAIL_FILE: {
    value: 310020,
    label: '修改日志文件',
    classify: 310
  },
  DELETE_TAIL_FILE: {
    value: 310030,
    label: '删除日志文件',
    classify: 310
  },
  UPLOAD_TAIL_FILE: {
    value: 310040,
    label: '上传日志文件',
    classify: 310
  },
  ADD_SCHEDULER_TASK: {
    value: 320010,
    label: '添加调度任务',
    classify: 320
  },
  UPDATE_SCHEDULER_TASK: {
    value: 320020,
    label: '修改调度任务',
    classify: 320
  },
  UPDATE_SCHEDULER_TASK_STATUS: {
    value: 320030,
    label: '更新任务状态',
    classify: 320
  },
  DELETE_SCHEDULER_TASK: {
    value: 320040,
    label: '删除调度任务',
    classify: 320
  },
  MANUAL_TRIGGER_SCHEDULER_TASK: {
    value: 320050,
    label: '手动触发任务',
    classify: 320
  },
  TERMINATE_ALL_SCHEDULER_TASK: {
    value: 320060,
    label: '停止任务',
    classify: 320
  },
  TERMINATE_SCHEDULER_TASK_MACHINE: {
    value: 320070,
    label: '停止机器操作',
    classify: 320
  },
  SKIP_SCHEDULER_TASK_MACHINE: {
    value: 320080,
    label: '跳过机器操作',
    classify: 320
  },
  DELETE_TASK_RECORD: {
    value: 320090,
    label: '删除调度明细',
    classify: 320
  },
  ADD_APP: {
    value: 400010,
    label: '添加应用',
    classify: 400
  },
  UPDATE_APP: {
    value: 400020,
    label: '修改应用',
    classify: 400
  },
  DELETE_APP: {
    value: 400030,
    label: '删除应用',
    classify: 400
  },
  CONFIG_APP: {
    value: 400040,
    label: '配置应用',
    classify: 400
  },
  SYNC_APP: {
    value: 400050,
    label: '同步应用',
    classify: 400
  },
  COPY_APP: {
    value: 400060,
    label: '复制应用',
    classify: 400
  },
  ADD_PROFILE: {
    value: 410010,
    label: '添加应用环境',
    classify: 410
  },
  UPDATE_PROFILE: {
    value: 410020,
    label: '修改应用环境',
    classify: 410
  },
  DELETE_PROFILE: {
    value: 410030,
    label: '删除应用环境',
    classify: 410
  },
  DELETE_APP_ENV: {
    value: 420010,
    label: '删除应用环境变量',
    classify: 420
  },
  SYNC_APP_ENV: {
    value: 420020,
    label: '同步应用环境变量',
    classify: 420
  },
  ADD_REPOSITORY: {
    value: 430010,
    label: '添加版本仓库',
    classify: 430
  },
  INIT_REPOSITORY: {
    value: 430020,
    label: '初始化版本仓库',
    classify: 430
  },
  RE_INIT_REPOSITORY: {
    value: 430030,
    label: '重新初始化版本仓库',
    classify: 430
  },
  UPDATE_REPOSITORY: {
    value: 430040,
    label: '更新版本仓库',
    classify: 430
  },
  DELETE_REPOSITORY: {
    value: 430050,
    label: '删除版本仓库',
    classify: 430
  },
  CLEAN_REPOSITORY: {
    value: 430060,
    label: '清空版本仓库',
    classify: 430
  },
  SUBMIT_BUILD: {
    value: 440010,
    label: '提交应用构建',
    classify: 440
  },
  BUILD_TERMINATE: {
    value: 440020,
    label: '停止应用构建',
    classify: 440
  },
  DELETE_BUILD: {
    value: 440030,
    label: '删除应用构建',
    classify: 440
  },
  SUBMIT_REBUILD: {
    value: 440040,
    label: '重新构建应用',
    classify: 440
  },
  SUBMIT_RELEASE: {
    value: 450010,
    label: '提交应用发布',
    classify: 450
  },
  AUDIT_RELEASE: {
    value: 450020,
    label: '应用发布审核',
    classify: 450
  },
  RUNNABLE_RELEASE: {
    value: 450030,
    label: '执行应用发布',
    classify: 450
  },
  ROLLBACK_RELEASE: {
    value: 450040,
    label: '应用回滚发布',
    classify: 450
  },
  TERMINATE_RELEASE: {
    value: 450050,
    label: '停止应用发布',
    classify: 450
  },
  DELETE_RELEASE: {
    value: 450060,
    label: '删除应用发布',
    classify: 450
  },
  COPY_RELEASE: {
    value: 450070,
    label: '复制应用发布',
    classify: 450
  },
  CANCEL_TIMED_RELEASE: {
    value: 450080,
    label: '取消定时发布',
    classify: 450
  },
  SET_TIMED_RELEASE: {
    value: 450090,
    label: '设置定时发布',
    classify: 450
  },
  TERMINATE_MACHINE_RELEASE: {
    value: 450100,
    label: '停止机器操作',
    classify: 450
  },
  SKIP_MACHINE_RELEASE: {
    value: 450110,
    label: '跳过机器操作',
    classify: 450
  },
  ADD_PIPELINE: {
    value: 460010,
    label: '添加流水线',
    classify: 460
  },
  UPDATE_PIPELINE: {
    value: 460020,
    label: '修改流水线',
    classify: 460
  },
  DELETE_PIPELINE: {
    value: 460030,
    label: '删除流水线',
    classify: 460
  },
  SUBMIT_PIPELINE_TASK: {
    value: 460040,
    label: '提交执行任务',
    classify: 460
  },
  AUDIT_PIPELINE_TASK: {
    value: 460050,
    label: '审核任务',
    classify: 460
  },
  COPY_PIPELINE_TASK: {
    value: 460060,
    label: '复制任务',
    classify: 460
  },
  EXEC_PIPELINE_TASK: {
    value: 460070,
    label: '执行任务',
    classify: 460
  },
  DELETE_PIPELINE_TASK: {
    value: 460080,
    label: '删除任务',
    classify: 460
  },
  SET_PIPELINE_TIMED_TASK: {
    value: 460090,
    label: '设置定时执行',
    classify: 460
  },
  CANCEL_PIPELINE_TIMED_TASK: {
    value: 460100,
    label: '取消定时执行',
    classify: 460
  },
  TERMINATE_PIPELINE_TASK: {
    value: 460110,
    label: '停止执行任务',
    classify: 460
  },
  TERMINATE_PIPELINE_TASK_DETAIL: {
    value: 460120,
    label: '停止执行操作',
    classify: 460
  },
  SKIP_PIPELINE_TASK_DETAIL: {
    value: 460130,
    label: '跳过执行操作',
    classify: 460
  },
  ADD_TEMPLATE: {
    value: 500010,
    label: '添加模板',
    classify: 500
  },
  UPDATE_TEMPLATE: {
    value: 500020,
    label: '修改模板',
    classify: 500
  },
  DELETE_TEMPLATE: {
    value: 500030,
    label: '删除模板',
    classify: 500
  },
  ADD_WEBHOOK: {
    value: 510010,
    label: '添加配置',
    classify: 510
  },
  UPDATE_WEBHOOK: {
    value: 510020,
    label: '修改配置',
    classify: 510
  },
  DELETE_WEBHOOK: {
    value: 510030,
    label: '删除配置',
    classify: 510
  },
  CONFIG_IP_LIST: {
    value: 600010,
    label: '配置IP过滤器',
    classify: 600
  },
  RE_ANALYSIS_SYSTEM: {
    value: 600020,
    label: '系统统计分析',
    classify: 600
  },
  CLEAN_SYSTEM_FILE: {
    value: 600030,
    label: '清理系统文件',
    classify: 600
  },
  UPDATE_SYSTEM_OPTION: {
    value: 600040,
    label: '修改系统配置',
    classify: 600
  },
  ADD_SYSTEM_ENV: {
    value: 610010,
    label: '添加系统环境变量',
    classify: 610
  },
  UPDATE_SYSTEM_ENV: {
    value: 610020,
    label: '修改系统环境变量',
    classify: 610
  },
  DELETE_SYSTEM_ENV: {
    value: 610030,
    label: '删除系统环境变量',
    classify: 610
  },
  SAVE_SYSTEM_ENV: {
    value: 610040,
    label: '保存系统环境变量',
    classify: 610
  },
  DATA_CLEAR: {
    value: 620010,
    label: '清理数据',
    classify: 620
  },
  DATA_IMPORT: {
    value: 630010,
    label: '导入数据',
    classify: 630
  },
  DATA_EXPORT: {
    value: 640010,
    label: '导出数据',
    classify: 640
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
  },
  TERMINAL_ACTIVE_PUSH_HEARTBEAT: {
    key: 'terminalActivePushHeartbeat',
    value: 130
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
  },
  ALARM: {
    value: 30,
    label: '系统报警',
    icon: 'thunderbolt'
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
    redirect: row => `/batch/exec/list?id=${row.relId || ''}`
  },
  EXEC_FAILURE: {
    classify: 10,
    value: 1020,
    label: '命令执行失败',
    notify: 'error',
    duration: null,
    redirect: row => `/batch/exec/list?id=${row.relId || ''}`
  },
  REPOSITORY_INIT_SUCCESS: {
    classify: 10,
    value: 1030,
    label: '版本仓库初始化成功',
    notify: 'success',
    redirect: row => `/app/repo?id=${row.relId || ''}`
  },
  REPOSITORY_INIT_FAILURE: {
    classify: 10,
    value: 1040,
    label: '版本仓库初始化失败',
    notify: 'error',
    duration: null,
    redirect: row => `/app/repo?id=${row.relId || ''}`
  },
  BUILD_SUCCESS: {
    classify: 10,
    value: 1050,
    label: '构建执行成功',
    notify: 'success',
    redirect: row => `/app/build/list?id=${row.relId || ''}`
  },
  BUILD_FAILURE: {
    classify: 10,
    value: 1060,
    label: '构建执行失败',
    notify: 'error',
    duration: null,
    redirect: row => `/app/build/list?id=${row.relId || ''}`
  },
  RELEASE_AUDIT_RESOLVE: {
    classify: 10,
    value: 1070,
    label: '发布审批通过',
    notify: 'success',
    redirect: row => `/app/release/list?id=${row.relId || ''}`
  },
  RELEASE_AUDIT_REJECT: {
    classify: 10,
    value: 1080,
    label: '发布审批驳回',
    notify: 'warning',
    redirect: row => `/app/release/list?id=${row.relId || ''}`
  },
  RELEASE_SUCCESS: {
    classify: 10,
    value: 1090,
    label: '发布执行成功',
    notify: 'success',
    redirect: row => `/app/release/list?id=${row.relId || ''}`
  },
  RELEASE_FAILURE: {
    classify: 10,
    value: 1100,
    label: '发布执行失败',
    notify: 'error',
    duration: null,
    redirect: row => `/app/release/list?id=${row.relId || ''}`
  },
  PIPELINE_AUDIT_RESOLVE: {
    classify: 10,
    value: 1110,
    label: '应用流水线审批通过',
    notify: 'success',
    redirect: row => `/app/pipeline/task?id=${row.relId || ''}`
  },
  PIPELINE_AUDIT_REJECT: {
    classify: 10,
    value: 1120,
    label: '应用流水线审批驳回',
    notify: 'warning',
    redirect: row => `/app/pipeline/task?id=${row.relId || ''}`
  },
  PIPELINE_EXEC_SUCCESS: {
    classify: 10,
    value: 1130,
    label: '流水线执行成功',
    notify: 'success',
    redirect: row => `/app/pipeline/task?id=${row.relId || ''}`
  },
  PIPELINE_EXEC_FAILURE: {
    classify: 10,
    value: 1140,
    label: '流水线执行失败',
    notify: 'error',
    duration: null,
    redirect: row => `/app/pipeline/task?id=${row.relId || ''}`
  },
  MACHINE_AGENT_INSTALL_SUCCESS: {
    classify: 10,
    value: 1150,
    label: '机器监控插件安装成功',
    notify: 'success',
    redirect: row => `/machine/monitor/list?machineId=${row.relId || ''}`
  },
  MACHINE_AGENT_INSTALL_FAILURE: {
    classify: 10,
    value: 1160,
    label: '机器监控插件安装失败',
    notify: 'error',
    duration: null,
    redirect: row => `/machine/monitor/list?machineId=${row.relId || ''}`
  },
  DATA_IMPORT_SUCCESS: {
    classify: 20,
    value: 2010,
    label: '数据导入成功',
    notify: 'success',
    redirect: row => {
      if (!row.relId) {
        return null
      }
      return enumValueOf(IMPORT_TYPE, row.relId).redirect
    }
  },
  DATA_IMPORT_FAILURE: {
    classify: 20,
    value: 2020,
    label: '数据导入失败',
    notify: 'error',
    redirect: row => {
      if (!row.relId) {
        return null
      }
      return enumValueOf(IMPORT_TYPE, row.relId).redirect
    }
  },
  MACHINE_ALARM: {
    classify: 30,
    value: 3010,
    label: '机器发生报警',
    notify: 'error',
    duration: null,
    redirect: row => `/machine/monitor/metrics/${row.relId || ''}`
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
  DAY: { value: 10 },
  TOTAL: { value: 20 },
  REL_ID: { value: 30 }
}

/**
 * 数据导出类型
 */
export const EXPORT_TYPE = {
  MACHINE_INFO: { value: 100 },
  MACHINE_PROXY: { value: 110 },
  TERMINAL_LOG: { value: 120 },
  MACHINE_ALARM_HISTORY: { value: 130 },
  APP_PROFILE: { value: 200 },
  APPLICATION: { value: 210 },
  APP_REPOSITORY: { value: 220 },
  COMMAND_TEMPLATE: { value: 300 },
  USER_EVENT_LOG: { value: 310 },
  TAIL_FILE: { value: 320 },
  WEBHOOK: { value: 330 }
}

/**
 * 数据导入类型
 */
export const IMPORT_TYPE = {
  MACHINE_INFO: {
    value: 100,
    tips: '使用唯一标识来区分数据, 存在更新不存在新增, 优先使用导入密码',
    title: '机器信息 导入',
    redirect: '/machine/list'
  },
  MACHINE_PROXY: {
    value: 110,
    tips: '导入时优先使用导入密码',
    title: '机器代理 导入',
    redirect: '/machine/proxy'
  },
  TAIL_FILE: {
    value: 120,
    tips: '通过机器标识来区分机器, 机器名称无需填写',
    title: '日志文件 导入',
    redirect: '/log/list'
  },
  APP_PROFILE: {
    value: 200,
    tips: '使用唯一标识来区分数据, 存在更新不存在新增',
    title: '应用环境 导入',
    redirect: '/app/profile'
  },
  APPLICATION: {
    value: 210,
    tips: '使用唯一标识来区分数据, 存在更新不存在新增',
    title: '应用信息 导入',
    redirect: '/app/list'
  },
  APP_REPOSITORY: {
    value: 220,
    tips: '使用名称来区分数据, 存在更新不存在新增, 优先使用导入密码',
    title: '版本仓库 导入',
    redirect: '/app/repo'
  },
  COMMAND_TEMPLATE: {
    value: 300,
    tips: '使用模板名称来区分数据, 存在更新不存在新增',
    title: '命令模板 导入',
    redirect: '/template/list'
  },
  WEBHOOK: {
    value: 310,
    tips: '使用名称来区分数据, 存在更新不存在新增',
    title: 'webhook 导入',
    redirect: '/webhook/list'
  }
}

/**
 * 数据清理类型
 */
export const DATA_CLEAR_TYPE = {
  BATCH_EXEC: { value: 10 },
  TERMINAL_LOG: { value: 20 },
  SCHEDULER_RECORD: { value: 30 },
  APP_BUILD: { value: 40 },
  APP_RELEASE: { value: 50 },
  APP_PIPELINE_EXEC: { value: 60 },
  USER_EVENT_LOG: { value: 70 },
  MACHINE_ALARM_HISTORY: { value: 80 }
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

/**
 * webhook 类型
 */
export const WEBHOOK_TYPE = {
  DING_ROBOT: {
    value: 10,
    label: '钉钉机器人'
  }
}

/**
 * 机器报警类型
 */
export const MACHINE_ALARM_TYPE = {
  CPU_USAGE: {
    value: 10,
    label: 'CPU使用率',
    alarmProp: 'cpuAlarm'
  },
  MEMORY_USAGE: {
    value: 20,
    label: '内存使用率',
    alarmProp: 'memoryAlarm'
  }
}

/**
 * 树状结构移动类型
 */
export const TREE_MOVE_TYPE = {
  IN_TOP: {
    value: 1
  },
  IN_BOTTOM: {
    value: 2
  },
  PREV: {
    value: 3
  },
  NEXT: {
    value: 4
  }
}
