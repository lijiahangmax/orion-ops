FROM mysql:8.0.28
COPY ./sql/init-1-schema.sql /tmp
COPY ./sql/init-2-data.sql /tmp
COPY ./my.cnf /etc/mysql/conf.d/my.cnf
RUN cat /tmp/init-1-schema.sql >> /tmp/init.sql && \
    cat /tmp/init-2-data.sql >> /tmp/init.sql && \
    cp /tmp/init.sql /docker-entrypoint-initdb.d
