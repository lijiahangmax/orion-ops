## orion-ops 是什么

`orion-ops` 一站式自动化运维平台, 无 Agent 的方式接入应用, 使用多环境的概念, 提供了机器管理、WebTerminal、 WebSftp、 机器批量执行、日志在线查看、定时调度任务、应用环境维护、应用构建及发布任务等功能, 帮助开发人员快速定位问题, 致力于企业级应用。

Github: https://github.com/lijiahangmax/orion-ops  
Gitee: https://gitee.com/lijiahangmax/orion-ops  
CSDN: https://blog.csdn.net/qq_41011894  
文档: https://lijiahangmax.github.io/orion-ops/#/    
Demo: http://101.43.254.243/ops/#/  
演示账号: `orionadmin`    
演示密码: `orionadmin`

## 特性

* 易用方便: 极简配置, 开箱即用, 无 Agent 接入
* 在线终端: 支持 Web 终端登录远程机器, 记录操作日志, 管理端可强制下线
* 文件管理: 支持远程机器文件批量上传、批量下载、暂停断点续传、实时传输速率、实时进度、打包传输等功能
* 环境变量: 基于不同 Profile 有着不同的应用环境变量, 命令执行时使用占位符自动替换
* 高兼容性: 自定义 CI/CD 操作, 不论是什么项目, 都是配置 SSH 执行命令, 灵活操作
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

## 功能预览

> 机器列表

![机器列表](./assert/img/machine_list.png "机器列表")

> 在线终端

![在线终端](./assert/img/web_terminal.png "在线终端")

> 在线文件管理

![在线文件管理](./assert/img/sftp_1.png "在线文件管理")  
![在线文件管理](./assert/img/sftp_2.png "在线文件管理")

> 批量执行

![批量执行](./assert/img/batch_exec.png "批量执行")

> 日志文件

![日志文件](./assert/img/log_view.png "日志文件")

> 调度任务

![调度任务](./assert/img/scheduler.png "调度任务")  
![调度执行列表](./assert/img/scheduler_record.png "调度执行列表")  
![调度执行日志](./assert/img/scheduler_record_log.png "调度执行日志")

> 应用配置

![应用列表](./assert/img/app_list.png "应用列表")  
![构建配置](./assert/img/app_build_config.png "构建配置")  
![发布配置](./assert/img/app_release_config.png "发布配置")

> 构建列表

![构建列表](./assert/img/build_list.png "构建列表")  
![构建日志](./assert/img/build_log.png "构建日志")

> 发布列表

![发布列表](./assert/img/release_list.png "发布列表")
![发布日志](./assert/img/release_log.png "发布日志")

## 支持

......

## License

[Apache-2.0](https://github.com/lijiahangmax/orion-ops/blob/main/LICENSE)
