<template>
  <a-auto-complete v-model="value"
                   :disabled="disabled"
                   :placeholder="placeholder"
                   @change="change"
                   @search="search"
                   allowClear>
    <template #dataSource>
      <a-select-option v-for="profile in visibleProfile"
                       :key="profile.id"
                       :value="JSON.stringify(profile)"
                       @click="choose">
        {{ profile.name }}
      </a-select-option>
    </template>
  </a-auto-complete>
</template>

<script>
export default {
  name: 'ProfileAutoComplete',
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
      profileList: [],
      visibleProfile: [],
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
          val = v.name
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
        this.visibleProfile = this.profileList
        return
      }
      this.visibleProfile = this.profileList.filter(s => s.name.toLowerCase().includes(value.toLowerCase()))
    },
    reset() {
      this.value = undefined
      this.visibleProfile = this.profileList
    }
  },
  async created() {
    const { data } = await this.$api.getProfileList({
      limit: 10000
    })
    if (data && data.rows && data.rows.length) {
      for (const row of data.rows) {
        this.profileList.push({
          id: row.id,
          name: row.name
        })
      }
      this.visibleProfile = this.profileList
    }
  }
}
</script>

<style scoped>

</style>
