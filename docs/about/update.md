⚡ 注意: 应用不支持跨版本升级

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
