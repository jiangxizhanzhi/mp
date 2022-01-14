#增加索引字段
ALTER TABLE `mp`.`mp_devops_target`
    ADD INDEX `idx_main_category`(`main_category`) USING BTREE,
    ADD INDEX `idx_sub_category`(`sub_category`) USING BTREE,
    ADD INDEX `idx_role`(`role`) USING BTREE;
