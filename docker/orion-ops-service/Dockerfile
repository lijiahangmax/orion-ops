FROM openjdk:8
WORKDIR /app
ADD orion-ops-service/target/orion-ops-service-1.0.0.jar /app
ADD docker/orion-ops-service/entrypoint.sh /app/entrypoint.sh
ENTRYPOINT [ "sh", "/app/entrypoint.sh" ]
