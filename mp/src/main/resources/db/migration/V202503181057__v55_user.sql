-- 用户表添加4A系统用户相关字段
ALTER TABLE `mp_auth_user`
    ADD COLUMN `four_a_ticket` varchar(64) NULL COMMENT '4a平台人员登录的凭证' AFTER `account_id`;