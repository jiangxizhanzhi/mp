ALTER TABLE `mp`.`mp_auth_role` MODIFY COLUMN `bc_ids` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '靶场ids' AFTER `visible`;

ALTER TABLE `mp`.`mp_auth_role` MODIFY COLUMN `bc_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '靶场名称' AFTER `bc_ids`;
ALTER TABLE `mp`.`mp_scene_scene` ADD COLUMN `stage_list` varchar(1048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `update_time`;

ALTER TABLE `mp`.`mp_scene_scene` ADD COLUMN `stage_percentage` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `stage_list`;

ALTER TABLE `mp`.`mp_scene_scene` ADD COLUMN `stage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `stage_percentage`;

ALTER TABLE `mp`.`mp_scene_scene` ADD COLUMN `stage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `stage_name`;
