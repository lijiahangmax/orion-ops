#/bin/bash
version=1.3.1
cp -r ../../sql ./sql
docker build -t orion-ops-mysql:${version} .
rm -rf ./sql
docker tag orion-ops-mysql:${version} registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-mysql:${version}
