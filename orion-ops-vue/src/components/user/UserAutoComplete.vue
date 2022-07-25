<template>
  <a-auto-complete v-model="value"
                   :disabled="disabled"
                   :placeholder="placeholder"
                   @change="change"
                   @search="search"
                   allowClear>
    <template #dataSource>
      <a-select-option v-for="user in visibleUser"
                       :key="user.id"
                       :value="JSON.stringify(user)"
                       @click="choose">
        {{ user.username }}
      </a-select-option>
    </template>
  </a-auto-complete>
</template>

<script>
export default {
  name: 'UserAutoComplete',
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '全部'
    }
  },
  data() {
    return {
      userList: [],
      visibleUser: [],
      value: undefined
    }
  },
  methods: {
    change(value) {
      let id
      let val = value
      try {
        const v = JSON.parse(value)
        if (typeof v === 'object') {
          id = v.id
          val = v.username
        }
      } catch (e) {
      }
      this.$emit('change', id, val)
      this.value = val
    },
    choose() {
      this.$nextTick(() => {
        this.$emit('choose')
      })
    },
    search(value) {
      if (!value) {
        this.visibleUser = this.userList
        return
      }
      this.visibleUser = this.userList.filter(s => s.username.toLowerCase().includes(value.toLowerCase()))
    },
    set(id) {
      const filterUsers = this.userList.filter(s => s.id === id)
      if (filterUsers.length) {
        this.value = JSON.stringify(filterUsers[0])
      }
    },
    reset() {
      this.value = undefined
      this.visibleUser = this.userList
    }
  },
  async created() {
    const userListRes = await this.$api.getUserList({ limit: 10000 })
    if (userListRes.data && userListRes.data.rows && userListRes.data.rows.length) {
      for (const row of userListRes.data.rows) {
        this.userList.push({
          id: row.id,
          username: row.username,
          nickname: row.nickname
        })
      }
      this.visibleUser = this.userList
    }
  }
}
</script>

<style scoped>

</style>
