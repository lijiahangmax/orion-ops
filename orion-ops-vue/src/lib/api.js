import $http from './http'

/**
 * webSocket
 */
const $ws = {

  /**
   * 机器终端
   */
  terminal: param => {
    return `ws://${$http.BASE_HOST}/orion/keep-alive/machine/terminal/${param.token}`
  },

  /**
   * sftp传输列表
   */
  sftpNotify: param => {
    return `ws://${$http.BASE_HOST}/orion/keep-alive/sftp/notify/${param.token}`
  },

  /**
   * 文件tail
   */
  fileTail: param => {
    return `ws://${$http.BASE_HOST}/orion/keep-alive/tail/${param.token}`
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
   * 获取菜单
   */
  getMenu: () => {
    return $http.$post('/common/menu')
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
   * 新增用户
   */
  addUser: param => {
    return $http.$post('/user/add', param)
  },

  /**
   * 更新用户
   */
  updateUser: param => {
    return $http.$post('/user/update', param)
  },

  /**
   * 更新用户
   */
  updateAvatar: param => {
    return $http.$post('/user/update/avatar', param, {
      timeout: 600000
    })
  },

  /**
   * 删除用户
   */
  deleteUser: param => {
    return $http.$post('/user/delete', param)
  },

  /**
   * 更新用户
   */
  updateUserStatus: param => {
    return $http.$post('/user/status', param)
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
    return $http.$post('/machine/copy', param, {
      timeout: 600000
    })
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
   * 终端日志列表
   */
  terminalLogList: param => {
    return $http.$post('/terminal/log/list', param)
  },

  /**
   * 终端会话列表
   */
  terminalSessionList: param => {
    return $http.$post('/terminal/manager/session/list', param)
  },

  /**
   * 强制下线终端
   */
  terminalOffline: param => {
    return $http.$post('/terminal/manager/offline', param)
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
   * sftp 重新上传
   */
  sftpTransferReUpload: param => {
    return $http.$post(`/sftp/transfer/${param.fileToken}/re/upload`, param)
  },

  /**
   * sftp 重新下载
   */
  sftpTransferReDownload: param => {
    return $http.$post(`/sftp/transfer/${param.fileToken}/re/download`, param)
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
   * 机器代理详情
   */
  getMachineProxyDetail: param => {
    return $http.$post('/proxy/detail', param)
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
   * 查询秘钥详情
   */
  getMachineKeyDetail: param => {
    return $http.$post('/key/detail', param)
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
    return $http.$post('/env/update', param, {
      timeout: 600000
    })
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
   * 获取机器环境变量详情
   */
  getMachineEnvDetail: param => {
    return $http.$post('/env/detail', param)
  },

  /**
   * 获取机器环境变量视图
   */
  getMachineEnvView: param => {
    return $http.$post('/env/view', param)
  },

  /**
   * 保存机器环境变量视图
   */
  saveMachineEnvView: param => {
    return $http.$post('/env/view/save', param, {
      skipErrorMessage: true,
      timeout: 600000
    })
  },

  /**
   * 同步机器环境变量
   */
  syncMachineEnv: param => {
    return $http.$post('/env/sync', param, {
      timeout: 600000
    })
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
    return $http.$post('/exec/submit', param, {
      timeout: 600000
    })
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
   * 执行输入
   */
  writeExecTask: param => {
    return $http.$post('/exec/write', param)
  },

  /**
   * 执行停止
   */
  terminatedExecTask: param => {
    return $http.$post('/exec/terminated', param)
  },

  /**
   * 获取执行状态
   */
  getExecTaskStatus: param => {
    return $http.$post('/exec/list/status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 新增模板
   */
  addTemplate: param => {
    return $http.$post('/template/add', param)
  },

  /**
   * 修改模板
   */
  updateTemplate: param => {
    return $http.$post('/template/update', param)
  },

  /**
   * 模板列表
   */
  getTemplateList: param => {
    return $http.$post('/template/list', param)
  },

  /**
   * 模板详情
   */
  getTemplateDetail: param => {
    return $http.$post('/template/detail', param)
  },

  /**
   * 删除模板
   */
  deleteTemplate: param => {
    return $http.$post('/template/delete', param)
  },

  /**
   * 获取 tail token
   */
  getTailToken: param => {
    return $http.$post('/file-tail/token', param)
  },

  /**
   * 添加 tail 文件
   */
  addTailFile: param => {
    return $http.$post('/file-tail/add', param)
  },

  /**
   * 修改 tail 文件
   */
  updateTailFile: param => {
    return $http.$post('/file-tail/update', param)
  },

  /**
   * 删除 tail 文件
   */
  deleteTailFile: param => {
    return $http.$post('/file-tail/delete', param)
  },

  /**
   * 获取 tail 文件列表
   */
  getTailList: param => {
    return $http.$post('/file-tail/list', param)
  },

  /**
   * 获取 tail 详情
   */
  getTailDetail: param => {
    return $http.$post('/file-tail/detail', param)
  },

  /**
   * 获取 tail 机器默认配置
   */
  getTailConfig: param => {
    return $http.$post('/file-tail/config', param, { skipErrorMessage: true })
  },

  /**
   * 应用环境列表
   */
  getProfileList: param => {
    return $http.$post('/app-profile/list', param)
  },

  /**
   * 应用环境详情
   */
  getProfileDetail: param => {
    return $http.$post('/app-profile/detail', param)
  },

  /**
   * 添加应用环境
   */
  addProfile: param => {
    return $http.$post('/app-profile/add', param)
  },

  /**
   * 修改应用环境
   */
  updateProfile: param => {
    return $http.$post('/app-profile/update', param)
  },

  /**
   * 删除应用环境
   */
  deleteProfile: param => {
    return $http.$post('/app-profile/delete', param)
  },

  /**
   * 添加应用变量
   */
  addAppEnv: param => {
    return $http.$post('/app-env/add', param)
  },

  /**
   * 删除应用变量
   */
  deleteAppEnv: param => {
    return $http.$post('/app-env/delete', param)
  },

  /**
   * 修改应用变量
   */
  updateAppEnv: param => {
    return $http.$post('/app-env/update', param)
  },

  /**
   * 应用环境变量列表
   */
  getAppEnvList: param => {
    return $http.$post('/app-env/list', param)
  },

  /**
   * 应用环境变量详情
   */
  getAppEnvDetail: param => {
    return $http.$post('/app-env/detail', param)
  },

  /**
   * 同步应用环境变量
   */
  syncAppEnv: param => {
    return $http.$post('/app-env/sync', param, {
      timeout: 600000
    })
  },

  /**
   * 应用环境变量视图
   */
  getAppEnvView: param => {
    return $http.$post('/app-env/view', param)
  },

  /**
   * 应用环境变量视图保存
   */
  saveAppEnvView: param => {
    return $http.$post('/app-env/view/save', param, {
      skipErrorMessage: true,
      timeout: 600000
    })
  },

  /**
   * 添加应用版本仓库
   */
  addVcs: param => {
    return $http.$post('/app-vcs/add', param)
  },

  /**
   * 修改应用版本仓库
   */
  updateVcs: param => {
    return $http.$post('/app-vcs/update', param, {
      timeout: 600000
    })
  },

  /**
   * 删除版本仓库
   */
  deleteVcs: param => {
    return $http.$post('/app-vcs/delete', param, {
      timeout: 600000
    })
  },

  /**
   * 获取版本仓库列表
   */
  getVcsList: param => {
    return $http.$post('/app-vcs/list', param)
  },

  /**
   * 获取版本仓库详情
   */
  getVcsDetail: param => {
    return $http.$post('/app-vcs/detail', param)
  },

  /**
   * 初始化版本仓库
   */
  initVcs: param => {
    return $http.$post('/app-vcs/init', param, {
      timeout: 600000
    })
  },

  /**
   * 重新初始化版本仓库
   */
  reInitVcs: param => {
    return $http.$post('/app-vcs/re/init', param, {
      timeout: 600000
    })
  },

  /**
   * 清空
   */
  cleanVcs: param => {
    return $http.$post('/app-vcs/clean', param, {
      timeout: 600000
    })
  },

  /**
   * 获取版本仓库分支和提交记录信息
   */
  getVcsInfo: param => {
    return $http.$post('/app-vcs/info', param)
  },

  /**
   * 获取版本仓库分支列表
   */
  getVcsBranchList: param => {
    return $http.$post('/app-vcs/branch', param)
  },

  /**
   * 获取版本仓库提交列表
   */
  getVcsCommitList: param => {
    return $http.$post('/app-vcs/commit', param)
  },

  /**
   * 添加应用
   */
  addApp: param => {
    return $http.$post('/app-info/add', param)
  },

  /**
   * 更新应用
   */
  updateApp: param => {
    return $http.$post('/app-info/update', param)
  },

  /**
   * 更新应用排序
   */
  adjustAppSort: param => {
    return $http.$post('/app-info/sort', param)
  },

  /**
   * 删除应用
   */
  deleteApp: param => {
    return $http.$post('/app-info/delete', param)
  },

  /**
   * 应用列表
   */
  getAppList: param => {
    return $http.$post('/app-info/list', param)
  },

  /**
   * 获取应用
   */
  getAppDetail: param => {
    return $http.$post('/app-info/detail', param)
  },

  /**
   * 配置应用
   */
  configApp: param => {
    return $http.$post('/app-info/config', param, {
      timeout: 600000
    })
  },

  /**
   * 同步应用
   */
  syncApp: param => {
    return $http.$post('/app-info/sync', param, {
      timeout: 600000
    })
  },

  /**
   * 复制应用
   */
  copyApp: param => {
    return $http.$post('/app-info/copy', param, {
      timeout: 600000
    })
  },

  /**
   * 删除应用机器
   */
  deleteAppMachine: param => {
    return $http.$post('/app-info/delete/machine', param)
  },

  /**
   * 提交应用构建
   */
  submitAppBuild: param => {
    return $http.$post('/app-build/submit', param, {
      timeout: 600000
    })
  },

  /**
   * 应用构建列表
   */
  getAppBuildList: param => {
    return $http.$post('/app-build/list', param)
  },

  /**
   * 应用构建详情
   */
  getAppBuildDetail: param => {
    return $http.$post('/app-build/detail', param)
  },

  /**
   * 获取应用构建状态
   */
  getAppBuildStatus: param => {
    return $http.$post('/app-build/status', param)
  },

  /**
   * 停止应用构建
   */
  terminatedAppBuild: param => {
    return $http.$post('/app-build/terminated', param)
  },

  /**
   * 重新构建应用
   */
  rebuildApp: param => {
    return $http.$post('/app-build/rebuild', param, {
      timeout: 600000
    })
  },

  /**
   * 应用构建列表
   */
  getAppBuildStatusList: param => {
    return $http.$post('/app-build/list/status', param, {
      skipErrorMessage: true
    })
  }

}

export default $api
