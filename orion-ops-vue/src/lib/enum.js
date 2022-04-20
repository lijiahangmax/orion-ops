const $enum = {

  /**
   * 用名称获取枚举值
   */
  nameOf(enumName, name) {
    return this[enumName][name]
  },

  /**
   * 用value获取枚举值
   */
  valueOf(e, value) {
    let _enum
    if (typeof e === 'string') {
      _enum = this[e]
    } else {
      _enum = e
    }
    for (const key in _enum) {
      const val = _enum[key]
      if (val && val.value === value) {
        return val
      }
    }
    return {}
  },

  /**
   * 终端操作
   */
  TERMINAL_OPERATOR: {
    /**
     * 建立连接
     */
    CONNECT: {
      value: '05'
    },
    /**
     * ping
     */
    PING: {
      value: '10'
    },
    /**
     * 更改大小
     */
    RESIZE: {
      value: '15'
    },
    /**
     * 键入key
     */
    KEY: {
      value: '20'
    },
    /**
     * 键入命令
     */
    COMMAND: {
      value: '25'
    },
    /**
     * 中断 ctrl+c
     */
    INTERRUPT: {
      value: '30'
    },
    /**
     *  挂起 ctrl+x
     */
    HANGUP: {
      value: '35'
    },
    /**
     * 关闭连接
     */
    DISCONNECT: {
      value: '40'
    }
  },

  /**
   * 终端状态
   */
  TERMINAL_STATUS: {
    NOT_CONNECT: {
      value: 0,
      label: '未连接',
      color: '#FFD43B'
    },
    UNAUTHORIZED: {
      value: 10,
      label: '未认证',
      color: '#82C91E'
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
  },

  /**
   * WS protocol
   */
  WS_PROTOCOL: {
    ACK: {
      value: '010',
      label: 'ack'
    },
    CONNECTED: {
      value: '015',
      label: 'connected'
    },
    PONG: {
      value: '020',
      label: 'pong'
    },
    OK: {
      value: '100',
      label: 'ok'
    },
    UNKNOWN_OPERATE: {
      value: '200',
      label: 'unknown_operate'
    },
    MISS_ARGUMENT: {
      value: '210',
      label: 'miss_argument'
    },
    ILLEGAL_BODY: {
      value: '220',
      label: 'illegal_body'
    }
  },

  /**
   * 文件类型
   */
  FILE_TYPE: {
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
  },

  /**
   * 启用状态
   */
  ENABLE_STATUS: {
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
  },

  /**
   * 启用状态
   */
  AUDIT_STATUS: {
    RESOLVE: {
      value: 10,
      label: '通过'
    },
    REJECT: {
      value: 20,
      label: '驳回'
    }
  },

  /**
   * 应用环境审核状态
   */
  PROFILE_AUDIT_STATUS: {
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
  },

  /**
   * 机器认证类型
   */
  MACHINE_AUTH_TYPE: {
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
  },

  /**
   * 机器代理方式
   */
  MACHINE_PROXY_TYPE: {
    HTTP: {
      value: 1,
      label: 'http',
      color: '#228BE6'
    },
    SOCKET4: {
      value: 2,
      label: 'socket4',
      color: '#1098AD'
    },
    SOCKET5: {
      value: 3,
      label: 'socket5',
      color: '#0CA678'
    }
  },

  /**
   * 秘钥挂载状态
   */
  MACHINE_KEY_MOUNT_STATUS: {
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
  },

  /**
   * 历史值类型
   */
  HISTORY_VALUE_TYPE: {
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
  },

  /**
   * 历史值操作类型
   */
  HISTORY_VALUE_OPTION_TYPE: {
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
  },

  /**
   * sftp 传输类型
   */
  SFTP_TRANSFER_TYPE: {
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
  },

  /**
   * sftp 传输状态
   */
  SFTP_TRANSFER_STATUS: {
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
  },

  /**
   * 批量执行
   */
  BATCH_EXEC_STATUS: {
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
  },

  /**
   * 文件 tail 模式
   */
  FILE_TAIL_MODE: {
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
  },

  /**
   * 文件 tail 类型
   */
  FILE_TAIL_TYPE: {
    EXEC_LOG: {
      value: 10,
      label: '命令执行日志'
    },
    TAIL_LIST: {
      value: 20,
      label: 'tail 列表'
    },
    APP_BUILD_LOG: {
      value: 30,
      label: '应用构建日志'
    },
    APP_RELEASE_LOG: {
      value: 40,
      label: '应用发布日志'
    },
    SCHEDULER_TASK_MACHINE_LOG: {
      value: 50,
      label: '调度任务机器日志'
    }
  },

  /**
   * 日志 tail 状态
   */
  LOG_TAIL_STATUS: {
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
  },

  /**
   * 文件下载类型
   */
  FILE_DOWNLOAD_TYPE: {
    SECRET_KEY: {
      value: 10,
      label: '秘钥'
    },
    TERMINAL_LOG: {
      value: 20,
      label: '终端日志'
    },
    EXEC_LOG: {
      value: 30,
      label: '命令执行日志'
    },
    SFTP_DOWNLOAD: {
      value: 40,
      label: 'sftp下载文件'
    },
    TAIL_LIST_FILE: {
      value: 50,
      label: 'tail列表文件'
    },
    APP_BUILD_LOG: {
      value: 60,
      label: '应用构建日志'
    },
    APP_BUILD_ACTION_LOG: {
      value: 70,
      label: '应用构建操作日志'
    },
    APP_BUILD_BUNDLE: {
      value: 80,
      label: '应用构建产物文件'
    },
    APP_RELEASE_MACHINE_LOG: {
      value: 90,
      label: '应用发布机器日志'
    },
    APP_RELEASE_ACTION_LOG: {
      value: 100,
      label: '应用发布操作日志'
    },
    SCHEDULER_TASK_MACHINE_LOG: {
      value: 110,
      label: '调度任务机器日志'
    }
  },

  /**
   * 角色类型
   */
  ROLE_TYPE: {
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
  },

  /**
   * 视图类型
   */
  VIEW_TYPE: {
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
  },

  /**
   * 仓库状态
   */
  VCS_STATUS: {
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
  },

  /**
   * 仓库认证方式
   */
  VCS_AUTH_TYPE: {
    PASSWORD: {
      value: 10,
      label: '密码认证'
    },
    TOKEN: {
      value: 20,
      label: '私人令牌'
    }
  },

  /**
   * 仓库令牌类型
   */
  VCS_TOKEN_TYPE: {
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
  },

  /**
   * 配置类型
   */
  CONFIG_STATUS: {
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
  },

  /**
   * 构建操作类型
   */
  BUILD_ACTION_TYPE: {
    CHECKOUT: {
      value: 110,
      label: '检出'
    },
    COMMAND: {
      value: 120,
      label: '主机命令'
    }
  },

  /**
   * 发布操作类型
   */
  RELEASE_ACTION_TYPE: {
    TRANSFER: {
      value: 210,
      label: '传输'
    },
    COMMAND: {
      value: 220,
      label: '目标主机命令'
    }
  },

  /**
   * 序列类型
   */
  SERIAL_TYPE: {
    SERIAL: {
      value: 10,
      label: '串行'
    },
    PARALLEL: {
      value: 20,
      label: '并行'
    }
  },

  /**
   * 发布产物为文件夹传输类型
   */
  RELEASE_TRANSFER_DIR_TYPE: {
    DIR: {
      value: 'dir',
      label: '文件夹'
    },
    ZIP: {
      value: 'zip',
      label: 'zip文件'
    }
  },

  /**
   * 构建状态
   */
  BUILD_STATUS: {
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
  },

  /**
   * 操作状态
   */
  ACTION_STATUS: {
    WAIT: {
      value: 10,
      color: '',
      label: '未开始',
      stepStatus: 'wait',
      actionStyle: {
        background: '#63E6BE'
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
      actionStyle: {
        background: '#A9E34B'
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
      actionStyle: {
        background: '#A5D8FF',
        'font-size': '14px'
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
      actionStyle: {
        background: '#FF6B6B',
        'font-size': '14px'
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
      actionStyle: {
        background: '#FFE066'
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
      actionStyle: {
        background: '#FFC078'
      },
      actionValue() {
        return '已停止'
      }
    }
  },

  /**
   * 发布状态
   */
  RELEASE_STATUS: {
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
  },

  /**
   * 发布类型
   */
  RELEASE_TYPE: {
    NORMAL: {
      value: 10,
      label: '正常发布'
    },
    ROLLBACK: {
      value: 20,
      label: '回滚发布'
    }
  },

  /**
   * 发布状态
   */
  TIMED_TYPE: {
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
  },

  /**
   * 操作阶段类型
   */
  STAGE_TYPE: {
    BUILD: {
      value: 10,
      label: '构建'
    },
    RELEASE: {
      value: 20,
      label: '发布'
    }
  },

  /**
   * 操作日志分类
   */
  EVENT_CLASSIFY: {
    AUTHENTICATION: {
      value: 5,
      label: '认证操作',
      type: {
        LOGIN: {
          value: 1005,
          label: '登陆'
        },
        LOGOUT: {
          value: 1010,
          label: '登出'
        },
        RESET_PASSWORD: {
          value: 1015,
          label: '重置密码'
        }
      }
    },
    MACHINE: {
      value: 10,
      label: '机器操作',
      type: {
        ADD_MACHINE: {
          value: 2005,
          label: '添加机器'
        },
        UPDATE_MACHINE: {
          value: 2010,
          label: '修改机器'
        },
        DELETE_MACHINE: {
          value: 2015,
          label: '删除机器'
        },
        CHANGE_MACHINE_STATUS: {
          value: 2020,
          label: '修改状态'
        },
        COPY_MACHINE: {
          value: 2025,
          label: '复制机器'
        }
      }
    },
    MACHINE_ENV: {
      value: 15,
      label: '机器变量操作',
      type: {
        DELETE_MACHINE_ENV: {
          value: 2105,
          label: '删除变量'
        },
        SYNC_MACHINE_ENV: {
          value: 2110,
          label: '同步变量'
        }
      }
    },
    MACHINE_KEY: {
      value: 20,
      label: '秘钥操作',
      type: {
        ADD_MACHINE_KEY: {
          value: 2205,
          label: '新增秘钥'
        },
        UPDATE_MACHINE_KEY: {
          value: 2210,
          label: '修改秘钥'
        },
        DELETE_MACHINE_KEY: {
          value: 2215,
          label: '删除秘钥'
        },
        MOUNT_MACHINE_KEY: {
          value: 2220,
          label: '挂载秘钥'
        },
        DUMP_MACHINE_KEY: {
          value: 2225,
          label: '卸载秘钥'
        },
        MOUNT_ALL_MACHINE_KEY: {
          value: 2230,
          label: '挂载全部秘钥'
        },
        DUMP_ALL_MACHINE_KEY: {
          value: 2235,
          label: '卸载全部秘钥'
        },
        TEMP_MOUNT_MACHINE_KEY: {
          value: 2240,
          label: '临时挂载秘钥'
        }
      }
    },
    MACHINE_PROXY: {
      value: 25,
      label: '代理操作',
      type: {
        ADD_MACHINE_PROXY: {
          value: 2305,
          label: '新增代理'
        },
        UPDATE_MACHINE_PROXY: {
          value: 2310,
          label: '修改代理'
        },
        DELETE_MACHINE_PROXY: {
          value: 2315,
          label: '删除代理'
        }
      }
    },
    TERMINAL: {
      value: 30,
      label: '终端操作',
      type: {
        OPEN_TERMINAL: {
          value: 2400,
          label: '打开机器终端'
        },
        FORCE_OFFLINE_TERMINAL: {
          value: 2405,
          label: '强制下线终端'
        },
        UPDATE_TERMINAL_CONFIG: {
          value: 2410,
          label: '修改终端配置'
        },
        DELETE_TERMINAL_LOG: {
          value: 2415,
          label: '删除终端日志'
        }
      }
    },
    SFTP: {
      value: 35,
      label: '远程文件操作',
      type: {
        OPEN_SFTP: {
          value: 2500,
          label: '打开机器SFTP'
        },
        SFTP_MKDIR: {
          value: 2505,
          label: '创建文件夹'
        },
        SFTP_TOUCH: {
          value: 2510,
          label: '创建文件'
        },
        SFTP_TRUNCATE: {
          value: 2515,
          label: '截断文件'
        },
        SFTP_MOVE: {
          value: 2520,
          label: '移动文件'
        },
        SFTP_REMOVE: {
          value: 2525,
          label: '删除文件'
        },
        SFTP_CHMOD: {
          value: 2530,
          label: '修改权限'
        },
        SFTP_CHOWN: {
          value: 2535,
          label: '修改所有者'
        },
        SFTP_CHGRP: {
          value: 2540,
          label: '修改所有组'
        },
        SFTP_UPLOAD: {
          value: 2545,
          label: '上传文件'
        },
        SFTP_DOWNLOAD: {
          value: 2550,
          label: '下载文件'
        },
        SFTP_PACKAGE: {
          value: 2555,
          label: '打包文件'
        }
      }
    },
    EXEC: {
      value: 40,
      label: '批量执行操作',
      type: {
        EXEC_SUBMIT: {
          value: 2605,
          label: '批量执行'
        },
        EXEC_DELETE: {
          value: 2610,
          label: '删除执行'
        },
        EXEC_TERMINATE: {
          value: 2615,
          label: '停止执行'
        }
      }
    },
    TAIL: {
      value: 45,
      label: '日志文件操作',
      type: {
        ADD_TAIL_FILE: {
          value: 2705,
          label: '添加文件'
        },
        UPDATE_TAIL_FILE: {
          value: 2710,
          label: '修改文件'
        },
        DELETE_TAIL_FILE: {
          value: 2715,
          label: '删除文件'
        }
      }
    },
    TEMPLATE: {
      value: 50,
      label: '模板操作',
      type: {
        ADD_TEMPLATE: {
          value: 2805,
          label: '添加模板'
        },
        UPDATE_TEMPLATE: {
          value: 2810,
          label: '修改模板'
        },
        DELETE_TEMPLATE: {
          value: 2815,
          label: '删除模板'
        }
      }
    },
    USER: {
      value: 55,
      label: '用户操作',
      type: {
        ADD_USER: {
          value: 1105,
          label: '添加用户'
        },
        UPDATE_USER: {
          value: 1110,
          label: '修改信息'
        },
        DELETE_USER: {
          value: 1115,
          label: '删除用户'
        },
        CHANGE_USER_STATUS: {
          value: 1120,
          label: '修改状态'
        },
        UNLOCK_USER: {
          value: 1125,
          label: '解锁用户'
        }
      }
    },
    APP: {
      value: 60,
      label: '应用操作',
      type: {
        ADD_APP: {
          value: 3005,
          label: '添加应用'
        },
        UPDATE_APP: {
          value: 3010,
          label: '修改应用'
        },
        DELETE_APP: {
          value: 3015,
          label: '删除应用'
        },
        CONFIG_APP: {
          value: 3020,
          label: '配置应用'
        },
        SYNC_APP: {
          value: 3025,
          label: '同步应用'
        },
        COPY_APP: {
          value: 3030,
          label: '复制应用'
        }
      }
    },
    PROFILE: {
      value: 65,
      label: '环境操作',
      type: {
        ADD_PROFILE: {
          value: 3105,
          label: '添加环境'
        },
        UPDATE_PROFILE: {
          value: 3110,
          label: '修改环境'
        },
        DELETE_PROFILE: {
          value: 3115,
          label: '删除环境'
        }
      }
    },
    APP_ENV: {
      value: 70,
      label: '应用变量操作',
      type: {
        DELETE_APP_ENV: {
          value: 3205,
          label: '删除变量'
        },
        SYNC_APP_ENV: {
          value: 3210,
          label: '同步变量'
        }
      }
    },
    VCS: {
      value: 75,
      label: '应用仓库操作',
      type: {
        ADD_VCS: {
          value: 3305,
          label: '添加仓库'
        },
        INIT_VCS: {
          value: 3310,
          label: '初始化仓库'
        },
        RE_INIT_VCS: {
          value: 3315,
          label: '重新初始化'
        },
        UPDATE_VCS: {
          value: 3320,
          label: '更新仓库'
        },
        DELETE_VCS: {
          value: 3325,
          label: '删除仓库'
        },
        CLEAN_VCS: {
          value: 3330,
          label: '清空仓库'
        }
      }
    },
    BUILD: {
      value: 80,
      label: '应用构建操作',
      type: {
        SUBMIT_BUILD: {
          value: 4005,
          label: '提交构建'
        },
        BUILD_TERMINATE: {
          value: 4010,
          label: '停止构建'
        },
        DELETE_BUILD: {
          value: 4015,
          label: '删除构建'
        },
        SUBMIT_REBUILD: {
          value: 4020,
          label: '重新构建'
        }
      }
    },
    RELEASE: {
      value: 85,
      label: '应用发布操作',
      type: {
        SUBMIT_RELEASE: {
          value: 5005,
          label: '提交发布'
        },
        AUDIT_RELEASE: {
          value: 5010,
          label: '发布审核'
        },
        RUNNABLE_RELEASE: {
          value: 5015,
          label: '执行发布'
        },
        ROLLBACK_RELEASE: {
          value: 5020,
          label: '回滚发布'
        },
        TERMINATE_RELEASE: {
          value: 5025,
          label: '停止发布'
        },
        DELETE_RELEASE: {
          value: 5030,
          label: '删除发布'
        },
        COPY_RELEASE: {
          value: 5035,
          label: '复制发布'
        },
        CANCEL_TIMED_RELEASE: {
          value: 5040,
          label: '取消发布'
        },
        SET_TIMED_RELEASE: {
          value: 5045,
          label: '设置定时发布'
        },
        TERMINATE_MACHINE_RELEASE: {
          value: 5050,
          label: '停止机器操作'
        },
        SKIP_MACHINE_RELEASE: {
          value: 5055,
          label: '跳过机器操作'
        }
      }
    },
    APP_PIPELINE: {
      value: 88,
      label: '应用流水线',
      type: {
        ADD_PIPELINE: {
          value: 5505,
          label: '添加流水线'
        },
        UPDATE_PIPELINE: {
          value: 5510,
          label: '修改流水线'
        },
        DELETE_PIPELINE: {
          value: 5515,
          label: '删除流水线'
        },
        SUBMIT_PIPELINE_EXEC: {
          value: 5605,
          label: '提交执行任务'
        },
        AUDIT_PIPELINE_EXEC: {
          value: 5610,
          label: '审核任务'
        },
        COPY_PIPELINE_EXEC: {
          value: 5615,
          label: '复制任务'
        },
        EXEC_PIPELINE_EXEC: {
          value: 5620,
          label: '执行任务'
        },
        DELETE_PIPELINE_EXEC: {
          value: 5625,
          label: '删除任务'
        },
        SET_PIPELINE_TIMED_EXEC: {
          value: 5630,
          label: '设置定时执行'
        },
        CANCEL_PIPELINE_TIMED_EXEC: {
          value: 5635,
          label: '取消定时执行'
        },
        TERMINATE_PIPELINE_EXEC: {
          value: 5640,
          label: '停止执行任务'
        },
        TERMINATE_PIPELINE_EXEC_DETAIL: {
          value: 5645,
          label: '停止执行部分操作'
        },
        SKIP_PIPELINE_EXEC_DETAIL: {
          value: 5650,
          label: '跳过执行部分操作'
        }
      }
    },
    SYSTEM_ENV: {
      value: 90,
      label: '系统变量操作',
      type: {
        ADD_SYSTEM_ENV: {
          value: 6005,
          label: '添加变量'
        },
        UPDATE_SYSTEM_ENV: {
          value: 6010,
          label: '更新变量'
        },
        DELETE_SYSTEM_ENV: {
          value: 6015,
          label: '删除变量'
        },
        SAVE_SYSTEM_ENV: {
          value: 6020,
          label: '保存变量'
        }
      }
    },
    SYSTEM: {
      value: 95,
      label: '系统操作',
      type: {
        CONFIG_IP_LIST: {
          value: 6105,
          label: '配置IP过滤器'
        },
        RE_ANALYSIS_SYSTEM: {
          value: 6110,
          label: '系统统计分析'
        },
        CLEAN_SYSTEM_FILE: {
          value: 6115,
          label: '清理系统文件'
        },
        UPDATE_SYSTEM_CONFIG: {
          value: 6120,
          label: '修改系统配置'
        }
      }
    },
    SCHEDULER: {
      value: 100,
      label: '调度任务操作',
      type: {
        ADD_SCHEDULER_TASK: {
          value: 7105,
          label: '添加调度任务'
        },
        UPDATE_SCHEDULER_TASK: {
          value: 7110,
          label: '修改调度任务'
        },
        UPDATE_SCHEDULER_TASK_STATUS: {
          value: 7115,
          label: '更新任务状态'
        },
        DELETE_SCHEDULER_TASK: {
          value: 7120,
          label: '删除调度任务'
        },
        MANUAL_TRIGGER_SCHEDULER_TASK: {
          value: 7125,
          label: '手动触发任务'
        },
        TERMINATE_ALL_SCHEDULER_TASK: {
          value: 7130,
          label: '停止任务'
        },
        TERMINATE_SCHEDULER_TASK_MACHINE: {
          value: 7135,
          label: '停止机器操作'
        },
        SKIP_SCHEDULER_TASK_MACHINE: {
          value: 7140,
          label: '跳过机器操作'
        },
        DELETE_TASK_RECORD: {
          value: 7145,
          label: '删除调度明细'
        }
      }
    }
  },

  /**
   * 系统清理类型
   */
  SYSTEM_CLEAR_TYPE: {
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
    VCS_FILE: {
      key: 'vcsFile',
      value: 50,
      label: '旧版本应用仓库'
    }
  },

  /**
   * 系统配置项
   */
  SYSTEM_OPTION_KEY: {
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
  },

  /**
   * 调度任务执行状态
   */
  SCHEDULER_TASK_STATUS: {
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
  },

  /**
   * 调度任务执行机器状态
   */
  SCHEDULER_TASK_MACHINE_STATUS: {
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
  },

  /**
   * 异常处理类型
   */
  EXCEPTION_HANDLER_TYPE: {
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
  },

  /**
   * 线程池指标类型
   */
  THREAD_POOL_METRICS_TYPE: {
    TERMINAL: {
      value: 10,
      label: '远程终端线程池'
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
  },

  /**
   * 阅读状态
   */
  READ_STATUS: {
    UNREAD: {
      value: 1,
      label: '未读'
    },
    READ: {
      value: 2,
      label: '已读'
    }
  },

  /**
   * 消息分类
   */
  MESSAGE_CLASSIFY: {
    SYSTEM: {
      value: 10,
      label: '系统消息',
      type: {
        EXEC_SUCCESS: {
          value: 1010,
          label: '命令执行完成',
          notify: 'success',
          redirect: '#/batch/exec/list'
        },
        EXEC_FAILURE: {
          value: 1020,
          label: '命令执行失败',
          notify: 'error',
          redirect: '#/batch/exec/list'
        },
        VCS_INIT_SUCCESS: {
          value: 1030,
          label: '版本仓库初始化成功',
          notify: 'success',
          redirect: '#/app/vcs'
        },
        VCS_INIT_FAILURE: {
          value: 1040,
          label: '版本仓库初始化失败',
          notify: 'error',
          redirect: '#/app/vcs'
        },
        BUILD_SUCCESS: {
          value: 1050,
          label: '构建执行成功',
          notify: 'success',
          redirect: '#/app/build/list'
        },
        BUILD_FAILURE: {
          value: 1060,
          label: '构建执行失败',
          notify: 'error',
          redirect: '#/app/build/list'
        },
        RELEASE_AUDIT_RESOLVE: {
          value: 1070,
          label: '发布审批通过',
          notify: 'success',
          redirect: '#/app/release/list'
        },
        RELEASE_AUDIT_REJECT: {
          value: 1080,
          label: '发布审批驳回',
          notify: 'warning',
          redirect: '#/app/release/list'
        },
        RELEASE_SUCCESS: {
          value: 1090,
          label: '发布执行成功',
          notify: 'success',
          redirect: '#/app/release/list'
        },
        RELEASE_FAILURE: {
          value: 1100,
          label: '发布执行失败',
          notify: 'error',
          redirect: '#/app/release/list'
        },
        PIPELINE_AUDIT_RESOLVE: {
          value: 1110,
          label: '应用流水线审批通过',
          notify: 'success',
          redirect: '#/app/pipeline/record'
        },
        PIPELINE_AUDIT_REJECT: {
          value: 1120,
          label: '应用流水线审批驳回',
          notify: 'warning',
          redirect: '#/app/pipeline/record'
        },
        PIPELINE_EXEC_SUCCESS: {
          value: 1130,
          label: '流水线执行成功',
          notify: 'success',
          redirect: '#/app/pipeline/record'
        },
        PIPELINE_EXEC_FAILURE: {
          value: 1140,
          label: '流水线执行失败',
          notify: 'error',
          redirect: '#/app/pipeline/record'
        }
      }
    }
  },

  /**
   * 流水线状态
   */
  PIPELINE_STATUS: {
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
  },

  /**
   * 流水线操作状态
   */
  PIPELINE_DETAIL_STATUS: {
    WAIT: {
      value: 10,
      color: '',
      label: '未开始'
    },
    RUNNABLE: {
      value: 20,
      color: 'green',
      label: '进行中'
    },
    FINISH: {
      value: 30,
      color: 'blue',
      label: '已完成'
    },
    FAILURE: {
      value: 40,
      color: 'red',
      label: '已失败'
    },
    SKIPPED: {
      value: 50,
      color: 'orange',
      label: '已跳过'
    },
    TERMINATED: {
      value: 60,
      color: 'orange',
      label: '已停止'
    }
  },

  /**
   * 流水线日志状态
   */
  PIPELINE_LOG_STATUS: {
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

}

export default $enum
