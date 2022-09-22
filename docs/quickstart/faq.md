> ##### 1. 什么时候会进行版本迭代?

因为前后端都是我一个人在开发, 都是在抽时间写, 我的大部分的闲暇时间都贡献在这个上面了。而且我对代码要求比较高, 也在不断打磨之前的代码, 所以开发周期比较长。  
<br/>

> ##### 2. 忘记了默认管理员密码无法登陆怎么办?

重启后端服务 添加启动参数 `--reset-admin` 会将 `orionadmin` 的密码重置为 `orionadmin`

```
nohup java -jar orion-ops-web-1.2.0.jar --spring.profiles.active=prod --reset-admin &
```

<br/> 

> ##### 3. 开启了IP黑白名单, 无法访问系统了怎么办?

重启后端服务 添加启动参数 `--disable-ip-filter` 会将禁用ip过滤器

```
nohup java -jar orion-ops-web-1.2.0.jar --spring.profiles.active=prod --disable-ip-filter &
```

<br/> 

> ##### 4. 数据误删除怎么办?

数据库的数据都采用了逻辑删除, 可以将已删除的数据中的 `deleted` 字段改为 `1`   
如果不知道数据是哪一条, 可以查询用户操作日志, 点击 `参数` 寻找操作的id       
<br/>

> ##### 5. 可以将宿主机 IP 修改为其他远程机器吗?

CI 功能底层暂时不支持, 如果不使用 CI 功能则可以修改    
⚡ 不推荐这么做, 设计的初衷是宿主机就是部署 orion-ops 机器, 用来做数据存储以及数据交换  
<br/>

> ##### 6. orion-ops 可以实现异地构建吗?

暂时不支持   
<br/>

> ##### 7. 为什么 SFTP 无法上传特大文件?

考虑到系统性能和内存占用 默认限制单次上传最大阈值为 512MB

1. 修改 application.properties 中的 `spring.servlet.multipart.max-file-size` 为合适的值 (默认2048MB)
2. 修改 系统管理 > 系统设置 > 其他设置 `上传文件最大阈值` 为合适的值 (默认512MB)
3. 如果使用 nginx 代理后端接口, 修改 nginx 请求限制 `client_max_body_size` 为合适的值

```
server {
  listen 80;
  client_max_body_size 1024m;
  ...
}
```

⛔ 不建议上传特别大的文件, 可能会出现 `OOM`  
<br/>

> ##### 8. 应用构建时提示 '版本控制仓库操作执行失败'?

应用管理 > 版本仓库 找到应用关联的仓库, 检查密码/私人令牌后 重新初始化即可  
<br/>

> ##### 9. orion-ops 是否支持 Windows 部署?

可以支持, 前提是 Windows 安装了 OpenSSH Server  
但是不建议, 因为 Windows 的 ssh 命令兼容性不好, 一切需要执行ssh命令的地方都不友好  
如: 批量执行, 应用构建发布, 调度任务兼容性非常不友好
<br/>

> ##### 10. orion-ops 是否支持 svn 作为版本仓库?

仅支持 git, 但是构建时可以执行 svn 命令  
<br/>

> ##### 11. 如果构建不会生成产物, 怎么防止构建失败?

将构建产物路径设置为一个已存在的文件的绝对路径即可  
<br/>

> ##### 12. 如果应用没有构建操作, 该如何执行发布操作?

`同问题 11` 将构建产物路径设置为一个已存在的文件的绝对路径, 构建操作随便设置一个命令即可, 如: echo 1  
<br/>

> ##### 13. 为什么会找不到环境变量?

可以在执行命令的第一行设置 `source /etc/profile` 来加载环境变量  
<br/>

> ##### 14. nohup 为什么无法启动程序?

执行命令行使用的伪终端关闭时会停止终端进程, 可以使用 sleep 来跳过  
`(nohup java -jar springboot-0.0.1-SNAPSHOT.jar >> run.log 2>&1 &) && sleep 1`  
<br/>

> ##### 15. 命令中途执行失败如何设置中断执行?

可以在执行命令的第一行设置 `set -e`  
作用是: 当执行出现意料之外的情况时, 立即退出   
<br/>

> ##### 16. 在调度任务、应用构建、应用发布 命令执行成功的依据是什么?

是获取命令的 `exitcode` 判断是否为 `0` 如果非0则代表命令执行失败  
同理, 在命令的最后一行设置 `exit 1` 结果将会是失败, 可以用此来中断后续流程  
<br/>

> ##### 17. 为什么使用秘钥认证还是无法连接机器?

```
# 升级 openssh
yum update openssh
sshd -v (我的版本: OpenSSH_7.4p1, OpenSSL 1.0.2k-fips  26 Jan 2017)

# 生成秘钥时添加参数 -m PEM
ssh-keygen -t rsa -m PEM
chmod 700  ~/.ssh
chmod 700  ~/.ssh/authorized_keys 

# 修改 sshd 配置 /etc/ssh/sshd_config
PubkeyAuthentication yes
RSAAuthentication yes
AuthorizedKeysFile .ssh/authorized_keys

# 重启 sshd 服务
service sshd restart
```

<br/>

> ##### 18. 应用发布中选择 SCP 作为传输方式, 为什么还需要手动输入密码?

需要配置宿主机与目标机器的 ssh 免密登陆, 配置好后就无需输入密码了  
<br/>

> ##### 19. 下载的日志中为什么会有这么多乱码 如: \x1b[0m?

这是 ANSI 码, 用于日志展示中的着色  
系统提供了批量清除的功能, 在 `执行管理` > `日志面板` > `清除ANSI`
<br/>

> ##### 20. 执行命令时 可以用哪些环境变量?

| 环境变量           | 描述                 |        
| :----            | :---                 | 
| `@{app.xxx}`     | 应用信息以及应用环境变量  | 
| `@{machine.xxx}` | 机器信息以及机器环境变量  | 
| `@{system.xxx}`  | 系统环境变量            |
| `@{build.xxx}`   | 应用构建业务上下文       | 
| `@{release.xxx}` | 应用发布业务上下文       |

| 业务场景 | 可用变量                            |        
| :----   | :---                              | 
| 批量执行 | `machine` `system`                 | 
| 定时任务 | `machine` `system`                 | 
| 构建配置 | `build` `app` `machine` `system`   |
| 发布配置 | `release` `app` `machine` `system` | 

⚡ 详细使用请参考操作手册~

<br/>
