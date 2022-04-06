ALTER TABLE `mp_devops_screenshot`
    ADD COLUMN `scene` varchar(64) NOT NULL DEFAULT '' COMMENT '场景id' AFTER `user_org_name`,
    ADD COLUMN `node_id` varchar(64) NOT NULL DEFAULT '' COMMENT '节点id' AFTER `scene`;