import utils from './utils'

const $storage = {

  keys: {
    LOGIN_TOKEN: 'O-Login-Token',
    USER: 'User',
    ACTIVE_PROFILE: 'activeProfile'
  },

  /**
   * 获取值
   */
  get(key, def) {
    const item = localStorage.getItem(key)
    return utils.isEmptyStr(item) ? def : item
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
  }

}

export default $storage
