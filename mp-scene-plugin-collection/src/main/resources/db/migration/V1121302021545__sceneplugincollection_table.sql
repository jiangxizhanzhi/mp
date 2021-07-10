# v5.3.7
alter table mp_sceneplugincollection_scene add column flow_file varchar(256) not null default '' comment '流量采集pcap文件地址' ;
alter table mp_sceneplugincollection_scene add column enable_flow_mirror tinyint(1)  NOT NULL COMMENT '开启流量流量镜像';
alter table mp_sceneplugincollection_scene_config add column enable_flow_mirror tinyint(1)  NOT NULL COMMENT '开启流量流量镜像';
alter table mp_sceneplugincollection_scene_element_port add column enable_flow_mirror tinyint(1)  NOT NULL COMMENT '开启流量流量镜像';
alter table mp_sceneplugincollection_scene_config_element_port add column enable_flow_mirror tinyint(1)  NOT NULL COMMENT '开启流量流量镜像';