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
      color: '#e03131'
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
      color: '#845ef7'
    },
    KEY: {
      value: 2,
      label: '秘钥认证',
      color: '#5c7cfa'
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
      color: '#dee2e6'
    },
    MOUNTED: {
      value: 2,
      label: '已挂载',
      color: '#4c6ef5'
    },
    C: {
      value: 3,
      label: '未挂载',
      color: '#ff922b'
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
      color: '#94d82d'
    },
    UPDATE: {
      value: 2,
      label: '修改',
      color: '#4c6ef5'
    },
    DELETE: {
      value: 3,
      label: '删除',
      color: '#ff922b'
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
    RELEASE_HOST_LOG: {
      value: 50,
      label: '上线单宿主机日志'
    },
    RELEASE_STAGE_LOG: {
      value: 60,
      label: '上线单目标机器日志'
    },
    RELEASE_SNAPSHOT: {
      value: 70,
      label: '上线单快照文件'
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
  }

}

export default $enum
