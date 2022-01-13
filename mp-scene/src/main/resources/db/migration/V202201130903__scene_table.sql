-- description
alter table mp_scene_scene modify column description varchar(2048) NOT NULL COMMENT '描述' ;
alter table mp_scene_scene_config modify column description varchar(2048) NOT NULL COMMENT '描述' ;
-- name
alter table mp_cscene_cscene modify column `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称' ;
alter table mp_cscene_cscene_instance modify column `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称';

alter table mp_scene_scene modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_area modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_element modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_element_port modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_element_port_connection modify column `name` varchar(255) NOT NULL COMMENT '名称' ;

alter table mp_scene_scene_config modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_config_area modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_config_element modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_config_element_port modify column `name` varchar(255) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_config_element_port_connection modify column `name` varchar(255) NOT NULL COMMENT '名称' ;