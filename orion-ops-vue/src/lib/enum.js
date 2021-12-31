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
    for (var key in _enum) {
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
      'badge-status': 'processing'
    },
    DISABLE: {
      value: 2,
      label: '停用',
      'badge-status': 'default'
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
      color: '#228BE6'
    },
    DISABLE: {
      value: 2,
      label: '无需审核',
      color: '#40C057'
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
      color: '#FF922B'
    },
    RUNNABLE: {
      value: 20,
      label: '执行中',
      color: '#51cf66'
    },
    COMPLETE: {
      value: 30,
      label: '执行完成',
      color: '#4C6EF5'
    },
    EXCEPTION: {
      value: 40,
      label: '执行异常',
      color: '#F03E3E'
    },
    TERMINATED: {
      value: 50,
      label: '执行终止',
      color: '#868E96'
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
   * 角色类型
   */
  VCS_STATUS: {
    UNINITIALIZED: {
      value: 10,
      label: '未初始化',
      color: '#CED4DA'
    },
    INITIALIZING: {
      value: 20,
      label: '未初始中',
      color: '#51CF66'
    },
    OK: {
      value: 30,
      label: '正常',
      color: '#228BE6'
    },
    ERROR: {
      value: 40,
      label: '失败',
      color: '#FD7E14'
    }
  },

  /**
   * 配置类型
   */
  CONFIG_STATUS: {
    CONFIGURED: {
      value: 1,
      label: '已配置',
      color: '#4C6EF5'
    },
    NOT_CONFIGURED: {
      value: 2,
      label: '未配置',
      color: '#DEE2E6'
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
   * 发布序列类型
   */
  RELEASE_SERIAL_TYPE: {
    SERIAL: {
      value: 10,
      label: '串行发布'
    },
    PARALLEL: {
      value: 20,
      label: '并行发布'
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
      color: '#DEE2E6',
      label: '未开始',
      stepStatus: 'wait'
    },
    RUNNABLE: {
      value: 20,
      color: '#51CF66',
      label: '进行中',
      stepStatus: 'process'
    },
    FINISH: {
      value: 30,
      color: '#4C6EF5',
      label: '构建完成',
      stepStatus: 'finish'
    },
    FAILURE: {
      value: 40,
      color: '#F03E3E',
      label: '构建失败',
      stepStatus: 'error'
    },
    TERMINATED: {
      value: 50,
      color: '#FD7E14',
      label: '已终止',
      stepStatus: 'finish'
    }
  },

  /**
   * 操作状态
   */
  ACTION_STATUS: {
    WAIT: {
      value: 10,
      color: '#DEE2E6',
      label: '未开始',
      stepStatus: 'wait'
    },
    RUNNABLE: {
      value: 20,
      color: '#51CF66',
      label: '进行中',
      stepStatus: 'process'
    },
    FINISH: {
      value: 30,
      color: '#4C6EF5',
      label: '已完成',
      stepStatus: 'finish'
    },
    FAILURE: {
      value: 40,
      color: '#F03E3E',
      label: '执行失败',
      stepStatus: 'error'
    },
    SKIPPED: {
      value: 50,
      color: '#FCC419',
      label: '已跳过',
      stepStatus: 'finish'
    },
    TERMINATED: {
      value: 60,
      color: '#FD7E14',
      label: '已终止',
      stepStatus: 'finish'
    }
  },

  /**
   * 发布状态
   */
  RELEASE_STATUS: {
    WAIT_AUDIT: {
      value: 10,
      color: '#CED4DA',
      label: '待审核'
    },
    AUDIT_REJECT: {
      value: 20,
      color: '#FF922B',
      label: '审核驳回'
    },
    WAIT_RUNNABLE: {
      value: 30,
      color: '#12B886',
      label: '待发布'
    },
    RUNNABLE: {
      value: 40,
      color: '#51CF66',
      label: '发布中'
    },
    FINISH: {
      value: 50,
      color: '#4C6EF5',
      label: '发布完成'
    },
    TERMINATED: {
      value: 60,
      color: '#FD7E14',
      label: '发布停止'
    },
    INITIAL_ERROR: {
      value: 70,
      color: '#F03E3E',
      label: '初始化失败'
    },
    FAILURE: {
      value: 80,
      color: '#C92A2A',
      label: '发布失败'
    }
  },

  /**
   * 发布类型
   */
  RELEASE_TYPE: {
    WAIT_AUDIT: {
      value: 10,
      label: '正常发布'
    },
    AUDIT_REJECT: {
      value: 20,
      label: '回滚发布'
    }
  },

  /**
   * 发布状态
   */
  TIMED_RELEASE_TYPE: {
    NORMAL: {
      value: 10,
      label: '普通发布'
    },
    TIMED: {
      value: 20,
      label: '定时发布'
    }
  }

}

export default $enum
