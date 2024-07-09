alter table mp_devops_target add COLUMN template_id VARCHAR(128) not null default '' ;
alter table mp_devops_target add COLUMN template_name VARCHAR(128) not null default '' ;
alter table mp_sceneplugincollection_scene add column flow_collector_container_id VARCHAR(128)  NOT NULL default '' COMMENT '流量采集器即arkime容器的id';