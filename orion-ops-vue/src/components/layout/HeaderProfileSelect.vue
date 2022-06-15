<template>
  <a-dropdown v-if="profileList.length">
    <a class="ant-dropdown-link" id="current-profile" @click="e => e.preventDefault()">
      {{ currentProfile.name }}
      <a-icon type="down"/>
    </a>
    <template #overlay>
      <a-menu @click="chooseProfile">
        <a-menu-item v-for="profile in profileList" :key="JSON.stringify(profile)">
          {{ profile.name }}
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script>
import { isEmptyStr } from '@/lib/utils'

export default {
  name: 'HeaderProfileSelect',
  data() {
    return {
      currentProfile: '',
      profileList: []
    }
  },
  methods: {
    chooseProfile({ key }) {
      this.currentProfile = JSON.parse(key)
      this.$storage.set(this.$storage.keys.ACTIVE_PROFILE, key)
      this.$emit('chooseProfile', this.currentProfile)
    },
    loadProfile() {
      this.$api.fastGetProfileList().then(({ data }) => {
        if (!data || !data.length) {
          return
        }
        this.profileList = data
      }).then(() => {
        if (!this.profileList.length) {
          this.$storage.remove(this.$storage.keys.ACTIVE_PROFILE)
          return
        }
        let storageProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
        if (isEmptyStr(storageProfile)) {
          // 如果没有则拿到第一个
          storageProfile = this.profileList[0]
        } else {
          let matches = false
          storageProfile = JSON.parse(storageProfile)
          for (var profileValue of this.profileList) {
            if (profileValue.id === storageProfile.id) {
              matches = true
            }
          }
          if (!matches) {
            storageProfile = this.profileList[0]
          }
        }
        this.$storage.set(this.$storage.keys.ACTIVE_PROFILE, JSON.stringify(storageProfile))
        this.currentProfile = storageProfile
      })
    }
  },
  mounted() {
    this.loadProfile()
  }
}
</script>

<style scoped>
#current-profile {
  font-size: 16px;
}
</style>
