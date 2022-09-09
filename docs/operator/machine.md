### 机器列表

管理和维护可操作的机器, 项目初始化后会生成一台默认机器 (本机) 可以称为 `宿主机`, 需要手动设置 `ssh` 密码或添加秘钥。  
这里可以打开 WebTerminal, WebSftp。

⚡ 按住 `Ctrl` 点击 `Terminal` 可以打开一个新页面, 否则将会打开一个模态框。  
⚡ 按住 `Ctrl` 点击 `sftp` 可以打开一个新页面, 否则将会在当前页面跳转。

### 机器监控

配置已启动的机器监控, 具有可视化图形以及数据粒度和时间区间的选择  
统计的指标有 `CPU监控` `内存监控` `网络带宽监控` `硬盘使用监控`

[插件仓库](https://github.com/lijiahangmax/orion-ops-extension-plugins) [安装文档](https://lijiahangmax.github.io/orion-ops-extension-plugins/#/machine-monitor-agent/quickstart-download)  
⚡ **如果自动安装失败可以参考这里** [常见问题](https://lijiahangmax.github.io/orion-ops-extension-plugins/#/machine-monitor-agent/faq)

* 安装: 批量安装本页机器的监控插件
* 升级: 批量升级本页机器的监控插件
* 检测: 批量检测本页机器的监控状态
* 监控: 进入机器监控页面
* 插件配置: 配置监控插件的地址和请求头
* 报警配置: 配置机器监控的报警类型以及报警联系组
* 报警记录: 查看当前机器的报警历史记录

### 环境变量

配置机器环境变量, 这个环境变量并不会设置到远程机器内, 而是运行命令时动态替换命令内容。  
`批量执行` `调度任务` `应用发布` `应用构建` 都可以用到, 设计的初衷是每台机器的配置不一样, 不同的机器执行相同的命令时设置不同的参数。  
系统提供了几个默认的机器变量, 执行命令时使用 `@{machine.xxx}` 来替换。

| key               | 示例       | 描述      |
| :----             | :---      | :----     |
| machine_id        | 1         | 机器id    |
| machine_name      | server1   | 机器名称   |
| machine_tag       | host      | 机器标签   |
| machine_host      | 127.0.0.1 | 机器主机   |
| machine_port      | 22        | 机器端口   |
| machine_username  | root      | 机器用户名 |

> ##### 示例

```
# 当前机器 名称: dev-server1
# 环境变量 dumpPath: /usr/local/oom

# 执行命令
echo @{machine.machine_name}
java -jar demo.jar -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=@{machine.dumpPath}

# 命令在执行前会被替换为
echo dev-server1
java -jar demo.jar -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/local/oom
```

机器创建后系统会生成几个默认的环境变量, 用于部分功能的交互。

| key                    | 示例                             | 描述                |
| :----                  | :---                            | :----              |
| sftp_charset           | UTF-8                           | sftp 文件名称编码格式 |
| tail_charset           | UTF-8                           | 文件追踪编码格式      |
| tail_offset            | 400                             | 文件追踪偏移量(行)    |
| tail_default_command   | tail -f -n @{offset} '@{file}'  | 文件追踪默认命令      |

⚡ 注意: 切换视图后保存只会增量保存, 并不会删除变量

### 机器秘钥

管理和维护可操作机器 SSH 秘钥

### 代理列表

管理和维护可操作机器 SSH 连接代理

### 终端日志

查看用户连接 Terminal 的日志以及进行终端录屏回放

### 终端控制

`管理员菜单`

查看当前所有已连接的终端会话, 具体到用户和机器, 可强制下线终端, 以及只读、读写接入终端监视

