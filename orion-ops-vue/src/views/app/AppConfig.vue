<template>
  <div class="app-config-container">
    <!-- 配置tab -->
    <div id="app-config-wrapper">
      <a-tabs defaultActiveKey="1" tabPosition="left">
        <!-- 基本配置 -->
        <a-tab-pane key="1" tab="基本配置">
          <div class="app-basic-form-wrapper">
            <a-spin :spinning="loading">
              <!-- 表单 -->
              <AddAppForm ref="basicForm" :layout="{ labelCol: { span: 4 }, wrapperCol: { span: 20 }}"/>
              <div class="app-basic-form-footer">
                <a-button type="primary" @click="updateBasic">修改</a-button>
                <a-button class="ml16" @click="loadAppConfig">刷新</a-button>
              </div>
            </a-spin>
          </div>
        </a-tab-pane>
        <!-- 构建配置 -->
        <a-tab-pane key="2" tab="构建配置">
          <AppBuildConfigForm :appId="appId" :dataLoading="loading" :detail="detail" @updated="loadAppConfig"/>
        </a-tab-pane>
        <!-- 发布配置 -->
        <a-tab-pane key="3" tab="发布配置">
          <AppReleaseConfigForm :appId="appId" :dataLoading="loading" :detail="detail" @updated="loadAppConfig"/>
        </a-tab-pane>
      </a-tabs>
    </div>
    <!-- 事件 -->
    <div id="app-config-event">
      <!-- 模板选择 -->
      <TemplateSelector ref="templateSelector" @selected="chooseTemplate"/>
    </div>
  </div>
</template>

<script>
import AddAppForm from '@/components/app/AddAppForm'

export default {
  name: 'AddApp',
  components: {
    AddAppForm,
    AppBuildConfigForm: () => import('@/components/app/AppBuildConfigForm'),
    AppReleaseConfigForm: () => import('@/components/app/AppReleaseConfigForm'),
    TemplateSelector: () => import('@/components/content/TemplateSelector')
  },
  data() {
    return {
      appId: null,
      profileId: null,
      loading: false,
      detail: {}
    }
  },
  watch: {
    profileId() {
      this.loadAppConfig()
    }
  },
  methods: {
    chooseProfile({ id }) {
      this.profileId = id
    },
    openTemplate() {
      this.$refs.templateSelector.open()
    },
    chooseTemplate(e) {
      this.$copy(e)
      this.$refs.templateSelector.close()
    },
    onHeaderEvent(e) {
      e && this[e] && this[e]()
    },
    loadAppConfig() {
      this.loading = true
      this.$api.getAppDetail({
        id: this.appId,
        profileId: this.profileId
      }).then(({ data }) => {
        this.loading = false
        data.profileId = this.profileId
        this.detail = data
        // 初始化表单
        this.$refs.basicForm && this.$refs.basicForm.initRecord(data)
      }).catch(() => {
        this.loading = false
      })
    },
    updateBasic() {
      this.$refs.basicForm.check()
    }
  },
  created() {
    this.appId = parseInt(this.$route.params.appId)
    // 读取当前环境
    const activeProfile = this.$storage.get(this.$storage.keys.ACTIVE_PROFILE)
    if (!activeProfile) {
      this.$message.warning('请先维护应用环境')
      return
    }
    this.profileId = JSON.parse(activeProfile).id
  }
}
</script>

<style lang="less" scoped>

#app-config-wrapper {
  background: #FFF;
  padding: 8px;
  border-radius: 4px;

  .app-basic-form-wrapper {
    padding: 16px 16px 8px 16px;
    width: 580px;

    ::v-deep .ant-row.ant-form-item {
      margin-bottom: 18px;
    }
  }

  .app-basic-form-footer {
    padding-left: 16.83%;
  }

}

</style>
