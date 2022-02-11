import _utils from './utils'

/**
 * 格式化时间
 */
function formatDate(origin, pattern = 'yyyy-MM-dd HH:mm:ss') {
  return _utils.dateFormat(new Date(origin), pattern)
}

export default {
  formatDate
}
