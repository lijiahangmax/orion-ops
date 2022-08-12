echo "ps -ef | grep ${killTag} | grep -v grep | awk '{print \$2}' | xargs kill -9 || echo \$?
nohup java -jar ${jarPath} --machineId=${machineId} --spring.profiles.active=prod >> ${output} &" > ${scriptPath}
chmod 777 ${scriptPath}
${scriptPath}
