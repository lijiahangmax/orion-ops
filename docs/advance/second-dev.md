### 所需环境

* JDK 1.8
* Mysql 8.0(+)
* Redis 5.0.5(+)
* Node 11.12.0(+)
* Maven 3.5.4(+)

⚡ maven 推荐使用阿里云 mirror  
⚡ windows 环境二次开发需要安装 OpenSSH Server

### 配置

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
   orion-ops/orion-ops-service/init-schema.sql
   # 执行默认数据脚本 [默认用户, 默认应用环境, 常用命令模板] (可选)
   orion-ops/orion-ops-service/init-data.sql
   ```

3. 修改后端配置
   ```
   # 修改配置文件
   orion-ops/orion-ops-service/src/main/resources/application-dev.properties
   # 修改全局加密秘钥, 为了密码安全考虑 (推荐修改)
   orion-ops/orion-ops-service/src/main/resources/application.properties value.mix.secret.key
   ```

4. 修改前端配置
   ```
   # 修改配置文件
   orion-ops/orion-ops-vue/.env.development
   # 下载依赖
   npm i 或 yarn
   # 运行
   npm run serve:dev
   ```   

5. 运行  
   后端服务启动时需要设置启动参数 `--generator-admin` 来生成默认管理员用户 (已执行 `init-data.sql` 则可以忽略)    
   启动参数 idea 中设置  `应用设置` > `Environment` > `Program arguments`

### 测试访问

在浏览器中输入 http://localhost:10010/ops 访问  
账号: `orionadmin`  
密码: `orionadmin`

### swagger

地址: http://127.0.0.1:9119/doc.html#/home  
这个只有在 `dev` `local` 环境才可以访问

### 配置系统

登录后需要配置宿主机 SSH 信息, 本地调试阶段宿主机可以改为远程机器   
如果是密码登录: 机器管理 > 机器列表 > `宿主机` > 更多 > 编辑 > 选择认证方式为密码 > 输入密码 > 确定  
如果是秘钥登陆: 机器管理 > 机器秘钥 > 新建  
配置完成后测试连接: 机器管理 > 机器列表 > `宿主机` > 更多 > 测试连接  
创建应用环境: 应用管理 > 环境管理 > 添加 (已执行 `init-data.sql` 则可以忽略)

### 启动参数

> 启动项目时提供了一些可选的执行参数

关闭IP过滤器   `--disable-ip-filter`  
生成默认管理员账号 `--generator-admin`  账号: `orionadmin` 密码: `orionadmin`  
重置默认管理员账号 `--reset-admin`      账号: `orionadmin` 密码: `orionadmin`  
