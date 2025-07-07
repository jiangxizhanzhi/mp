alter table mp_flow_task_task
    add column `flow_name` varchar(128) NOT NULL default '' COMMENT '流量包名称';