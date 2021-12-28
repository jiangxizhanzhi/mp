ALTER TABLE mp_sceneplugincollection_scene_config ADD COLUMN `enable_probe_collect` tinyint(1) NOT NULL COMMENT '开启探针采集';
ALTER TABLE mp_sceneplugincollection_scene_config_element ADD COLUMN `enable_probe_collect` tinyint(1) NOT NULL COMMENT '开启探针采集';
ALTER TABLE mp_sceneplugincollection_scene ADD COLUMN `enable_probe_collect` tinyint(1) NOT NULL COMMENT '开启探针采集';
ALTER TABLE mp_sceneplugincollection_scene_element ADD COLUMN `enable_probe_collect` tinyint(1) NOT NULL COMMENT '开启探针采集';

ALTER TABLE mp_sceneplugincollection_scene ADD COLUMN `probe_router` varchar(64) NOT NULL COMMENT '采集路由id';

CREATE TABLE mp_sceneplugincollection_scene_probe_router_port (
     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `id` varchar(64)  NOT NULL COMMENT 'id',
     `scene` varchar(64) NOT NULL COMMENT '场景实例id',
     `scene_element` varchar(64) NOT NULL COMMENT '节点id',
     `network_id` varchar(64) NOT NULL COMMENT '网络id',
     `ip` varchar(32) NOT NULL COMMENT 'ip',
     `create_time` datetime(6) NOT NULL COMMENT '创建时间',
     `update_time` datetime(6) NOT NULL COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE (`id`),
     UNIQUE (`scene`, `network_id`)
);
