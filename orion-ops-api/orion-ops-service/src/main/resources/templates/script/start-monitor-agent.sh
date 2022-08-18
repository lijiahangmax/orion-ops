mkdir -p ${pluginPath}
echo "ps -ef | grep ${killTag} | grep -v grep | awk '{print \$2}' | xargs kill -9 || echo \$?
nohup java -jar ${agentJarPath} --machineId=${machineId} --spring.profiles.active=prod >> ${logPath} &" > ${scriptPath}
chmod 777 ${scriptPath}
${scriptPath}
