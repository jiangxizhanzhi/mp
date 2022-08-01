alter table `mp_scene_scene_element_snapshot` add column `scene_config` varchar(64) not null comment '源场景模板' ;
alter table `mp_scene_scene_element_snapshot` add column `source_name` varchar(64) not null comment '源快照名称' ;
alter table `mp_scene_scene_element_snapshot` modify column `name` varchar(255) not null comment '名称' ;