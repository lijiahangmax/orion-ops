⚡ 注意: 应用不支持跨版本升级

## 1.0.1 > 1.1.0-beta

> sql 脚本

```sql
ALTER TABLE `orion-ops`.`web_side_message` 
MODIFY COLUMN `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间' AFTER `deleted`,
MODIFY COLUMN `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间' AFTER `create_time`;

ALTER TABLE `orion-ops`.`application_action_log` 
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

## 1.0.0 > 1.0.1

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

ALTER TABLE `orion-ops`.`application_release` 
MODIFY COLUMN `release_status` tinyint(0) NULL DEFAULT NULL COMMENT '发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败' AFTER `release_type`;
```

## 1.0.0-beta > 1.0.0

> sql 脚本

```sql
INSERT INTO machine_env ( machine_id, attr_key, attr_value, description, deleted ) 
SELECT m.id, 'tail_default_command', 'tail -f -n @{offset} @{file}', '文件追踪默认命令', m.deleted FROM machine_info m;

ALTER TABLE `orion-ops`.`file_tail_list` 
ADD COLUMN `tail_mode` char(8) NULL DEFAULT 'tacker' COMMENT '宿主机文件追踪类型 tacker/tail' AFTER `tail_command`;

UPDATE file_tail_list SET tail_mode = 'tacker' WHERE machine_id = 1;
UPDATE file_tail_list SET tail_mode = 'tail' WHERE machine_id <> 1;
```
