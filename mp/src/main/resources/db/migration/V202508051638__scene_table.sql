alter table mp_sceneplugincollection_scene_config
    add column custome_mirror text not null default '' comment '自定义流量镜像';

alter table mp_sceneplugincollection_scene
    add column custome_mirror text not null default '' comment '自定义流量镜像';