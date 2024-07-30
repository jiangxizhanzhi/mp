ALTER TABLE `mp_scene_scene_config_element_port_connection` ADD COLUMN `info` text COMMENT '其他信息,前端可自由定义';
ALTER TABLE `mp_scene_scene_element_port_connection` ADD COLUMN `info` text COMMENT '其他信息,前端可自由定义';
ALTER TABLE `mp_scene_scene_config_node_connection` ADD COLUMN `type` varchar(64) COMMENT '连线类型';
ALTER TABLE `mp_scene_scene_config_node_connection` ADD COLUMN `vertices` text COMMENT '路径点';
ALTER TABLE `mp_scene_scene_node_connection` ADD COLUMN `type` varchar(64) COMMENT '连线类型';
ALTER TABLE `mp_scene_scene_node_connection` ADD COLUMN `vertices` text COMMENT '路径点';