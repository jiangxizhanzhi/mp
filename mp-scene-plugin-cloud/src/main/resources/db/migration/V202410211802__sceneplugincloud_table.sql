alter table mp_sceneplugincloud_scene
    add column cloud_index varchar(16) default 'default' comment '多云环境下云平台的索引';
alter table mp_sceneplugincloud_scene_config
    add column cloud_index varchar(16) default 'default' comment '多云环境下云平台的索引';