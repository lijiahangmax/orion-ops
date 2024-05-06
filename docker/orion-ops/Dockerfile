FROM nginx:alpine
USER root
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
    apk update
RUN apk add openjdk8
RUN apk add openssh-server
RUN apk add openssh-sftp-server
RUN rm -rf /etc/nginx/conf.d/*
RUN sed -i 's/#PermitRootLogin.*/PermitRootLogin yes/g' /etc/ssh/sshd_config && \
    ssh-keygen -t dsa -P '' -f /etc/ssh/ssh_host_dsa_key && \
    ssh-keygen -t rsa -P '' -f /etc/ssh/ssh_host_rsa_key && \
    ssh-keygen -t ecdsa -P '' -f /etc/ssh/ssh_host_ecdsa_key && \
    ssh-keygen -t ed25519 -P '' -f /etc/ssh/ssh_host_ed25519_key && \
    echo 'root:orionadmin' | chpasswd
WORKDIR /app
COPY ./machine-monitor-agent-latest.jar /root/orion-ops/lib/machine-monitor-agent-latest.jar
COPY ./orion-ops-web-1.2.3.jar /app/app.jar
COPY ./dist/index.html /usr/share/nginx/html/
COPY ./dist /usr/share/nginx/html/ops
ADD ./entrypoint.sh /app/entrypoint.sh
ADD ./nginx.conf /etc/nginx/conf.d
ENTRYPOINT [ "sh", "/app/entrypoint.sh" ]
