<template>
  <a-select v-model="id"
            @change="$emit('change', id)"
            placeholder="全部"
            :disabled="disabled"
            allowClear>
    <a-select-option v-for="user in userList"
                     :value="user.id"
                     :key="user.id">
      {{ `${user.username} (${user.nickname})` }}
    </a-select-option>
  </a-select>
</template>

<script>
export default {
  name: 'UserSelector',
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      id: undefined,
      userList: []
    }
  },
  methods: {
    reset() {
      this.id = undefined
    },
    set(id) {
      this.id = id
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
    }
  }
}
</script>

<style scoped>

</style>
