mv ../../orion-ops-api/orion-ops-web/target/orion-ops-web-1.2.4.jar ./
mv ../../orion-ops-vue/dist ./
cp $HOME/orion-ops/lib/machine-monitor-agent-latest.jar ./
docker build -t orion-ops:1.2.4 .
rm -f ./orion-ops-web-1.2.4.jar
rm -rf ./dist
