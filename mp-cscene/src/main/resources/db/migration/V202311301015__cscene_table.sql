# yz4
alter table mp_devops_target add column business_tag int(11) not null default 1 comment '业务标签,1:终端防护靶标,2:路由交换靶标,3:边界安全防护设备靶标,4:流量分析靶标,5:应用服务靶标' ;
alter table mp_scene_scene_element add column physical_location varchar(128) not null default '' comment '物理位置' ;
alter table mp_scene_scene_config_element add column physical_location varchar(128) not null default '' comment '物理位置' ;