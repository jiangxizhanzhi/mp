ALTER TABLE `mp`.`mp_sceneplugincloud_scene_element`
    ADD COLUMN `load_access_model` varchar(255) NULL COMMENT 'access_mode_id' AFTER `load_source`;
