<template>
  <div class="ip-config-container">
    <!-- 当前ip -->
    <div class="current-ip-container">
      <a-alert type="info">
        <template #message>
          <span class="mr4">当前访问IP: </span>
          <span class="ip-text span-blue mr16">{{ (currentIp || '--') }}</span>
          <span class="ip-text"> {{ (ipLocation || '--') }}</span>
        </template>
      </a-alert>
    </div>
    <!-- 提示 -->
    <div class="ip-tips-container">
      <a-alert type="warning" :show-icon="true">
        <template #message>
          <span class="mr8">请仔细确认后再保存, IP配置后立即生效.</span>
          <span class="ip-text span-blue mr4">127.0.0.1</span>
          <span class="mr4">不受访问限制.</span>
          <span class="mr8">支持配置IP段</span>
          <span class="mr8 ip-text span-blue">192.168.1.1/20</span>
          <span class="ip-text span-blue mr8">192.168.1.1/192.168.2.100</span>
          <span class="mr4">可在项目启动时设置</span>
          <span class="mr4 span-blue pointer" title="点击复制" @click="$copy('--disable-ip-filter')">
            --disable-ip-filter
          </span>
          <span class="mr4">来停用IP过滤</span>
        </template>
      </a-alert>
    </div>
    <!-- 配置 -->
    <div class="ip-config-form-wrapper">
      <a-form v-bind="layout">
        <!-- 黑名单 -->
        <a-form-item class="my8">
          <template #label>
            <span>
              <a-icon class="mr4" type="stop" theme="twoTone"/>IP 黑名单
            </span>
          </template>
          <a-textarea class="ip-list-textarea"
                      v-model="blackIpList"
                      ref="black-textarea"
                      placeholder="请输入IP黑名单规则, 多个则使用换行 仅支持IPV4地址."
                      :disabled="!$isAdmin() || !enableIpFilter"
                      :autoSize="{minRows: 10, maxRows: 10}"
                      :maxLength="2000"
                      allowClear/>
        </a-form-item>
        <!-- 白名单 -->
        <a-form-item class="my8">
          <template #label>
            <span>
              <a-icon class="mr4" type="check-circle" theme="twoTone"/>IP 白名单
            </span>
          </template>
          <a-textarea class="ip-list-textarea"
                      v-model="whiteIpList"
                      ref="white-textarea"
                      :disabled="!$isAdmin() || !enableIpFilter"
                      placeholder="请输入IP白名单规则, 多个则使用换行 仅支持IPV4地址."
                      :autoSize="{minRows: 10, maxRows: 10}"
                      :maxLength="2000"
                      allowClear/>
        </a-form-item>
      </a-form>
    </div>
    <!-- 按钮 -->
    <div class="ip-config-form-buttons" v-if="$isAdmin()">
      <!-- 启用 -->
      <div class="mr16">
        是否启用:
        <a-tooltip title="需点击保存生效">
          <a-switch v-model="enableIpFilter"
                    checkedChildren="启用"
                    unCheckedChildren="停用"/>
        </a-tooltip>
      </div>
      <!-- 规则类型 -->
      <div class="mr16">
        规则类型:
        <a-tooltip title="需点击保存生效">
          <a-switch v-model="enableWhiteIpList"
                    :disabled="!enableIpFilter"
                    checkedChildren="白名单"
                    unCheckedChildren="黑名单"
                    @change="changeEnableWhiteFilter"/>
        </a-tooltip>
      </div>
      <!-- 保存 -->
      <a-button type="primary" class="save-button" @click="save">
        保存
      </a-button>
    </div>
  </div>
</template>

<script>
const layout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 22 }
}

export default {
  name: 'IpConfig',
  data() {
    return {
      layout,
      currentIp: null,
      ipLocation: null,
      whiteIpList: null,
      blackIpList: null,
      enableIpFilter: false,
      enableWhiteIpList: false
    }
  },
  methods: {
    changeEnableWhiteFilter(e) {
      if (this.enableIpFilter) {
        this.$refs[e ? 'white-textarea' : 'black-textarea'].focus()
      }
    },
    save() {
      this.$api.configIpList({
        currentIp: this.currentIp,
        whiteIpList: this.whiteIpList,
        blackIpList: this.blackIpList,
        enableIpFilter: this.enableIpFilter,
        enableWhiteIpList: this.enableWhiteIpList
      }).then(() => {
        this.$message.success('已生效')
      })
    }
  },
  mounted() {
    this.$api.getIpInfo()
      .then(({ data }) => {
        this.currentIp = data.currentIp
        this.ipLocation = data.ipLocation
        this.whiteIpList = data.whiteIpList
        this.blackIpList = data.blackIpList
        this.enableIpFilter = data.enableIpFilter
        this.enableWhiteIpList = data.enableWhiteIpList
      })
  }
}
</script>

<style lang="less" scoped>
.ip-config-container {
  margin-top: 8px;

  .ip-text {
    font-weight: 500;
    font-size: 15px;
  }

  .ip-tips-container {
    margin: 16px 0;
  }

  .ip-label {
    display: flex;
    align-items: center;
  }

  .ip-config-form-buttons {
    margin: 0 0 12px 8.33%;
    display: flex;
    align-items: center;
  }

}
</style>
