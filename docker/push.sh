#/bin/bash
version=1.2.9
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-adminer:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-mysql:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops-redis:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops:${version}
