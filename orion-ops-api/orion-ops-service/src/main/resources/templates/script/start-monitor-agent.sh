mkdir -p ${pluginPath}
echo "eof=$\"\n\"
ps -ef | grep ${killTag} | grep -v grep | awk '{print \$2}' | xargs kill -9 || echo \$?
nohup java -jar ${agentJarPath} --machineId=${machineId} --spring.profiles.active=prod 2>&1 >> ${logPath} &
eof=\$(echo -e \$eof)
echo \$eof" > ${scriptPath}
chmod 777 ${scriptPath}
${scriptPath}
