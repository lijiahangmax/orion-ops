<template>
  <div id="search-card" v-show="search.visible" @keydown.esc="closeSearch">
    <a-card title="搜索" size="small">
      <template #extra>
        <a-icon class="span-blue pointer" type="close" title="关闭" @click="closeSearch"/>
      </template>
      <!-- 搜索框 -->
      <a-input class="search-input"
               ref="searchInput"
               placeholder="请输入查找内容"
               v-model="search.value"
               @keyup.enter.native="searchKeywords(true)"
               allowClear>
      </a-input>
      <!-- 选项 -->
      <div class="search-options">
        <a-row>
          <a-col :span="12">
            <a-checkbox class="usn" v-model="search.regex">
              正则匹配
            </a-checkbox>
          </a-col>
          <a-col :span="12">
            <a-checkbox class="usn" v-model="search.words">
              单词全匹配
            </a-checkbox>
          </a-col>
          <a-col :span="12">
            <a-checkbox class="usn" v-model="search.matchCase">
              区分大小写
            </a-checkbox>
          </a-col>
          <a-col :span="12">
            <a-checkbox class="usn" v-model="search.incremental">
              增量查找
            </a-checkbox>
          </a-col>
        </a-row>
      </div>
      <!-- 按钮 -->
      <div class="search-buttons">
        <a-button class="terminal-search-button search-button-prev"
                  type="primary"
                  @click="searchKeywords(false)">
          上一个
        </a-button>
        <a-button class="terminal-search-button search-button-next"
                  type="primary"
                  @click="searchKeywords(true)">
          下一个
        </a-button>
      </div>
    </a-card>
  </div>
</template>

<script>
export default {
  name: 'TerminalSearch',
  props: {
    searchPlugin: Object
  },
  data() {
    return {
      search: {
        visible: false,
        value: '',
        regex: false,
        words: false,
        matchCase: false,
        incremental: false
      }
    }
  },
  methods: {
    open() {
      const visible = this.search.visible
      this.search.visible = !visible
      if (!visible) {
        this.$nextTick(() => {
          this.$refs.searchInput.focus()
        })
      }
    },
    closeSearch() {
      this.search.visible = false
      this.search.value = ''
      this.$emit('close')
    },
    searchKeywords(direction) {
      if (!this.search.value) {
        return
      }
      const option = {
        regex: this.search.regex,
        wholeWord: this.search.words,
        caseSensitive: this.search.matchCase,
        incremental: this.search.incremental
      }
      let res
      if (direction) {
        res = this.searchPlugin.findNext(this.search.value, option)
      } else {
        res = this.searchPlugin.findPrevious(this.search.value, option)
      }
      if (!res) {
        this.$message.info('未查询到匹配项', 0.3)
      }
    }
  }
}
</script>

<style lang="less" scoped>
#search-card {
  position: fixed;
  top: 94px;
  right: 20px;
  z-index: 200;
  width: 290px;

  .search-input {
    width: 260px;
  }

  .search-options {
    margin: 12px 0;
  }

  .search-buttons {
    margin-top: 5px;
    display: flex;
    justify-content: flex-end;
  }

  .terminal-search-button {
    margin-left: 10px;
  }
}
</style>
