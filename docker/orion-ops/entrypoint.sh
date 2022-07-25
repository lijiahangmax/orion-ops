#!/bin/bash
/usr/sbin/sshd
nginx
cd /app
java -jar app.jar --spring.profiles.active=prod --generator-admin