# v5.4
alter table mp_sceneplugincloud_scene add column enable_rate tinyint(1) not null comment '是否开启环境总速率统计';
alter table mp_sceneplugincloud_scene_config add column enable_rate tinyint(1) not null comment '是否开启环境总速率统计';
