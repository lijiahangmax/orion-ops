### 模板管理

维护命令模板。 在 `批量执行` `调度任务` `应用配置` 可以直接选择使用。

### 系统变量

配置以及维护系统环境变量。

系统内部维护了几个默认的变量, 执行命令时使用 `@{system.xxx}` 来替换。

| key        | 示例               | 描述                      |
| :----      | :---              | :----                     |
| key_path   | /root/orion/keys  | 秘钥存放目录                |
| pic_path   | /root/orion/pic   | 图片存放目录                |
| swap_path  | /root/orion/swap  | 交换分区目录                | 
| log_path   | /root/orion/log   | 日志存放目录                | 
| temp_path  | /root/orion/temp  | 临时文件目录                | 
| repo_path  | /root/orion/repo  | 应用版本仓库目录             | 
| dist_path  | /root/orion/dist  | 构建产物目录                | 
| tail_mode  | tracker           | 文件追踪模式 tracker 或 tail | 

> ##### 示例

```
# 执行命令
echo @{system.log_path}

# 命令在执行前会被替换为
echo /root/orion/log 
```

⚡ 注意: 切换视图后保存只会增量保存, 并不会删除变量

### 系统设置

`管理员菜单`

对系统进行一些自定义设置。

* 系统系统分析
    * 自动清理开关
    * 自动清理阈值
    * 系统资源统计分析
* 安全配置
    * 多端登陆开关
    * 登陆失败锁定开关
    * 登陆IP绑定开关
    * 登陆凭证自动续签开关
    * 凭证有效期设置
    * 登陆失败锁定阈值设置
    * 登陆自动续签阈值设置
* IP 过滤器配置
    * 黑名单配置
    * 白名单配置
    * 过滤器开启开关
    * 启用白名单开关
* 其他配置
    * 调度任务自动恢复开关
    * 上传文件最大阈值设置
    * 统计缓存有效时间
* 系统线程池指标
