#/bin/bash
version=1.2.6
mv ../../orion-ops-api/orion-ops-web/target/orion-ops-web-${version}.jar ./orion-ops-web.jar
mv ../../orion-ops-vue/dist ./
cp $HOME/orion-ops/lib/machine-monitor-agent-latest.jar ./
docker build -t orion-ops:${version} .
rm -rf ./dist
rm -rf ./orion-ops-web.jar
rm -rf ./machine-monitor-agent-latest.jar
docker tag orion-ops:${version} registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops:${version}
docker push registry.cn-hangzhou.aliyuncs.com/lijiahangmax/orion-ops:${version}
