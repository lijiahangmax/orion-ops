### 所需环境

* Docker
* Node 11.12.0(+)
* Maven 3.5.4(+)

⚡ maven 推荐使用阿里云 mirror    
⚡ npm 建议使用淘宝镜像 `npm config set registry https://registry.npm.taobao.org`

### 编译Docker镜像

1. 拉取代码
   ```
   # github
   git clone https://github.com/lijiahangmax/orion-ops
   # gitee
   git clone https://gitee.com/lijiahangmax/orion-ops
   ```
2. 构建后端代码
   ```
   # 修改全局加密秘钥, 为了密码安全考虑 (推荐修改)
   orion-ops/orion-ops-api/orion-ops-web/src/main/resources/application.properties value.mix.secret.key
   # 进入代码目录
   cd orion-ops/orion-ops-api
   # 编译
   mvn -U clean install -DskipTests
   ```
3. 构建前端代码
   ```
   # 进入代码目录
   cd orion-ops/orion-ops-vue
   # 下载依赖
   npm i 或 yarn
   # 编译
   npm run build:prod
   ```
4. 构建 Docker 镜像
   ```
   # 进入仓库目录
   cd orion-ops
   # 构建镜像
   docker compose build
   ```
   由于访问 DockerHub 镜像比较慢, 可以修改一下配置加速镜像 /etc/docker/daemon.json, 如果没有此文件可以创建此文件 (Linux系统, Window 和 Mac 直接可以通过 Docker 的 Dashboard 修改)
    ```json
    {
      "registry-mirrors": ["https://registry.docker-cn.com", "https://registry.cn-hangzhou.aliyuncs.com", "https://mirror.ccs.tencentyun.com", "https://docker.mirrors.ustc.edu.cn"]
    }
    ```
5. 启动镜像
    ```
    docker compose up -d
    ```

### 测试访问

在浏览器中输入 http://localhost:3090/ops 访问  
账号: orionadmin  
密码: orionadmin  
