<template>
  <a-layout id="log-view-layout-container">
    <!-- tail列表 -->
    <a-layout-sider id="log-view-list-fixed-left" :collapsible="true">
      <a-spin :spinning="listLoading">
        <!-- 工具按钮 -->
        <div class="log-list-tools">
          <div class="log-list-tools-item" title="刷新" @click="getTailList">
            <a-icon type="reload"/>
          </div>
        </div>
        <!-- 过滤文件 -->
        <div class="name-filter">
          <a-input placeholder="文件名 / 文件路径过滤" v-model="nameFilter" allowClear/>
        </div>
        <!-- tail文件列表菜单 -->
        <a-menu theme="dark" mode="inline" :selectedKeys="selectedKeys">
          <a-menu-item v-for="item of filterTailList" :key="item.id"
                       :title="`双击打开 ${item.name}`"
                       @dblclick="chooseFile(item.id)">
            <a-icon type="file-text"/>
            <span class="usn">{{ item.name }}</span>
          </a-menu-item>
        </a-menu>
      </a-spin>
    </a-layout-sider>
    <!-- main -->
    <a-layout>
      <a-layout-content id="log-view-content-fixed-right">
        <!-- 日志 -->
        <div v-if="selectedTailFiles.length" class="log-view-tabs-container">
          <a-tabs v-model="activeTab"
                  :tabBarStyle="{margin: 0}"
                  :hideAdd="true"
                  type="editable-card"
                  @edit="removeTab"
                  :animated="false">
            <a-tab-pane v-for="selectedTailFile of selectedTailFiles"
                        size="default"
                        :key="selectedTailFile.id"
                        :forceRender="true">
              <!-- tab -->
              <template #tab>
                <span class="usn">{{ selectedTailFile.name }}</span>
              </template>
              <!-- 日志 -->
              <div class="file-log-view">
                <LogAppender :ref="'appender' + selectedTailFile.id"
                             size="default"
                             :appendStyle="{height: 'calc(100vh - 100px)'}"
                             :relId="selectedTailFile.id"
                             :tailType="$enum.FILE_TAIL_TYPE.TAIL_LIST.value"
                             :downloadType="$enum.FILE_DOWNLOAD_TYPE.TAIL_LIST_FILE.value">
                  <!-- 左侧工具栏 -->
                  <template #left-tools>
                    <div class="appender-left-tools">
                      <!-- 信息 -->
                      <a-breadcrumb>
                        <!-- 机器名称 -->
                        <a-breadcrumb-item v-if="selectedTailFile.machineName">
                          <a-tag color="#7950F2" class="m0">
                            {{ selectedTailFile.machineName }}
                          </a-tag>
                        </a-breadcrumb-item>
                        <!-- 机器主机 -->
                        <a-breadcrumb-item v-if="selectedTailFile.machineHost">
                          <a-tag color="#5C7CFA" class="m0">
                            {{ selectedTailFile.machineHost }}
                          </a-tag>
                        </a-breadcrumb-item>
                        <!-- 名称 -->
                        <a-breadcrumb-item v-if="selectedTailFile.name">
                          <a-tag color="#15AABF" class="m0">
                            {{ selectedTailFile.name }}
                          </a-tag>
                        </a-breadcrumb-item>
                        <!-- 文件名称 -->
                        <a-breadcrumb-item v-if="selectedTailFile.path">
                          <a-tag color="#40C057"
                                 class="pointer mr8"
                                 :title="selectedTailFile.path"
                                 @click="$copy(selectedTailFile.path)">
                            {{ selectedTailFile.fileName }}
                          </a-tag>
                        </a-breadcrumb-item>
                      </a-breadcrumb>
                    </div>
                  </template>
                </LogAppender>
              </div>
            </a-tab-pane>
          </a-tabs>
        </div>
        <!-- 日志空状态 -->
        <div v-else class="log-view-tabs-empty-container">
          <a-empty class="empty-status" description="双击左侧菜单查看日志"/>
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
import LogAppender from '@/components/log/LogAppender'

export default {
  name: 'LoggerView',
  components: {
    LogAppender
  },
  data() {
    return {
      listLoading: false,
      selectedKeys: [],
      selectedTailFiles: [],
      tailList: [],
      filterTailList: [],
      nameFilter: null,
      activeTab: 0
    }
  },
  watch: {
    // activeTab(b, a) {
    //   if (!a) {
    //     return
    //   }
    //   const $refAfter = this.$refs['appender' + a]
    //   if ($refAfter) {
    //     $refAfter[0].storeScroll()
    //   }
    //   const $refBefore = this.$refs['appender' + b]
    //   if ($refBefore) {
    //     $refBefore[0].toScroll()
    //   }
    // }
    nameFilter(v) {
      if (v) {
        this.filterTailList = this.tailList.filter(s => s.name.toLowerCase().includes(v.toLowerCase()) || s.path.toLowerCase().includes(v.toLowerCase()))
      } else {
        this.filterTailList = [...this.tailList]
      }
    }
  },
  methods: {
    async getTailList() {
      this.nameFilter = null
      this.listLoading = true
      try {
        const tailListRes = await this.$api.getTailList({
          limit: 10000
        })
        this.tailList = tailListRes.data.rows.map(i => {
          return {
            id: i.id,
            name: i.name,
            path: i.path,
            fileName: i.fileName,
            machineName: i.machineName,
            machineHost: i.machineHost
          }
        })
        this.filterTailList = [...this.tailList]
      } catch (e) {
        // ignore
      }
      this.listLoading = false
    },
    chooseFile(id) {
      this.selectedKeys[0] = id
      this.activeTab = id
      this.$forceUpdate()
      const filterTailFiles = this.tailList.filter(s => s.id === id)
      if (!filterTailFiles.length) {
        // 不存在与列表
        return
      }
      if (this.selectedTailFiles.filter(s => s.id === id).length) {
        // 已加载
        return
      }
      const filterTailFile = filterTailFiles[0]
      this.selectedTailFiles.push({
        id: id,
        name: filterTailFile.name,
        path: filterTailFile.path,
        fileName: filterTailFile.fileName,
        machineName: filterTailFile.machineName,
        machineHost: filterTailFile.machineHost
      })
      this.$nextTick(() => {
        const $ref = this.$refs['appender' + id]
        if ($ref && $ref.length) {
          $ref[0].openTail()
        }
      })
    },
    removeTab(targetTab) {
      let activeTab = this.activeTab
      let lastIndex
      this.selectedTailFiles.forEach((fileTab, i) => {
        if (fileTab.id === targetTab) {
          lastIndex = i - 1
        }
      })
      const $ref = this.$refs['appender' + targetTab]
      if ($ref && $ref.length) {
        $ref[0].close()
      }
      const selectedTailFiles = this.selectedTailFiles.filter(tailFile => tailFile.id !== targetTab)
      if (selectedTailFiles.length && activeTab === targetTab) {
        if (lastIndex >= 0) {
          activeTab = selectedTailFiles[lastIndex].id
        } else {
          activeTab = selectedTailFiles[0].id
        }
      }
      if (!selectedTailFiles.length) {
        activeTab = null
      }
      this.selectedTailFiles = selectedTailFiles
      this.activeTab = activeTab
    }
  },
  async mounted() {
    await this.getTailList()
    const chooseId = this.$route.params.id
    if (chooseId) {
      const id = parseInt(chooseId)
      this.selectedKeys.push(id)
      this.chooseFile(id)
    }
  }
}
</script>

<style lang="less" scoped>

#log-view-layout-container {
  height: 100vh;
}

#log-view-content-fixed-right {
  overflow: auto;
  background-color: #FFF;
  padding: 4px 8px 8px 8px;

  .file-log-view {
    padding-top: 8px;
  }
}

#log-view-list-fixed-left {
  overflow: auto;

  .log-list-tools {
    height: 32px;
    margin: 8px 8px 4px 8px;
    display: flex;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 4px;

    .log-list-tools-item {
      width: 100%;
      height: 100%;
      color: hsla(0, 0%, 100%, 0.65);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
      border-radius: 4px;
      cursor: pointer;

      i {
        font-size: 20px;
      }
    }

    .log-list-tools-item:hover {
      background-color: #1890FF;
      color: #FFFFFF;
    }
  }
}

.log-view-tabs-empty-container {
  margin-top: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.name-filter {
  padding: 6px 8px;
}

</style>
