### 所需环境

* JDK 1.8
* Mysql 8.0(+)
* Redis 5.0.5(+)
* Node 11.12.0(+)
* Maven
* Nginx
* npm / yarn

### 构建

1. 拉取代码
   ```
   # github
   git clone https://github.com/lijiahangmax/orion-ops
   # gitee
   git clone https://gitee.com/lijiahangmax/orion-ops
   ```

2. 初始化数据库  
   `执行脚本` > `orion-ops/orion-ops-service/init.sql`

3. 构建后端代码
   ```
   # 修改配置文件
   orion-ops/orion-ops-service/src/main/resources/application-prod.properties
   # 编译
   mvn clean package -DskipTests
   ```   
   构建时如果有依赖拉不下来可以选择使用阿里云的 maven 镜像
   ```setting.xml
   <mirror>
       <id>nexus-aliyun</id>
       <mirrorOf>central</mirrorOf>
       <name>Nexus aliyun</name>
       <url>http://maven.aliyun.com/nexus/content/groups/public</url>
   </mirror>
   ```

4. 构建前端代码
   ```
   # 修改配置文件
   orion-ops/orion-ops-vue/.env.production
   # 下载依赖
   npm i 或 yarn
   # 编译
   npm run build:prod
   ```   

### 修改 nginx 配置

```
 server {
        listen       80;
        client_max_body_size 4m;

        location / {
            root   html;
            index  index.html index.htm;
            proxy_set_header  X-Real-IP  $remote_addr;
            proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header Host $http_host;
        }
 }
```

### 部署

```
复制 orion-ops/orion-ops-vue/dist 的所有文件到 nginx映射目录/ops
复制 orion-ops/orion-ops-service/target/orion-ops-service-1.0.0.jar 到 /data/orion
# 启动后台服务
nohup java -jar orion-ops-service-1.0.0.jar --spring.profiles.active=prod --generator-admin &
# 启动 nginx
service nginx start
```

### 测试访问

在浏览器中输入 http://localhost/ops 访问  
账号: `orionadmin`  
密码: `orionadmin`

### 配置宿主机信息

登录后需要配置宿主机信息, 直到可以访问  
如果是密码登录: 机器管理 > 机器列表 > `宿主机` > 更多 > 编辑 > 选择认证方式为密码 > 输入密码 > 确定  
如果是秘钥登陆: 机器管理 > 机器秘钥 > 新建  
配置完成后测试连接:  机器管理 > 机器列表 > `宿主机` > 更多 > 测试连接

### 启动参数

> 启动项目时提供了一些可选的执行参数

关闭IP过滤器   `--disable-ip-filter`  
生成默认管理员账号 `--generator-admin`  账号: `orionadmin` 密码: `orionadmin`  
重置默认管理员账号 `--reset-admin`    账号: `orionadmin` 密码: `orionadmin`  
