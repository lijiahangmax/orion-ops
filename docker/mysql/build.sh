#/bin/bash
version=1.2.6
cp -r ../../sql ./sql
docker build -t orion-ops-mysql:${version} .
rm -rf ./sql
docker tag orion-ops-mysql:${version} registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-mysql:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-mysql:${version}
