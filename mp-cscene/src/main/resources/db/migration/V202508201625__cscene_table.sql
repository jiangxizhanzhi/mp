alter table mp_devops_target add column domain varchar(512) default '' comment '域' ;
alter table mp_sceneplugincloud_scene_element
    add column domain varchar(512) default '' comment '域' ;
alter table mp_sceneplugincloud_scene_config_element
    add column domain varchar(512) default '' comment '域' ;