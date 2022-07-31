import Vue from 'vue'

/**
 * 设置title data-title v-title
 */
Vue.directive('title', {
  inserted: function(el, binding) {
    document.title = el.dataset.title
  }
})

/**
 * 限制input只能输入正则匹配的
 */
Vue.directive('limit-pattern', limitPattern())

/**
 * 限制input只能输入正整数
 */
Vue.directive('limit-integer', limitPattern(/^(0+)|[^\d]+/g))

/**
 * 限制input输入n位小数
 */
Vue.directive('limit-decimal', {
  inserted: function(el, binding, vnode) {
    // el是 input外层的div
    const input = el.tagName === 'INPUT' ? el : el.querySelector('input')
    input.addEventListener('keyup', function() {
      const reg = new RegExp('\\d+(\\.\\d{0,' + binding.value + '})?')
      input.value = input.value.match(reg) ? input.value.match(reg)[0] : ''
      // 当输入汉字时会导致vue model 数据不同步, 因此在回调函数添加上以下代码, 手动触发数据的双向绑定
      if (vnode.componentInstance) {
        vnode.componentInstance.$emit('input', input.value)
      } else {
        vnode.elm.dispatchEvent(new CustomEvent('input', input.value))
      }
    })

    input.addEventListener('afterpaste', function() {
      const reg = new RegExp('\\d+(\\.\\d{0,' + binding.value + '})?')
      input.value = input.value.match(reg) ? input.value.match(reg)[0] : ''
      // 当输入汉字时会导致vue model 数据不同步, 因此在回调函数添加上以下代码, 手动触发数据的双向绑定
      if (vnode.componentInstance) {
        vnode.componentInstance.$emit('input', input.value)
      } else {
        vnode.elm.dispatchEvent(new CustomEvent('input', input.value))
      }
    })
  }
})

/**
 * 可拖拽模态框
 */
Vue.directive('drag-modal', (el, bindings, vnode) => {
  Vue.nextTick(() => {
    const {
      visible,
      destroyOnClose
    } = vnode.componentInstance
    // 防止未定义 destroyOnClose 关闭弹窗时dom未被销毁, 指令被重复调用
    if (!visible) return
    const modal = el.getElementsByClassName('ant-modal')[0]
    const draggable = el.getElementsByClassName('ant-modal-draggable')[0]

    let left = 0
    let top = 0
    // 未定义 destroyOnClose 时, dom未被销毁, 关闭弹窗再次打开, 弹窗会停留在上一次拖动的位置
    if (!destroyOnClose) {
      left = modal.left || 0
      top = modal.top || 0
    }
    // top 初始值为 offsetTop
    top = top || modal.offsetTop
    // 点击title部分拖动
    draggable.onmousedown = e => {
      const startX = e.clientX
      const startY = e.clientY
      // draggable.left = draggable.offsetLeft
      // draggable.top = draggable.offsetTop
      el.onmousemove = event => {
        const endX = event.clientX
        const endY = event.clientY
        // modal.left = draggable.left + (endX - startX) + left
        // modal.top = draggable.top + (endY - startY) + top
        modal.left = (endX - startX) + left
        modal.top = (endY - startY) + top
        modal.style.left = modal.left + 'px'
        modal.style.top = modal.top + 'px'
      }
      el.onmouseup = event => {
        left = modal.left
        top = modal.top
        el.onmousemove = null
        el.onmouseup = null
        draggable.releaseCapture && draggable.releaseCapture()
      }
      draggable.setCapture && draggable.setCapture()
    }
  })
})

function limitPattern(pattern) {
  return {
    inserted: function(el, binding, vnode) {
      // el是 input外层的div
      const input = el.tagName === 'INPUT' ? el : el.querySelector('input')
      input.addEventListener('keyup', function() {
        input.value = input.value.replace(pattern || binding.value, '')
        // 当输入汉字时会导致vue model 数据不同步, 因此在回调函数添加上以下代码, 手动触发数据的双向绑定
        vnode.context.$nextTick(() => {
          if (vnode.componentInstance) {
            vnode.componentInstance.$emit('input', input.value)
          } else {
            vnode.elm.dispatchEvent(new CustomEvent('input', input.value))
          }
        })
      })
    }
  }
}
