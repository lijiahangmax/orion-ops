import $http from './http'

/**
 * webSocket
 */
const $ws = {

  /**
   * 机器终端
   */
  terminal: param => {
    return `ws://${$http.BASE_HOST}/orion/keep-alive/machine/terminal/${param}`
  },

  /**
   * sftp传输列表
   */
  sftpNotify: param => {
    return `ws://${$http.BASE_HOST}/orion/keep-alive/sftp/notify/${param}`
  }

}

/**
 * url
 */
const $url = {

  /**
   * 执行下载文件请求
   */
  fileDownloadExec: param => {
    return `${$http.BASE_URL}/orion/api/file-download/${param.token}/exec`
  }

}

const $api = {

  /**
   * webSocketUrl
   */
  ...$ws,

  /**
   * url
   */
  ...$url,

  /**
   * 登陆
   */
  login: param => {
    return $http.$post('/auth/login', param, { auth: false })
  },

  /**
   * 登出
   */
  logout: param => {
    return $http.$post('/auth/logout', param, { auth: false })
  },

  /**
   * 修改密码
   */
  resetPassword: param => {
    return $http.$post('/auth/reset', param, { skipErrorMessage: true })
  },

  /**
   * 获取菜单
   */
  getMenu: () => {
    return $http.$post('/common/menu')
  },

  /**
   * 环境列表
   */
  getProfileList: param => {
    return $http.$post('/app-profile/list', param)
  },

  /**
   * 获取用户列表
   */
  getUserList: param => {
    return $http.$post('/user/list', param)
  },

  /**
   * 获取用户详情
   */
  getUserDetail: param => {
    return $http.$post('/user/detail', param)
  },

  /**
   * 添加机器
   */
  addMachine: param => {
    return $http.$post('/machine/add', param)
  },

  /**
   * 修改机器
   */
  updateMachine: param => {
    return $http.$post('/machine/update', param)
  },

  /**
   * 删除机器
   */
  deleteMachine: param => {
    return $http.$post('/machine/delete', param)
  },

  /**
   * 修改机器状态
   */
  updateMachineStatus: param => {
    return $http.$post('/machine/status', param)
  },

  /**
   * 机器列表
   */
  getMachineList: param => {
    return $http.$post('/machine/list', param)
  },

  /**
   * 机器详情
   */
  getMachineDetail: param => {
    return $http.$post('/machine/detail', param)
  },

  /**
   * 复制机器
   */
  copyMachine: param => {
    return $http.$post('/machine/copy', param)
  },

  /**
   * 测试机器ping
   */
  machineTestPing: param => {
    return $http.$post('/machine/test/ping', param, { skipErrorMessage: true })
  },

  /**
   * 测试机器连接
   */
  machineTestConnect: param => {
    return $http.$post('/machine/test/connect', param, { skipErrorMessage: true })
  },

  /**
   * 获取终端访问信息
   */
  accessTerminal: param => {
    return $http.$post('/terminal/access', param, { skipErrorMessage: true })
  },

  /**
   * 获取支持的终端类型
   */
  getTerminalSupportPyt: param => {
    return $http.$post('/terminal/support/pty', param)
  },

  /**
   * 获取机器终端配置
   */
  getTerminalSetting: param => {
    return $http.$post(`/terminal/get/${param.machineId}`, param)
  },

  /**
   * 修改机器终端配置
   */
  updateTerminalSetting: param => {
    return $http.$post('/terminal/update', param, { skipErrorMessage: true })
  },

  /**
   * sftp 打开连接
   */
  sftpOpen: param => {
    return $http.$post('/sftp/open', param, { skipErrorMessage: true })
  },

  /**
   * sftp 文件列表
   */
  sftpList: param => {
    return $http.$post('/sftp/list', param)
  },

  /**
   * sftp 文件夹列表
   */
  sftpListDir: param => {
    return $http.$post('/sftp/list-dir', param)
  },

  /**
   * sftp 创建目录
   */
  sftpMkdir: param => {
    return $http.$post('/sftp/mkdir', param, { skipErrorMessage: true })
  },

  /**
   * sftp 创建文件
   */
  sftpTouch: param => {
    return $http.$post('/sftp/touch', param, { skipErrorMessage: true })
  },

  /**
   * sftp 移动文件
   */
  sftpMove: param => {
    return $http.$post('/sftp/move', param, { skipErrorMessage: true })
  },

  /**
   * sftp 删除文件
   */
  sftpRemove: param => {
    return $http.$post('/sftp/remove', param)
  },

  /**
   * sftp 修改权限
   */
  sftpChmod: param => {
    return $http.$post('/sftp/chmod', param, { skipErrorMessage: true })
  },

  /**
   * sftp 截断文件
   */
  sftpTruncate: param => {
    return $http.$post('/sftp/truncate', param)
  },

  /**
   * sftp 修改所有者
   */
  sftpChown: param => {
    return $http.$post('/sftp/chown', param)
  },

  /**
   * sftp 修改组
   */
  sftpChgrp: param => {
    return $http.$post('/sftp/chgrp', param)
  },

  /**
   * sftp 检查文件是否存在
   */
  sftpCheckFilePresent: param => {
    return $http.$post('/sftp/check/present', param)
  },

  /**
   * sftp 获取上传文件fileToken
   */
  getSftpUploadToken: param => {
    return $http.$post(`/sftp/upload/${param.sessionToken}/token`, param)
  },

  /**
   * sftp 上传文件
   */
  sftpUploadExec: param => {
    return $http.$post('/sftp/upload/exec', param, {
      timeout: 18000000,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * sftp 下载文件
   */
  sftpDownloadExec: param => {
    return $http.$post('/sftp/download/exec', param, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输列表
   */
  sftpTransferList: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/list`, param)
  },

  /**
   * sftp 传输暂停
   */
  sftpTransferPause: param => {
    return $http.$post(`/sftp/transfer/${param.fileToken}/pause`, param)
  },

  /**
   * sftp 传输恢复
   */
  sftpTransferResume: param => {
    return $http.$post(`/sftp/transfer/${param.fileToken}/resume`, param)
  },

  /**
   * sftp 传输失败重试
   */
  sftpTransferRetry: param => {
    return $http.$post(`/sftp/transfer/${param.fileToken}/retry`, param)
  },

  /**
   * sftp 传输暂停全部
   */
  sftpTransferPauseAll: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/pause/all`, param, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输恢复全部
   */
  sftpTransferResumeAll: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/resume/all`, param, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输失败重试全部
   */
  sftpTransferRetryAll: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/retry/all`, param, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输删除(单个)
   */
  sftpTransferRemove: param => {
    return $http.$post(`/sftp/transfer/${param.fileToken}/remove`, param)
  },

  /**
   * sftp 传输清空(全部)
   */
  sftpTransferClear: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/clear`, param)
  },

  /**
   * sftp 传输打包
   */
  sftpTransferPackage: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/${param.packageType}/package`, param)
  },

  /**
   * 获取文件下载token
   */
  getFileDownloadToken: param => {
    return $http.$post('/file-download/token', param)
  },

  /**
   * 添加机器代理
   */
  addMachineProxy: param => {
    return $http.$post('/proxy/add', param)
  },

  /**
   * 更新机器代理
   */
  updateMachineProxy: param => {
    return $http.$post('/proxy/update', param)
  },

  /**
   * 机器代理列表
   */
  getMachineProxyList: param => {
    return $http.$post('/proxy/list', param)
  },

  /**
   * 删除机器代理
   */
  deleteMachineProxy: param => {
    return $http.$post('/proxy/delete', param)
  },

  /**
   * 添加秘钥
   */
  addMachineKey: param => {
    return $http.$post('/key/add', param)
  },

  /**
   *更新秘钥
   */
  updateMachineKey: param => {
    return $http.$post('/key/update', param)
  },

  /**
   * 删除秘钥
   */
  removeMachineKey: param => {
    return $http.$post('/key/remove', param)
  },

  /**
   * 查询秘钥列表
   */
  getMachineKeyList: param => {
    return $http.$post('/key/list', param)
  },

  /**
   *挂载秘钥
   */
  mountMachineKey: param => {
    return $http.$post('/key/mount', param, { skipErrorMessage: true })
  },

  /**
   * 卸载秘钥
   */
  dumpMachineKey: param => {
    return $http.$post('/key/dump', param, { skipErrorMessage: true })
  },

  /**
   * 挂载所有秘钥
   */
  mountAllMachineKey: () => {
    return $http.$post('/key/mount-all', { skipErrorMessage: true })
  },

  /**
   * 卸载所有秘钥
   */
  dumpAllMachineKey: () => {
    return $http.$post('/key/dump-all', { skipErrorMessage: true })
  },

  /**
   * 临时挂载秘钥
   */
  tempMountMachineKey: param => {
    return $http.$post('/key/temp-mount', param)
  },

  /**
   * 添加机器环境变量
   */
  addMachineEnv: param => {
    return $http.$post('/env/add', param)
  },

  /**
   * 更新机器环境变量
   */
  updateMachineEnv: param => {
    return $http.$post('/env/update', param)
  },

  /**
   * 删除机器环境变量
   */
  deleteMachineEnv: param => {
    return $http.$post('/env/delete', param)
  },

  /**
   * 获取机器环境变量列表
   */
  getMachineEnvList: param => {
    return $http.$post('/env/list', param)
  },

  /**
   * 获取机器环境变量视图
   */
  getMachineEnvView: param => {
    return $http.$post('/env/view', param)
  },

  /**
   * 获取历史值列表
   */
  getHistoryValueList: param => {
    return $http.$post('/history-value/list', param)
  },

  /**
   * 回滚历史值
   */
  rollbackHistoryValue: param => {
    return $http.$post('/history-value/rollback', param)
  },

  /**
   * 执行提交
   */
  submitExecTask: param => {
    return $http.$post('/exec/submit', param)
  },

  /**
   * 执行列表
   */
  getExecList: param => {
    return $http.$post('/exec/list', param)
  },

  /**
   * 执行详情
   */
  getExecDetail: param => {
    return $http.$post('/exec/detail', param)
  },

  /**
   * 执行停止
   */
  terminatedExecTask: param => {
    return $http.$post('/exec/terminated', param)
  }

}

export default $api
