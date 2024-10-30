#/bin/bash
version=1.3.0
docker build -t orion-ops-adminer:${version} .
docker tag orion-ops-adminer:${version} registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-adminer:${version}
