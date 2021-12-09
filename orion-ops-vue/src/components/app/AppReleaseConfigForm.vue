<template>
  <a-spin :spinning="loading">
    <div id="app-conf-container">
      <!-- 发布机器 -->
      <div id="app-machine-wrapper">
      <span class="label">
        <span class="span-red mr4">*</span>
        发布机器 :
      </span>
        <MachineChecker style="margin-left: 8px"
                        ref="machineChecker"
                        :defaultValue="machines"
                        :query="{status: $enum.ENABLE_STATUS.ENABLE.value}">
          <a slot="trigger">已选择 {{ machines.length }} 台机器</a>
          <a-button type="primary" size="small" slot="footer" @click="chooseMachines">确定</a-button>
        </MachineChecker>
      </div>
      <!-- 发布操作 -->
      <div id="app-action-container">
        <template v-for="(action, index) in actions">
          <div class="app-action-block" :key="index" v-if="action.visible">
            <!-- 分隔符 -->
            <a-divider class="action-divider">发布操作{{ index + 1 }}</a-divider>
            <div class="app-action-wrapper">
              <!-- 操作 -->
              <div class="app-action">
                <div class="action-name-wrapper">
                <span class="label action-label">
                  <span class="span-red mr4">*</span>
                  操作名称 :
                </span>
                  <a-input class="action-name-input" v-model="action.name" :maxLength="16" placeholder="操作名称"/>
                </div>
                <!-- 代码块 -->
                <div class="action-editor-wrapper" v-if="action.type === $enum.RELEASE_ACTION_TYPE.TARGET_COMMAND.value">
                <span class="label action-label">
                  <span class="span-red mr4">*</span>
                  目标主机命令 :
                </span>
                  <div class="app-action-editor">
                    <Editor :config="editorConfig" :value="action.command" @change="(v) => action.command = v"/>
                  </div>
                </div>
                <div class="action-transfer-wrapper" v-else-if="action.type === $enum.RELEASE_ACTION_TYPE.TRANSFER.value">
                  <span class="label action-label">
                      <span class="span-red mr4">*</span>
                      产物传输路径 :
                  </span>
                  <a-input class="transfer-input" placeholder="目标机器产物传输绝对路径" :maxLength="512" v-model="transferPath"/>
                </div>
              </div>
              <!-- 操作 -->
              <div class="app-action-handler">
                <a-button-group v-if="actions.length > 1">
                  <a-button title="移除" @click="removeAction(index)" icon="minus-circle"/>
                  <a-button title="上移" v-if="index !== 0" @click="swapAction(index, index - 1)" icon="arrow-up"/>
                  <a-button title="下移" v-if="index !== actions.length - 1" @click="swapAction(index + 1, index )" icon="arrow-down"/>
                </a-button-group>
              </div>
            </div>
          </div>
        </template>
      </div>
      <!-- 底部按钮 -->
      <div id="app-action-footer">
        <a-button class="app-action-footer-button" type="dashed"
                  @click="addAction($enum.RELEASE_ACTION_TYPE.TARGET_COMMAND.value)">
          添加命令操作
        </a-button>
        <a-button class="app-action-footer-button" type="dashed"
                  v-if="visibleAddTransfer"
                  @click="addAction($enum.RELEASE_ACTION_TYPE.TRANSFER.value)">
          添加传输操作
        </a-button>
        <a-button class="app-action-footer-button" type="primary" @click="save">保存</a-button>
      </div>
    </div>
  </a-spin>
</template>

<script>

import _enum from '@/lib/enum'
import Editor from '@/components/editor/Editor'
import MachineChecker from '@/components/machine/MachineChecker'

const editorConfig = {
  enableLiveAutocompletion: true,
  fontSize: 14
}

export default {
  name: 'AppReleaseConfigForm',
  props: {
    appId: Number,
    profileId: Number,
    detail: Object
  },
  components: {
    Editor,
    MachineChecker
  },
  computed: {
    visibleAddTransfer() {
      return this.actions.map(s => s.type).filter(t => t === this.$enum.RELEASE_ACTION_TYPE.TRANSFER.value).length < 1
    }
  },
  data() {
    return {
      loading: false,
      transferPath: undefined,
      releaseSerial: _enum.RELEASE_SERIAL_TYPE.PARALLEL.value,
      machines: [],
      actions: [],
      editorConfig
    }
  },
  methods: {
    chooseMachines() {
      const ref = this.$refs.machineChecker
      this.machines = ref.checkedList
      ref.hide()
    },
    addAction(type) {
      this.actions.push({
        type,
        command: '',
        name: undefined,
        visible: true
      })
    },
    removeAction(index) {
      this.actions[index].visible = false
      setTimeout(() => {
        this.actions.splice(index, 1)
      })
    },
    swapAction(index, target) {
      const temp = this.actions[target]
      this.$set(this.actions, target, this.actions[index])
      this.$set(this.actions, index, temp)
    },
    save() {
      if (!this.machines.length) {
        this.$message.warning('请选择发布机器')
        return
      }
      for (let i = 0; i < this.actions.length; i++) {
        const action = this.actions[i]
        if (!action.name) {
          this.$message.warning(`请输入操作名称 [发布操作${i + 1}]`)
          return
        }
        if (this.$enum.RELEASE_ACTION_TYPE.TARGET_COMMAND.value === action.type) {
          if (!action.command) {
            this.$message.warning(`请输入操作命令 [发布操作${i + 1}]`)
            return
          } else if (action.command.length > 1024) {
            this.$message.warning(`操作命令长度不能大于1024位 [发布操作${i + 1}]`)
            return
          }
        } else if (this.$enum.RELEASE_ACTION_TYPE.TRANSFER.value === action.type) {
          if (!this.transferPath || !this.transferPath.trim().length) {
            this.$message.warning('传输操作 传输路径不能为空')
            return
          }
        }
      }
      this.loading = true
      this.$api.configApp({
        appId: this.appId,
        profileId: this.profileId,
        stageType: 20,
        env: {
          transferPath: this.transferPath,
          releaseSerial: this.releaseSerial
        },
        machineIdList: this.machines,
        releaseActions: this.actions
      }).then(() => {
        this.$message.success('保存成功')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  },
  mounted() {
    this.transferPath = this.detail.transferPath
    this.releaseSerial = this.detail.releaseSerial
    if (this.detail.releaseMachines && this.detail.releaseMachines.length) {
      this.machines = this.detail.releaseMachines.map(s => s.machineId)
    }
    if (this.detail.releaseActions && this.detail.releaseActions.length) {
      this.actions = this.detail.releaseActions.map(s => {
        return {
          visible: true,
          name: s.name,
          type: s.type,
          command: s.command
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>

#app-conf-container {
  padding: 18px 8px 0 8px;
  width: 760px;

  .label {
    text-align: end;
    display: block;
    width: 160px;
    font-size: 15px;
    line-height: 32px;
  }

  #app-machine-wrapper {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    align-content: center;
  }

  #app-action-container {
    width: 660px;
    margin-top: 16px;

    .app-action-wrapper {
      width: 100%;
      display: flex;

      .action-label {
        padding: 8px;
      }

      .app-action {
        width: 606px;
        padding: 0 8px 8px 8px;

        .action-name-wrapper {
          display: flex;
          align-items: center;

          .action-name-input {
            width: 430px;
          }
        }

        .action-editor-wrapper {
          display: flex;

          .app-action-editor {
            width: 430px;
            height: 200px;
            margin-top: 8px;
          }
        }

        .action-transfer-wrapper {
          display: flex;
          align-items: center;

          .transfer-input {
            width: 430px;
          }
        }
      }

      .app-action-handler {
        width: 120px;
        margin: 8px 0 0 8px;
      }
    }

    .action-divider {
      width: 720px;
      min-width: 560px;
      margin: 16px 0;
    }
  }

  #app-action-footer {
    margin: 16px 0 8px 168px;
    width: 430px;

    .app-action-footer-button {
      width: 100%;
      margin-bottom: 8px;
    }
  }

}

</style>
