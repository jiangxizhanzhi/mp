ALTER TABLE `mp`.`mp_auth_role`
    ADD COLUMN `bc_ids` varchar(1024) NULL COMMENT '关联靶场IDS' AFTER `visible`,
ADD COLUMN `bc_name` varchar(1024) NULL COMMENT '关联靶场名称' AFTER `bc_ids`;


ALTER TABLE `mp`.`mp_auth_user`
    ADD COLUMN `bc_ids` varchar(1024) NULL COMMENT '关联靶场ids' AFTER `update_time`,
ADD COLUMN `bc_name` varchar(1024) NULL COMMENT '关联靶场名称' AFTER `bc_ids`;