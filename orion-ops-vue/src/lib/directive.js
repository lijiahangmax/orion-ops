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
