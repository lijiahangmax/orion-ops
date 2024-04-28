mv ../../orion-ops-api/orion-ops-web/target/orion-ops-web-1.2.3.jar ./
mv ../../orion-ops-vue/dist ./
docker build -t orion-ops:1.2.3 .
rm -f ./orion-ops-web-1.2.3.jar
rm -rf ./dist
