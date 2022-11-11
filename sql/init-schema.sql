SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alarm_group
-- ----------------------------
DROP TABLE IF EXISTS `alarm_group`;
CREATE TABLE `alarm_group`
(
    `id`                bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `group_name`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报警组名称',
    `group_description` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报警组描述',
    `deleted`           tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`       datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`       datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报警组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for alarm_group_notify
-- ----------------------------
DROP TABLE IF EXISTS `alarm_group_notify`;
CREATE TABLE `alarm_group_notify`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `group_id`    bigint(0) NULL DEFAULT NULL COMMENT '报警组id',
    `notify_id`   bigint(0) NULL DEFAULT NULL COMMENT '通知id',
    `notify_type` int(0) NULL DEFAULT NULL COMMENT '通知类型 10 webhook',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `group_idx`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报警组通知方式' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for alarm_group_user
-- ----------------------------
DROP TABLE IF EXISTS `alarm_group_user`;
CREATE TABLE `alarm_group_user`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `group_id`    bigint(0) NULL DEFAULT NULL COMMENT '报警组id',
    `user_id`     bigint(0) NULL DEFAULT NULL COMMENT '报警组成员id',
    `username`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报警组成员用户名',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `group_id_idx`(`group_id`, `user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报警组成员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_action
-- ----------------------------
DROP TABLE IF EXISTS `application_action`;
CREATE TABLE `application_action`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_id`         bigint(0) NULL DEFAULT NULL COMMENT 'appId',
    `profile_id`     bigint(0) NULL DEFAULT NULL COMMENT 'profileId',
    `action_name`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `action_type`    int(0) NULL DEFAULT NULL COMMENT '类型 110: 构建代码检出 120: 构建主机命令 210: 发布产物传输 220: 发布目标机器命令',
    `stage_type`     tinyint(0) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
    `action_command` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行命令',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `app_profile_idx`(`app_id`, `profile_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用构建发布执行块' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_action_log
-- ----------------------------
DROP TABLE IF EXISTS `application_action_log`;
CREATE TABLE `application_action_log`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `stage_type`     tinyint(0) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
    `rel_id`         bigint(0) NULL DEFAULT NULL COMMENT '引用id 构建id 发布机器id',
    `machine_id`     bigint(0) NULL DEFAULT NULL COMMENT '执行机器id',
    `action_id`      bigint(0) NULL DEFAULT NULL COMMENT '操作id',
    `action_name`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作名称',
    `action_type`    int(0) NULL DEFAULT NULL COMMENT '操作类型',
    `action_command` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作命令',
    `log_path`       varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作日志路径',
    `run_status`     tinyint(0) NULL DEFAULT 10 COMMENT '状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止',
    `exit_code`      int(0) NULL DEFAULT NULL COMMENT '退出码',
    `start_time`     datetime(4) NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`       datetime(4) NULL DEFAULT NULL COMMENT '结束时间',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `rel_id_idx`(`stage_type`, `rel_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_build
-- ----------------------------
DROP TABLE IF EXISTS `application_build`;
CREATE TABLE `application_build`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_id`           bigint(0) NULL DEFAULT NULL COMMENT '应用id',
    `app_name`         varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
    `app_tag`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识',
    `profile_id`       bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `profile_name`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
    `profile_tag`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识',
    `build_seq`        int(0) NULL DEFAULT NULL COMMENT '构建序列',
    `branch_name`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建分支',
    `commit_id`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建提交id',
    `repo_id`          bigint(0) NULL DEFAULT NULL COMMENT '版本仓库id',
    `log_path`         varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建日志路径',
    `bundle_path`      varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建产物路径',
    `build_status`     tinyint(0) NULL DEFAULT 10 COMMENT '状态 10未开始 20执行中 30已完成 40执行失败 50已取消',
    `description`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `create_user_id`   bigint(0) NULL DEFAULT NULL COMMENT '创建人id',
    `create_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
    `build_start_time` datetime(4) NULL DEFAULT NULL COMMENT '构建开始时间',
    `build_end_time`   datetime(4) NULL DEFAULT NULL COMMENT '构建结束时间',
    `deleted`          tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用构建' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_env
-- ----------------------------
DROP TABLE IF EXISTS `application_env`;
CREATE TABLE `application_env`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_id`      bigint(0) NULL DEFAULT NULL COMMENT '应用id',
    `profile_id`  bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `attr_key`    varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'key',
    `attr_value`  varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value',
    `system_env`  tinyint(0) NULL DEFAULT 2 COMMENT '是否为系统变量 1是 2否',
    `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `app_profile_idx`(`app_id`, `profile_id`, `attr_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用环境变量' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_info
-- ----------------------------
DROP TABLE IF EXISTS `application_info`;
CREATE TABLE `application_info`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_name`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
    `app_tag`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识',
    `app_sort`    int(0) NULL DEFAULT NULL COMMENT '排序',
    `repo_id`     bigint(0) NULL DEFAULT NULL COMMENT '版本仓库id',
    `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_machine
-- ----------------------------
DROP TABLE IF EXISTS `application_machine`;
CREATE TABLE `application_machine`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_id`      bigint(0) NULL DEFAULT NULL COMMENT '应用id',
    `profile_id`  bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `machine_id`  bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `release_id`  bigint(0) NULL DEFAULT NULL COMMENT '当前版本发布id',
    `build_id`    bigint(0) NULL DEFAULT NULL COMMENT '当前版本构建id',
    `build_seq`   int(0) NULL DEFAULT NULL COMMENT '当前版本构建序列',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `app_profile_idx`(`app_id`, `profile_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用依赖机器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_pipeline
-- ----------------------------
DROP TABLE IF EXISTS `application_pipeline`;
CREATE TABLE `application_pipeline`
(
    `id`            bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `profile_id`    bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `pipeline_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流水线名称',
    `description`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`       tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`   datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    `update_time`   datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_pipeline_detail
-- ----------------------------
DROP TABLE IF EXISTS `application_pipeline_detail`;
CREATE TABLE `application_pipeline_detail`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `pipeline_id` bigint(0) NULL DEFAULT NULL COMMENT '流水线id',
    `app_id`      bigint(0) NULL DEFAULT NULL COMMENT '应用id',
    `profile_id`  bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `stage_type`  tinyint(0) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `pipeline_id_idx`(`pipeline_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_pipeline_task
-- ----------------------------
DROP TABLE IF EXISTS `application_pipeline_task`;
CREATE TABLE `application_pipeline_task`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `pipeline_id`      bigint(0) NULL DEFAULT NULL COMMENT '流水线id',
    `pipeline_name`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流水线名称',
    `profile_id`       bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `profile_name`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
    `profile_tag`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识',
    `exec_title`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行标题',
    `exec_description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行描述',
    `exec_status`      int(0) NULL DEFAULT NULL COMMENT '执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败',
    `timed_exec`       tinyint(0) NULL DEFAULT NULL COMMENT '是否是定时执行 10普通执行 20定时执行',
    `timed_exec_time`  datetime(4) NULL DEFAULT NULL COMMENT '定时执行时间',
    `create_user_id`   bigint(0) NULL DEFAULT NULL COMMENT '创建人id',
    `create_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
    `audit_user_id`    bigint(0) NULL DEFAULT NULL COMMENT '审核人id',
    `audit_user_name`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人名称',
    `audit_time`       datetime(4) NULL DEFAULT NULL COMMENT '审核时间',
    `audit_reason`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核备注',
    `exec_user_id`     bigint(0) NULL DEFAULT NULL COMMENT '执行人id',
    `exec_user_name`   varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行人名称',
    `exec_start_time`  datetime(4) NULL DEFAULT NULL COMMENT '执行开始时间',
    `exec_end_time`    datetime(4) NULL DEFAULT NULL COMMENT '执行结束时间',
    `deleted`          tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `pipeline_id_idx`(`pipeline_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_pipeline_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `application_pipeline_task_detail`;
CREATE TABLE `application_pipeline_task_detail`
(
    `id`                 bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `pipeline_id`        bigint(0) NULL DEFAULT NULL COMMENT '流水线id',
    `pipeline_detail_id` bigint(0) NULL DEFAULT NULL COMMENT '流水线详情id',
    `task_id`            bigint(0) NULL DEFAULT NULL COMMENT '流水线任务id',
    `rel_id`             bigint(0) NULL DEFAULT NULL COMMENT '引用id',
    `app_id`             bigint(0) NULL DEFAULT NULL COMMENT '应用id',
    `app_name`           varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
    `app_tag`            varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识',
    `stage_type`         tinyint(0) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
    `stage_config`       varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阶段操作配置',
    `exec_status`        int(0) NULL DEFAULT NULL COMMENT '状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止',
    `exec_start_time`    datetime(4) NULL DEFAULT NULL COMMENT '执行开始时间',
    `exec_end_time`      datetime(4) NULL DEFAULT NULL COMMENT '执行结束时间',
    `deleted`            tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`        datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`        datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                `rel_id_idx`(`pipeline_id`, `pipeline_detail_id`, `task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线任务详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_pipeline_task_log
-- ----------------------------
DROP TABLE IF EXISTS `application_pipeline_task_log`;
CREATE TABLE `application_pipeline_task_log`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`        bigint(0) NULL DEFAULT NULL COMMENT '流水线任务id',
    `task_detail_id` bigint(0) NULL DEFAULT NULL COMMENT '流水线任务详情id',
    `log_status`     tinyint(0) NULL DEFAULT NULL COMMENT '日志状态 10创建 20执行 30成功 40失败 50停止 60跳过',
    `stage_type`     tinyint(0) NULL DEFAULT NULL COMMENT '阶段类型 10构建 20发布',
    `log_info`       varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志详情',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `record_idx`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用流水线任务日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_profile
-- ----------------------------
DROP TABLE IF EXISTS `application_profile`;
CREATE TABLE `application_profile`
(
    `id`            bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `profile_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
    `profile_tag`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识',
    `description`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境描述',
    `release_audit` tinyint(0) NULL DEFAULT 2 COMMENT '发布是否需要审核 1需要 2无需',
    `deleted`       tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`   datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`   datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用环境表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_release
-- ----------------------------
DROP TABLE IF EXISTS `application_release`;
CREATE TABLE `application_release`
(
    `id`                  bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `release_title`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布标题',
    `release_description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布描述',
    `build_id`            bigint(0) NULL DEFAULT NULL COMMENT '构建id',
    `build_seq`           int(0) NULL DEFAULT NULL COMMENT '构建序列',
    `app_id`              bigint(0) NULL DEFAULT NULL COMMENT '应用id',
    `app_name`            varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
    `app_tag`             varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用唯一标识',
    `profile_id`          bigint(0) NULL DEFAULT NULL COMMENT '环境id',
    `profile_name`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
    `profile_tag`         varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境唯一标识',
    `release_type`        tinyint(0) NULL DEFAULT 10 COMMENT '发布类型 10正常发布 20回滚发布',
    `release_status`      tinyint(0) NULL DEFAULT NULL COMMENT '发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败',
    `release_serialize`   tinyint(0) NULL DEFAULT 10 COMMENT '发布序列 10串行 20并行',
    `exception_handler`   tinyint(0) NULL DEFAULT 10 COMMENT '异常处理 10跳过所有 20跳过错误',
    `bundle_path`         varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建产物文件',
    `transfer_path`       varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产物传输路径',
    `transfer_mode`       char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'sftp' COMMENT '产物传输方式 sftp scp',
    `rollback_release_id` bigint(0) NULL DEFAULT NULL COMMENT '回滚发布id',
    `timed_release`       tinyint(0) NULL DEFAULT 10 COMMENT '是否是定时发布 10普通发布 20定时发布',
    `timed_release_time`  datetime(4) NULL DEFAULT NULL COMMENT '定时发布时间',
    `create_user_id`      bigint(0) NULL DEFAULT NULL COMMENT '创建人id',
    `create_user_name`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
    `audit_user_id`       bigint(0) NULL DEFAULT NULL COMMENT '审核人id',
    `audit_user_name`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人名称',
    `audit_time`          datetime(4) NULL DEFAULT NULL COMMENT '审核时间',
    `audit_reason`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核备注',
    `release_start_time`  datetime(4) NULL DEFAULT NULL COMMENT '发布开始时间',
    `release_end_time`    datetime(4) NULL DEFAULT NULL COMMENT '发布结束时间',
    `release_user_id`     bigint(0) NULL DEFAULT NULL COMMENT '发布人id',
    `release_user_name`   varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布人名称',
    `action_config`       text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '发布步骤json',
    `deleted`             tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`         datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`         datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '发布单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_release_machine
-- ----------------------------
DROP TABLE IF EXISTS `application_release_machine`;
CREATE TABLE `application_release_machine`
(
    `id`           bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `release_id`   bigint(0) NULL DEFAULT NULL COMMENT '上线单id',
    `machine_id`   bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `machine_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器名称',
    `machine_tag`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识',
    `machine_host` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器主机',
    `run_status`   tinyint(0) NULL DEFAULT 10 COMMENT '状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消',
    `log_path`     varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志路径',
    `start_time`   datetime(4) NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`     datetime(4) NULL DEFAULT NULL COMMENT '结束时间',
    `deleted`      tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`  datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`  datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `release_idx`(`release_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '发布单机器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for application_repository
-- ----------------------------
DROP TABLE IF EXISTS `application_repository`;
CREATE TABLE `application_repository`
(
    `id`                 bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `repo_name`          varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `repo_description`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `repo_type`          tinyint(0) NULL DEFAULT NULL COMMENT '类型 1git',
    `repo_url`           varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url',
    `repo_username`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
    `repo_password`      varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `repo_private_token` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'token',
    `repo_status`        int(0) NULL DEFAULT NULL COMMENT '状态 10未初始化 20初始化中 30正常 40失败',
    `repo_auth_type`     int(0) NULL DEFAULT 10 COMMENT '认证类型 10密码 20令牌',
    `repo_token_type`    int(0) NULL DEFAULT NULL COMMENT '令牌类型 10github 20gitee 30gitlab',
    `deleted`            tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`        datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`        datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用版本仓库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for command_exec
-- ----------------------------
DROP TABLE IF EXISTS `command_exec`;
CREATE TABLE `command_exec`
(
    `id`           bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`      bigint(0) NULL DEFAULT NULL COMMENT '用户id',
    `user_name`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名称',
    `exec_type`    int(0) NULL DEFAULT NULL COMMENT '执行类型 10批量执行',
    `machine_id`   bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `machine_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器名称',
    `machine_host` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器主机',
    `machine_tag`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识',
    `exec_status`  int(0) NULL DEFAULT NULL COMMENT '执行状态 10未开始 20执行中 30执行完成 40执行异常 50执行终止',
    `exit_code`    int(0) NULL DEFAULT NULL COMMENT '执行返回码',
    `exec_command` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '命令',
    `description`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `log_path`     varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志目录',
    `start_date`   datetime(4) NULL DEFAULT NULL COMMENT '执行开始时间',
    `end_date`     datetime(4) NULL DEFAULT NULL COMMENT '执行结束时间',
    `deleted`      tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`  datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`  datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `machine_idx`(`machine_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '命令执行表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for command_template
-- ----------------------------
DROP TABLE IF EXISTS `command_template`;
CREATE TABLE `command_template`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `template_name`    varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板名称',
    `template_value`   varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板命令',
    `description`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '命令描述',
    `create_user_id`   bigint(0) NULL DEFAULT NULL COMMENT '创建用户id',
    `create_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建用户名',
    `update_user_id`   bigint(0) NULL DEFAULT NULL COMMENT '修改用户id',
    `update_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改用户名',
    `deleted`          tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '命令模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_tail_list
-- ----------------------------
DROP TABLE IF EXISTS `file_tail_list`;
CREATE TABLE `file_tail_list`
(
    `id`           bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`   bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `alias_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
    `file_path`    varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径',
    `file_charset` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'UTF-8' COMMENT '文件编码',
    `file_offset`  int(0) NULL DEFAULT NULL COMMENT '文件偏移量',
    `tail_command` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'tail 命令',
    `tail_mode`    char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'tracker' COMMENT '宿主机文件追踪类型 tracker/tail',
    `deleted`      tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`  datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`  datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件tail表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_transfer_log
-- ----------------------------
DROP TABLE IF EXISTS `file_transfer_log`;
CREATE TABLE `file_transfer_log`
(
    `id`            bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`       bigint(0) NULL DEFAULT NULL COMMENT '用户id',
    `user_name`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
    `file_token`    varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件token',
    `transfer_type` tinyint(0) NULL DEFAULT NULL COMMENT '传输类型 10上传 20下载 30传输 40打包',
    `machine_id`    bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `remote_file`   varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '远程文件',
    `local_file`    varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本机文件',
    `current_size`  bigint(0) NULL DEFAULT NULL COMMENT '当前传输大小',
    `file_size`     bigint(0) NULL DEFAULT NULL COMMENT '文件大小',
    `now_progress`  double(5, 2
) NULL DEFAULT NULL COMMENT '当前进度',
  `transfer_status` tinyint(0) NULL DEFAULT NULL COMMENT '传输状态 10未开始 20进行中 30已暂停 40已完成 50已取消 60传输异常',
  `deleted` tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
  `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) COMMENT '创建时间',
  `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `token_unidx`(`file_token`) USING BTREE,
  INDEX `user_machine_idx`(`user_id`, `machine_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'sftp传输日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for history_value_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `history_value_snapshot`;
CREATE TABLE `history_value_snapshot`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `value_id`         bigint(0) NULL DEFAULT NULL COMMENT '值id',
    `value_type`       tinyint(0) NULL DEFAULT NULL COMMENT '值类型 10机器环境变量 20应用环境变量',
    `before_value`     varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始值',
    `after_value`      varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新值',
    `operator_type`    tinyint(0) NULL DEFAULT NULL COMMENT '操作类型 1新增 2修改 3删除',
    `update_user_id`   bigint(0) NULL DEFAULT NULL COMMENT '修改人id',
    `update_user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人用户名',
    `create_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `value_idx`(`value_id`, `value_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '历史值快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `machine_alarm_config`;
CREATE TABLE `machine_alarm_config`
(
    `id`                bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`        bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `alarm_type`        int(0) NULL DEFAULT NULL COMMENT '报警类型 10: cpu使用率 20: 内存使用率',
    `alarm_threshold`   double NULL DEFAULT NULL COMMENT '报警阈值',
    `trigger_threshold` int(0) NULL DEFAULT NULL COMMENT '触发报警阈值 次',
    `notify_silence`    int(0) NULL DEFAULT NULL COMMENT '报警通知沉默时间 分',
    `deleted`           tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`       datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`       datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX               `machine_idx`(`machine_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器报警配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_alarm_group
-- ----------------------------
DROP TABLE IF EXISTS `machine_alarm_group`;
CREATE TABLE `machine_alarm_group`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`  bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `group_id`    bigint(0) NULL DEFAULT NULL COMMENT ' 报警组id',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `config_idx`(`machine_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器报警通知组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_alarm_history
-- ----------------------------
DROP TABLE IF EXISTS `machine_alarm_history`;
CREATE TABLE `machine_alarm_history`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`  bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `alarm_type`  int(0) NULL DEFAULT NULL COMMENT '报警类型 10: cpu使用率 20: 内存使用率',
    `alarm_value` double NULL DEFAULT NULL COMMENT '报警值',
    `alarm_time`  datetime(4) NULL DEFAULT NULL COMMENT '报警时间',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `machine_idx`(`machine_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器报警历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_env
-- ----------------------------
DROP TABLE IF EXISTS `machine_env`;
CREATE TABLE `machine_env`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`  bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `attr_key`    varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'key',
    `attr_value`  varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value',
    `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `machine_key_index`(`machine_id`, `attr_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器环境变量' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_group
-- ----------------------------
DROP TABLE IF EXISTS `machine_group`;
CREATE TABLE `machine_group`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id`   bigint(0) NULL DEFAULT NULL COMMENT '父id',
    `group_name`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组名称',
    `sort`        int(0) NULL DEFAULT 0 COMMENT '排序',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `parent_idx`(`parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_group_rel
-- ----------------------------
DROP TABLE IF EXISTS `machine_group_rel`;
CREATE TABLE `machine_group_rel`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `group_id`    bigint(0) NULL DEFAULT NULL COMMENT '组id',
    `machine_id`  bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `group_idx`(`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器分组关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_info
-- ----------------------------
DROP TABLE IF EXISTS `machine_info`;
CREATE TABLE `machine_info`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `proxy_id`       bigint(0) NULL DEFAULT NULL COMMENT '代理id',
    `key_id`         bigint(0) NULL DEFAULT NULL COMMENT '秘钥id',
    `machine_host`   varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机ip',
    `ssh_port`       int(0) NULL DEFAULT 22 COMMENT 'ssh端口',
    `machine_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器名称',
    `machine_tag`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识',
    `description`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器描述',
    `username`       varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器账号',
    `password`       varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器密码',
    `auth_type`      tinyint(1) NULL DEFAULT NULL COMMENT '机器认证方式 1: 密码认证 2: 独立秘钥',
    `machine_status` tinyint(1) NULL DEFAULT 1 COMMENT '机器状态 1有效 2无效',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `host_idx`(`machine_host`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for machine_monitor
-- ----------------------------
DROP TABLE IF EXISTS `machine_monitor`;
CREATE TABLE `machine_monitor`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`     bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `monitor_status` tinyint(0) NULL DEFAULT 1 COMMENT '监控状态 1未启动 2启动中 3运行中',
    `monitor_url`    varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求 api url',
    `access_token`   varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求 api accessToken',
    `agent_version`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '插件版本',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器监控配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_proxy
-- ----------------------------
DROP TABLE IF EXISTS `machine_proxy`;
CREATE TABLE `machine_proxy`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `proxy_host`     varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理主机',
    `proxy_port`     int(0) NULL DEFAULT NULL COMMENT '代理端口',
    `proxy_username` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理用户名',
    `proxy_password` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代理密码',
    `proxy_type`     int(0) NULL DEFAULT NULL COMMENT '代理类型 1http代理 2socket4代理 3socket5代理',
    `description`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器代理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_secret_key
-- ----------------------------
DROP TABLE IF EXISTS `machine_secret_key`;
CREATE TABLE `machine_secret_key`
(
    `id`              bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `key_name`        varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '秘钥名称',
    `secret_key_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '秘钥文件本地路径',
    `password`        varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '秘钥密码',
    `description`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`         tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`     datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`     datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器ssh登陆秘钥' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_terminal
-- ----------------------------
DROP TABLE IF EXISTS `machine_terminal`;
CREATE TABLE `machine_terminal`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `machine_id`       bigint(0) NOT NULL COMMENT '机器id',
    `terminal_type`    varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'xterm' COMMENT '终端类型',
    `background_color` char(7) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '背景色',
    `font_color`       char(7) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字体颜色',
    `font_size`        int(0) NULL DEFAULT 14 COMMENT '字体大小',
    `font_family`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字体名称',
    `enable_web_link`  tinyint(0) NULL DEFAULT 2 COMMENT '是否开启url link 1开启 2关闭',
    `deleted`          tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `machine_idx`(`machine_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器终端配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for machine_terminal_log
-- ----------------------------
DROP TABLE IF EXISTS `machine_terminal_log`;
CREATE TABLE `machine_terminal_log`
(
    `id`                bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`           bigint(0) NULL DEFAULT NULL COMMENT '用户id',
    `username`          varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'username',
    `machine_id`        bigint(0) NULL DEFAULT NULL COMMENT '机器id',
    `machine_name`      varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器名称',
    `machine_host`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器host',
    `machine_tag`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识',
    `access_token`      varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'token',
    `connected_time`    datetime(4) NULL DEFAULT NULL COMMENT '建立连接时间',
    `disconnected_time` datetime(4) NULL DEFAULT NULL COMMENT '断开连接时间',
    `close_code`        int(0) NULL DEFAULT NULL COMMENT 'close code',
    `screen_path`       varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '录屏文件路径',
    `create_time`       datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`       datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机器终端操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for scheduler_task
-- ----------------------------
DROP TABLE IF EXISTS `scheduler_task`;
CREATE TABLE `scheduler_task`
(
    `id`                   bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_name`            varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名称',
    `expression`           varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
    `task_command`         varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行命令',
    `description`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务描述',
    `enable_status`        tinyint(0) NULL DEFAULT 2 COMMENT '启用状态 1启用 2停用',
    `lately_status`        tinyint(0) NULL DEFAULT NULL COMMENT '最近状态 10待调度 20调度中 30调度成功 40调度失败 50已终止',
    `serialize_type`       tinyint(0) NULL DEFAULT NULL COMMENT '调度序列 10串行 20并行',
    `exception_handler`    tinyint(0) NULL DEFAULT NULL COMMENT '异常处理 10跳过所有 20跳过错误',
    `lately_schedule_time` datetime(0) NULL DEFAULT NULL COMMENT '上次调度时间',
    `deleted`              tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`          datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`          datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for scheduler_task_machine
-- ----------------------------
DROP TABLE IF EXISTS `scheduler_task_machine`;
CREATE TABLE `scheduler_task_machine`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`     bigint(0) NULL DEFAULT NULL COMMENT '任务id',
    `machine_id`  bigint(0) NULL DEFAULT NULL COMMENT '调度机器id',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `task_idx`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度任务机器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for scheduler_task_machine_record
-- ----------------------------
DROP TABLE IF EXISTS `scheduler_task_machine_record`;
CREATE TABLE `scheduler_task_machine_record`
(
    `id`              bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`         bigint(0) NULL DEFAULT NULL COMMENT '任务id',
    `record_id`       bigint(0) NULL DEFAULT NULL COMMENT '明细id',
    `task_machine_id` bigint(0) NULL DEFAULT NULL COMMENT '任务机器id',
    `machine_id`      bigint(0) NULL DEFAULT NULL COMMENT '执行机器id',
    `machine_name`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器名称',
    `machine_host`    varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器主机',
    `machine_tag`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器唯一标识',
    `exec_command`    varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行命令',
    `exec_status`     int(0) NULL DEFAULT NULL COMMENT '执行状态 10待调度 20调度中 30调度成功 40调度失败 50跳过 60已停止',
    `exit_code`       int(0) NULL DEFAULT NULL COMMENT '退出码',
    `deleted`         tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `log_path`        varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志路径',
    `start_time`      datetime(4) NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`        datetime(4) NULL DEFAULT NULL COMMENT '结束时间',
    `create_time`     datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`     datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX             `task_record_idx`(`task_id`, `record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度任务执行明细机器详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for scheduler_task_record
-- ----------------------------
DROP TABLE IF EXISTS `scheduler_task_record`;
CREATE TABLE `scheduler_task_record`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `task_id`     bigint(0) NULL DEFAULT NULL COMMENT '任务id',
    `task_name`   varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务名称',
    `task_status` int(0) NULL DEFAULT NULL COMMENT '任务状态 10待调度 20调度中 30调度成功 40调度失败 50已停止',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `start_time`  datetime(4) NULL DEFAULT NULL COMMENT '开始时间',
    `end_time`    datetime(4) NULL DEFAULT NULL COMMENT '结束时间',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `task_id_idx`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度任务执行日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for system_env
-- ----------------------------
DROP TABLE IF EXISTS `system_env`;
CREATE TABLE `system_env`
(
    `id`          bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `attr_key`    varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'key',
    `attr_value`  varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value',
    `system_env`  tinyint(0) NULL DEFAULT 2 COMMENT '是否为系统变量 1是 2否',
    `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `deleted`     tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time` datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `key_idx`(`attr_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统环境变量' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_event_log
-- ----------------------------
DROP TABLE IF EXISTS `user_event_log`;
CREATE TABLE `user_event_log`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`        bigint(0) NULL DEFAULT NULL COMMENT '用户id',
    `username`       varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
    `event_classify` int(0) NULL DEFAULT NULL COMMENT '事件分类',
    `event_type`     int(0) NULL DEFAULT NULL COMMENT '事件类型',
    `log_info`       text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '日志信息',
    `params_json`    text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '日志参数',
    `exec_result`    tinyint(0) NULL DEFAULT NULL COMMENT '是否执行成功 1成功 2失败',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `user_event`(`user_id`, `event_classify`, `event_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户事件日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `id`                 bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`           varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
    `nickname`           varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
    `password`           varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `salt`               varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐值',
    `role_type`          int(0) NULL DEFAULT NULL COMMENT '角色类型 10超级管理员 20开发 30运维',
    `user_status`        tinyint(0) NULL DEFAULT 1 COMMENT '用户状态 1启用 2禁用',
    `lock_status`        tinyint(0) NULL DEFAULT 1 COMMENT '锁定状态 1正常 2锁定',
    `failed_login_count` int(0) NULL DEFAULT 0 COMMENT '登陆失败次数',
    `avatar_pic`         varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像路径',
    `contact_phone`      varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系手机',
    `contact_email`      varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系邮箱',
    `last_login_time`    datetime(4) NULL DEFAULT NULL COMMENT '最后登录时间',
    `deleted`            tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`        datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`        datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                `username_idx`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for web_side_message
-- ----------------------------
DROP TABLE IF EXISTS `web_side_message`;
CREATE TABLE `web_side_message`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `message_classify` tinyint(0) NULL DEFAULT NULL COMMENT '消息分类',
    `message_type`     int(0) NULL DEFAULT NULL COMMENT '消息类型',
    `read_status`      tinyint(0) NULL DEFAULT 1 COMMENT '是否已读 1未读 2已读',
    `to_user_id`       bigint(0) NULL DEFAULT NULL COMMENT '收信人id',
    `to_user_name`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收信人名称',
    `send_message`     text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '消息',
    `rel_id`           bigint(0) NULL DEFAULT NULL COMMENT '消息关联id',
    `deleted`          tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`      datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `to_user_id_idx`(`to_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统站内信' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for webhook_config
-- ----------------------------
DROP TABLE IF EXISTS `webhook_config`;
CREATE TABLE `webhook_config`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `webhook_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `webhook_url`    varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url',
    `webhook_type`   int(0) NULL DEFAULT NULL COMMENT '类型 10: 钉钉机器人',
    `webhook_config` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置项 json',
    `deleted`        tinyint(0) NULL DEFAULT 1 COMMENT '是否删除 1未删除 2已删除',
    `create_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) COMMENT '创建时间',
    `update_time`    datetime(4) NULL DEFAULT CURRENT_TIMESTAMP (4) ON UPDATE CURRENT_TIMESTAMP (4) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'webhook 配置' ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
