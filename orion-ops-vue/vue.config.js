// 判断开发环境
// const isProduction = process.env.NODE_ENV === 'production'

module.exports = {
  publicPath: '/ops/',
  // 打包时不生成.map文件
  productionSourceMap: false,
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
  // configureWebpack: config => {
  //   if (isProduction) {
  //     // 开启分离js
  //     config.optimization = {
  //       runtimeChunk: 'single',
  //       splitChunks: {
  //         chunks: 'all',
  //         maxInitialRequests: Infinity,
  //         minSize: 20000,
  //         cacheGroups: {
  //           vendor: {
  //             test: /[\\/]node_modules[\\/]/,
  //             name(module) {
  //               const packageName = module.context.match(/[\\/]node_modules[\\/](.*?)([\\/]|$)/)[1]
  //               return `pkg.${packageName.replace('@', '')}`
  //             }
  //           }
  //         }
  //       }
  //     }
  //   }
  // }
}
