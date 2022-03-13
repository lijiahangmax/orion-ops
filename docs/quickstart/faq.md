## Q&A

> ##### 1. 什么时候会进行版本迭代?

因为前后端都是我一个人在开发, 都是在抽时间写, 我的大部分的闲暇时间都贡献在这个上面了。而且我对代码要求比较高, 也在不断打磨之前的代码, 所以开发周期比较长。  
<br/>

> ##### 2. 忘记了默认管理员密码无法登陆怎么办?

重启后端服务 添加启动参数 `--reset-admin` 会将 `orionadmin` 的密码重置为 `orionadmin`

```
nohup java -jar orion-ops-service-1.0.0.jar --spring.profiles.active=prod --reset-admin &
```

<br/> 

> ##### 3. 开启了IP黑白名单, 无法访问系统了怎么办?

重启后端服务 添加启动参数 `--disable-ip-filter` 会将禁用ip过滤器

```
nohup java -jar orion-ops-service-1.0.0.jar --spring.profiles.active=prod --disable-ip-filter &
```

<br/> 

> ##### 4. 数据误删除怎么办?

数据库的数据都采用了逻辑删除, 可以直接修改表中的 `deleted` 字段改为 `1`  
<br/>

> ##### 5. 可以将宿主机 IP 修改为其他机器吗?

暂时不支持    
<br/>

> ##### 6. orion-ops 可以实现异地构建吗?

暂时不支持   
<br/>

> ##### 7. 为什么 SFTP 大文件无法上传?

1. 修改 application.properties 中的 `spring.servlet.multipart.max-file-size` 为合适的值
2. 修改 系统管理 > 系统设置 > 其他设置 `上传文件最大阈值` 为合适的值
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

应用管理 > 版本仓库 找到应用关联的仓库, 检查密码/token后 重新初始化即可  
<br/>

> ##### 9. orion-ops 是否支持 Windows 部署?

不支持, 仅支持 Linux 环境下部署  
<br/>

> ##### 10. orion-ops 是否支持 svn 作为版本仓库?

不支持, 但是构建时可以执行 svn 命令  
<br/>

> ##### 11. 如果构建不会生成产物, 怎么防止构建失败?

将构建产物路径设置为一个已存在的文件的绝对路径即可  
<br/>
