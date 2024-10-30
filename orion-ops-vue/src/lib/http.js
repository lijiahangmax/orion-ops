import axios from 'axios'
import $message from 'ant-design-vue/lib/message'
import $storage from './storage'
import router from '../router/index'

const $http = axios.create({
  responseType: 'json',
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 10000
})

// 默认配置项
const defaultConfig = {
  // 是否需要登录
  auth: true,
  // 超时时间
  timeout: 10000,
  // 请求头
  contentType: 'application/json',
  // 跳过响应拦截器异常处理提示
  skipErrorMessage: false,
  // 跳过响应拦截器
  skipRespInterceptor: false
}

/**
 * get请求
 */
const $get = (url, params = {}, config = {}) => {
  config.params = params
  return new Promise((resolve, reject) => {
    $http.get(url, fillDefaultConfig(config))
      .then(res => resolve(res))
      .catch(err => reject(err))
  })
}

/**
 * post请求
 */
const $post = (url, data = {}, config = {}) => {
  return new Promise((resolve, reject) => {
    $http.post(url, data, fillDefaultConfig(config))
      .then(res => resolve(res))
      .catch(err => reject(err))
  })
}

/**
 * http请求
 */
const $fetch = (url, method = 'get', config) => {
  return new Promise((resolve, reject) => {
    $http.request({
      url: url,
      method: method,
      ...fillDefaultConfig(config)
    })
      .then(res => resolve(res))
      .catch(err => reject(err))
  })
}

/**
 * 导出请求
 */
const $export = (url, data, config) => {
  return new Promise((resolve, reject) => {
    $http.post(url, data, fillDefaultConfig({
      skipRespInterceptor: true,
      responseType: 'blob',
      timeout: 600000,
      loading: '正在导出请耐心等待...',
      ...config
    })).then(res => resolve(res))
      .catch(err => reject(err))
  })
}

/**
 * 填充默认配置
 */
function fillDefaultConfig(config) {
  for (const defaultConfigKey in defaultConfig) {
    if (!(defaultConfigKey in config)) {
      config[defaultConfigKey] = defaultConfig[defaultConfigKey]
    }
  }
  return config
}

/**
 * 请求拦截器
 */
$http.interceptors.request.use(
  config => {
    const loginToken = $storage.get($storage.keys.LOGIN_TOKEN)
    // 设置 Content-Type
    config.headers['Content-Type'] = config.contentType
    // 登录判断
    if (config.auth && !loginToken) {
      throw new RequestError(700, '会话过期')
    }
    config.headers[$storage.keys.LOGIN_TOKEN] = loginToken
    // 设置加载
    if (config.loading) {
      config.loadingKey = $message.loading(config.loading)
    }
    return config
  }, err => {
    return Promise.reject(err)
  }
)

/**
 * 响应拦截器
 */
$http.interceptors.response.use(
  resp => {
    // 加载关闭
    resp.config.loadingKey && resp.config.loadingKey()
    // 跳过响应拦截器
    if (resp.config.skipRespInterceptor) {
      return resp
    }
    const skipErrorMessage = resp.config.skipErrorMessage
    // 判断data
    var respData = resp.data
    if (!respData || !respData.code) {
      if (!skipErrorMessage) {
        $message.warning('请求无效')
      }
      return Promise.reject(resp)
    }
    // 判断code
    switch (respData.code) {
      case 200:
        // 正常返回
        return respData
      case 700:
      case 730:
      case 740:
        // 未登录/IP封禁/用户禁用
        $message.warning(respData.msg)
        $storage.clear()
        $storage.clearSession()
        router.push({ path: '/login' })
        return Promise.reject(respData)
      case 500:
        if (!skipErrorMessage) {
          $message.error(respData.msg)
        }
        return Promise.reject(respData)
      default:
        if (!skipErrorMessage) {
          $message.warning(respData.msg)
        }
        return Promise.reject(respData)
    }
  }, err => {
    // 加载关闭
    err.config.loadingKey && err.config.loadingKey()
    // 跳过响应拦截器
    if (err.config.skipRespInterceptor) {
      return Promise.reject(err)
    }
    let rejectWrapper
    if (err instanceof RequestError) {
      // 自定义error
      rejectWrapper = err.toWrapper()
      if (err.code === 700) {
        rejectWrapper.notifyLevel('warning')
        router.push({ path: '/login' })
      }
    } else {
      // http错误
      if (!err.response || !err.response.status) {
        rejectWrapper = new RejectWrapper(0, '网络异常')
      } else {
        rejectWrapper = new RejectWrapper(err.response.status, '请求失败')
      }
    }
    if (!err.config.skipErrorMessage) {
      rejectWrapper.tips()
    }
    return Promise.reject(rejectWrapper)
  }
)

/**
 * 请求异常
 */
class RequestError extends Error {
  code
  msg

  constructor(code, msg) {
    super()
    this.code = code
    this.msg = msg
    Error.captureStackTrace(this, this.constructor)
  }

  toWrapper() {
    return new RejectWrapper(this.code, this.msg)
  }
}

/**
 * reject包装
 */
class RejectWrapper {
  code
  msg
  level

  constructor(code, msg, level = 'error') {
    this.code = code
    this.msg = msg
    this.level = level
  }

  notifyLevel(_level = 'error') {
    this.level = _level
    return this
  }

  tips() {
    $message[this.level].call(this, this.msg)
    delete this.level
  }
}

export default {
  $get,
  $post,
  $fetch,
  $export
}
