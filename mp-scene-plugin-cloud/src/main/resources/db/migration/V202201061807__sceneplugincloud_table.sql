ALTER TABLE mp_sceneplugincloud_scene_connection DROP INDEX scene ;
ALTER TABLE mp_sceneplugincloud_scene_connection ADD INDEX `scene` (`scene`, `access_mode_id`, `access_user`);