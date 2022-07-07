ALTER TABLE `mp_flow_flow_info`
    MODIFY COLUMN `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流量标题' AFTER `id`;