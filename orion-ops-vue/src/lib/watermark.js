const watermark = {
  currentId: undefined
}

/**
 * 设置水印
 */
watermark.set = (str) => {
  let reset = true
  if (watermark.currentId) {
    // 存在并且文本变化
    const before = document.getElementById(watermark.currentId)
    if (before) {
      if (before.mv !== str) {
        document.body.removeChild(before)
      } else {
        reset = false
      }
    }
  }
  // 重置
  if (reset) {
    return watermark.reset(str)
  } else {
    return watermark.currentId
  }
}

/**
 * 重设水印
 */
watermark.reset = (str) => {
  const id = 'water.mask.' + new Date().getTime()
  const can = document.createElement('canvas')
  can.width = 400
  can.height = 300

  const cans = can.getContext('2d')
  cans.rotate((-15 * Math.PI) / 180)
  cans.font = '14px Vedana'
  cans.fillStyle = 'rgba(200, 200, 200, 0.60)'
  cans.textAlign = 'center'
  cans.textBaseline = 'hanging'
  cans.fillText(str, can.width / 8, can.height / 6)

  const div = document.createElement('div')
  div.id = id
  div.mv = str
  div.style.pointerEvents = 'none'
  div.style.top = '30px'
  div.style.left = '0px'
  div.style.position = 'fixed'
  div.style.zIndex = '100000'
  div.style.width = document.documentElement.clientWidth + 'px'
  div.style.height = document.documentElement.clientHeight + 'px'
  div.style.background = 'url(' + can.toDataURL('image/png') + ') left top repeat'
  document.body.appendChild(div)
  watermark.currentId = id
  return id
}

/**
 * 移除水印
 */
watermark.remove = () => {
  if (!watermark.currentId) {
    return
  }
  const ele = document.getElementById(watermark.currentId)
  if (ele !== null) {
    document.body.removeChild(ele)
  }
  watermark.currentId = undefined
}

export default watermark
