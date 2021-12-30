alter table mp_scene_scene modify column `name` varchar(256) NOT NULL COMMENT '名称' ;
alter table mp_scene_scene_config modify column `name` varchar(256) NOT NULL COMMENT '名称' ;
alter table mp_cscene_cscene add column `name` varchar(256) NOT NULL DEFAULT '' COMMENT '名称' after id ;
alter table mp_cscene_cscene_instance add column `name` varchar(256) NOT NULL DEFAULT '' COMMENT '名称' after id ;