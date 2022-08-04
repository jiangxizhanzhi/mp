alter table `mp_scene_scene_element_snapshot` add column `scene` varchar(64) not null comment '所属场景id' ;
alter table `mp_scene_scene_element_snapshot` add column `source_name` varchar(255) not null comment '源快照名称' ;
alter table `mp_scene_scene_element_snapshot` modify column `name` varchar(255) not null comment '名称' ;