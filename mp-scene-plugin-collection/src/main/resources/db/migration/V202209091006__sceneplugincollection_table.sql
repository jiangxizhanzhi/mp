ALTER TABLE mp_sceneplugincollection_scene_config
    ADD COLUMN flow_mirror_target_node varchar(64) NOT NULL comment '流量镜像目标节点';
ALTER TABLE mp_sceneplugincollection_scene_config
    ADD COLUMN flow_mirror_target_port varchar(64) NOT NULL comment '流量镜像目标端口';

ALTER TABLE mp_sceneplugincollection_scene
    ADD COLUMN flow_mirror_target_node varchar(64) NOT NULL comment '流量镜像目标节点';
ALTER TABLE mp_sceneplugincollection_scene
    ADD COLUMN flow_mirror_target_port varchar(64) NOT NULL comment '流量镜像目标端口';