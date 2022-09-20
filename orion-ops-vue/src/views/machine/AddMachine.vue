<template>
  <a-spin :spinning="loading" class="machine-container gray-box-shadow">
    <!-- 基本信息容器 -->
    <div class="machine-info-container">
      <h2 class="m0">基本信息</h2>
      <a-divider class="title-divider"/>
      <div class="machine-form-wrapper">
        <!-- 机器名称 -->
        <div class="form-item-wrapper">
          <span class="normal-label required-label machine-label">机器名称</span>
          <a-input class="form-item-input" v-model="submit.name" :maxLength="32" allowClear/>
        </div>
        <!-- 唯一标识 -->
        <div class="form-item-wrapper">
          <span class="normal-label required-label machine-label">唯一标识</span>
          <a-input class="form-item-input" v-model="submit.tag" :maxLength="32" allowClear/>
        </div>
        <!-- 机器描述 -->
        <div class="form-item-wrapper">
          <span class="normal-label machine-label">机器描述</span>
          <a-textarea class="form-item-input" v-model="submit.description" :maxLength="64" allowClear/>
        </div>
      </div>
    </div>
    <!-- ssh配置容器 -->
    <div class="machine-info-container my16">
      <h2 class="m0">ssh 配置</h2>
      <a-divider class="title-divider"/>
      <div class="machine-form-wrapper">
        <!-- 主机IP -->
        <div class="form-item-wrapper">
          <span class="normal-label required-label machine-label">主机IP</span>
          <a-input class="form-item-input" v-model="submit.host" :maxLength="128" allowClear/>
          <a class="ml16" @click="testPing">ping</a>
        </div>
        <!-- 用户名 -->
        <div class="form-item-wrapper">
          <span class="normal-label required-label machine-label">用户名</span>
          <a-input class="form-item-input" v-model="submit.username" :maxLength="128" allowClear/>
        </div>
        <!-- SSH端口 -->
        <div class="form-item-wrapper">
          <span class="normal-label required-label machine-label">SSH端口</span>
          <a-input class="form-item-input" v-model="submit.sshPort" v-limit-integer :maxLength="5" allowClear/>
        </div>
        <!-- 认证方式 -->
        <div class="form-item-wrapper">
          <span class="normal-label required-label machine-label">认证方式</span>
          <a-radio-group class="form-item-input" v-model="submit.authType" buttonStyle="solid">
            <a-radio-button :value="type.value" v-for="type in MACHINE_AUTH_TYPE" :key="type.value">
              {{ type.label }}
            </a-radio-button>
          </a-radio-group>
        </div>
        <!-- 密码 -->
        <div class="form-item-wrapper" v-show="submit.authType === MACHINE_AUTH_TYPE.PASSWORD.value">
          <span class="normal-label required-label machine-label">认证密码</span>
          <a-input class="form-item-input" v-model="submit.password" type="password" :maxLength="128"/>
        </div>
        <!-- 秘钥 -->
        <div class="form-item-wrapper" v-show="submit.authType === MACHINE_AUTH_TYPE.SECRET_KEY.value">
          <span class="normal-label required-label machine-label">认证秘钥</span>
          <a-select class="form-item-input" placeholder="请选择" v-model="submit.keyId" allowClear>
            <a-select-option v-for="key in keyList" :key="key.id" :value="key.id">
              {{ key.name }}
            </a-select-option>
          </a-select>
          <a class="ml16" @click="addKey">添加秘钥</a>
          <a class="reload-icon" title="刷新" @click="getKeyList">
            <a-icon type="reload"/>
          </a>
        </div>
        <!-- 机器代理 -->
        <div class="form-item-wrapper">
          <span class="normal-label machine-label">机器代理</span>
          <a-select class="form-item-input proxy-selector" placeholder="请选择" v-model="submit.proxyId" allowClear>
            <a-select-option v-for="proxy in proxyList" :key="proxy.id" :value="proxy.id">
              <div class="proxy-select-option">
                <span>{{ proxy.host }}:{{ proxy.port }}</span>
                <a-tag :color="proxy.type | formatProxyType('color')">
                  {{ proxy.type | formatProxyType('label') }}
                </a-tag>
              </div>
            </a-select-option>
          </a-select>
          <a class="ml16" @click="addProxy">添加代理</a>
          <a class="reload-icon" title="刷新" @click="getProxyList">
            <a-icon type="reload"/>
          </a>
        </div>
      </div>
    </div>
    <!-- 按钮组 -->
    <div class="machine-info-container button-group">
      <a-button class="mr16" type="primary" @click="save">保存</a-button>
      <a-button class="mr16" type="default" @click="testConnect">测试SSH连接</a-button>
    </div>
    <!-- 事件 -->
    <div class="machine-info-container">
      <!-- 添加秘钥模态框 -->
      <AddMachineKeyModal ref="addKeyModal" @added="getKeyList"/>
      <!-- 添加代理模态框 -->
      <AddMachineProxyModal ref="addProxyModal" @added="getProxyList"/>
    </div>
  </a-spin>
</template>

<script>
import { enumValueOf, MACHINE_PROXY_TYPE, MACHINE_AUTH_TYPE } from '@/lib/enum'
import AddMachineKeyModal from '@/components/machine/AddMachineKeyModal'
import AddMachineProxyModal from '@/components/machine/AddMachineProxyModal'

export default {
  name: 'AddMachine',
  components: {
    AddMachineProxyModal,
    AddMachineKeyModal
  },
  data() {
    return {
      MACHINE_AUTH_TYPE,
      loading: false,
      submit: {
        id: undefined,
        name: undefined,
        tag: undefined,
        description: undefined,
        host: undefined,
        username: 'root',
        sshPort: 22,
        proxyId: undefined,
        keyId: undefined,
        authType: MACHINE_AUTH_TYPE.SECRET_KEY.value,
        password: undefined
      },
      proxyList: [],
      keyList: []
    }
  },
  methods: {
    getKeyList() {
      this.$api.getMachineKeyList({
        limit: 10000
      }).then(({ data }) => {
        this.keyList = data.rows
      })
    },
    getProxyList() {
      this.$api.getMachineProxyList({
        limit: 10000
      }).then(({ data }) => {
        this.proxyList = data.rows
      })
    },
    addKey() {
      this.$refs.addKeyModal.add()
    },
    addProxy() {
      this.$refs.addProxyModal.add()
    },
    testPing() {
      if (!this.submit.host) {
        this.$message.error('请输入主机IP')
        return
      }
      const ping = this.$message.loading(`ping ${this.submit.host}`)
      this.$api.machineDirectTestPing({
        host: this.submit.host
      }).then(e => {
        ping()
        if (e.data === 1) {
          this.$message.success('ok')
        } else {
          this.$message.error(`无法访问 ${this.submit.host}`)
        }
      }).catch(() => {
        ping()
        this.$message.error(`无法访问 ${this.submit.host}`)
      })
    },
    testConnect() {
      if (!this.checkSshInfo()) {
        return
      }
      if (this.submit.authType === MACHINE_AUTH_TYPE.PASSWORD.value && !this.submit.password) {
        this.$message.error('请输入认证密码')
        return false
      }
      const ssh = `${this.submit.username}@${this.submit.host}:${this.submit.sshPort}`
      const connecting = this.$message.loading(`connecting ${ssh}`)
      this.$api.machineDirectConnect({
        ...this.submit
      }).then(e => {
        connecting()
        if (e.data === 1) {
          this.$message.success('ok')
        } else {
          this.$message.error(`无法连接 ${ssh}`)
        }
      }).catch(() => {
        connecting()
        this.$message.error(`无法连接 ${ssh}`)
      })
    },
    checkBaseInfo() {
      if (!this.submit.name) {
        this.$message.error('请输入机器名称')
        return false
      }
      if (!this.submit.tag) {
        this.$message.error('请输入机器唯一标识')
        return false
      }
      return true
    },
    checkSshInfo() {
      if (!this.submit.host) {
        this.$message.error('请输入主机IP')
        return false
      }
      if (!this.submit.username) {
        this.$message.error('请输入用户名')
        return false
      }
      if (!this.submit.sshPort) {
        this.$message.error('请输入SSH端口')
        return false
      }
      if (parseInt(this.submit.sshPort) < 2 || parseInt(this.submit.sshPort) > 65534) {
        this.$message.error('SSH端口必须在2~65534之间')
        return false
      }
      if (this.submit.authType === MACHINE_AUTH_TYPE.PASSWORD.value) {
        if (!this.submit.password && !this.submit.id) {
          this.$message.error('请输入认证密码')
          return false
        }
      } else if (this.submit.authType === MACHINE_AUTH_TYPE.SECRET_KEY.value) {
        if (!this.submit.keyId) {
          this.$message.error('请选择认证秘钥')
          return false
        }
      }
      return true
    },
    save() {
      if (!this.checkBaseInfo()) {
        return
      }
      if (!this.checkSshInfo()) {
        return
      }
      this.loading = true
      if (this.submit.id) {
        // 更新
        this.$api.updateMachine({
          ...this.submit
        }).then(() => {
          this.loading = false
          this.$message.success('修改成功')
        }).catch(() => {
          this.loading = false
        })
      } else {
        // 新增
        this.$api.addMachine({
          ...this.submit
        }).then(() => {
          this.loading = false
          this.$message.success('添加成功')
        }).catch(() => {
          this.loading = false
        })
      }
    }
  },
  filters: {
    formatProxyType(type, f) {
      return enumValueOf(MACHINE_PROXY_TYPE, type)[f]
    }
  },
  mounted() {
    // 加载秘钥列表
    this.getKeyList()
    // 加载代理列表
    this.getProxyList()
    // 加载机器信息
    const machineId = this.$route.params.id
    if (machineId) {
      this.loading = true
      this.$api.getMachineDetail({
        id: machineId
      }).then(({ data }) => {
        this.submit = data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style lang="less" scoped>
.machine-container {
  padding: 24px;
  background: #FFF;
}

.machine-form-wrapper {
  width: 600px;

  .form-item-wrapper {
    display: flex;
    align-items: center;
    padding: 8px 0;
    margin-bottom: 8px;
  }

  .form-item-input {
    width: 380px;
  }
}

.machine-label {
  width: 100px;
  margin-right: 8px;
}

.title-divider {
  margin: 8px 0;
  width: 600px;
}

.reload-icon {
  font-size: 18px;
  margin-left: 8px;
}

.proxy-select-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button-group {
  margin-left: 32px;
}

::v-deep .proxy-selector .ant-select-selection-selected-value {
  width: 100%;
}

</style>
