ALTER TABLE `mp_flow_port`
    MODIFY COLUMN `port_type` tinyint NOT NULL DEFAULT 0 COMMENT '网口类型：0 pcap包，1 脚本' AFTER `actuator_id`;