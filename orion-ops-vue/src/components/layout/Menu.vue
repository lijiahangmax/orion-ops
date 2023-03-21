<template>
  <a-menu theme="dark"
          mode="inline"
          :selectedKeys="selectedKeys"
          :defaultOpenKeys="defaultOpenKeys">
    <template v-for="menuItem in menuList">
      <!-- 一级菜单 -->
      <a-menu-item v-if="!menuItem.children" :key="menuItem.id">
        <a :href="`#${menuItem.path}`" :title="menuItem.name">
          <a-icon :type="menuItem.icon"/>
          <span>{{ menuItem.name }}</span>
        </a>
      </a-menu-item>
      <!-- 二级菜单 -->
      <a-sub-menu v-else :key="menuItem.id">
        <template #title>
          <a-icon :type="menuItem.icon"/>
          <span class="usn">{{ menuItem.name }}</span>
        </template>
        <a-menu-item v-for="subMenuItem in menuItem.children" :key="subMenuItem.id">
          <a :href="`#${subMenuItem.path}`" :title="subMenuItem.name">
            <span>{{ subMenuItem.name }}</span>
          </a>
        </a-menu-item>
      </a-sub-menu>
    </template>
  </a-menu>
</template>

<script>
export default {
  name: 'Menu',
  data() {
    return {
      menuList: [],
      selectedKeys: [],
      defaultOpenKeys: []
    }
  },
  methods: {
    chooseMenu(route = this.$route) {
      const routerPath = route.path
      for (const menu of this.menuList) {
        if (menu.path && routerPath.startsWith(menu.path)) {
          // 一级菜单选中
          this.selectedKeys[0] = menu.id
          this.$forceUpdate()
          return
        }
        if (menu.children) {
          for (const child of menu.children) {
            if (child.path && routerPath.startsWith(child.path)) {
              // 二级菜单选中
              let present = false
              for (const defaultOpenKey of this.defaultOpenKeys) {
                if (defaultOpenKey === menu.id) {
                  present = true
                  break
                }
              }
              this.selectedKeys[0] = child.id
              if (!present) {
                this.defaultOpenKeys.push(menu.id)
              }
              this.$forceUpdate()
              return
            }
          }
        }
      }
    }
  },
  async mounted() {
    // 加载菜单
    this.$api.getMenu().then(({ data }) => {
      let id = 0
      for (const menu of data) {
        menu.id = ++id
        const children = menu.children
        if (children) {
          for (let i = 0; i < children.length; i++) {
            children[i].id = ++id
          }
        }
        this.menuList.push(menu)
      }
      // 选中
      this.chooseMenu()
    })
  }
}
</script>

<style scoped>

</style>
