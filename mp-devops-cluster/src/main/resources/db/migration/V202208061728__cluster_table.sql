create unique index mp_pnode_config_uidx_aname on mp_devops_cpnode_monitor_script_config (agent_name);
alter table mp_devops_cpnode_monitor_script_config add config varchar(2000) not null comment '脚本配置JSON字符串，包括launchid, interval，scriptUrl等信息' after ip;

ALTER table mp_devops_target ADD column is_init tinyint(1) default 0 COMMENT '是否初始靶标，0：false，1：true';
