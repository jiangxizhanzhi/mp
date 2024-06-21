alter table mp_scene_scene_config_element add COLUMN config_command text ;
alter table mp_scene_scene_element add COLUMN config_command text ;
ALTER TABLE mp_flow_actuator ADD COLUMN `status` int(11) NOT NULL COMMENT '运行状态';