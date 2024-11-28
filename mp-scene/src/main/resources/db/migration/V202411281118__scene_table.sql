alter table mp_sceneplugincloud_scene_config_element
    add load_source int(1) null comment '来源(1载荷平台 2本地)';

alter table mp_sceneplugincloud_scene_element
    add load_source int(1) null comment '来源(1载荷平台 2本地)';