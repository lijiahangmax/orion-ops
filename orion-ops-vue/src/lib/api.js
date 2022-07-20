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
  logout: () => {
    return $http.$get('/auth/logout', null, { auth: false })
  },

  /**
   * 修改密码
   */
  resetPassword: param => {
    return $http.$post('/auth/reset', param)
  },

  /**
   * 检查token是否有效
   */
  validToken: () => {
    return $http.$get('/auth/valid')
  },

  /**
   * 获取菜单
   */
  getMenu: () => {
    return $http.$get('/common/menu')
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
    return $http.$post('/user/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 更新用户
   */
  updateUser: param => {
    return $http.$post('/user/update', param, {
      loading: '正在修改...'
    })
  },

  /**
   * 更新用户
   */
  updateAvatar: param => {
    return $http.$post('/user/update-avatar', param, {
      timeout: 600000
    })
  },

  /**
   * 删除用户
   */
  deleteUser: param => {
    return $http.$post('/user/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 更新用户
   */
  updateUserStatus: param => {
    return $http.$post('/user/update-status', param)
  },

  /**
   * 解锁用户
   */
  unlockUser: param => {
    return $http.$post('/user/unlock', param, {
      loading: '正在解锁...'
    })
  },

  /**
   * 添加机器
   */
  addMachine: param => {
    return $http.$post('/machine/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 修改机器
   */
  updateMachine: param => {
    return $http.$post('/machine/update', param, {
      loading: '正在修改...'
    })
  },

  /**
   * 删除机器
   */
  deleteMachine: param => {
    return $http.$post('/machine/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 修改机器状态
   */
  updateMachineStatus: param => {
    return $http.$post('/machine/update-status', param)
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
      timeout: 600000,
      loading: '正在复制...'
    })
  },

  /**
   * 测试机器ping
   */
  machineTestPing: param => {
    return $http.$post('/machine/test-ping', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 测试机器连接
   */
  machineTestConnect: param => {
    return $http.$post('/machine/test-connect', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 获取终端访问信息
   */
  accessTerminal: param => {
    return $http.$post('/terminal/access', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 获取支持的终端类型
   */
  getTerminalSupportPyt: () => {
    return $http.$get('/terminal/support/pty')
  },

  /**
   * 获取机器终端配置
   */
  getTerminalSetting: param => {
    return $http.$get(`/terminal/get/${param.machineId}`)
  },

  /**
   * 修改机器终端配置
   */
  updateTerminalSetting: param => {
    return $http.$post('/terminal/update', param, {
      skipErrorMessage: true,
      loading: '正在修改...'
    })
  },

  /**
   * 终端日志列表
   */
  terminalLogList: param => {
    return $http.$post('/terminal/log/list', param)
  },

  /**
   * 删除终端日志
   */
  deleteTerminalLog: param => {
    return $http.$post('/terminal/log/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 终端会话列表
   */
  terminalSessionList: param => {
    return $http.$post('/terminal/manager/session', param)
  },

  /**
   * 强制下线终端
   */
  terminalOffline: param => {
    return $http.$post('/terminal/manager/offline', param, {
      loading: '正在操作...'
    })
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
    return $http.$post('/sftp/mkdir', param, {
      skipErrorMessage: true,
      loading: '正在创建...'
    })
  },

  /**
   * sftp 创建文件
   */
  sftpTouch: param => {
    return $http.$post('/sftp/touch', param, {
      skipErrorMessage: true,
      loading: '正在创建...'
    })
  },

  /**
   * sftp 移动文件
   */
  sftpMove: param => {
    return $http.$post('/sftp/move', param, {
      skipErrorMessage: true,
      loading: '正在移动...'
    })
  },

  /**
   * sftp 删除文件
   */
  sftpRemove: param => {
    return $http.$post('/sftp/remove', param, {
      loading: '正在删除...'
    })
  },

  /**
   * sftp 截断文件
   */
  sftpTruncate: param => {
    return $http.$post('/sftp/truncate', param)
  },

  /**
   * sftp 修改权限
   */
  sftpChmod: param => {
    return $http.$post('/sftp/chmod', param, {
      skipErrorMessage: true,
      loading: '正在修改...'
    })
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
    return $http.$post('/sftp/check-present', param, {
      timeout: 18000000
    })
  },

  /**
   * sftp 获取上传文件 accessToken
   */
  getSftpUploadToken: param => {
    return $http.$post('/sftp/upload/token', param)
  },

  /**
   * sftp 上传文件
   */
  sftpUploadExec: param => {
    return $http.$post('/sftp/upload/exec', param, {
      timeout: 18000000,
      loading: '正在提交上传请求...',
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
      timeout: 180000,
      loading: '正在请求下载文件...'
    })
  },

  /**
   * sftp 压缩下载文件
   */
  sftpPackageDownloadExec: param => {
    return $http.$post('/sftp/package-download/exec', param, {
      timeout: 180000,
      loading: '正在执行压缩命令, 执行完毕自动下载'
    })
  },

  /**
   * sftp 传输列表
   */
  sftpTransferList: param => {
    return $http.$get(` / sftp / transfer /${param.sessionToken}/list`)
  },

  /**
   * sftp 传输暂停
   */
  sftpTransferPause: param => {
    return $http.$get(`/sftp/transfer/${param.fileToken}/pause`)
  },

  /**
   * sftp 传输恢复
   */
  sftpTransferResume: param => {
    return $http.$get(`/sftp/transfer/${param.fileToken}/resume`)
  },

  /**
   * sftp 传输失败重试
   */
  sftpTransferRetry: param => {
    return $http.$get(`/sftp/transfer/${param.fileToken}/retry`)
  },

  /**
   * sftp 重新上传
   */
  sftpTransferReUpload: param => {
    return $http.$get(`/sftp/transfer/${param.fileToken}/re-upload`)
  },

  /**
   * sftp 重新下载
   */
  sftpTransferReDownload: param => {
    return $http.$get(`/sftp/transfer/${param.fileToken}/re-download`)
  },

  /**
   * sftp 传输暂停全部
   */
  sftpTransferPauseAll: param => {
    return $http.$get(`/sftp/transfer/${param.sessionToken}/pause-all`, null, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输恢复全部
   */
  sftpTransferResumeAll: param => {
    return $http.$get(`/sftp/transfer/${param.sessionToken}/resume-all`, null, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输失败重试全部
   */
  sftpTransferRetryAll: param => {
    return $http.$get(`/sftp/transfer/${param.sessionToken}/retry-all`, null, {
      timeout: 180000
    })
  },

  /**
   * sftp 传输删除(单个)
   */
  sftpTransferRemove: param => {
    return $http.$get(`/sftp/transfer/${param.fileToken}/remove`)
  },

  /**
   * sftp 传输清空(全部)
   */
  sftpTransferClear: param => {
    return $http.$get(`/sftp/transfer/${param.sessionToken}/clear`)
  },

  /**
   * sftp 传输打包
   */
  sftpTransferPackage: param => {
    return $http.$get(`/sftp/transfer/${param.sessionToken}/${param.packageType}/package`)
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
    return $http.$post('/proxy/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 更新机器代理
   */
  updateMachineProxy: param => {
    return $http.$post('/proxy/update', param, {
      loading: '正在修改...'
    })
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
    return $http.$post('/proxy/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 添加秘钥
   */
  addMachineKey: param => {
    return $http.$post('/key/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 更新秘钥
   */
  updateMachineKey: param => {
    return $http.$post('/key/update', param, {
      loading: '正在修改...'
    })
  },

  /**
   * 删除秘钥
   */
  removeMachineKey: param => {
    return $http.$post('/key/remove', param, {
      loading: '正在删除...'
    })
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
   * 挂载秘钥
   */
  mountMachineKey: param => {
    return $http.$post('/key/mount', param, {
      skipErrorMessage: true,
      loading: '正在挂载...'
    })
  },

  /**
   * 卸载秘钥
   */
  dumpMachineKey: param => {
    return $http.$post('/key/dump', param, {
      skipErrorMessage: true,
      loading: '正在卸载...'
    })
  },

  /**
   * 挂载所有秘钥
   */
  mountAllMachineKey: () => {
    return $http.$get('/key/mount-all', null, {
      skipErrorMessage: true,
      loading: '正在挂载...'
    })
  },

  /**
   * 卸载所有秘钥
   */
  dumpAllMachineKey: () => {
    return $http.$get('/key/dump-all', null, {
      skipErrorMessage: true,
      loading: '正在卸载...'
    })
  },

  /**
   * 临时挂载秘钥
   */
  tempMountMachineKey: param => {
    return $http.$post('/key/temp-mount', param, {
      loading: '正在挂载...'
    })
  },

  /**
   * 添加机器环境变量
   */
  addMachineEnv: param => {
    return $http.$post('/machine-env/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 更新机器环境变量
   */
  updateMachineEnv: param => {
    return $http.$post('/machine-env/update', param, {
      timeout: 600000,
      loading: '正在修改...'
    })
  },

  /**
   * 删除机器环境变量
   */
  deleteMachineEnv: param => {
    return $http.$post('/machine-env/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 获取机器环境变量列表
   */
  getMachineEnvList: param => {
    return $http.$post('/machine-env/list', param)
  },

  /**
   * 获取机器环境变量详情
   */
  getMachineEnvDetail: param => {
    return $http.$post('/machine-env/detail', param)
  },

  /**
   * 获取机器环境变量视图
   */
  getMachineEnvView: param => {
    return $http.$post('/machine-env/view', param)
  },

  /**
   * 保存机器环境变量视图
   */
  saveMachineEnvView: param => {
    return $http.$post('/machine-env/view-save', param, {
      skipErrorMessage: true,
      timeout: 600000,
      loading: '正在保存...'
    })
  },

  /**
   * 同步机器环境变量
   */
  syncMachineEnv: param => {
    return $http.$post('/machine-env/sync', param, {
      timeout: 600000,
      loading: '正在同步...'
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
    return $http.$post('/history-value/rollback', param, {
      loading: '正在回滚...'
    })
  },

  /**
   * 执行提交
   */
  submitExecTask: param => {
    return $http.$post('/batch-exec/submit', param, {
      timeout: 600000,
      loading: '正在提交...'
    })
  },

  /**
   * 执行列表
   */
  getExecList: param => {
    return $http.$post('/batch-exec/list', param)
  },

  /**
   * 执行详情
   */
  getExecDetail: param => {
    return $http.$post('/batch-exec/detail', param)
  },

  /**
   * 执行输入
   */
  writeExecTask: param => {
    return $http.$post('/batch-exec/write', param)
  },

  /**
   * 执行停止
   */
  terminateExecTask: param => {
    return $http.$post('/batch-exec/terminate', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 删除执行任务
   */
  deleteExecTask: param => {
    return $http.$post('/batch-exec/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 获取执行状态
   */
  getExecTaskStatus: param => {
    return $http.$post('/batch-exec/list-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 批量上传检查机器以及文件
   */
  checkBatchUploadFiles: param => {
    return $http.$post('/batch-upload/check', param, {
      timeout: 18000000
    })
  },

  /**
   * 获取批量上传 token
   */
  getBatchUploadToken: param => {
    return $http.$post('/batch-upload/token', param, {
      timeout: 18000000
    })
  },

  /**
   * 执行批量上传
   */
  execBatchUpload: param => {
    return $http.$post('/batch-upload/exec', param, {
      timeout: 18000000,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 新增模板
   */
  addTemplate: param => {
    return $http.$post('/template/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 修改模板
   */
  updateTemplate: param => {
    return $http.$post('/template/update', param, {
      loading: '正在修改...'
    })
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
    return $http.$post('/template/delete', param, {
      loading: '正在删除...'
    })
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
    return $http.$post('/file-tail/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 修改 tail 文件
   */
  updateTailFile: param => {
    return $http.$post('/file-tail/update', param, {
      loading: '正在修改...'
    })
  },

  /**
   * 上传 tail 文件
   */
  uploadTailFile: param => {
    return $http.$post('/file-tail/upload', param, {
      loading: '正在上传...',
      timeout: 18000000,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 清理文件 ANSI 码
   */
  cleanFileAnsiCode: param => {
    return $http.$post('/file-tail/clean-ansi', param, {
      skipRespInterceptor: true,
      responseType: 'blob',
      timeout: 18000000,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 删除 tail 文件
   */
  deleteTailFile: param => {
    return $http.$post('/file-tail/delete', param, {
      loading: '正在删除...'
    })
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
   * tail 输入命令
   */
  writeTailCommand: param => {
    return $http.$post('/file-tail/write', param)
  },

  /**
   * 应用环境列表
   */
  getProfileList: param => {
    return $http.$post('/app-profile/list', param)
  },

  /**
   * 应用环境列表 (快速)
   */
  fastGetProfileList: param => {
    return $http.$get('/app-profile/fast-list', param)
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
    return $http.$post('/app-profile/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 修改应用环境
   */
  updateProfile: param => {
    return $http.$post('/app-profile/update', param, {
      loading: '正在修改...'
    })
  },

  /**
   * 删除应用环境
   */
  deleteProfile: param => {
    return $http.$post('/app-profile/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 添加应用变量
   */
  addAppEnv: param => {
    return $http.$post('/app-env/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 删除应用变量
   */
  deleteAppEnv: param => {
    return $http.$post('/app-env/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 修改应用变量
   */
  updateAppEnv: param => {
    return $http.$post('/app-env/update', param, {
      loading: '正在修改...'
    })
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
      timeout: 600000,
      loading: '正在同步...'
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
    return $http.$post('/app-env/view-save', param, {
      skipErrorMessage: true,
      timeout: 600000,
      loading: '正在保存...'
    })
  },

  /**
   * 添加应用版本仓库
   */
  addVcs: param => {
    return $http.$post('/app-vcs/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 修改应用版本仓库
   */
  updateVcs: param => {
    return $http.$post('/app-vcs/update', param, {
      timeout: 600000,
      loading: '正在修改...'
    })
  },

  /**
   * 删除版本仓库
   */
  deleteVcs: param => {
    return $http.$post('/app-vcs/delete', param, {
      timeout: 600000,
      loading: '正在删除...'
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
      timeout: 600000,
      loading: '正在初始化...'
    })
  },

  /**
   * 重新初始化版本仓库
   */
  reInitVcs: param => {
    return $http.$post('/app-vcs/re-init', param, {
      timeout: 600000,
      loading: '正在初始化...'
    })
  },

  /**
   * 清空
   */
  cleanVcs: param => {
    return $http.$post('/app-vcs/clean', param, {
      timeout: 600000,
      loading: '正在清空...'
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
    return $http.$post('/app-info/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 更新应用
   */
  updateApp: param => {
    return $http.$post('/app-info/update', param, {
      loading: '正在修改...'
    })
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
    return $http.$post('/app-info/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 应用列表
   */
  getAppList: param => {
    return $http.$post('/app-info/list', param)
  },

  /**
   * 应用机器列表
   */
  getAppMachineList: param => {
    return $http.$post('/app-info/list-machine', param)
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
      timeout: 600000,
      loading: '正在保存...'
    })
  },

  /**
   * 同步应用
   */
  syncApp: param => {
    return $http.$post('/app-info/sync', param, {
      timeout: 600000,
      loading: '正在同步...'
    })
  },

  /**
   * 复制应用
   */
  copyApp: param => {
    return $http.$post('/app-info/copy', param, {
      timeout: 600000,
      loading: '正在复制...'
    })
  },

  /**
   * 删除应用机器
   */
  deleteAppMachine: param => {
    return $http.$post('/app-info/delete-machine', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 获取应用机器id
   */
  getAppMachineId: param => {
    return $http.$post('/app-info/get-machine-id', param)
  },

  /**
   * 提交应用构建
   */
  submitAppBuild: param => {
    return $http.$post('/app-build/submit', param, {
      timeout: 600000,
      loading: '正在提交...'
    })
  },

  /**
   * 应用操作详情
   */
  getAppActionDetail: param => {
    return $http.$post('/app-action-log/detail', param)
  },

  /**
   * 应用操作状态
   */
  getAppActionStatus: param => {
    return $http.$post('/app-action-log/status', param, {
      skipErrorMessage: true
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
    return $http.$post('/app-build/status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 停止应用构建
   */
  terminateAppBuild: param => {
    return $http.$post('/app-build/terminate', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 应用构建输入命令
   */
  writeAppBuild: param => {
    return $http.$post('/app-build/write', param)
  },

  /**
   * 删除应用构建
   */
  deleteAppBuild: param => {
    return $http.$post('/app-build/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 重新构建应用
   */
  rebuildApp: param => {
    return $http.$post('/app-build/rebuild', param, {
      timeout: 600000,
      loading: '正在重新构建...'
    })
  },

  /**
   * 获取发布构建列表
   */
  getBuildReleaseList: param => {
    return $http.$post('/app-build/release-list', param)
  },

  /**
   * 应用构建状态列表
   */
  getAppBuildStatusList: param => {
    return $http.$post('/app-build/list-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 应用发布列表
   */
  getAppReleaseList: param => {
    return $http.$post('/app-release/list', param)
  },

  /**
   * 应用发布机器列表
   */
  getAppReleaseMachineList: param => {
    return $http.$post('/app-release/list-machine', param)
  },

  /**
   * 应用发布详情
   */
  getAppReleaseDetail: param => {
    return $http.$post('/app-release/detail', param)
  },

  /**
   * 应用发布机器详情
   */
  getAppReleaseMachineDetail: param => {
    return $http.$post('/app-release/machine-detail', param)
  },

  /**
   * 提交应用发布
   */
  submitAppRelease: param => {
    return $http.$post('/app-release/submit', param, {
      timeout: 600000,
      loading: '正在提交...'
    })
  },

  /**
   * 复制应用发布
   */
  copyAppRelease: param => {
    return $http.$post('/app-release/copy', param, {
      timeout: 600000,
      loading: '正在复制...'
    })
  },

  /**
   * 审核应用发布
   */
  auditAppRelease: param => {
    return $http.$post('/app-release/audit', param, {
      loading: '正在操作...'
    })
  },

  /**
   * 执行应用发布
   */
  runnableAppRelease: param => {
    return $http.$post('/app-release/runnable', param, {
      loading: '正在执行...'
    })
  },

  /**
   * 应用取消定时发布
   */
  cancelAppTimedRelease: param => {
    return $http.$post('/app-release/cancel-timed', param, {
      loading: '正在取消...'
    })
  },

  /**
   * 设置定时发布时间
   */
  setAppTimedRelease: param => {
    return $http.$post('/app-release/set-timed', param, {
      loading: '正在设置...'
    })
  },

  /**
   * 应用回滚发布
   */
  rollbackAppRelease: param => {
    return $http.$post('/app-release/rollback', param, {
      timeout: 600000,
      loading: '正在提交...'
    })
  },

  /**
   * 删除应用发布
   */
  deleteAppRelease: param => {
    return $http.$post('/app-release/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 应用发布停止
   */
  terminateAppRelease: param => {
    return $http.$post('/app-release/terminate', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 应用发布机器停止
   */
  terminateAppReleaseMachine: param => {
    return $http.$post('/app-release/terminate-machine', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 应用发布机器跳过
   */
  skipAppReleaseMachine: param => {
    return $http.$post('/app-release/skip-machine', param, {
      loading: '正在跳过...'
    })
  },

  /**
   * 应用发布机器输入命令
   */
  writeAppReleaseMachine: param => {
    return $http.$post('/app-release/write-machine', param)
  },

  /**
   * 应用发布列表状态
   */
  getAppReleaseListStatus: param => {
    return $http.$post('/app-release/list-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 应用发布状态
   */
  getAppReleaseStatus: param => {
    return $http.$post('/app-release/status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 应用发布机器列表状态
   */
  getAppReleaseMachineListStatus: param => {
    return $http.$post('/app-release/list-machine-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 应用发布机器状态
   */
  getAppReleaseMachineStatus: param => {
    return $http.$post('/app-release/machine-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 首页统计信息
   */
  getHomeStatistics: param => {
    return $http.$post('/statistics/home', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 调度任务统计信息
   */
  getSchedulerTaskStatistics: param => {
    return $http.$post('/statistics/scheduler-task', param)
  },

  /**
   * 应用构建统计信息 指标
   */
  getAppBuildStatisticsMetrics: param => {
    return $http.$post('/statistics/app-build/metrics', param)
  },

  /**
   * 应用构建统计信息 视图
   */
  getAppBuildStatisticsView: param => {
    return $http.$post('/statistics/app-build/view', param)
  },

  /**
   * 应用构建统计信息 折线图
   */
  getAppBuildStatisticsChart: param => {
    return $http.$post('/statistics/app-build/chart', param)
  },

  /**
   * 应用发布统计信息 指标
   */
  getAppReleaseStatisticsMetrics: param => {
    return $http.$post('/statistics/app-release/metrics', param)
  },

  /**
   * 应用发布统计信息 视图
   */
  getAppReleaseStatisticsView: param => {
    return $http.$post('/statistics/app-release/view', param)
  },

  /**
   * 应用发布统计信息 折线图
   */
  getAppReleaseStatisticsChart: param => {
    return $http.$post('/statistics/app-release/chart', param)
  },

  /**
   * 应用流水线统计信息 指标
   */
  getAppPipelineTaskStatisticsMetrics: param => {
    return $http.$post('/statistics/app-pipeline/metrics', param)
  },

  /**
   * 应用流水线统计信息 视图
   */
  getAppPipelineTaskStatisticsView: param => {
    return $http.$post('/statistics/app-pipeline/view', param)
  },

  /**
   * 应用流水线统计信息 折线图
   */
  getAppPipelineTaskStatisticsChart: param => {
    return $http.$post('/statistics/app-pipeline/chart', param)
  },

  /**
   * 获取操作日志列表
   */
  getEventLogList: param => {
    return $http.$post('/event-log/list', param)
  },

  /**
   * 添加系统环境变量
   */
  addSystemEnv: param => {
    return $http.$post('/system-env/add', param, {
      loading: '正在添加...'
    })
  },

  /**
   * 更新系统环境变量
   */
  updateSystemEnv: param => {
    return $http.$post('/system-env/update', param, {
      timeout: 600000,
      loading: '正在修改...'
    })
  },

  /**
   * 删除系统环境变量
   */
  deleteSystemEnv: param => {
    return $http.$post('/system-env/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 获取系统环境变量列表
   */
  getSystemEnvList: param => {
    return $http.$post('/system-env/list', param)
  },

  /**
   * 获取系统环境变量详情
   */
  getSystemEnvDetail: param => {
    return $http.$post('/system-env/detail', param)
  },

  /**
   * 获取系统环境变量视图
   */
  getSystemEnvView: param => {
    return $http.$post('/system-env/view', param)
  },

  /**
   * 保存系统环境变量视图
   */
  saveSystemEnvView: param => {
    return $http.$post('/system-env/view-save', param, {
      skipErrorMessage: true,
      timeout: 600000,
      loading: '正在保存...'
    })
  },

  /**
   * 获取 ip 配置
   */
  getIpInfo: () => {
    return $http.$get('/system/ip-info')
  },

  /**
   * 配置 ip 列表
   */
  configIpList: param => {
    return $http.$post('/system/config-ip', param, {
      loading: '正在保存...'
    })
  },

  /**
   * 获取系统分析信息
   */
  getSystemAnalysis: () => {
    return $http.$get('/system/get-system-analysis')
  },

  /**
   * 重新进行系统统计分析
   */
  reAnalysisSystem: () => {
    return $http.$get('/system/re-analysis', null, {
      timeout: 600000
    })
  },

  /**
   * 清理系统文件
   */
  cleanSystemFile: param => {
    return $http.$post('/system/clean-system-file', param)
  },

  /**
   * 修改系统配置项
   */
  updateSystemOption: param => {
    return $http.$post('/system/update-system-option', param)
  },

  /**
   * 获取系统配置项
   */
  getSystemOptions: () => {
    return $http.$get('/system/get-system-options')
  },

  /**
   * 获取系统线程池指标
   */
  getSystemThreadMetrics: () => {
    return $http.$get('/system/get-thread-metrics')
  },

  /**
   * 获取系统信息
   */
  getSystemAbout: () => {
    return $http.$get('/system/about')
  },

  /**
   * 获取 cron 下几次执行时间
   */
  getCronNextTime: param => {
    return $http.$post('/scheduler/cron-next', param)
  },

  /**
   * 添加调度任务
   */
  addSchedulerTask: param => {
    return $http.$post('/scheduler/add', param, {
      loading: '正在保存...'
    })
  },

  /**
   * 修改调度任务
   */
  updateSchedulerTask: param => {
    return $http.$post('/scheduler/update', param, {
      loading: '正在修改...'
    })
  },

  /**
   * 获取调度任务详情
   */
  getSchedulerTask: param => {
    return $http.$post('/scheduler/get', param)
  },

  /**
   * 获取调度任务列表
   */
  getSchedulerTaskList: param => {
    return $http.$post('/scheduler/list', param)
  },

  /**
   * 更新调度任务状态
   */
  updateSchedulerTaskStatus: param => {
    return $http.$post('/scheduler/update-status', param, {
      loading: '正在更新...'
    })
  },

  /**
   * 删除调度任务
   */
  deleteSchedulerTask: param => {
    return $http.$post('/scheduler/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 手动触发调度任务
   */
  manualTriggerSchedulerTask: param => {
    return $http.$post('/scheduler/manual-trigger', param, {
      loading: '正在触发...'
    })
  },

  /**
   * 查询调度任务执行列表
   */
  getSchedulerTaskRecordList: param => {
    return $http.$post('/scheduler-record/list', param)
  },

  /**
   * 查询调度任务执行详情
   */
  getSchedulerTaskRecordDetail: param => {
    return $http.$post('/scheduler-record/detail', param)
  },

  /**
   * 查询调度任务机器列表
   */
  getSchedulerTaskMachinesRecordList: param => {
    return $http.$post('/scheduler-record/machines', param)
  },

  /**
   * 查询调度任务状态
   */
  getSchedulerTaskRecordStatus: param => {
    return $http.$post('/scheduler-record/list-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 查询调度任务机器明细状态
   */
  getSchedulerTaskMachinesRecordStatus: param => {
    return $http.$post('/scheduler-record/machines-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 删除调度任务明细
   */
  deleteSchedulerTaskRecord: param => {
    return $http.$post('/scheduler-record/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 停止所有调度任务机器
   */
  terminateAllSchedulerTaskRecord: param => {
    return $http.$post('/scheduler-record/terminate-all', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 停止单个调度任务机器
   */
  terminateMachineSchedulerTaskRecord: param => {
    return $http.$post('/scheduler-record/terminate-machine', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 跳过单个调度任务机器
   */
  skipMachineSchedulerTaskRecord: param => {
    return $http.$post('/scheduler-record/skip-machine', param, {
      loading: '正在跳过...'
    })
  },

  /**
   * 发送调度任务机器命令
   */
  writeMachineSchedulerTaskRecord: param => {
    return $http.$post('/scheduler-record/write-machine', param)
  },

  /**
   * 获取站内信未读数量
   */
  getWebSideMessageUnreadCount: () => {
    return $http.$get('/message/unread-count', null, {
      skipErrorMessage: true
    })
  },

  /**
   * 设置站内信全部已读
   */
  setWebSideMessageAllRead: () => {
    return $http.$get('/message/set-all-read')
  },

  /**
   * 设置已读站内信
   */
  setMessageRead: params => {
    return $http.$post('/message/read', params)
  },

  /**
   * 删除全部已读站内信
   */
  deleteAllReadMessage: () => {
    return $http.$get('/message/delete-all-read')
  },

  /**
   * 删除站内信
   */
  deleteWebSideMessage: param => {
    return $http.$post('/message/delete', param)
  },

  /**
   * 获取站内信详情
   */
  getWebSideMessageDetail: param => {
    return $http.$post('/message/detail', param)
  },

  /**
   * 获取站内信列表
   */
  getWebSideMessageList: param => {
    return $http.$post('/message/list', param)
  },

  /**
   * 获取最新站内信
   */
  getNewMessage: param => {
    return $http.$post('/message/get-new-message', param)
  },

  /**
   * 获取更多站内信
   */
  getMoreMessage: param => {
    return $http.$post('/message/get-more-message', param)
  },

  /**
   * 轮询获取站内信
   */
  pollWebSideMessage: param => {
    return $http.$post('/message/poll-new-message', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 新增应用流水线
   */
  addAppPipeline: param => {
    return $http.$post('/app-pipeline/add', param, {
      loading: '正在保存...'
    })
  },

  /**
   * 修改应用流水线
   */
  updateAppPipeline: param => {
    return $http.$post('/app-pipeline/update', param, {
      loading: '正在保存...'
    })
  },

  /**
   * 获取应用流水线列表
   */
  getAppPipelineList: param => {
    return $http.$post('/app-pipeline/list', param)
  },

  /**
   * 获取应用流水线详情
   */
  getAppPipelineDetail: param => {
    return $http.$post('/app-pipeline/get', param)
  },

  /**
   * 删除应用流水线
   */
  deleteAppPipeline: param => {
    return $http.$post('/app-pipeline/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 获取应用流水线任务列表
   */
  getAppPipelineTaskList: param => {
    return $http.$post('/app-pipeline-task/list', param)
  },

  /**
   * 获取应用流水线任务详情
   */
  getAppPipelineTaskDetail: param => {
    return $http.$post('/app-pipeline-task/detail', param)
  },

  /**
   * 获取应用流水线任务详情
   */
  getAppPipelineTaskDetails: param => {
    return $http.$post('/app-pipeline-task/task-details', param)
  },

  /**
   * 提交执行应用流水线任务
   */
  submitAppPipelineTask: param => {
    return $http.$post('/app-pipeline-task/submit', param, {
      timeout: 600000,
      loading: '正在提交...'
    })
  },

  /**
   * 审核应用流水线任务
   */
  auditAppPipelineTask: param => {
    return $http.$post('/app-pipeline-task/audit', param, {
      loading: '正在操作...'
    })
  },

  /**
   * 复制应用流水线任务
   */
  copyAppPipelineTask: param => {
    return $http.$post('/app-pipeline-task/copy', param, {
      timeout: 600000,
      loading: '正在复制...'
    })
  },

  /**
   * 执行应用流水线任务
   */
  execAppPipelineTask: param => {
    return $http.$post('/app-pipeline-task/exec', param, {
      loading: '正在执行...'
    })
  },

  /**
   * 删除应用流水线任务
   */
  deleteAppPipelineTask: param => {
    return $http.$post('/app-pipeline-task/delete', param, {
      loading: '正在删除...'
    })
  },

  /**
   * 设置应用流水线定时任务
   */
  setAppPipelineTaskTimedExec: param => {
    return $http.$post('/app-pipeline-task/set-timed', param, {
      loading: '正在设置...'
    })
  },

  /**
   * 取消应用流水线定时任务
   */
  cancelAppPipelineTaskTimedExec: param => {
    return $http.$post('/app-pipeline-task/cancel-timed', param, {
      loading: '正在取消...'
    })
  },

  /**
   * 停止执行应用流水线任务
   */
  terminateAppPipelineTask: param => {
    return $http.$post('/app-pipeline-task/terminate', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 停止执行应用流水线任务部分操作
   */
  terminateAppPipelineTaskDetail: param => {
    return $http.$post('/app-pipeline-task/terminate-detail', param, {
      loading: '正在停止...'
    })
  },

  /**
   * 停止执行应用流水线任务部分操作
   */
  skipAppPipelineTaskDetail: param => {
    return $http.$post('/app-pipeline-task/skip-detail', param, {
      loading: '正在跳过...'
    })
  },

  /**
   * 应用流水线任务状态
   */
  geAppPipelineTaskStatus: param => {
    return $http.$post('/app-pipeline-task/status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 应用流水线任务状态列表
   */
  getAppPipelineTaskListStatus: param => {
    return $http.$post('/app-pipeline-task/list-status', param, {
      skipErrorMessage: true
    })
  },

  /**
   * 应用流水线任务日志
   */
  getAppPipelineTaskLog: param => {
    return $http.$post('/app-pipeline-task/log', param)
  },

  /**
   * 清理批量执行数据
   */
  clearBatchExec: param => {
    return $http.$post('/data-clear/batch-exec', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理终端日志
   */
  clearTerminalLog: param => {
    return $http.$post('/data-clear/terminal-log', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理调度记录
   */
  clearSchedulerRecord: param => {
    return $http.$post('/data-clear/scheduler-record', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理应用构建记录
   */
  clearAppBuild: param => {
    return $http.$post('/data-clear/app-build', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理应用发布记录
   */
  clearAppRelease: param => {
    return $http.$post('/data-clear/app-release', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理应用流水线记录
   */
  clearAppPipeline: param => {
    return $http.$post('/data-clear/app-pipeline', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理站内信
   */
  clearWebSideMessage: param => {
    return $http.$post('/data-clear/web-side-message', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 清理操作日志
   */
  clearEventLog: param => {
    return $http.$post('/data-clear/event-log', param, {
      loading: '正在清理...'
    })
  },

  /**
   * 导出机器信息
   */
  exportMachine: param => {
    return $http.$export('/data-export/machine', param)
  },

  /**
   * 导出机器代理
   */
  exportMachineProxy: param => {
    return $http.$export('/data-export/machine-proxy', param)
  },

  /**
   * 导出终端日志
   */
  exportMachineTerminalLog: param => {
    return $http.$export('/data-export/machine-terminal-log', param)
  },

  /**
   * 导出日志文件
   */
  exportMachineTailFile: param => {
    return $http.$export('/data-export/machine-tail-file', param)
  },

  /**
   * 导出应用环境
   */
  exportAppProfile: param => {
    return $http.$export('/data-export/app-profile', param)
  },

  /**
   * 导出应用信息
   */
  exportApplication: param => {
    return $http.$export('/data-export/application', param)
  },

  /**
   * 导出应用版本仓库
   */
  exportAppVcs: param => {
    return $http.$export('/data-export/app-vcs', param)
  },

  /**
   * 导出命令模板
   */
  exportCommandTemplate: param => {
    return $http.$export('/data-export/command-template', param)
  },

  /**
   * 导出站内信
   */
  exportWebSideMessage: param => {
    return $http.$export('/data-export/web-side-message', param)
  },

  /**
   * 导出操作日志
   */
  exportEventLog: param => {
    return $http.$export('/data-export/event-log', param)
  },

  /**
   * 获取导入模板
   */
  getImportTemplate: param => {
    return $http.$get('/data-import/get-template', param, {
      skipRespInterceptor: true,
      timeout: 600000,
      responseType: 'blob'
    })
  },

  /**
   * 检查导入数据
   */
  checkImportData: param => {
    return $http.$post('/data-import/check-data', param, {
      timeout: 18000000,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 导入机器信息
   */
  importMachineData: param => {
    return $http.$post('/data-import/import-machine', param)
  },

  /**
   * 导入机器代理
   */
  importMachineProxy: param => {
    return $http.$post('/data-import/import-machine-proxy', param)
  },

  /**
   * 导入日志文件
   */
  importTailFile: param => {
    return $http.$post('/data-import/import-tail-file', param)
  },

  /**
   * 导入应用环境
   */
  importAppProfile: param => {
    return $http.$post('/data-import/import-app-profile', param)
  },

  /**
   * 导入应用信息
   */
  importApplication: param => {
    return $http.$post('/data-import/import-application', param)
  },

  /**
   * 导入应用版本仓库
   */
  importAppVcs: param => {
    return $http.$post('/data-import/import-app-vcs', param)
  },

  /**
   * 导入命令模板
   */
  importCommandTemplate: param => {
    return $http.$post('/data-import/import-command-template', param)
  },

  /**
   * 取消导入数据
   */
  cancelImportData: param => {
    return $http.$post('/data-import/cancel-import', param)
  }

}

export default $api
