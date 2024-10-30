<div align="center"><img src="https://bjuimg.obs.cn-north-4.myhuaweicloud.com/images/2024/9/11/b50ed730-e6c9-4a17-972b-10439d97e1a7.png" alt="logo" width="520" /></div>
<p style="margin-top: 12px" align="center"><b>一站式自动化运维及自动化部署平台, 使用多环境的概念, 提供了机器管理、机器监控报警、Web终端、WebSftp、机器批量执行、机器批量上传、在线查看日志、定时调度任务、应用环境维护、应用构建及发布任务、操作流水线等功能,
帮助企业实现一站式轻量化运维治理, 致力于企业级应用的智能运维。</b></p>
<p align="center">
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://app.codacy.com/gh/lijiahangmax/orion-ops/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade">
      <img src="https://app.codacy.com/project/badge/Grade/49eaab3a9a474af3b87e1d21ffec71c4" alt="quality" />
    </a>
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://www.apache.org/licenses/LICENSE-2.0">
      <img src="https://img.shields.io/github/license/lijiahangmax/orion-ops" alt="License" />
    </a>
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://github.com/lijiahangmax/orion-ops/releases">
      <img src="https://img.shields.io/github/v/release/lijiahangmax/orion-ops" alt="release" />
    </a>
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://gitee.com/lijiahangmax/orion-ops/stargazers">
      <img src="https://gitee.com/lijiahangmax/orion-ops/badge/star.svg?theme=dark" alt="star" />
    </a>
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://gitee.com/lijiahangmax/orion-ops/members">
      <img src="https://gitee.com/lijiahangmax/orion-ops/badge/fork.svg?theme=dark" alt="fork" />
    </a>
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://github.com/lijiahangmax/orion-ops">
      <img src="https://img.shields.io/github/stars/lijiahangmax/orion-ops" alt="star" />
    </a>
    <a target="_blank"
       style="text-decoration: none !important;"
       href="https://github.com/lijiahangmax/orion-ops">
      <img src="https://img.shields.io/github/forks/lijiahangmax/orion-ops" alt="star" />
    </a>
</p>

------------------------------

## 特性

* 易用方便: 极简配置, 开箱即用, 支持 docker 部署
* 在线终端: 支持在线 Web 终端, 记录操作日志, 管理员可强制下线, 录屏回放, 终端监视等
* 文件管理: 支持远程机器文件批量上传、批量下载、暂停断点续传、实时传输速率、实时进度、打包传输等功能
* 机器监控: 支持维护机器的监控以及报警, 支持采集 agent 的一键安装以及升级
* 批量操作: 支持远程机器批量执行命令 以及 批量执行上传文件
* 调度任务: 维护 cron 表达式, 定时执行机器命令
* 环境隔离: 不同应用环境的配置及环境变量是相互隔离的
* 环境变量: 命令执行时使用占位符自动替换, 支持 properties, json, yml, xml多种格式维护
* 高兼容性: 自定义构建发布操作, 不论是什么项目都是配置 SSH 执行命令, 灵活操作
* 功能强大: 命令批量执行, 任务定时调度, 远程日志查看, 操作日志全记录等
* 高扩展性: 前后端代码规范统一, 代码健壮质量高, 写法优雅, 易读好拓展
* 免费开源: 前后端代码完全开源, 方便二次开发

## 演示环境

* 🔗 演示地址: [https://do.orionsec.cn/#/](https://do.orionsec.cn/#/)
* 🔏 演示账号: orionadmin/orionadmin
* ⭐ 体验后可以点一下 `star`
  这对我很重要! [github](https://github.com/lijiahangmax/orion-ops) [gitee](https://gitee.com/lijiahangmax/orion-ops) [gitcode](https://gitcode.com/lijiahangmax/orion-ops/overview)
* 🌈 如果本项目对你有帮助请帮忙推广一下 让更多的人知道此项目!
* 🎭 演示环境部分功能不可用, 完整功能请本地部署!
* 📛 演示环境请不要随便删除数据!
* 📧 如果演示环境不可用请联系我!

## 快速开始

* [文档地址](https://ops.orionsec.cn)
* [安装文档](https://ops.orionsec.cn/quickstart/docker.html)
* [本地调试](https://ops.orionsec.cn/quickstart/dev.html)
* [操作手册](https://ops.orionsec.cn/operator/machine.html)
* [常见问题](https://ops.orionsec.cn/support/faq.html)
* [通用模板](https://ops.orionsec.cn/template/java-springboot-template.html)

## 重构版 orion-visor

`orion-visor` 为 `orion-ops` 的重构版本, 优化了交互逻辑以及UI风格、操作更友好、系统更快速&安全! 目前该项目已荣获 `GVP`
[项目地址](https://visor.orionsec.cn)

## 技术栈

* SpringBoot 2.4.4
* MybatisPlus 3.4.0
* Mysql 8.0
* Redis 5.0.5
* Vue 2.6.11
* Ant Design 1.7.8

## 功能预览

### 控制台

![控制台](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/25/1d5d1740-dff1-4e17-93dc-b2b329e3bbe4.png "控制台")

### 机器列表

![机器列表](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/23/f8b84d93-6cbe-4aa0-8e29-104a82ec9fc0.jpg "机器列表")

### 在线终端

![终端模态框](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/31/72d02782-cbf1-4eea-8c23-637f1193f289.png "终端模态框")
![终端banner](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/31/b0353c63-3d25-4945-95a4-b1f2735743a5.png "终端banner")
![在线终端](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/31/c3402b21-4d97-430d-b6cf-444389be6ee6.png "在线终端")
![录屏回放](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/31/5c460a5c-d934-47df-9e70-dcee87e0c801.png "录屏回放")
![终端监视](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/31/58257d2e-37ed-4244-b21f-183fe02c1f91.png "终端监视")

### 在线文件管理

![在线文件管理](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files6081069371629929397.png "在线文件管理")  
![在线文件管理](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-03-14/files4411594441981208271.png "在线文件管理")

### 机器监控

![机器监控概要](https://oos-sdqd.ctyunapi.cn/album/2022/9/2/30401c36-add4-4f8c-955e-7721740b6646.png "机器监控概要")  
![机器监控信息](https://oos-sdqd.ctyunapi.cn/album/2022/9/2/824a63d9-48ac-4c89-a9bf-8cc4f71fe608.png "机器监控信息")
![机器监控报警-钉钉](https://oos-sdqd.ctyunapi.cn/album/2022/9/2/a064eb5c-896b-496c-90d4-c9abc18ff39a.png "机器监控报警-钉钉")

### 批量执行

![批量执行](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files4899857856529374903.png "批量执行")

### 日志文件

![日志文件](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/31/9b1beae3-a480-47bb-8c79-668ad52ec965.png "日志文件")

### 调度任务

![调度任务](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files5065881246088616914.png "调度任务")  
![调度执行列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files9138174215052119985.png "调度执行列表")  
![调度执行日志](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files3786363821214889886.png "调度执行日志")

### 应用配置

![应用列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files2712336456731415928.png "应用列表")  
![构建配置](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files7988531022696126381.png "构建配置")  
![发布配置](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files1800464505453752748.png "发布配置")

### 构建列表

![构建列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files2590331112837583257.png "构建列表")  
![构建日志](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files4232688502598769434.png "构建日志")

### 发布列表

![发布列表](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files6859723679488097151.png "发布列表")
![发布日志](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files3662304038159928145.png "发布日志")

### 流水线任务

![流水线配置](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files694386343461173439.png "流水线配置")
![流水线任务](https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files6446052342050924823.png "流水线任务")

### 登录日志

![登录日志](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/23/4a548584-6cee-4b95-9811-5d2a670bfca8.jpg "登录日志")

### 安全配置

![安全配置](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/23/f79d16a1-500c-4b4c-bd2e-1bc212d453bf.png "安全配置")

### 消息

![操作日志](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/23/432c6dd0-0943-4868-9a2a-32147f566d55.jpg "操作日志")
![站内信](http://bjucloud.oss-cn-qingdao.aliyuncs.com/album/2022/7/23/cfcbb03c-fafa-4e7c-9e2d-f22f8c56c753.jpg "站内信")

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=lijiahangmax/orion-ops&type=Date)](https://star-history.com/#lijiahangmax/orion-ops&Date)

## 添砖加瓦

PR 规则: 只能在 `github` 的 `dev` 分支提交 PR, merge 后我会手动同步 gitee  
orion-ops 作为开源项目, 欢迎任何人提出建议以及贡献代码。你所做出的每一次贡献都是有意义的, 同时也会永远保留在贡献者名单中  
欢迎大家添砖加瓦以及文档纠错, 这也是开源项目的意义所在!

[代码结构及开发规范](https://lijiahangmax.github.io/orion-ops/#/about/code-structure)  
[贡献者名单](https://lijiahangmax.github.io/orion-ops/#/about/contributor)

## 关于我

本人专注于使用 Java 和 Vue 进行全栈开发, 并在系统自动化运维方面拥有丰富开发的经验。如果您在这些领域有需求或遇到痛点, 请随时联系我, 并备注“合作”。

## 联系我

<img src="https://oos-sdqd.ctyunapi.cn/album/2022/9/22/018ac2ff-164a-4f7d-a8ae-030c5d899726.png" alt="联系方式" width="540px"/>   

📧 问题/加群微信备注: ops  
📧 合作/功能定制备注: 合作

## 支持一下

<img src="https://yxythpt.oss-cn-shenzhen.aliyuncs.com/2022-04-25/files4948235556147091810.png" alt="收款码" width="540px"/>  

🎁 为了项目能健康持续的发展, 我期望获得相应的资金支持, 你们的支持是我不断更新前进的动力!
<br/>

## License

使用 [Apache-2.0](https://github.com/lijiahangmax/orion-ops/blob/main/LICENSE) 开源许可证。前后端代码完全开源, 根据自己的需求打造出独一无二的智能运维平台。
