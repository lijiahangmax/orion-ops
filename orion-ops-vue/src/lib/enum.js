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
    return null
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
      label: 'akc'
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
   * 机器状态
   */
  MACHINE_STATUS: {
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
      color: '#228be6'
    },
    SOCKET4: {
      value: 2,
      label: 'socket4',
      color: '#1098ad'
    },
    SOCKET5: {
      value: 3,
      label: 'socket5',
      color: '#0ca678'
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
  }

}

export default $enum
