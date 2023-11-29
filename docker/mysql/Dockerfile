FROM mysql:8.0.28
COPY sql/init-data.sql /tmp
COPY sql/init-schema.sql /tmp
COPY docker/mysql/my.cnf /etc/mysql/conf.d/my.cnf
RUN cat /tmp/init-schema.sql >> /tmp/orion.sql && \
    cat /tmp/init-data.sql >> /tmp/orion.sql && \
    cp /tmp/orion.sql /docker-entrypoint-initdb.d

