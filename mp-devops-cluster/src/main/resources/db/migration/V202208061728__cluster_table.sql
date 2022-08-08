alter table mp_devops_cpnode_monitor_script_config drop column script_name;
alter table mp_devops_cpnode_monitor_script_config add config varchar(2000) not null comment '脚本配置JSON字符串，包括launchid, interval，scriptUrl等信息' after ip;
create unique index mp_pnode_config_uidx_aname on mp_devops_cpnode_monitor_script_config (agent_name);
