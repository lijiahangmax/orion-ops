FROM nginx:alpine
USER root
WORKDIR /app
# 系统时区
ARG TZ=Asia/Shanghai
# 添加包
RUN \
  sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
  apk update && \
  apk add udev && \
  apk add tzdata && \
  apk add openjdk8 && \
  apk add openssh-server && \
  apk add openssh-sftp-server
# 设置时区
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && \
  echo '${TZ}' > /etc/timezone
# 删除原 nginx 配置
RUN rm -rf /etc/nginx/conf.d/*
# ssh 配置
RUN sed -i 's/#PermitRootLogin.*/PermitRootLogin yes/g' /etc/ssh/sshd_config && \
    ssh-keygen -t dsa -P '' -f /etc/ssh/ssh_host_dsa_key && \
    ssh-keygen -t rsa -P '' -f /etc/ssh/ssh_host_rsa_key && \
    ssh-keygen -t ecdsa -P '' -f /etc/ssh/ssh_host_ecdsa_key && \
    ssh-keygen -t ed25519 -P '' -f /etc/ssh/ssh_host_ed25519_key && \
    echo 'root:orionadmin' | chpasswd
# 复制包
COPY ./machine-monitor-agent-latest.jar /root/orion-ops/lib/machine-monitor-agent-latest.jar
COPY ./orion-ops-web.jar /app/app.jar
COPY ./dist/index.html /usr/share/nginx/html/
COPY ./dist /usr/share/nginx/html/ops
ADD ./entrypoint.sh /app/entrypoint.sh
ADD ./nginx.conf /etc/nginx/conf.d
# 启动
ENTRYPOINT [ "sh", "/app/entrypoint.sh" ]
