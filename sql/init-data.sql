# 默认应用环境
INSERT INTO application_profile VALUES (1, '开发环境', 'dev', '开发环境', 1, 1, NOW(), NOW());

# 默认管理员用户
INSERT INTO user_info VALUES (1, 'orionadmin', '管理员', 'aa8d3073dbc3958d10bb6e5240816d57', 'Yxy3ZRJ0IOkc2zszJ3y', 10, 1, 1, 0, '/avatar/1.png', '18888888888', NULL, NULL, 1, NOW(), NOW());

# 默认机器分组
INSERT INTO machine_group VALUES (1, -1, '默认分组', 1, 1, NOW(), NOW());

# 默认命令模板
INSERT INTO command_template VALUES (1, 'echo 测试', 'echo \'123\'\necho \'1234\'', 'echo 模板', 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (2, 'for echo 0.05', 'for i in `seq 1 250`\ndo\n    sleep 0.05\n	echo $i\ndone', NULL, 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (3, 'for echo 1 c 10', 'for i in `seq 1 10`\ndo\n    sleep 1\n	echo $i\ndone', 'sleep 1 -c 10', 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (4, 'read value await', 'echo read\nread -p \"Enter value > \" value\necho \"value：$value\"', 'read await', 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (5, 'kill', 'PROGRESS=\nps -ef | grep $PROGRESS | grep -v grep | awk \'{print $2}\' | xargs kill -9 || echo $?', '通过进程名称执行kill命令', 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (6, '获取 pid', 'PROGRESS=\r\nPID=$(ps -ef | grep $PROGRESS | grep -v grep | awk \'{print $2}\')\r\nif [ ! -z $PID ]; then\r\n   echo \'pid: \' $PID\r\nfi', '获取进程 pid', 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (7, '删除文件', 'DEL_PATH=\r\nif [ -f \"$DEL_PATH\" ]; then\r\n rm -f $DEL_PATH\r\n echo \'删除文件\' $DEL_PATH\r\nfi', NULL, 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
INSERT INTO command_template VALUES (8, '删除文件夹', 'DEL_PATH=\r\nif [ -d \"$DEL_PATH\" ]; then\r\n rm -rf $DEL_PATH\r\n echo \'删除文件夹\' $DEL_PATH\r\nfi', NULL, 1, 'orionadmin', 1, 'orionadmin', 1, NOW(), NOW());
