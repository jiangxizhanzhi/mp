alter table mp_sceneplugincloud_scene_element
    add column flow_target varchar(1024) not null default '' comment '跨节点流量镜像';
alter table mp_sceneplugincloud_scene_config_element
    add column flow_target varchar(1024) not null default '' comment '跨节点流量镜像';