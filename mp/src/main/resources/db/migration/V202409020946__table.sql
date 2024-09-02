-- v5.5
ALTER TABLE `mp_scene_scene_element` ADD COLUMN `data_disk` int(11)  COMMENT '第二块磁盘大小';
ALTER TABLE `mp_scene_scene_config_element` ADD COLUMN `data_disk` int(11)  COMMENT '第二块磁盘大小';