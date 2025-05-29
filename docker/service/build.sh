#/bin/bash
set -e
version=1.3.1
mv ../../orion-ops-api/orion-ops-web/target/orion-ops-web.jar ./
mv ../../orion-ops-vue/dist ./
cp $HOME/orion-ops/lib/machine-monitor-agent-latest.jar ./
docker build --no-cache -t orion-ops:${version} .
rm -rf ./dist
rm -rf ./orion-ops-web.jar
rm -rf ./machine-monitor-agent-latest.jar
docker tag orion-ops:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops:${version}
docker tag orion-ops:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops:latest
