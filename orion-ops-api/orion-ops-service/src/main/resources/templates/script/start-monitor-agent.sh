#!/bin/sh
AGENT_PROCESS=${processName}
STARTED=$(ps -ef | grep $AGENT_PROCESS | grep '.jar' | grep -v grep | wc -l)
PIDS=$(ps -ef | grep $AGENT_PROCESS | grep '.jar' | grep -v grep | awk '{print $2}')

# KILL
if [ $STARTED -eq 0 ]
then
    echo "Agent is not running."
else
    echo "Killing agent with PID(s): $PIDS"
    for PID in $PIDS; do
        kill -9 $PID
    done
fi
echo 'Agent starting...'

# START
nohup java -jar -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xms128m -Xmx128m -Xmn32m -Xss512k -XX:SurvivorRatio=8 -XX:+UseG1GC ${agentJarPath} --machineId=${machineId} --spring.profiles.active=prod >/dev/null 2>&1 &

# CHECK
sleep 2
NEW_STARTED=$(ps -ef | grep $AGENT_PROCESS | grep '.jar' | grep -v grep | wc -l)
if [ $NEW_STARTED -eq 0 ]; then
    echo "Failed to start agent."
else
    echo "Agent started successfully."
fi
