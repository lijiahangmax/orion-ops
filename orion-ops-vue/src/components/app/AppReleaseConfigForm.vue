<template>
  <div id="app-conf-container">
    <a-spin :spinning="loading">
      <!-- 发布机器 -->
      <div id="app-machine-wrapper">
        <span class="label normal-label required-label">发布机器</span>
        <MachineChecker style="margin-left: 8px"
                        ref="machineChecker"
                        :defaultValue="machines"
                        :query="machineQuery">
          <template #trigger>
            <span class="span-blue pointer">已选择 {{ machines.length }} 台机器</span>
          </template>
          <template #footer>
            <a-button type="primary" size="small" @click="chooseMachines">确定</a-button>
          </template>
        </MachineChecker>
      </div>
      <!-- 发布序列 -->
      <div id="app-release-serial-wrapper">
        <span class="label normal-label required-label">发布序列</span>
        <a-radio-group class="ml8" v-model="releaseSerial">
          <a-radio v-for="type of SERIAL_TYPE" :key="type.value" :value="type.value">
            {{ type.label }}
          </a-radio>
        </a-radio-group>
      </div>
      <!-- 异常处理 -->
      <div id="app-release-exception-wrapper" v-show="releaseSerial === SERIAL_TYPE.SERIAL.value">
        <span class="label normal-label required-label">异常处理</span>
        <a-radio-group class="ml8" v-model="exceptionHandler">
          <a-tooltip v-for="type of EXCEPTION_HANDLER_TYPE" :key="type.value" :title="type.title">
            <a-radio :value="type.value">
              {{ type.label }}
            </a-radio>
          </a-tooltip>
        </a-radio-group>
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
                <!-- 名称 -->
                <div class="action-name-wrapper">
                  <span class="label action-label normal-label required-label">操作名称</span>
                  <a-input class="action-name-input" v-model="action.name" :maxLength="32" placeholder="操作名称"/>
                </div>
                <!-- 代码块 -->
                <div class="action-editor-wrapper" v-if="action.type === RELEASE_ACTION_TYPE.COMMAND.value">
                  <span class="label action-label normal-label required-label">目标主机命令</span>
                  <div class="app-action-editor">
                    <Editor :config="editorConfig" :value="action.command" @change="(v) => action.command = v"/>
                  </div>
                </div>
                <!-- 文件传输方式 -->
                <div class="action-transfer-wrapper" v-if="action.type === RELEASE_ACTION_TYPE.TRANSFER.value">
                  <span class="label action-label normal-label required-label">文件传输方式</span>
                  <!-- 类型选择 -->
                  <a-select :class="['transfer-input', transferMode === RELEASE_TRANSFER_MODE.SFTP.value ? 'help-input' : '']"
                            v-model="transferMode">
                    <a-select-option v-for="type of RELEASE_TRANSFER_MODE" :key="type.value" :value="type.value">
                      {{ type.label }}
                    </a-select-option>
                  </a-select>
                  <!-- 描述 -->
                  <a-popover placement="top" v-if="transferMode === RELEASE_TRANSFER_MODE.SFTP.value">
                    <template slot="content">
                      文件传输方式选择 SFTP 后, 当执行传输操作时, 会先删除文件传输路径再进行传输操作<br/>
                      ⚠ 这里一定要注意, 配置不正确会导致数据误删除!!!
                    </template>
                    <a-icon class="help-trigger span-red" type="question-circle"/>
                  </a-popover>
                </div>
                <!-- 文件传输路径 -->
                <div class="action-transfer-wrapper" v-if="action.type === RELEASE_ACTION_TYPE.TRANSFER.value">
                  <span class="label action-label normal-label required-label">文件传输路径</span>
                  <a-textarea class="transfer-input"
                              v-model="transferPath"
                              :autoSize="{minRows: 1}"
                              :maxLength="1024"
                              placeholder="目标机器产物传输绝对路径, 路径不能包含 \ 应该用 / 替换"/>
                </div>
                <!-- 文件传输类型 -->
                <div class="action-transfer-wrapper" v-if="action.type === RELEASE_ACTION_TYPE.TRANSFER.value">
                  <span class="label action-label normal-label required-label">文件传输类型</span>
                  <!-- 文件传输类型 -->
                  <a-select class="transfer-input help-input" v-model="transferFileType">
                    <a-select-option v-for="type of RELEASE_TRANSFER_FILE_TYPE" :key="type.value" :value="type.value">
                      {{ type.label }}
                    </a-select-option>
                  </a-select>
                  <!-- 描述 -->
                  <a-popover placement="top">
                    <template slot="content">
                      如构建产物为普通文件选择 (文件 / 文件夹)<br/>
                      如构建产物为文件夹且传输整个文件夹选择 (文件 / 文件夹)<br/>
                      如构建产物为文件夹且传输文件夹zip选择 (文件夹zip)
                    </template>
                    <a-icon class="help-trigger" type="question-circle"/>
                  </a-popover>
                </div>
                <!-- scp 传输命令 -->
                <div class="action-transfer-wrapper"
                     v-if="action.type === RELEASE_ACTION_TYPE.TRANSFER.value && transferMode === RELEASE_TRANSFER_MODE.SCP.value">
                  <span class="label action-label normal-label required-label"> scp 传输命令</span>
                  <!-- scp 传输命令 -->
                  <a-textarea class="transfer-input help-input"
                              v-model="action.command"
                              :autoSize="{minRows: 1}"
                              :maxLength="1024"
                              placeholder="目标机器和宿主机需要建立 ssh 免密登陆"/>
                  <!-- scp 传输命令描述 -->
                  <a-popover placement="top">
                    <template slot="content">
                      bundle_path&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;构建产物路径<br/>
                      target_username&nbsp;&nbsp;目标机器用户<br/>
                      target_host&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;目标机器主机<br/>
                      transfer_path&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;传输路径
                    </template>
                    <a-icon class="help-trigger" type="question-circle"/>
                  </a-popover>
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
                  @click="addAction(RELEASE_ACTION_TYPE.COMMAND.value)">
          添加命令操作 (发布机器执行)
        </a-button>
        <a-button class="app-action-footer-button" type="dashed"
                  v-if="visibleAddTransfer"
                  @click="addAction(RELEASE_ACTION_TYPE.TRANSFER.value)">
          添加传输操作 (构建产物传输至发布机器)
        </a-button>
        <a-button class="app-action-footer-button" type="primary" @click="save">保存</a-button>
      </div>
    </a-spin>
  </div>
</template>

<script>
import { ENABLE_STATUS, EXCEPTION_HANDLER_TYPE, RELEASE_ACTION_TYPE, RELEASE_TRANSFER_FILE_TYPE, RELEASE_TRANSFER_MODE, SERIAL_TYPE } from '@/lib/enum'
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
    dataLoading: Boolean,
    detail: Object
  },
  components: {
    Editor,
    MachineChecker
  },
  computed: {
    visibleAddTransfer() {
      return this.actions.map(s => s.type).filter(t => t === RELEASE_ACTION_TYPE.TRANSFER.value).length < 1
    }
  },
  watch: {
    detail(e) {
      this.initData(e)
    },
    dataLoading(e) {
      this.loading = e
    }
  },
  data() {
    return {
      RELEASE_ACTION_TYPE,
      RELEASE_TRANSFER_MODE,
      RELEASE_TRANSFER_FILE_TYPE,
      SERIAL_TYPE,
      EXCEPTION_HANDLER_TYPE,
      loading: false,
      profileId: null,
      transferPath: undefined,
      transferMode: RELEASE_TRANSFER_MODE.SCP.value,
      transferFileType: RELEASE_TRANSFER_FILE_TYPE.NORMAL.value,
      releaseSerial: SERIAL_TYPE.PARALLEL.value,
      exceptionHandler: EXCEPTION_HANDLER_TYPE.SKIP_ALL.value,
      machines: [],
      actions: [],
      editorConfig,
      machineQuery: { status: ENABLE_STATUS.ENABLE.value }
    }
  },
  methods: {
    initData(detail) {
      this.profileId = detail.profileId
      this.transferPath = detail.env && detail.env.transferPath
      this.transferMode = detail.env && detail.env.transferMode
      this.transferFileType = detail.env && detail.env.transferFileType
      this.releaseSerial = detail.env && detail.env.releaseSerial
      this.exceptionHandler = detail.env && detail.env.exceptionHandler
      if (detail.releaseMachines && detail.releaseMachines.length) {
        this.machines = detail.releaseMachines.map(s => s.machineId)
      } else {
        this.machines = []
      }
      if (detail.releaseActions && detail.releaseActions.length) {
        this.actions = detail.releaseActions.map(s => {
          return {
            visible: true,
            name: s.name,
            type: s.type,
            command: s.command
          }
        })
      } else {
        this.actions = []
      }
    },
    chooseMachines() {
      const ref = this.$refs.machineChecker
      this.machines = ref.checkedList
      ref.hide()
    },
    addAction(type) {
      const action = {
        type,
        name: undefined,
        visible: true
      }
      if (RELEASE_ACTION_TYPE.TRANSFER.value === type) {
        action.command = 'scp "@{bundle_path}" @{target_username}@@{target_host}:"@{transfer_path}"'
      } else {
        action.command = ''
      }
      this.actions.push(action)
    },
    removeAction(index) {
      this.actions[index].visible = false
      this.$nextTick(() => {
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
      if (!this.actions.length) {
        this.$message.warning('请设置发布操作')
        return
      }
      for (let i = 0; i < this.actions.length; i++) {
        const action = this.actions[i]
        if (!action.name) {
          this.$message.warning(`请输入操作名称 [发布操作${i + 1}]`)
          return
        }
        if (RELEASE_ACTION_TYPE.COMMAND.value === action.type) {
          if (!action.command || !action.command.trim().length) {
            this.$message.warning(`请输入操作命令 [发布操作${i + 1}]`)
            return
          } else if (action.command.length > 2048) {
            this.$message.warning(`操作命令长度不能大于2048位 [发布操作${i + 1}] 当前: ${action.command.length}`)
            return
          }
        } else if (RELEASE_ACTION_TYPE.TRANSFER.value === action.type) {
          if (!this.transferPath || !this.transferPath.trim().length) {
            this.$message.warning('传输操作 传输路径不能为空')
            return
          }
          if (this.transferPath.includes('\\')) {
            this.$message.warning('产物传输路径不能包含 \\ 应该用 / 替换')
            return
          }
          if (RELEASE_TRANSFER_MODE.SCP.value === this.transferMode) {
            if (!action.command || !action.command.trim().length) {
              this.$message.warning('请输入 scp 传输命令')
              return
            }
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
          transferMode: this.transferMode,
          transferFileType: this.transferFileType,
          releaseSerial: this.releaseSerial,
          exceptionHandler: this.exceptionHandler
        },
        machineIdList: this.machines,
        releaseActions: this.actions
      }).then(() => {
        this.$message.success('保存成功')
        this.$emit('updated')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  },
  mounted() {
    this.initData(this.detail)
  }
}
</script>

<style lang="less" scoped>
@label-width: 160px;
@action-handler-width: 120px;
@app-top-container-width: 994px;
@app-action-container-width: 994px;
@app-action-width: 876px;
@action-name-input-width: 700px;
@app-action-editor-width: 700px;
@app-action-editor-height: 250px;
@transfer-input-width: 700px;
@help-input-width: 670px;
@action-divider-min-width: 830px;
@action-divider-width: 990px;
@app-action-footer-width: 700px;
@footer-margin-left: 168px;
@desc-margin-left: 160px;

#app-conf-container {
  padding: 18px 8px 0 8px;
  overflow: auto;

  .label {
    width: @label-width;
    font-size: 15px;
    line-height: 32px;
  }

  #app-machine-wrapper {
    width: @app-top-container-width;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    align-content: center;
  }

  #app-release-serial-wrapper, #app-release-exception-wrapper {
    width: @app-top-container-width;
    display: flex;
    align-items: center;
    margin-top: 8px;
  }

  #app-action-container {
    width: @app-action-container-width;
    margin-top: 16px;

    .app-action-wrapper {
      width: 100%;
      display: flex;

      .action-label {
        padding: 8px;
      }

      .app-action {
        width: @app-action-width;
        padding: 0 8px 8px 8px;

        .action-name-wrapper {
          display: flex;
          align-items: center;

          .action-name-input {
            width: @action-name-input-width;
          }
        }

        .action-editor-wrapper {
          display: flex;

          .app-action-editor {
            width: @app-action-editor-width;
            height: @app-action-editor-height;
            margin-top: 8px;
          }
        }

        .action-transfer-wrapper {
          display: flex;
          align-items: center;

          .transfer-input {
            width: @transfer-input-width;
          }
        }
      }

      .app-action-handler {
        width: @action-handler-width;
        margin: 8px 0 0 8px;
      }
    }

    .action-divider {
      min-width: @action-divider-min-width;
      width: @action-divider-width;
      margin: 16px 0;
    }
  }

  #app-action-footer {
    margin: 16px 0 8px @footer-margin-left;
    width: @app-action-footer-width;

    .app-action-footer-button {
      width: 100%;
      margin-bottom: 8px;
    }
  }

  .config-description {
    margin: 4px 0 0 @desc-margin-left;
    display: block;
    color: rgba(0, 0, 0, .45);
    font-size: 13px;
  }

  .help-input {
    width: @help-input-width !important;
  }

  .help-trigger {
    cursor: pointer;
    color: #1890FF;
    font-size: 20px;
    padding-left: 8px;
  }

}

</style>
