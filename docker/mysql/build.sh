#!/bin/bash
set -e
version=1.3.1
cp -r ../../sql ./sql
docker build -t orion-ops-mysql:${version} .
rm -rf ./sql
docker tag orion-ops-mysql:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-mysql:${version}
docker tag orion-ops-mysql:${version} registry.cn-hangzhou.aliyuncs.com/orionsec/orion-ops-mysql:latest
