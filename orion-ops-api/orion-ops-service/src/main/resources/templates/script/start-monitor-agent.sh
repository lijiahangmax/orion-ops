AGENT_PROCESS=${processName}
STARTED=`ps -ef | grep $AGENT_PROCESS | grep '.jar' | grep -v grep | awk '{print $2}' | wc -l`
PID=`ps -ef | grep $AGENT_PROCESS | grep '.jar' | grep -v grep | awk '{print $2}'`
# KILL
if [ $STARTED -eq 0 ]
then
    echo "agent not started"
else
    echo "kill pid $PID"
	kill -9 $PID
fi
echo 'starting ' $AGENT_PROCESS '....'
# START
nohup java -jar -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=56m -Xms128m -Xmx128m -Xmn32m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC ${agentJarPath} --machineId=${machineId} --spring.profiles.active=prod >/dev/null 2>&1 &
echo 'agent started result: ' $?
