ALTER TABLE `mp`.`mp_sceneplugincloud_scene_config_element`
    ADD COLUMN `load_access_model` varchar(255) NULL COMMENT 'access_mode_id' AFTER `load_source`;

ALTER TABLE `mp`.`mp_sceneplugincloud_scene_element`
    DROP COLUMN `tra_scene_id`