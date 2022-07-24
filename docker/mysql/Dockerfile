FROM mysql:8.0.28
COPY orion-ops-service/init-data.sql /tmp
COPY orion-ops-service/init-schema.sql /tmp
RUN cat /tmp/init-schema.sql >> /tmp/orion.sql && \
    cat /tmp/init-data.sql >> /tmp/orion.sql && \
    cp /tmp/orion.sql /docker-entrypoint-initdb.d

