#/bin/bash
version=1.3.1
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-adminer:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-mysql:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-redis:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops:${version}
