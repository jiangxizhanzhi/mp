alter table mp_flow_actuator add column `cscene_instance_name` varchar(64) NOT NULL default '' COMMENT '场景名称';
alter table mp_flow_actuator add column `type` varchar(64) NOT NULL default '' COMMENT '执行器类型, cpcloud_trex';
alter table mp_flow_task_task add column `actuator_type` varchar(64) NOT NULL default '' COMMENT '执行器类型, cpcloud_trex';