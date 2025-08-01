### 所需环境

* Docker 20.10.14+
* Docker Compose 2.3.3+

由于访问 DockerHub 镜像比较慢, 可以修改一下配置加速镜像 /etc/docker/daemon.json, 如果没有此文件可以创建此文件 (Linux系统, Window 和 Mac 直接可以通过 Docker 的
Dashboard 修改)

 ```json
 {
  "registry-mirrors": [
    "https://registry.docker-cn.com",
    "https://registry.cn-hangzhou.aliyuncs.com",
    "https://mirror.ccs.tencentyun.com",
    "https://docker.mirrors.ustc.edu.cn"
  ]
}
 ```

### 拉取代码

```shell
# github
git clone https://github.com/lijiahangmax/orion-ops
# gitee
git clone https://gitee.com/lijiahangmax/orion-ops
# gitcode
git clone https://gitcode.com/lijiahangmax/orion-ops
```

### 修改配置

```
# 修改 docker-compose.yml
# REDIS_HOST           redis 主机
# REDIS_PASSWORD       redis 密码
# MYSQL_HOST           mysql 主机
# MYSQL_USER           mysql 用户名
# MYSQL_PASSWORD       mysql 密码
# SECRET_KEY           加密密钥
```

### 启动镜像

```shell
docker compose up -d
```

### 修改加密方式

```
访问 adminer: http://localhost:18080
服务器: orion-ops-db
用户名: root
密码: Data@123456
数据库: orion-ops

点击左侧 SQL命令 输入:
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'Data@123456';
执行 OK
```

### 测试访问

在浏览器中输入 http://localhost:1080/ 访问  
账号: orionadmin  
密码: orionadmin  
