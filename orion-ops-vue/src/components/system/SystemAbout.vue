<template>
  <div class="about-container">
    <div class="other-title normal-title">
      关于
    </div>
    <div class="about-descriptions">
      <a-descriptions>
        <a-descriptions-item label="orion-ops 版本" :span="3">
          <a class="about-value" :href="`https://github.com/lijiahangmax/orion-ops/releases/tag/v${about.orionOpsVersion}`">{{ about.orionOpsVersion }}</a>
        </a-descriptions-item>
        <a-descriptions-item label="orion-kit 版本" :span="3">
          <a class="about-value" :href="`https://github.com/lijiahangmax/orion-kit/releases/tag/v${about.orionKitVersion}`">{{ about.orionKitVersion }}</a>
        </a-descriptions-item>
        <a-descriptions-item label="orion-ops 作者" :span="3">
          <span class="about-value">{{ about.author }} ({{ about.authorCn }})</span>
        </a-descriptions-item>
        <a-descriptions-item label="github 地址" :span="3">
          <a target="_blank" class="about-value" href="https://github.com/lijiahangmax/orion-ops">
            https://github.com/lijiahangmax/orion-ops
          </a>
        </a-descriptions-item>
        <a-descriptions-item label="gitee 地址" :span="3">
          <a target="_blank" class="about-value" href="https://gitee.com/lijiahangmax/orion-ops">
            https://gitee.com/lijiahangmax/orion-ops
          </a>
        </a-descriptions-item>
        <a-descriptions-item label="问题反馈" :span="3">
          <a target="_blank" class="about-value" href="https://github.com/lijiahangmax/orion-ops/issues">
            https://github.com/lijiahangmax/orion-ops/issues
          </a>
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </div>
</template>

<script>

export default {
  name: 'SystemAbout',
  data() {
    return {
      about: {}
    }
  },
  methods: {
    init() {
      this.$api.getSystemAbout().then(({ data }) => {
        this.about = data
      })
    }
  },
  mounted() {
    this.init()
  }
}
</script>

<style lang="less" scoped>

.other-title {
  margin: 16px 0 16px 16px;
}

.about-descriptions {
  margin: 18px 0 0 0;
}

::v-deep .ant-descriptions-item-label {
  margin-left: 16px;
  width: 124px;
  text-align: end;
  font-size: 16px;
  color: rgba(0, 0, 0, .85);
}

.about-value {
  color: #1890FF;
}
</style>
