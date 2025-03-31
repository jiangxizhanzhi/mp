-- 用户表添加账号id索引
ALTER TABLE `mp_auth_user` ADD INDEX `user_acount_idx` (`account_id`);