### 所需环境

* JDK 1.8
* Mysql 8.0(+)
* Redis 5.0.5(+)
* Node 11.12.0(+)
* Maven
* Nginx
* npm / yarn

⚡ 注意: windows 环境二次开发需要安装 OpenSSH Server

### 配置

1. 拉取代码
   ```
   # github
   git clone https://github.com/lijiahangmax/orion-ops
   # gitee
   git clone https://gitee.com/lijiahangmax/orion-ops
   ```

2. 初始化数据库  
   `执行脚本` > `orion-ops/orion-ops-service/init.sql`

3. 修改后端配置
   ```
   # 修改配置文件
   orion-ops/orion-ops-service/src/main/resources/application-dev.properties
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
   后端服务启动时需要设置启动参数 `--generator-admin` 来生成默认管理员用户

### 测试访问

在浏览器中输入 http://localhost:10010/ops 访问  
账号: `orionadmin`  
密码: `orionadmin`

### 配置系统

登录后需要配置宿主机 SSH 信息, 本地调试阶段宿主机可以改为远程机器   
如果是密码登录: 机器管理 > 机器列表 > `宿主机` > 更多 > 编辑 > 选择认证方式为密码 > 输入密码 > 确定  
如果是秘钥登陆: 机器管理 > 机器秘钥 > 新建  
配置完成后测试连接:  机器管理 > 机器列表 > `宿主机` > 更多 > 测试连接

