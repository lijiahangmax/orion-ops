FROM mysql:8.0.28
COPY ./init-data.sql /tmp
COPY ./init-schema.sql /tmp
COPY ./my.cnf /etc/mysql/conf.d/my.cnf
RUN cat /tmp/init-schema.sql >> /tmp/orion.sql && \
    cat /tmp/init-data.sql >> /tmp/orion.sql && \
    cp /tmp/orion.sql /docker-entrypoint-initdb.d

