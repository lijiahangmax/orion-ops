FROM nginx:alpine
USER root
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
    apk update
RUN echo 'root:orionadmin' | chpasswd
RUN apk add openjdk8
RUN rm -rf /etc/nginx/conf.d/*
WORKDIR /app
COPY orion-ops-service/target/orion-ops-service-1.1.4.jar /app/app.jar
COPY orion-ops-vue/dist/index.html /usr/share/nginx/html/
COPY orion-ops-vue/dist /usr/share/nginx/html/ops
ADD docker/orion-ops/entrypoint.sh /app/entrypoint.sh
ADD docker/orion-ops/nginx.conf /etc/nginx/conf.d
ENTRYPOINT [ "sh", "/app/entrypoint.sh" ]
