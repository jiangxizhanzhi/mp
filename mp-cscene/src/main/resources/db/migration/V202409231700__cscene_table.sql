alter table mp_flow_task_conf
    add column config int(11) NOT NULL default 0 COMMENT '是否开启流量重放和速率配置：0 否，1 是';