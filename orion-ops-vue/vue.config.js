module.exports = {
  publicPath: '/orion/ops/',
  devServer: {
    port: 10000
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
