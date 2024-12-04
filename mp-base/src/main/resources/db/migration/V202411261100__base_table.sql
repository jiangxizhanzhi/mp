ALTER TABLE `mp`.`mp_sys_notice`
    ADD COLUMN `team_id` varchar(64) NULL COMMENT '战队id' AFTER `is_public`,
ADD COLUMN `team_name` varchar(128) NULL COMMENT '战队名称' AFTER `team_id`;