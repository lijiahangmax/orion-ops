import * as $md5 from 'js-md5'

/**
 * 判断值是否非空
 */
function isEmptyStr(val) {
  return typeof (val) === 'undefined' || val == null
}

/**
 * 复制到剪切板
 */
function copyToClipboard(content) {
  const clipboardData = window.clipboardData
  if (clipboardData) {
    clipboardData.clearData()
    clipboardData.setData('Text', content)
    return true
  } else if (document.execCommand) {
    const el = document.createElement('textarea')
    el.value = content
    el.setAttribute('readonly', '')
    el.style.position = 'absolute'
    el.style.left = '-9999px'
    document.body.appendChild(el)
    el.select()
    document.execCommand('copy')
    document.body.removeChild(el)
    return true
  }
  return false
}

/**
 * 获取剪切板内容 返回promise
 */
function getClipboardText() {
  return navigator.clipboard.readText()
}

/**
 * md5
 */
function md5(val) {
  return $md5(val)
}

/**
 * ssh命令
 */
function getSshCommand(username, host, port) {
  return `ssh -p ${port} ${username}@${host}`
}

/**
 * 全屏
 */
function fullScreen() {
  const el = document.documentElement
  const rfs = el.requestFullScreen || el.webkitRequestFullScreen || el.mozRequestFullScreen || el.msRequestFullScreen
  if (typeof rfs !== 'undefined' && rfs) {
    rfs.call(el)
  }
}

/**
 * 取消全屏
 */
function exitFullScreen() {
  const el = document
  const cfs = el.cancelFullScreen || el.webkitCancelFullScreen || el.mozCancelFullScreen || el.exitFullScreen
  if (typeof cfs !== 'undefined' && cfs) {
    cfs.call(el)
  }
}

/**
 * 格式化时间
 */
function dateFormat(date, pattern) {
  const o = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'H+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    'S+': date.getMilliseconds()
  }
  if (/(y+)/.test(pattern)) {
    pattern = pattern.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  for (var k in o) {
    if (new RegExp('(' + k + ')').test(pattern)) {
      pattern = pattern.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)))
    }
  }
  return pattern
}

/**
 * 10进制权限 转 字符串权限
 */
function permission10toString(permission) {
  const ps = (permission + '')
  let res = ''
  for (let i = 0; i < ps.length; i++) {
    const per = ps.charAt(i)
    if ((per & 4) === 0) {
      res += '-'
    } else {
      res += 'r'
    }
    if ((per & 2) === 0) {
      res += '-'
    } else {
      res += 'w'
    }
    if ((per & 1) === 0) {
      res += '-'
    } else {
      res += 'x'
    }
  }
  return res
}

export default {
  isEmptyStr,
  copyToClipboard,
  getClipboardText,
  md5,
  getSshCommand,
  fullScreen,
  exitFullScreen,
  dateFormat,
  permission10toString
}
