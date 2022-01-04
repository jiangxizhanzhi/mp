ALTER TABLE mp_sceneplugincloud_scene_config ADD COLUMN `enable_recording` tinyint(1) NOT NULL COMMENT '是否开启录屏';
ALTER TABLE mp_sceneplugincloud_scene ADD COLUMN `enable_recording` tinyint(1) NOT NULL COMMENT '是否开启录屏';
ALTER TABLE mp_sceneplugincloud_scene_connection ADD COLUMN `connection_enable_recording` tinyint(1) NOT NULL COMMENT '是否开启录屏';
ALTER TABLE mp_sceneplugincloud_scene_connection ADD COLUMN `connection_recording_name` varchar(255) NOT NULL COMMENT '录屏名称';
