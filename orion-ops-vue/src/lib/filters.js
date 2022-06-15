import { dateFormat } from './utils'

/**
 * 格式化时间
 */
export function formatDate(origin, pattern = 'yyyy-MM-dd HH:mm:ss') {
  return dateFormat(new Date(origin), pattern)
}
