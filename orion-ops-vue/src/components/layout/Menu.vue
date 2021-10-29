<template>
  <a-menu theme="dark" mode="inline" :defaultSelectedKeys="defaultSelectedKeys" :defaultOpenKeys="defaultOpenKeys">
    <template v-for="menuItem in menuList">
      <!-- 一级菜单 -->
      <a-menu-item v-if="!menuItem.children" @click="toRoute(menuItem.path)" :key="menuItem.id">
        <a-icon :type="menuItem.icon"/>
        <span>{{menuItem.name}}</span>
      </a-menu-item>
      <!-- 二级菜单 -->
      <a-sub-menu v-else :key="menuItem.id">
        <span slot="title"><a-icon :type="menuItem.icon"/><span>{{menuItem.name}}</span></span>
        <a-menu-item v-for="subMenuItem in menuItem.children" :key="subMenuItem.id" @click="toRoute(subMenuItem.path)">
          {{subMenuItem.name}}
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
    created() {
      this.menuList.push(
        {
          id: 1,
          name: 'index',
          path: '/',
          icon: 'control'
        }
      )
      this.menuList.push(
        {
          id: 20,
          name: '机器管理',
          icon: 'laptop',
          children: [{
            id: 21,
            name: '机器列表',
            path: '/machine/list'
          }, {
            id: 22,
            name: '环境变量',
            path: '/machine/env'
          }, {
            id: 23,
            name: '机器秘钥',
            path: '/machine/key'
          }, {
            id: 24,
            name: '机器代理',
            path: '/machine/proxy'
          }]
        }
      )
    },
    mounted() {
      const route = this.$utils.getRoute()
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
