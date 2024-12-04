ALTER TABLE `mp`.`mp_devops_log_operation`
    ADD COLUMN `count` int(10) ZEROFILL NULL COMMENT '下载次数' AFTER `update_time`;