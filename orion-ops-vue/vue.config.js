module.exports = {
  publicPath: '/ops/',
  devServer: {
    port: 10010
    // proxy: {
    //   '/api': {
    //     target: '',
    //     // 允许跨域
    //     changeOrigin: true,
    //     ws: true,
    //     pathRewrite: {
    //       '^/api': ''
    //     }
    //   }
    // }
  }
}
