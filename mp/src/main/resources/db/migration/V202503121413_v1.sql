-- 用户表添加4A系统用户相关字段
ALTER TABLE `mp_auth_user`
    ADD COLUMN `out_user_type` varchar(64) NULL COMMENT '外部平台用户类型' AFTER `id`,
    ADD COLUMN `account_id` varchar(64) NULL COMMENT '外部用户id' AFTER `out_user_type`;