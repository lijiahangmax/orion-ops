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
    return $http.$post('/auth/reset', param)
  },

  /**
   * 环境列表
   */
  getProfileList: param => {
    return $http.$post('/app-profile/list', param)
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
    return $http.$post('/machine/test/ping', param)
  },

  /**
   * 测试机器连接
   */
  machineTestConnect: param => {
    return $http.$post('/machine/test/connect', param)
  },

  /**
   * 同步机器属性
   */
  syncMachineProp: param => {
    return $http.$post('/machine/sync/prop', param)
  },

  /**
   * 获取终端访问信息
   */
  accessTerminal: param => {
    return $http.$post('/terminal/access', param)
  },

  /**
   * 获取支持的终端类型
   */
  getTerminalSupportPyt: param => {
    return $http.$post('/terminal/support/pty', param)
  },

  /**
   * 修改机器终端配置
   */
  updateTerminalSetting: param => {
    return $http.$post('/terminal/setting', param)
  },

  /**
   * sftp 打开连接
   */
  sftpOpen: param => {
    return $http.$post('/sftp/open', param)
  },

  /**
   * sftp 文件列表
   */
  sftpList: param => {
    return $http.$post('/sftp/list', param)
  },

  /**
   * sftp 创建目录
   */
  sftpMkdir: param => {
    return $http.$post('/sftp/mkdir', param)
  },

  /**
   * sftp 创建文件
   */
  sftpTouch: param => {
    return $http.$post('/sftp/touch', param)
  },

  /**
   * sftp 移动文件
   */
  sftpMove: param => {
    return $http.$post('/sftp/move', param)
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
    return $http.$post('/sftp/chmod', param)
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
    return $http.$post('/sftp/download/exec', param)
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
    return $http.$post('/sftp/transfer/pause', param)
  },

  /**
   * sftp 传输恢复
   */
  sftpTransferResume: param => {
    return $http.$post('/sftp/transfer/resume', param)
  },

  /**
   * sftp 传输删除(单个)
   */
  sftpTransferRemove: param => {
    return $http.$post('/sftp/transfer/remove', param)
  },

  /**
   * sftp 传输清空(全部)
   */
  sftpTransferClear: param => {
    return $http.$post(`/sftp/transfer/${param.sessionToken}/clear`, param)
  },

  /**
   * 获取文件下载token
   */
  getFileDownloadToken: param => {
    return $http.$post('/file-download/token', param)
  }

}

export default $api
