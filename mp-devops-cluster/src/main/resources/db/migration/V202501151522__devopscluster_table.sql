ALTER TABLE `mp_devops_target`
    ADD COLUMN `cpcloud_update_time` datetime(6) NULL COMMENT '云平台同步修改时间' AFTER `syslog_config`;