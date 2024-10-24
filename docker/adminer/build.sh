#/bin/bash
version=1.2.9
docker build -t orion-ops-adminer:${version} .
docker tag orion-ops-adminer:${version} registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-adminer:${version}
