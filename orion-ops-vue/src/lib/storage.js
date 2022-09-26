import { isEmptyStr } from './utils'

const $storage = {

  keys: {
    LOGIN_TOKEN: 'O-Login-Token',
    CURRENT_USER: 'currentUser',
    ACTIVE_PROFILE: 'activeProfile',
    MACHINE_VIEW: 'machineView'
  },

  /**
   * 获取值
   */
  get(key, def) {
    const item = localStorage.getItem(key)
    return isEmptyStr(item) ? def : item
  },

  /**
   * 设置值
   */
  set(key, val) {
    localStorage.setItem(key, val)
  },

  /**
   * 删除key
   */
  remove(key) {
    localStorage.removeItem(key)
  },

  /**
   * 清空本地存储
   */
  clear() {
    localStorage.clear()
  },

  /**
   * 获取值
   */
  getSession(key, def) {
    const item = sessionStorage.getItem(key)
    return isEmptyStr(item) ? def : item
  },

  /**
   * 设置值
   */
  setSession(key, val) {
    sessionStorage.setItem(key, val)
  },

  /**
   * 删除key
   */
  removeSession(key) {
    sessionStorage.removeItem(key)
  },

  /**
   * 清空本地存储
   */
  clearSession() {
    sessionStorage.clear()
  }

}

export default $storage
