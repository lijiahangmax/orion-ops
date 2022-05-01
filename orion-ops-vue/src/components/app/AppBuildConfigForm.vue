<template>
  <div id="app-conf-container">
    <a-spin :spinning="loading">
      <!-- 产物路径 -->
      <div id="app-bundle-container">
        <div id="app-bundle-wrapper">
          <span class="label normal-label required-label">构建产物路径</span>
          <a-textarea class="bundle-input"
                      v-model="bundlePath"
                      :maxLength="1024"
                      :autoSize="{minRows: 1}"
                      :placeholder="'基于版本仓库的相对路径 或 绝对路径, 路径不能包含 \\\ 应该用 / 替换'"/>
        </div>
      </div>
      <!-- 构建操作 -->
      <div id="app-action-container">
        <template v-for="(action, index) in actions">
          <div class="app-action-block" :key="index" v-if="action.visible">
            <!-- 分隔符 -->
            <a-divider class="action-divider">构建操作{{ index + 1 }}</a-divider>
            <div class="app-action-wrapper">
              <!-- 操作 -->
              <div class="app-action">
                <div class="action-name-wrapper">
                  <span class="label normal-label required-label action-label">操作名称{{ index + 1 }}</span>
                  <a-input class="action-name-input" v-model="action.name" :maxLength="32" placeholder="操作名称"/>
                </div>
                <!-- 代码块 -->
                <div class="action-editor-wrapper" v-if="action.type === $enum.BUILD_ACTION_TYPE.COMMAND.value">
                  <span class="label normal-label required-label action-label">主机命令{{ index + 1 }}</span>
                  <div class="app-action-editor">
                    <Editor :config="editorConfig" :value="action.command" @change="(v) => action.command = v"/>
                  </div>
                </div>
                <div class="action-type-wrapper" v-else>
                  <span class="label normal-label action-label">操作类型</span>
                  <a-button class="action-type-name" ghost disabled>
                    {{ $enum.valueOf($enum.BUILD_ACTION_TYPE, action.type).label }}
                  </a-button>
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
                  @click="addAction($enum.BUILD_ACTION_TYPE.COMMAND.value)">
          添加命令操作
        </a-button>
        <a-button class="app-action-footer-button" type="dashed"
                  v-if="visibleAddCheckout"
                  @click="addAction($enum.BUILD_ACTION_TYPE.CHECKOUT.value)">
          添加检出操作
        </a-button>
        <a-button class="app-action-footer-button" type="primary" @click="save">保存</a-button>
      </div>
    </a-spin>
  </div>
</template>

<script>

import Editor from '@/components/editor/Editor'

const editorConfig = {
  enableLiveAutocompletion: true,
  fontSize: 14
}

export default {
  name: 'AppBuildConfigForm',
  props: {
    appId: Number,
    dataLoading: Boolean,
    detail: Object
  },
  components: {
    Editor
  },
  computed: {
    visibleAddCheckout() {
      return this.vcsId &&
        this.vcsId !== null &&
        this.actions.map(s => s.type).filter(t => t === this.$enum.BUILD_ACTION_TYPE.CHECKOUT.value).length < 1
    }
  },
  data() {
    return {
      loading: false,
      profileId: null,
      vcsId: null,
      bundlePath: undefined,
      actions: [],
      editorConfig
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
  methods: {
    initData(detail) {
      this.profileId = detail.profileId
      this.vcsId = detail.vcsId
      this.bundlePath = detail.env && detail.env.bundlePath
      if (detail.buildActions && detail.buildActions.length) {
        this.actions = detail.buildActions.map(s => {
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
      if (!this.bundlePath || !this.bundlePath.trim().length) {
        this.$message.warning('请输入构建产物路径')
        return
      }
      if (this.bundlePath.includes('\\')) {
        this.$message.warning('构建产物路径不能包含 \\ 应该用 / 替换')
        return
      }
      if (!this.actions.length) {
        this.$message.warning('请设置构建操作')
        return
      }
      for (let i = 0; i < this.actions.length; i++) {
        const action = this.actions[i]
        if (!action.name) {
          this.$message.warning(`请输入操作名称 [构建操作${i + 1}]`)
          return
        }
        if (this.$enum.BUILD_ACTION_TYPE.COMMAND.value === action.type) {
          if (!action.command) {
            this.$message.warning(`请输入操作命令 [构建操作${i + 1}]`)
            return
          } else if (action.command.length > 2048) {
            this.$message.warning(`操作命令长度不能大于2048位 [构建操作${i + 1}] 当前: ${action.command.length}`)
            return
          }
        }
      }
      this.loading = true
      this.$api.configApp({
        appId: this.appId,
        profileId: this.profileId,
        stageType: 10,
        env: {
          bundlePath: this.bundlePath
        },
        buildActions: this.actions
      }).then(() => {
        this.$message.success('保存成功')
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
@config-container-width: 1030px;
@bundle-input-width: 700px;
@app-action-container-width: 994px;
@app-action-width: 876px;
@action-name-input-width: 700px;
@app-action-editor-width: 700px;
@app-action-editor-height: 250px;
@action-type-name-width: 700px;
@action-divider-min-width: 830px;
@action-divider-width: 990px;
@app-action-footer-width: 700px;
@footer-margin-left: 168px;
@desc-margin-left: 168px;

#app-conf-container {
  padding: 18px 8px 0 8px;
  width: @config-container-width;

  .label {
    width: @label-width;
    font-size: 15px;
    line-height: 32px;
  }

  #app-bundle-wrapper {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    align-content: center;

    .bundle-input {
      width: @bundle-input-width;
      margin-left: 8px;
    }
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

        .action-type-wrapper {
          display: flex;
          align-items: center;

          .action-type-name {
            width: @action-type-name-width;
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

}

</style>
