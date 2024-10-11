alter table mp_scene_scene_config
    add column interval_sec varchar(32) default '180' not null comment '探针执行间隔，为大于1的数值，单位秒';
alter table mp_scene_scene
    add column interval_sec varchar(32) default '180' not null comment '探针执行间隔，为大于1的数值，单位秒';