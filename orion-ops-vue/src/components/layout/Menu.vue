<template>
  <a-menu theme="dark" mode="inline" :defaultSelectedKeys="defaultSelectedKeys" :defaultOpenKeys="defaultOpenKeys">
    <template v-for="menuItem in menuList">
      <!-- 一级菜单 -->
      <a-menu-item v-if="!menuItem.children" @click="toRoute(menuItem.path)" :key="menuItem.id">
        <a-icon :type="menuItem.icon"/>
        <span>{{ menuItem.name }}</span>
      </a-menu-item>
      <!-- 二级菜单 -->
      <a-sub-menu v-else :key="menuItem.id">
        <span slot="title"><a-icon :type="menuItem.icon"/><span>{{ menuItem.name }}</span></span>
        <a-menu-item v-for="subMenuItem in menuItem.children" :key="subMenuItem.id" @click="toRoute(subMenuItem.path)">
          {{ subMenuItem.name }}
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
      defaultSelectedKeys: [],
      defaultOpenKeys: []
    }
  },
  methods: {
    toRoute(path) {
      this.$router.push({ path })
    }
  },
  async mounted() {
    // 加载菜单
    const menuRes = await this.$api.getMenu()
    const menuData = menuRes.data
    let id = 0
    for (const menu of menuData) {
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
    const route = this.$route.path
    for (var menu of this.menuList) {
      if (menu.path === route) {
        // 一级菜单选中
        this.defaultSelectedKeys.push(menu.id)
        return
      }
      if (menu.children) {
        for (var child of menu.children) {
          if (child.path === route) {
            // 二级菜单选中
            this.defaultSelectedKeys.push(child.id)
            this.defaultOpenKeys.push(menu.id)
            return
          }
        }
      }
    }
  }
}
</script>

<style scoped>

</style>
