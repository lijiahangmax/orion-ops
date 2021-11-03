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
      const menuData = [
        {
          name: '控制台',
          path: '/console',
          icon: 'dashboard'
        },
        {
          name: '机器管理',
          icon: 'laptop',
          children: [
            {
              name: '机器列表',
              path: '/machine/list'
            }, {
              name: '环境变量',
              path: '/machine/env'
            }, {
              name: '机器秘钥',
              path: '/machine/key'
            }, {
              name: '机器代理',
              path: '/machine/proxy'
            }, {
              name: '终端控制',
              requireAdmin: true,
              path: '/terminal/session'
            }
          ]
        },
        {
          name: '执行管理',
          icon: 'apartment',
          children: [
            {
              name: '批量执行',
              path: '/batch/exec'
            }, {
              name: '日志面板',
              path: '/log/view'
            }
          ]
        },
        {
          name: '应用管理',
          icon: 'appstore',
          children: [
            {
              name: '应用列表',
              path: '/app/list'
            }, {
              name: '环境管理',
              path: '/app/profile'
            }, {
              name: '环境变量',
              path: '/app/env'
            }
          ]
        },
        {
          name: 'CI/CD',
          icon: 'deployment-unit',
          children: [
            {
              name: '发布配置',
              path: '/release/config'
            }, {
              name: '发布单',
              path: '/release/bill'
            }
          ]
        },
        {
          name: '用户中心',
          icon: 'user',
          children: [
            {
              name: '用户列表',
              path: '/user/list'
            }, {
              name: '个人信息',
              path: '/user/detail'
            }
          ]
        },
        {
          name: '设置',
          icon: 'control',
          children: [
            {
              name: '模板配置',
              path: '/template/list'
            }
          ]
        }
      ]
      var isAdmin
      try {
        const user = JSON.parse(this.$storage.get(this.$storage.keys.USER))
        isAdmin = user.roleType === 10
      } catch (e) {
        isAdmin = false
      }
      let id = 0
      for (const menu of menuData) {
        // 过滤管理员一级菜单
        if (menu.requireAdmin && !isAdmin) {
          continue
        }
        menu.id = ++id
        const children = menu.children
        if (children) {
          for (let i = 0; i < children.length; i++) {
            // 过滤管理员二级菜单
            if (children[i].requireAdmin && !isAdmin) {
              children.splice(i, 1)
            } else {
              children[i].id = ++id
            }
          }
        }
        this.menuList.push(menu)
      }
    },
    mounted() {
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
