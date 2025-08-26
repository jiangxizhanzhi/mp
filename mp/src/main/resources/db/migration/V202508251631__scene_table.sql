alter table mp_sceneplugincloud_scene
    add column flow_target varchar(1024) not null default '' comment '跨节点流量镜像';
alter table mp_sceneplugincloud_scene_config
    add column flow_target varchar(1024) not null default '' comment '跨节点流量镜像';