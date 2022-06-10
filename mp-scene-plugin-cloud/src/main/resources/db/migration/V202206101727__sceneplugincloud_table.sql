ALTER TABLE mp_sceneplugincloud_scene_element MODIFY COLUMN `static_routes` text NOT NULL COMMENT '虚拟路由静态路由' ;
ALTER TABLE mp_sceneplugincloud_scene_element MODIFY COLUMN `host_routes` text NOT NULL COMMENT '虚拟路由静态路由' ;
ALTER TABLE mp_sceneplugincloud_scene_element MODIFY COLUMN `auxiliary_network_host_routes` text NOT NULL COMMENT '辅助网络静态路由';

ALTER TABLE mp_sceneplugincloud_scene_config_element MODIFY COLUMN `static_routes` text NOT NULL COMMENT '虚拟路由静态路由' ;
ALTER TABLE mp_sceneplugincloud_scene_config_element MODIFY COLUMN `host_routes` text NOT NULL COMMENT '虚拟路由静态路由' ;
ALTER TABLE mp_sceneplugincloud_scene_config_element MODIFY COLUMN `auxiliary_network_host_routes` text NOT NULL COMMENT '辅助网络静态路由';

ALTER TABLE mp_sceneplugincollection_scene_element MODIFY COLUMN `probes` longtext NOT NULL COMMENT '探针列表';
ALTER TABLE mp_sceneplugincollection_scene_config_element MODIFY COLUMN `probes` longtext NOT NULL COMMENT '探针列表';

ALTER TABLE mp_sceneplugincloud_scene_element_network MODIFY COLUMN `host_routes` text NOT NULL COMMENT '虚拟路由静态路由';