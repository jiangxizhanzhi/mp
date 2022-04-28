ALTER TABLE `mp_flow_task`
    ADD COLUMN `custom_flag` tinyint NOT NULL DEFAULT 0 COMMENT '自定义流量任务标志 0：否， 1：是' AFTER `target_ips`,
    ADD COLUMN `flow_protocol` varchar(512) NOT NULL COMMENT '流量协议' AFTER `custom_flag`,
    ADD COLUMN `content` varchar(1024) NOT NULL COMMENT '流量内容' AFTER `flow_protocol`,
    ADD COLUMN `mult` int NOT NULL DEFAULT 1 COMMENT '倍数:大于等于1的整数' AFTER `content`;

ALTER TABLE `mp_flow_port`
    ADD COLUMN `network_id` varchar(64) NOT NULL DEFAULT '' COMMENT '网络id' AFTER `client_gateway`,
    ADD COLUMN `actuator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '流量执行器id' AFTER `network_id`;