⚡ 注意: 应用不支持跨版本升级

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
