#/bin/bash
version=1.2.6
docker build -t orion-ops-redis:${version} .
docker tag orion-ops-redis:${version} registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-redis:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-redis:${version}
