## orion-ops 是什么

`orion-ops` 一站式自动化运维及自动化部署平台, 无 Agent 的方式接入应用, 使用多环境的概念, 提供了机器管理、Web终端、WebSftp、机器批量执行、机器批量上传、在线查看日志、定时调度任务、应用环境维护、应用构建及发布任务、操作流水线等功能, 帮助企业实现一站式轻量化运维治理,
致力于企业级应用的智能运维。

<p style="text-align: center">
	<a target="_blank" style="text-decoration: none" href="https://www.codacy.com/gh/lijiahangmax/orion-ops/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lijiahangmax/orion-ops&amp;utm_campaign=Badge_Grade">
		<img src="https://app.codacy.com/project/badge/Grade/18b08ef5e7294e80836c56d595fea4bb" alt="Codacy"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" alt="jdk8"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://www.apache.org/licenses/LICENSE-2.0">
		<img src="https://img.shields.io/github/license/lijiahangmax/orion-ops" alt="License"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://gitee.com/lijiahangmax/orion-ops/stargazers">
		<img src="https://gitee.com/lijiahangmax/orion-ops/badge/star.svg?theme=dark" alt="star"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://gitee.com/lijiahangmax/orion-ops/members">
		<img src="https://gitee.com/lijiahangmax/orion-ops/badge/fork.svg?theme=dark" alt="fork"/>
	</a>		
	<!-- <a target="_blank" style="text-decoration: none" href="https://github.com/lijiahangmax/orion-ops">
		<img src="https://img.shields.io/github/stars/lijiahangmax/orion-ops.svg?style=social" alt="star"/>
	</a> -->	
</p>

<br/>  

github: https://github.com/lijiahangmax/orion-ops  
gitee: https://gitee.com/lijiahangmax/orion-ops  
csdn: https://blog.csdn.net/qq_41011894  
orion-kit: [gitee](https://gitee.com/lijiahangmax/orion-kit) [github](https://github.com/lijiahangmax/orion-kit)    
文档: https://lijiahangmax.gitee.io/orion-ops/#/    
demo: http://101.43.254.243/ops/#/

演示账号: `orionadmin`    
演示密码: `orionadmin`  
留个小星星再走吧⭐

## 特性

* 易用方便: 极简配置, 开箱即用, 无 Agent 接入
* 在线终端: 支持在线 Web 终端, 记录操作日志, 管理员可强制下线, 相当于轻量跳板机
* 文件管理: 支持远程机器文件批量上传、批量下载、暂停断点续传、实时传输速率、实时进度、打包传输等功能
* 批量操作: 支持远程机器批量执行命令 以及 批量执行上传文件
* 调度任务: 维护 cron 表达式, 定时执行机器命令
* 环境隔离: 不同应用环境的配置及环境变量是相互隔离的
* 环境变量: 命令执行时使用占位符自动替换, 支持 properties, json, yml, xml多种格式维护
* 高兼容性: 自定义构建发布操作, 不论是什么项目都是配置 SSH 执行命令, 灵活操作
* 功能强大: 命令批量执行, 任务定时调度, 远程日志查看, 操作日志全记录等
* 高扩展性: 前后端代码规范统一, 代码健壮质量高, 写法优雅, 易读好拓展
* 免费开源: 前后端代码完全开源, 方便二次开发

## 快速开始

安装文档: https://lijiahangmax.gitee.io/orion-ops/#/quickstart/install   
开发文档: https://lijiahangmax.gitee.io/orion-ops/#/advance/second-dev   
操作手册: https://lijiahangmax.gitee.io/orion-ops/#/operator/machine  
常见问题: https://lijiahangmax.gitee.io/orion-ops/#/quickstart/faq  
通用模板: https://lijiahangmax.gitee.io/orion-ops/#/template/java-springboot-template  
roadmap: https://lijiahangmax.gitee.io/orion-ops/#/about/roadmap

## 技术栈

* SpringBoot 2.4.4
* MybatisPlus 3.4.0
* Mysql 8.0
* Redis 5.0.5
* Vue 2.6.11
* Ant Design 1.7.8

## 功能预览

> 控制台

![控制台](./assert/img/console.png "控制台")

> 机器列表

![机器列表](./assert/img/machine_list.png "机器列表")

> 在线终端

![终端banner](./assert/img/web_terminal_banner.png "终端banner")
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

> 流水线任务

![流水线配置](./assert/img/pipeline_list.png "流水线配置")
![流水线任务](./assert/img/pipeline_record_list.png "流水线任务")

> 登陆日志

![登陆日志](./assert/img/login_history.png "登陆日志")

> 安全配置

![安全配置](./assert/img/security_config.png "安全配置")

> 消息

![操作日志](./assert/img/event_log.png "操作日志")
![站内信](./assert/img/about_messsage.png "站内信")

## 添砖加瓦

PR 规则: 只能在 `github` 的 `dev` 分支提交 PR, merge 后我会手动同步 gitee  
orion-ops 作为开源项目, 欢迎任何人提出建议以及贡献代码。你所做出的每一次贡献都是有意义的, 同时也会永远保留在贡献者名单中  
欢迎大家添砖加瓦以及文档纠错, 这也是开源项目的意义所在!

[代码结构及开发规范](/about/code-structure)  
[贡献者名单](/about/contributor)

## 联系我

<img src="./assert/img/concat_wx.jpg" alt="微信" width="268px"/>  

📧 还不快骚扰一下~

## 支持一下

<img src="./assert/img/support_pay.jpg" alt="收款码" width="540px"/>  

🎁 为了项目能健康持续的发展, 我期望获得相应的资金支持, 你们的支持是我不断更新前进的动力!

## License

使用 [Apache-2.0](https://github.com/lijiahangmax/orion-ops/blob/main/LICENSE) 开源许可证。前后端代码完全开源, 根据自己的需求打造出独一无二的智能运维平台。
