⚡ 注意: 应用不支持跨版本升级, 可以进行多次升级

## 1.1.4

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
