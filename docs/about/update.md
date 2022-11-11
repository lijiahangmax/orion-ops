⚡ 注意: 应用不支持跨版本升级, 可以进行多次升级

## 1.2.1

> sql 脚本

```sql
CREATE TABLE `machine_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `parent_id` bigint DEFAULT NULL COMMENT '父id',
  `group_name` varchar(32) DEFAULT NULL COMMENT '组名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `parent_idx` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器分组';

CREATE TABLE `machine_group_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint DEFAULT NULL COMMENT '组id',
  `machine_id` bigint DEFAULT NULL COMMENT '机器id',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `group_idx` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机器分组关联表';
```

## 1.2.0

> sql 脚本

```sql
ALTER TABLE `web_side_message` 
DROP COLUMN `params_json`,
ADD COLUMN `rel_id` bigint(0) NULL COMMENT '消息关联id' AFTER `send_message`;

ALTER TABLE `machine_info` 
ADD COLUMN `key_id` bigint(0) NULL COMMENT '秘钥id' AFTER `proxy_id`,
MODIFY COLUMN `auth_type` tinyint(1) NULL DEFAULT NULL COMMENT '机器认证方式 1: 密码认证 2: 独立秘钥' AFTER `password`;
 
UPDATE web_side_message SET message_type = 2010 WHERE message_type IN (2030, 2050, 2070, 2090, 2110, 2130);
UPDATE web_side_message SET message_type = 2020 WHERE message_type IN (2040, 2060, 2080, 2100, 2120, 2140);

UPDATE user_event_log SET event_classify = 100 WHERE event_classify = 5;
UPDATE user_event_log SET event_classify = 110 WHERE event_classify = 55;
UPDATE user_event_log SET event_classify = 120 WHERE event_classify = 57;
UPDATE user_event_log SET event_classify = 200 WHERE event_classify = 10;
UPDATE user_event_log SET event_classify = 210 WHERE event_classify = 15;
UPDATE user_event_log SET event_classify = 220 WHERE event_classify = 20;
UPDATE user_event_log SET event_classify = 230 WHERE event_classify = 25;
UPDATE user_event_log SET event_classify = 240 WHERE event_classify = 27;
UPDATE user_event_log SET event_classify = 250 WHERE event_classify = 28;
UPDATE user_event_log SET event_classify = 260 WHERE event_classify = 30;
UPDATE user_event_log SET event_classify = 270 WHERE event_classify = 35;
UPDATE user_event_log SET event_classify = 300 WHERE event_classify = 40;
UPDATE user_event_log SET event_classify = 310 WHERE event_classify = 45;
UPDATE user_event_log SET event_classify = 320 WHERE event_classify = 100;
UPDATE user_event_log SET event_classify = 400 WHERE event_classify = 60;
UPDATE user_event_log SET event_classify = 410 WHERE event_classify = 65;
UPDATE user_event_log SET event_classify = 420 WHERE event_classify = 70;
UPDATE user_event_log SET event_classify = 430 WHERE event_classify = 75;
UPDATE user_event_log SET event_classify = 440 WHERE event_classify = 80;
UPDATE user_event_log SET event_classify = 450 WHERE event_classify = 85;
UPDATE user_event_log SET event_classify = 460 WHERE event_classify = 88;
UPDATE user_event_log SET event_classify = 500 WHERE event_classify = 50;
UPDATE user_event_log SET event_classify = 510 WHERE event_classify = 52;
UPDATE user_event_log SET event_classify = 600 WHERE event_classify = 95;
UPDATE user_event_log SET event_classify = 610 WHERE event_classify = 90;
UPDATE user_event_log SET event_classify = 620 WHERE event_classify = 110;
UPDATE user_event_log SET event_classify = 630 WHERE event_classify = 120;
UPDATE user_event_log SET event_classify = 640 WHERE event_classify = 130;

UPDATE user_event_log SET event_type = 100010 WHERE event_type = 1005;
UPDATE user_event_log SET event_type = 100020 WHERE event_type = 1010;
UPDATE user_event_log SET event_type = 100030 WHERE event_type = 1015;
UPDATE user_event_log SET event_type = 110010 WHERE event_type = 1105;
UPDATE user_event_log SET event_type = 110020 WHERE event_type = 1110;
UPDATE user_event_log SET event_type = 110030 WHERE event_type = 1115;
UPDATE user_event_log SET event_type = 110040 WHERE event_type = 1120;
UPDATE user_event_log SET event_type = 110050 WHERE event_type = 1125;
UPDATE user_event_log SET event_type = 120010 WHERE event_type = 1205;
UPDATE user_event_log SET event_type = 120020 WHERE event_type = 1210;
UPDATE user_event_log SET event_type = 120030 WHERE event_type = 1215;
UPDATE user_event_log SET event_type = 200010 WHERE event_type = 2005;
UPDATE user_event_log SET event_type = 200020 WHERE event_type = 2010;
UPDATE user_event_log SET event_type = 200030 WHERE event_type = 2015;
UPDATE user_event_log SET event_type = 200040 WHERE event_type = 2020;
UPDATE user_event_log SET event_type = 200050 WHERE event_type = 2025;
UPDATE user_event_log SET event_type = 210010 WHERE event_type = 2105;
UPDATE user_event_log SET event_type = 210020 WHERE event_type = 2110;
UPDATE user_event_log SET event_type = 220010 WHERE event_type = 2205;
UPDATE user_event_log SET event_type = 220020 WHERE event_type = 2210;
UPDATE user_event_log SET event_type = 220030 WHERE event_type = 2215;
UPDATE user_event_log SET event_type = 220040 WHERE event_type = 2220;
UPDATE user_event_log SET event_type = 220050 WHERE event_type = 2225;
UPDATE user_event_log SET event_type = 220060 WHERE event_type = 2230;
UPDATE user_event_log SET event_type = 220070 WHERE event_type = 2235;
UPDATE user_event_log SET event_type = 220080 WHERE event_type = 2240;
UPDATE user_event_log SET event_type = 230010 WHERE event_type = 2305;
UPDATE user_event_log SET event_type = 230020 WHERE event_type = 2310;
UPDATE user_event_log SET event_type = 230030 WHERE event_type = 2315;
UPDATE user_event_log SET event_type = 240010 WHERE event_type = 2350;
UPDATE user_event_log SET event_type = 240020 WHERE event_type = 2355;
UPDATE user_event_log SET event_type = 250010 WHERE event_type = 2370;
UPDATE user_event_log SET event_type = 250020 WHERE event_type = 2375;
UPDATE user_event_log SET event_type = 250030 WHERE event_type = 2380;
UPDATE user_event_log SET event_type = 260010 WHERE event_type = 2400;
UPDATE user_event_log SET event_type = 260020 WHERE event_type = 2405;
UPDATE user_event_log SET event_type = 260030 WHERE event_type = 2410;
UPDATE user_event_log SET event_type = 260040 WHERE event_type = 2415;
UPDATE user_event_log SET event_type = 270010 WHERE event_type = 2500;
UPDATE user_event_log SET event_type = 270020 WHERE event_type = 2505;
UPDATE user_event_log SET event_type = 270030 WHERE event_type = 2510;
UPDATE user_event_log SET event_type = 270040 WHERE event_type = 2515;
UPDATE user_event_log SET event_type = 270050 WHERE event_type = 2520;
UPDATE user_event_log SET event_type = 270060 WHERE event_type = 2525;
UPDATE user_event_log SET event_type = 270070 WHERE event_type = 2530;
UPDATE user_event_log SET event_type = 270080 WHERE event_type = 2535;
UPDATE user_event_log SET event_type = 270090 WHERE event_type = 2540;
UPDATE user_event_log SET event_type = 270100 WHERE event_type = 2545;
UPDATE user_event_log SET event_type = 270110 WHERE event_type = 2550;
UPDATE user_event_log SET event_type = 270120 WHERE event_type = 2555;
UPDATE user_event_log SET event_type = 300010 WHERE event_type = 2605;
UPDATE user_event_log SET event_type = 300020 WHERE event_type = 2610;
UPDATE user_event_log SET event_type = 300030 WHERE event_type = 2615;
UPDATE user_event_log SET event_type = 310010 WHERE event_type = 2705;
UPDATE user_event_log SET event_type = 310020 WHERE event_type = 2710;
UPDATE user_event_log SET event_type = 310030 WHERE event_type = 2715;
UPDATE user_event_log SET event_type = 310040 WHERE event_type = 2720;
UPDATE user_event_log SET event_type = 320010 WHERE event_type = 7105;
UPDATE user_event_log SET event_type = 320020 WHERE event_type = 7110;
UPDATE user_event_log SET event_type = 320030 WHERE event_type = 7115;
UPDATE user_event_log SET event_type = 320040 WHERE event_type = 7120;
UPDATE user_event_log SET event_type = 320050 WHERE event_type = 7125;
UPDATE user_event_log SET event_type = 320060 WHERE event_type = 7130;
UPDATE user_event_log SET event_type = 320070 WHERE event_type = 7135;
UPDATE user_event_log SET event_type = 320080 WHERE event_type = 7140;
UPDATE user_event_log SET event_type = 320090 WHERE event_type = 7145;
UPDATE user_event_log SET event_type = 400010 WHERE event_type = 3005;
UPDATE user_event_log SET event_type = 400020 WHERE event_type = 3010;
UPDATE user_event_log SET event_type = 400030 WHERE event_type = 3015;
UPDATE user_event_log SET event_type = 400040 WHERE event_type = 3020;
UPDATE user_event_log SET event_type = 400050 WHERE event_type = 3025;
UPDATE user_event_log SET event_type = 400060 WHERE event_type = 3030;
UPDATE user_event_log SET event_type = 410010 WHERE event_type = 3105;
UPDATE user_event_log SET event_type = 410020 WHERE event_type = 3110;
UPDATE user_event_log SET event_type = 410030 WHERE event_type = 3115;
UPDATE user_event_log SET event_type = 420010 WHERE event_type = 3205;
UPDATE user_event_log SET event_type = 420020 WHERE event_type = 3210;
UPDATE user_event_log SET event_type = 430010 WHERE event_type = 3305;
UPDATE user_event_log SET event_type = 430020 WHERE event_type = 3310;
UPDATE user_event_log SET event_type = 430030 WHERE event_type = 3315;
UPDATE user_event_log SET event_type = 430040 WHERE event_type = 3320;
UPDATE user_event_log SET event_type = 430050 WHERE event_type = 3325;
UPDATE user_event_log SET event_type = 430060 WHERE event_type = 3330;
UPDATE user_event_log SET event_type = 440010 WHERE event_type = 4005;
UPDATE user_event_log SET event_type = 440020 WHERE event_type = 4010;
UPDATE user_event_log SET event_type = 440030 WHERE event_type = 4015;
UPDATE user_event_log SET event_type = 440040 WHERE event_type = 4020;
UPDATE user_event_log SET event_type = 450010 WHERE event_type = 5005;
UPDATE user_event_log SET event_type = 450020 WHERE event_type = 5010;
UPDATE user_event_log SET event_type = 450030 WHERE event_type = 5015;
UPDATE user_event_log SET event_type = 450040 WHERE event_type = 5020;
UPDATE user_event_log SET event_type = 450050 WHERE event_type = 5025;
UPDATE user_event_log SET event_type = 450060 WHERE event_type = 5030;
UPDATE user_event_log SET event_type = 450070 WHERE event_type = 5035;
UPDATE user_event_log SET event_type = 450080 WHERE event_type = 5040;
UPDATE user_event_log SET event_type = 450090 WHERE event_type = 5045;
UPDATE user_event_log SET event_type = 450100 WHERE event_type = 5050;
UPDATE user_event_log SET event_type = 450110 WHERE event_type = 5055;
UPDATE user_event_log SET event_type = 460010 WHERE event_type = 5505;
UPDATE user_event_log SET event_type = 460020 WHERE event_type = 5510;
UPDATE user_event_log SET event_type = 460030 WHERE event_type = 5515;
UPDATE user_event_log SET event_type = 460040 WHERE event_type = 5605;
UPDATE user_event_log SET event_type = 460050 WHERE event_type = 5610;
UPDATE user_event_log SET event_type = 460060 WHERE event_type = 5615;
UPDATE user_event_log SET event_type = 460070 WHERE event_type = 5620;
UPDATE user_event_log SET event_type = 460080 WHERE event_type = 5625;
UPDATE user_event_log SET event_type = 460090 WHERE event_type = 5630;
UPDATE user_event_log SET event_type = 460100 WHERE event_type = 5635;
UPDATE user_event_log SET event_type = 460110 WHERE event_type = 5640;
UPDATE user_event_log SET event_type = 460120 WHERE event_type = 5645;
UPDATE user_event_log SET event_type = 460130 WHERE event_type = 5650;
UPDATE user_event_log SET event_type = 500010 WHERE event_type = 2805;
UPDATE user_event_log SET event_type = 500020 WHERE event_type = 2810;
UPDATE user_event_log SET event_type = 500030 WHERE event_type = 2815;
UPDATE user_event_log SET event_type = 510010 WHERE event_type = 2905;
UPDATE user_event_log SET event_type = 510020 WHERE event_type = 2910;
UPDATE user_event_log SET event_type = 510030 WHERE event_type = 2915;
UPDATE user_event_log SET event_type = 600010 WHERE event_type = 6105;
UPDATE user_event_log SET event_type = 600020 WHERE event_type = 6110;
UPDATE user_event_log SET event_type = 600030 WHERE event_type = 6115;
UPDATE user_event_log SET event_type = 600040 WHERE event_type = 6120;
UPDATE user_event_log SET event_type = 610010 WHERE event_type = 6005;
UPDATE user_event_log SET event_type = 610020 WHERE event_type = 6010;
UPDATE user_event_log SET event_type = 610030 WHERE event_type = 6015;
UPDATE user_event_log SET event_type = 610040 WHERE event_type = 6020;
UPDATE user_event_log SET event_type = 620010 WHERE event_type = 8105;
UPDATE user_event_log SET event_type = 620020 WHERE event_type = 8110;
UPDATE user_event_log SET event_type = 620030 WHERE event_type = 8115;
UPDATE user_event_log SET event_type = 620040 WHERE event_type = 8120;
UPDATE user_event_log SET event_type = 620050 WHERE event_type = 8125;
UPDATE user_event_log SET event_type = 620060 WHERE event_type = 8130;
UPDATE user_event_log SET event_type = 620070 WHERE event_type = 8140;
UPDATE user_event_log SET event_type = 630010 WHERE event_type = 8305;
UPDATE user_event_log SET event_type = 630020 WHERE event_type = 8310;
UPDATE user_event_log SET event_type = 630030 WHERE event_type = 8315;
UPDATE user_event_log SET event_type = 630040 WHERE event_type = 8320;
UPDATE user_event_log SET event_type = 630050 WHERE event_type = 8325;
UPDATE user_event_log SET event_type = 630060 WHERE event_type = 8330;
UPDATE user_event_log SET event_type = 630070 WHERE event_type = 8335;
UPDATE user_event_log SET event_type = 640010 WHERE event_type = 8505;
UPDATE user_event_log SET event_type = 640020 WHERE event_type = 8510;
UPDATE user_event_log SET event_type = 640030 WHERE event_type = 8515;
UPDATE user_event_log SET event_type = 640040 WHERE event_type = 8520;
UPDATE user_event_log SET event_type = 640050 WHERE event_type = 8550;
UPDATE user_event_log SET event_type = 640060 WHERE event_type = 8555;
UPDATE user_event_log SET event_type = 640070 WHERE event_type = 8560;
UPDATE user_event_log SET event_type = 640080 WHERE event_type = 8605;
UPDATE user_event_log SET event_type = 640090 WHERE event_type = 8615;
 
UPDATE user_event_log SET event_type = 620010 WHERE event_type IN (620020, 620030, 620040, 620050, 620060, 620070);
UPDATE user_event_log SET event_type = 630010 WHERE event_type IN (630020, 630030, 630040, 630050, 630060, 630070);
UPDATE user_event_log SET event_type = 640010 WHERE event_type IN (640020, 640030, 640040, 640050, 640060, 640070, 640080, 640090);
```

## 1.2.0-beta

> sql 脚本

```sql
CREATE TABLE `machine_monitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `machine_id` bigint DEFAULT NULL COMMENT '机器id',
  `monitor_status` tinyint DEFAULT '1' COMMENT '监控状态 1未启动 2启动中 3运行中',
  `monitor_url` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '请求 api url',
  `access_token` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '请求 api accessToken',
  `agent_version` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '插件版本',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='机器监控配置表';

CREATE TABLE `webhook_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `webhook_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '名称',
  `webhook_url` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'url',
  `webhook_type` int DEFAULT NULL COMMENT '类型 10: 钉钉机器人',
  `webhook_config` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '配置项 json',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='webhook 配置';

CREATE TABLE `alarm_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报警组名称',
  `group_description` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报警组描述',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='报警组';

CREATE TABLE `alarm_group_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint DEFAULT NULL COMMENT '报警组id',
  `user_id` bigint DEFAULT NULL COMMENT '报警组成员id',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报警组成员用户名',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id_idx` (`group_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='报警组成员';

CREATE TABLE `alarm_group_notify` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint DEFAULT NULL COMMENT '报警组id',
  `notify_id` bigint DEFAULT NULL COMMENT '通知id',
  `notify_type` int DEFAULT NULL COMMENT '通知类型 10 webhook',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_idx` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='报警组通知方式';

CREATE TABLE `machine_alarm_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `machine_id` bigint DEFAULT NULL COMMENT '机器id',
  `alarm_type` int DEFAULT NULL COMMENT '报警类型 10: cpu使用率 20: 内存使用率',
  `alarm_threshold` double DEFAULT NULL COMMENT '报警阈值',
  `trigger_threshold` int DEFAULT NULL COMMENT '触发报警阈值 次',
  `notify_silence` int DEFAULT NULL COMMENT '报警通知沉默时间 分',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `machine_idx` (`machine_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='机器报警配置';

CREATE TABLE `machine_alarm_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `machine_id` bigint DEFAULT NULL COMMENT '机器id',
  `group_id` bigint DEFAULT NULL COMMENT ' 报警组id',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `config_idx` (`machine_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='机器报警通知组';

CREATE TABLE `machine_alarm_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `machine_id` bigint DEFAULT NULL COMMENT '机器id',
  `alarm_type` int DEFAULT NULL COMMENT '报警类型 10: cpu使用率 20: 内存使用率',
  `alarm_value` double DEFAULT NULL COMMENT '报警值',
  `alarm_time` datetime(4) DEFAULT NULL COMMENT '报警时间',
  `deleted` tinyint DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `machine_idx` (`machine_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='机器报警历史';

ALTER TABLE `machine_terminal_log` 
CHANGE COLUMN `operate_log_file` `screen_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '录屏文件路径' AFTER `close_code`;
```

## 1.1.4

> 应用发布配置

出于安全考虑以及防止误操作 **移除**了应用发布配置中传输方式选择 `SFTP` 时自动删除原文件的特性, 请检查应用配置!

> nginx 配置

出于安全考虑, 修改了后端跨域配置, 升级时需要修改 nginx 配置 [如何修改](/quickstart/install.md?id=修改-nginx-配置)

> sql 脚本

```sql
ALTER TABLE `machine_terminal` 
DROP COLUMN `enable_web_gl`;

RENAME TABLE `application_vcs` TO `application_repository`;

ALTER TABLE `application_repository` 
CHANGE COLUMN `vcs_name` `repo_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称' AFTER `id`,
CHANGE COLUMN `vcs_description` `repo_description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述' AFTER `repo_name`,
CHANGE COLUMN `vcs_type` `repo_type` tinyint(4) NULL DEFAULT NULL COMMENT '类型 1git' AFTER `repo_description`,
CHANGE COLUMN `vsc_url` `repo_url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url' AFTER `repo_type`,
CHANGE COLUMN `vsc_username` `repo_username` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名' AFTER `repo_url`,
CHANGE COLUMN `vcs_password` `repo_password` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码' AFTER `repo_username`,
CHANGE COLUMN `vcs_private_token` `repo_private_token` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'token' AFTER `repo_password`,
CHANGE COLUMN `vcs_status` `repo_status` int(11) NULL DEFAULT NULL COMMENT '状态 10未初始化 20初始化中 30正常 40失败' AFTER `repo_private_token`,
CHANGE COLUMN `vcs_auth_type` `repo_auth_type` int(11) NULL DEFAULT 10 COMMENT '认证类型 10密码 20令牌' AFTER `repo_status`,
CHANGE COLUMN `vcs_token_type` `repo_token_type` int(11) NULL DEFAULT NULL COMMENT '令牌类型 10github 20gitee 30gitlab' AFTER `repo_auth_type`,
COMMENT = '应用版本仓库';

ALTER TABLE `application_build` 
CHANGE COLUMN `vcs_id` `repo_id` bigint(20) NULL DEFAULT NULL COMMENT '版本仓库id' AFTER `commit_id`;

ALTER TABLE `application_info` 
CHANGE COLUMN `vcs_id` `repo_id` bigint(20) NULL DEFAULT NULL COMMENT '版本仓库id' AFTER `app_sort`;

UPDATE system_env SET attr_key = 'repo_path' WHERE attr_key = 'vcs_path';

UPDATE application_action a SET action_command = REPLACE (a.action_command, 'vcs', 'repo');
```

## 1.1.3

> sql 脚本

```sql
ALTER TABLE `application_info` 
MODIFY COLUMN `vcs_id` bigint(20) NULL DEFAULT NULL COMMENT '应用版本仓库id' AFTER `app_sort`;

ALTER TABLE `application_build` 
MODIFY COLUMN `vcs_id` bigint(20) NULL DEFAULT NULL COMMENT '应用版本仓库id' AFTER `commit_id`;

ALTER TABLE `application_vcs` COMMENT = '应用版本仓库';

ALTER TABLE `application_release` COMMENT = '发布任务';

ALTER TABLE `application_release_machine` 
MODIFY COLUMN `release_id` bigint(20) NULL DEFAULT NULL COMMENT '发布任务id' AFTER `id`,
COMMENT = '发布任务机器表';
```

## 1.1.2

> sql 脚本

```sql
ALTER TABLE `machine_info` 
MODIFY COLUMN `machine_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识' AFTER `machine_name`;

ALTER TABLE `application_info` 
MODIFY COLUMN `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称' AFTER `id`,
MODIFY COLUMN `app_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识' AFTER `app_name`;

ALTER TABLE `application_profile` 
MODIFY COLUMN `profile_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称' AFTER `id`,
MODIFY COLUMN `profile_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识' AFTER `profile_name`;

ALTER TABLE `machine_proxy` 
MODIFY COLUMN `proxy_host` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理主机' AFTER `id`;

ALTER TABLE `user_event_log` 
ADD COLUMN `deleted` tinyint(4) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除' AFTER `exec_result`;

UPDATE user_event_log SET deleted = 1;

UPDATE machine_env SET attr_value = "tail -f -n @{offset} '@{file}'" WHERE attr_value = "tail -f -n @{offset} @{file}";

UPDATE application_action SET action_command = 'scp "@{bundle_path}" @{target_username}@@{target_host}:"@{transfer_path}"' WHERE action_command = "scp @{bundle_path} @{target_username}@@{target_host}:@{transfer_path}";

ALTER TABLE `application_build` 
MODIFY COLUMN `app_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识' AFTER `app_name`,
MODIFY COLUMN `profile_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识' AFTER `profile_name`;

ALTER TABLE `application_pipeline_task` 
MODIFY COLUMN `profile_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识' AFTER `profile_name`;

ALTER TABLE `application_pipeline_task_detail` 
MODIFY COLUMN `app_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识' AFTER `app_name`;

ALTER TABLE `application_release` 
MODIFY COLUMN `app_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识' AFTER `app_name`,
MODIFY COLUMN `profile_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称' AFTER `profile_id`,
MODIFY COLUMN `profile_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识' AFTER `profile_name`;

ALTER TABLE `application_release_machine` 
MODIFY COLUMN `machine_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识' AFTER `machine_name`;

ALTER TABLE `command_exec` 
MODIFY COLUMN `machine_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识' AFTER `machine_host`;

ALTER TABLE `machine_terminal_log` 
MODIFY COLUMN `machine_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '机器唯一标识' AFTER `machine_host`;

ALTER TABLE `scheduler_task_machine_record` 
MODIFY COLUMN `machine_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识' AFTER `machine_host`;
```

## 1.1.0

> sql 脚本

```sql
ALTER TABLE `application_action` 
MODIFY COLUMN `action_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称' AFTER `profile_id`;

ALTER TABLE `application_action_log` 
MODIFY COLUMN `action_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作名称' AFTER `action_id`;

ALTER TABLE `machine_env` 
MODIFY COLUMN `attr_value` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value' AFTER `attr_key`;

ALTER TABLE `application_env` 
MODIFY COLUMN `attr_value` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value' AFTER `attr_key`;

ALTER TABLE `history_value_snapshot` 
MODIFY COLUMN `before_value` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始值' AFTER `value_type`,
MODIFY COLUMN `after_value` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新值' AFTER `before_value`;

ALTER TABLE `application_action` 
MODIFY COLUMN `action_command` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行命令' AFTER `stage_type`;

ALTER TABLE `application_action_log` 
MODIFY COLUMN `action_command` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作命令' AFTER `action_type`;

ALTER TABLE `application_release` 
MODIFY COLUMN `bundle_path` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建产物文件' AFTER `exception_handler`,
MODIFY COLUMN `transfer_path` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产物传输路径' AFTER `bundle_path`;

ALTER TABLE `application_build` 
MODIFY COLUMN `bundle_path` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建产物路径' AFTER `log_path`;

ALTER TABLE `command_exec` 
MODIFY COLUMN `exec_command` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '命令' AFTER `exit_code`;

ALTER TABLE `application_build` 
MODIFY COLUMN `log_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建日志路径' AFTER `vcs_id`;

ALTER TABLE `file_tail_list` 
MODIFY COLUMN `file_path` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径' AFTER `alias_name`,
MODIFY COLUMN `tail_command` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'tail 命令' AFTER `file_offset`;

ALTER TABLE `file_transfer_log` 
MODIFY COLUMN `remote_file` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '远程文件' AFTER `machine_id`;

ALTER TABLE `machine_terminal` 
MODIFY COLUMN `font_family` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字体名称' AFTER `font_size`;

ALTER TABLE `web_side_message` 
MODIFY COLUMN `params_json` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息参数' AFTER `send_message`;

INSERT INTO machine_env (machine_id, attr_key, attr_value, description, deleted) 
SELECT m.id, 'connect_timeout', '3000', '连接超时时间 (ms)', m.deleted FROM machine_info m;

INSERT INTO machine_env (machine_id, attr_key, attr_value, description, deleted) 
SELECT m.id, 'connect_retry_times', '2', '连接失败重试次数', m.deleted FROM machine_info m;

INSERT INTO system_env (attr_key, attr_value, system_env, description, deleted) 
VALUES ('tracker_delay_time', '250', 2, '文件追踪器延迟时间 (ms)', 1);

ALTER TABLE `file_tail_list` 
MODIFY COLUMN `tail_mode` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'tracker' COMMENT '宿主机文件追踪类型 tracker/tail' AFTER `tail_command`;

UPDATE file_tail_list SET tail_mode = 'tracker' WHERE tail_mode = 'tacker';

UPDATE application_action SET action_command = 'scp @{bundle_path} @{target_username}@@{target_host}:@{transfer_path}' WHERE action_type = 210;

INSERT INTO application_env (app_id, profile_id, attr_key, attr_value, system_env, description, deleted) 
SELECT app.id, p.id, 'transfer_mode', 'sftp', 2, '产物传输方式 (sftp/scp)', app.deleted FROM application_profile p, application_info app;

ALTER TABLE `application_release` 
ADD COLUMN `transfer_mode` char(8) NULL DEFAULT 'sftp' COMMENT '产物传输方式 sftp scp' AFTER `transfer_path`;

UPDATE application_release SET transfer_mode = 'sftp' WHERE transfer_mode IS NULL;

UPDATE application_env SET attr_key = 'transfer_file_type', description = '产物传输文件类型 (normal/zip)' WHERE attr_key = 'transfer_dir_type';

UPDATE application_env SET attr_value = 'normal' WHERE attr_key = 'transfer_file_type' AND attr_value = 'dir';
```

## 1.1.0-beta

> sql 脚本

```sql
ALTER TABLE `web_side_message` 
MODIFY COLUMN `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间' AFTER `deleted`,
MODIFY COLUMN `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间' AFTER `create_time`;

ALTER TABLE `application_action_log` 
MODIFY COLUMN `run_status` tinyint(4) NULL DEFAULT 10 COMMENT '状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止' AFTER `log_path`;

CREATE TABLE `application_pipeline_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pipeline_id` bigint(20) NULL DEFAULT NULL COMMENT '流水线id',
  `pipeline_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流水线名称',
  `profile_id` bigint(20) NULL DEFAULT NULL COMMENT '环境id',
  `profile_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
  `profile_tag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境tag',
  `exec_title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行标题',
  `exec_description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行描述',
  `exec_status` int(11) NULL DEFAULT NULL COMMENT '执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败',
  `timed_exec` tinyint(4) NULL DEFAULT NULL COMMENT '是否是定时执行 10普通执行 20定时执行',
  `timed_exec_time` datetime(4) NULL DEFAULT NULL COMMENT '定时执行时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
  `create_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `audit_user_id` bigint(20) NULL DEFAULT NULL COMMENT '审核人id',
  `audit_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人名称',
  `audit_time` datetime(4) NULL DEFAULT NULL COMMENT '审核时间',
  `audit_reason` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核备注',
  `exec_user_id` bigint(20) NULL DEFAULT NULL COMMENT '执行人id',
  `exec_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行人名称',
  `exec_start_time` datetime(4) NULL DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime(4) NULL DEFAULT NULL COMMENT '执行结束时间',
  `deleted` tinyint(4) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pipeline_id_idx`(`pipeline_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线任务' ROW_FORMAT = Dynamic;

CREATE TABLE `application_pipeline_task_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pipeline_id` bigint(20) NULL DEFAULT NULL COMMENT '流水线id',
  `pipeline_detail_id` bigint(20) NULL DEFAULT NULL COMMENT '流水线详情id',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '流水线任务id',
  `rel_id` bigint(20) NULL DEFAULT NULL COMMENT '引用id',
  `app_id` bigint(20) NULL DEFAULT NULL COMMENT '应用id',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  `app_tag` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用tag',
  `stage_type` tinyint(4) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
  `stage_config` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阶段操作配置',
  `exec_status` int(11) NULL DEFAULT NULL COMMENT '状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止',
  `exec_start_time` datetime(4) NULL DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime(4) NULL DEFAULT NULL COMMENT '执行结束时间',
  `deleted` tinyint(4) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rel_id_idx`(`pipeline_id`, `pipeline_detail_id`, `task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线任务详情' ROW_FORMAT = Dynamic;

CREATE TABLE `application_pipeline_task_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '流水线任务id',
  `task_detail_id` bigint(20) NULL DEFAULT NULL COMMENT '流水线任务详情id',
  `log_status` tinyint(4) NULL DEFAULT NULL COMMENT '日志状态 10创建 20执行 30成功 40失败 50停止 60跳过',
  `stage_type` tinyint(4) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
  `log_info` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志详情',
  `deleted` tinyint(4) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `record_idx`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线任务日志' ROW_FORMAT = Dynamic;
```

## 1.0.1

> sql 脚本

```sql
CREATE TABLE `web_side_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `message_classify` tinyint(4) DEFAULT NULL COMMENT '消息分类',
  `message_type` int(11) DEFAULT NULL COMMENT '消息类型',
  `read_status` tinyint(4) DEFAULT '1' COMMENT '是否已读 1未读 2已读',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '收信人id',
  `to_user_name` varchar(64) DEFAULT NULL COMMENT '收信人名称',
  `send_message` text COMMENT '消息',
  `params_json` varchar(2048) DEFAULT NULL COMMENT '消息参数',
  `deleted` tinyint(4) DEFAULT '1' COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `to_user_id_idx` (`to_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统站内信';

ALTER TABLE `application_release` 
MODIFY COLUMN `release_status` tinyint(0) NULL DEFAULT NULL COMMENT '发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败' AFTER `release_type`;
```

## 1.0.0

> sql 脚本

```sql
INSERT INTO machine_env (machine_id, attr_key, attr_value, description, deleted) 
SELECT m.id, 'tail_default_command', 'tail -f -n @{offset} @{file}', '文件追踪默认命令', m.deleted FROM machine_info m;

ALTER TABLE `file_tail_list` 
ADD COLUMN `tail_mode` char(8) NULL DEFAULT 'tracker' COMMENT '宿主机文件追踪类型 tracker/tail' AFTER `tail_command`;

UPDATE file_tail_list SET tail_mode = 'tracker' WHERE machine_id = 1;
UPDATE file_tail_list SET tail_mode = 'tail' WHERE machine_id <> 1;
```
