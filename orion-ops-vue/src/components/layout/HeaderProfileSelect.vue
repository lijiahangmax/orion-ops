<template>
  <a-dropdown v-if="profileList.length">
    <a class="ant-dropdown-link" id="current-profile" @click="e => e.preventDefault()">
      {{currentProfile}}
      <a-icon type="down"/>
    </a>
    <a-menu slot="overlay" @click="chooseProfile">
      <template v-for="profile in profileList">
        <a-menu-item :key="profile.tag">
          {{profile.tag}}
        </a-menu-item>
      </template>
    </a-menu>
  </a-dropdown>
</template>

<script>
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
        this.currentProfile = key
        this.$storage.set(this.$storage.keys.CURRENT_PROFILE, key)
      }
    },
    mounted() {
      this.$api.getProfileList()
        .then(e => {
          if (!e.data) {
            return
          }
          e.data.forEach(d => this.profileList.push({
            name: d.name,
            tag: d.tag
          }))
        })
        .then(() => {
          if (!this.profileList) {
            this.$storage.remove(this.$storage.keys.CURRENT_PROFILE)
            return
          }
          let storageProfile = this.$storage.get(this.$storage.keys.CURRENT_PROFILE)
          if (this.$utils.isEmptyStr(storageProfile)) {
            storageProfile = this.profileList[0].tag
          } else {
            let matches = false
            for (var profileValue of this.profileList) {
              if (profileValue.tag === storageProfile) {
                matches = true
              }
            }
            if (!matches) {
              storageProfile = this.profileList[0].tag
            }
          }
          this.$storage.set(this.$storage.keys.CURRENT_PROFILE, storageProfile)
          this.currentProfile = storageProfile
        })
    }
  }
</script>

<style scoped>
  #current-profile {
    font-size: 16px;
  }
</style>
