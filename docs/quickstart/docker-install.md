## 编译Docker镜像
1. 拉取代码
   ```
   # github
   git clone https://github.com/lijiahangmax/orion-ops
   # gitee
   git clone https://gitee.com/lijiahangmax/orion-ops
   ```
2. 构建后端代码
   ```
   # 修改配置文件
   orion-ops/orion-ops-service/src/main/resources/application-prod.properties
   # 修改全局加密秘钥,为了密码安全考虑 (推荐修改)
   orion-ops/orion-ops-servicesrc/main/java/com/orion/ops/utils/ValueMix#SECRET_KEY
   # 编译
   mvn -U clean install -DskipTests
   ```
3. 构建前端代码
   ```
   # 修改配置文件
   orion-ops/orion-ops-vue/.env.production
   # 下载依赖
   npm i 或 yarn
   # 编译
   npm run build:prod
   ```
4. 构建Docker镜像
   ```
   # 进入仓库目录
   cd orion-ops
   # 构建镜像
   docker-compose build
   ```
    由于访问docker hub 镜像比较慢，可以修改一下配置加速镜像
    /etc/docker/daemon.json（Linux系统，Window和Mac直接可以通过Docker的Dashboard修改）
    如果没有此文件可以创建此文件
    ```
    {
    "registry-mirrors": ["https://registry.docker-cn.com","https://registry.cn-hangzhou.aliyuncs.com","https://mirror.ccs.tencentyun.com","https://docker.mirrors.ustc.edu.cn"]
    }
    ```
5. 启动镜像
    ```
      docker-compose up -d
    ```
6. 访问orion-ops
    ```
      http://localhost:3090
    ```