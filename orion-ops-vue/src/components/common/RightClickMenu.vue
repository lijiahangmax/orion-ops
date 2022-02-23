<template>
  <!-- 右键菜单 -->
  <div class="right-menu" ref="rightMenu" @contextmenu.prevent>
    <a-dropdown :trigger="['click']">
      <span ref="rightMenuTrigger" class="right-menu-trigger" @contextmenu.prevent></span>
      <template #overlay>
        <a-menu @click="clickRightMenuItem" @contextmenu.prevent>
          <slot name="items"/>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<script>
export default {
  name: 'RightClickMenu',
  props: {
    x: {
      type: Function,
      default: e => {
        return e.offsetX + 10
      }
    },
    y: {
      type: Function,
      default: e => {
        return e.clientY
      }
    }
  },
  methods: {
    openRightMenu(e) {
      if (e.button === 2) {
        this.$refs.rightMenu.style.left = this.x(e) + 'px'
        this.$refs.rightMenu.style.top = this.y(e) + 'px'
        this.$refs.rightMenu.style.display = 'block'
        this.$refs.rightMenuTrigger.click()
      } else {
        this.$refs.rightMenu.style.display = 'none'
      }
    },
    clickRightMenuItem({ key }) {
      this.$emit('clickRight', key)
    }
  }
}
</script>

<style lang="less" scoped>
</style>
