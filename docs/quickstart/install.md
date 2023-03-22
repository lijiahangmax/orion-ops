### 所需环境

* JDK 1.8
* Mysql 8.0(+)
* Redis 5.0.5(+)
* Node 11.12.0(+)
* Maven 3.5.4(+)
* Nginx

⚡ maven 推荐使用阿里云 mirror   
⚡ npm 建议使用淘宝镜像 `npm config set registry https://registry.npm.taobao.org`

### 构建

1. 拉取代码

```
# github
git clone https://github.com/lijiahangmax/orion-ops
# gitee
git clone https://gitee.com/lijiahangmax/orion-ops
```

2. 初始化数据库

```
# 执行DDL脚本
orion-ops/sql/init-schema.sql
# 执行默认数据脚本 [默认用户, 默认应用环境, 常用命令模板] (可选)
orion-ops/sql/init-data.sql
```

3. 构建后端代码

```
# 修改配置文件 (mysql, redis)
orion-ops/orion-ops-api/orion-ops-web/src/main/resources/application-prod.properties
# 修改全局加密秘钥, 为了密码安全考虑 (推荐修改)
orion-ops/orion-ops-api/orion-ops-web/src/main/resources/application.properties value.mix.secret.key
# 进入代码目录
cd orion-ops/orion-ops-api
# 编译
mvn -U clean install -DskipTests
```   

4. 构建前端代码

```
# 进入代码目录
cd orion-ops/orion-ops-vue
# 下载依赖
npm i 或 yarn
# 编译
npm run build:prod
```   

### 修改 nginx 配置

```
server {
    listen       80;
    server_name  localhost;

    # 是否启动 gzip 压缩
    gzip  on;
    # 需要压缩的常见静态资源
    gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
    # 如果文件大于 1k 就启动压缩
    gzip_min_length 1k;
    # 缓冲区
    gzip_buffers 4 16k;
    # 压缩的等级
    gzip_comp_level 2;
    # access_log  /var/log/nginx/host.access.log  main;

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
        proxy_set_header X-Real-IP  $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /orion/api {
        proxy_pass    http://localhost:9119/orion/api;
        proxy_set_header X-Real-IP  $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

     location /orion/keep-alive {
        proxy_pass    http://localhost:9119/orion/keep-alive;
        proxy_http_version 1.1;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_read_timeout 3600s;
        proxy_send_timeout 3600s;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

}
```

### 部署

```
复制 orion-ops/orion-ops-vue/dist/index.html 到 /usr/share/nginx/html
复制 orion-ops/orion-ops-vue/dist 到 /usr/share/nginx/html 并且重命名为 ops
复制 orion-ops/orion-ops-api/orion-ops-api/target/orion-ops-web-1.2.2.jar 到 /data/orion
# 启动后台服务
nohup java -jar orion-ops-web-1.2.2.jar --spring.profiles.active=prod --generator-admin &
# 启动 nginx
service nginx start
```

### 测试访问

在浏览器中输入 http://localhost 访问  
账号: `orionadmin`  
密码: `orionadmin`

### 配置宿主机信息

登录后需要配置宿主机 SSH 信息, 直到可以访问  
如果是密码登录: 机器管理 > 机器列表 > `宿主机` > 更多 > 编辑 > 选择认证方式为密码 > 输入密码 > 确定  
如果是秘钥登陆: 机器管理 > 机器秘钥 > 新建  
配置完成后测试连接: 机器管理 > 机器列表 > `宿主机` > 更多 > 测试连接   
创建应用环境: 应用管理 > 环境管理 > 添加 (已执行 `init-data.sql` 则可以忽略)

### 启动参数

> 启动项目时提供了一些可选的执行参数

关闭IP过滤器   `--disable-ip-filter`  
生成默认管理员账号 `--generator-admin`  账号: `orionadmin` 密码: `orionadmin`  
重置默认管理员账号 `--reset-admin`      账号: `orionadmin` 密码: `orionadmin`  
