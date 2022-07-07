ALTER TABLE `mp_flow_task`
    MODIFY COLUMN `third_flow_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量第三方名称' AFTER `third_flow_id`;