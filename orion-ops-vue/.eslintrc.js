module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [
    'plugin:vue/essential',
    '@vue/standard'
  ],
  parserOptions: {
    parser: 'babel-eslint'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    // 变量未使用不报错
    'no-unused-vars': 'off',
    // 行末分号 (启用)
    // 'semi': ['warn', 'always'],
    // 函数名后的空格 (禁用)
    'space-before-function-paren': ['error', 'never'],
    // 缩进规范 (两个空格，一倍缩进)
    // 'vue/script-indent': ['error', 2, { 'baseIndent': 1 }],
  },
  overrides: [
    {
      'files': ['*.vue'],
      'rules': {
        'indent': 'off'
      }
    }
  ]
}
