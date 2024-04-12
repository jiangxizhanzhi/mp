-- v5.5
ALTER TABLE mp_sceneplugincollection_scene_config_element ADD COLUMN edited TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有添加过探针' ;