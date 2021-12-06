<template>
  <div id="app-config-container">
    <a-tabs defaultActiveKey="1" tabPosition="left">
      <!-- 基本配置 -->
      <a-tab-pane key="1" tab="基本配置">
        <div class="app-basic-form-wrapper">
          <!-- 表单 -->
          <AddAppForm ref="basicForm" :layout="{
            labelCol: { span: 5 },
            wrapperCol: { span: 19 }
          }"/>
          <div class="app-basic-form-footer">
            <a-button type="primary" @click="updateBasic">修改</a-button>
          </div>
        </div>
      </a-tab-pane>
      <!-- 构建配置 -->
      <a-tab-pane key="2" tab="构建配置">
        <AppBuildConfigForm :appId="appId" :profileId="profileId"/>
      </a-tab-pane>
      <!-- 发布配置 -->
      <a-tab-pane key="3" tab="发布配置">
        <AppReleaseConfigForm :appId="appId" :profileId="profileId"/>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import AddAppForm from '@/components/app/AddAppForm'

export default {
  name: 'AddApp',
  components: {
    AddAppForm,
    AppBuildConfigForm: () => import('@/components/app/AppBuildConfigForm'),
    AppReleaseConfigForm: () => import('@/components/app/AppReleaseConfigForm')
  },
  data() {
    return {
      appId: null,
      profileId: null
    }
  },
  methods: {
    updateBasic() {
      this.$refs.basicForm.check()
    }
  },
  created() {
    this.appId = parseInt(this.$route.params.appId)
    this.profileId = parseInt(this.$route.params.profileId)
  },
  mounted() {
    this.$refs.basicForm && this.$refs.basicForm.update(this.appId)
  }
}
</script>

<style lang="less" scoped>

#app-config-container {
  background: #FFF;
  padding: 8px;
  border-radius: 4px;

  .app-basic-form-wrapper {
    padding: 16px 16px 8px 16px;
    width: 400px;

    /deep/ .ant-row.ant-form-item {
      margin-bottom: 18px;
    }
  }

  .app-basic-form-footer {
    padding-left: 20.83%;
  }

}

</style>
