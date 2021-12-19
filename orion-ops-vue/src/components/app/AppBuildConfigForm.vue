<template>
  <div id="app-conf-container">
    <a-spin :spinning="loading">
      <!-- 产物路径 -->
      <div id="app-bundle-wrapper">
      <span class="label">
        <span class="span-red mr4">*</span>
        构建产物路径 :
      </span>
        <a-input class="bundle-input" v-model="bundlePath" :maxLength="512" placeholder="主机构建产物路径 (绝对路径/基于版本仓库的相对路径)"/>
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
                <span class="label action-label">
                  <span class="span-red mr4">*</span>
                  操作名称{{ index + 1 }} :
                </span>
                  <a-input class="action-name-input" v-model="action.name" :maxLength="16" placeholder="操作名称"/>
                </div>
                <!-- 代码块 -->
                <div class="action-editor-wrapper" v-if="action.type === $enum.BUILD_ACTION_TYPE.HOST_COMMAND.value">
                <span class="label action-label">
                  <span class="span-red mr4">*</span>
                  主机命令{{ index + 1 }} :
                </span>
                  <div class="app-action-editor">
                    <Editor :config="editorConfig" :value="action.command" @change="(v) => action.command = v"/>
                  </div>
                </div>
                <div class="action-type-wrapper" v-else>
                  <span class="label action-label">操作类型 :</span>
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
                  @click="addAction($enum.BUILD_ACTION_TYPE.HOST_COMMAND.value)">
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
      return this.vcsId !== null && this.actions.map(s => s.type).filter(t => t === this.$enum.BUILD_ACTION_TYPE.CHECKOUT.value).length < 1
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
      this.bundlePath = detail.bundlePath
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
      if (!this.bundlePath.trim().length) {
        this.$message.warning('请输入构建产物路径')
        return
      }
      for (let i = 0; i < this.actions.length; i++) {
        const action = this.actions[i]
        if (!action.name) {
          this.$message.warning(`请输入操作名称 [构建操作${i + 1}]`)
          return
        }
        if (this.$enum.BUILD_ACTION_TYPE.HOST_COMMAND.value === action.type) {
          if (!action.command) {
            this.$message.warning(`请输入操作命令 [构建操作${i + 1}]`)
            return
          } else if (action.command.length > 1024) {
            this.$message.warning(`操作命令长度不能大于1024位 [构建操作${i + 1}]`)
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

  #app-bundle-wrapper {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    align-content: center;

    .bundle-input {
      width: 430px;
      margin-left: 8px;
    }
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

        .action-type-wrapper {
          display: flex;
          align-items: center;

          .action-type-name {
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
