#/bin/bash
set -e
version=1.3.1
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-adminer:${version}
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-mysql:${version}
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-redis:${version}
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops:${version}
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-adminer:latest
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-mysql:latest
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-redis:latest
docker push registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops:latest
