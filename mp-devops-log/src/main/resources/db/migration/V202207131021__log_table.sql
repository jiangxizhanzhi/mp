ALTER TABLE `mp_log_report`
    ADD COLUMN `host_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '节点ip' AFTER `download_url`;
