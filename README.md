## orion-ops 是什么

`orion-ops` 一站式自动化运维及自动化部署平台, 无 Agent 的方式接入应用, 使用多环境的概念, 提供了机器管理、WebTerminal、 WebSftp、 机器批量执行、日志在线查看、定时调度任务、应用环境维护、应用构建及发布任务 (CI / CD) 等功能, 帮助开发人员快速定位问题,
致力于企业级应用。

Github: https://github.com/lijiahangmax/orion-ops  
Gitee: https://gitee.com/lijiahangmax/orion-ops  
CSDN: https://blog.csdn.net/qq_41011894  
文档: https://lijiahangmax.gitee.io/orion-ops/#/    
Demo: http://101.43.254.243/ops/#/

演示账号: `orionadmin`    
演示密码: `orionadmin`

## 特性

* 易用方便: 极简配置, 开箱即用, 无 Agent 接入
* 在线终端: 支持 Web 终端登录远程机器, 记录操作日志, 管理端可强制下线
* 文件管理: 支持远程机器文件批量上传、批量下载、暂停断点续传、实时传输速率、实时进度、打包传输等功能
* 环境变量: 基于不同 Profile 有着不同的应用环境变量, 命令执行时使用占位符自动替换
* 高兼容性: 自定义 CI/CD 操作, 不论是什么项目, 都是基于 SSH 命令执行, 灵活操作
* 功能强大: 命令批量执行, 任务定时调度, 远程日志查看, 操作日志全记录等
* 高扩展性: 前后端代码规范统一, 代码质量高, 易读好拓展
* 免费开源: 前后端代码完全开源

## 技术栈

* SpringBoot 2.4.4
* MybatisPlus 3.4.0
* Mysql 8.0
* Redis 5.0.5
* Vue 2.6.11
* Ant Design 1.7.8

## 系统架构

> 总体架构

![总体架构](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-28/files1661632709499769734.png "总体架构")

> 发布架构 (CI / CD)

![发布架构](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-28/files6312827529386043725.png "发布架构")

<br/>

## 功能预览

> 机器列表

![机器列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files8512578085516682386.png "机器列表")

> 在线终端

![在线终端](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4787611964158821533.png "在线终端")

> 在线文件管理

![在线文件管理](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files6081069371629929397.png "在线文件管理")  
![在线文件管理](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4411594441981208271.png "在线文件管理")

> 批量执行

![批量执行](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files8195883496604225112.png "批量执行")

> 日志文件

![日志文件](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files8456725984172369436.png "日志文件")

> 调度任务

![调度任务](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files7140183316009047038.png "调度任务")  
![调度执行列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files5875226043882871962.png "调度执行列表")  
![调度执行日志](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files285645083754039026.png "调度执行日志")

> 应用配置

![应用列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files3132678810569551460.png "应用列表")  
![构建配置](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files8930817600030460.png "构建配置")  
![发布配置](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4557512125504191404.png "发布配置")

> 构建列表

![构建列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4960287647322644132.png "构建列表")  
![构建日志](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files6245253907567338465.png "构建日志")

> 发布列表

![发布列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4322661961526641390.png "发布列表")
![发布日志](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4699926352889499394.png "发布日志")

<br/>

## 联系我

<img src="https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-28/files5090991341399292419.jpg" alt="联系微信" width="268px"/>

<br/>

## 支持一下

<img src="https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-28/files7884174196576690325.png" alt="微信收款码" width="268px"/>
<img src="https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-28/files8581952463146065537.jpg" alt="支付宝收款码" width="268px"/>

<br/>

## License

使用 [Apache-2.0](https://github.com/lijiahangmax/orion-ops/blob/main/LICENSE) 开源许可证, 请自觉遵循。
