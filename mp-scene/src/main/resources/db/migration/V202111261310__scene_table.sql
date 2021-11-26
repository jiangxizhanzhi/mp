ALTER TABLE mp_scene_scene_config_area ADD COLUMN `display_context` varchar(2048) NOT NULL COMMENT '显示上下文';
ALTER TABLE mp_scene_scene_config_element ADD COLUMN `display_context` varchar(2048) NOT NULL COMMENT '显示上下文';
ALTER TABLE mp_scene_scene_config_element_port_connection ADD COLUMN `display_context` varchar(2048) NOT NULL COMMENT '显示上下文';

ALTER TABLE mp_scene_scene_area ADD COLUMN `display_context` varchar(2048) NOT NULL COMMENT '显示上下文';
ALTER TABLE mp_scene_scene_element ADD COLUMN `display_context` varchar(2048) NOT NULL COMMENT '显示上下文';
ALTER TABLE mp_scene_scene_element_port_connection ADD COLUMN `display_context` varchar(2048) NOT NULL COMMENT '显示上下文';
