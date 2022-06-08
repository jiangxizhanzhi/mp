ALTER TABLE `mp_flow_port`
    ADD COLUMN `port_type` tinyint NOT NULL DEFAULT 0 COMMENT '网口类型：0 背景流量，1攻击流量' AFTER `actuator_id`;