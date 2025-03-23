#/bin/bash
set -e
version=1.3.1
docker build -t orion-ops-redis:${version} .
docker tag orion-ops-redis:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-redis:${version}
docker tag orion-ops-redis:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-redis:latest
