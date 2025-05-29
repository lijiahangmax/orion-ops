#/bin/bash
set -e
version=1.3.1
docker build -t orion-ops-adminer:${version} .
docker tag orion-ops-adminer:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-adminer:${version}
docker tag orion-ops-adminer:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-adminer:latest
