alter table mp_sceneplugincloud_scene_element add column `key_klement` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是关键节点' ;
alter table mp_sceneplugincloud_scene_config_element add column `key_klement` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是关键节点' ;
alter table mp_sceneplugincloud_scene_element add column `start_priority` int(12) NOT NULL DEFAULT '' COMMENT '启动优先级：1：高，2：中，3：低' ;
alter table mp_sceneplugincloud_scene_config_element add column `start_priority` int(12) NOT NULL DEFAULT '' COMMENT '启动优先级：1：高，2：中，3：低' ;