ALTER TABLE `mp_auth_cloud_user`
    ADD COLUMN `area` varchar(64) NULL COMMENT '区域' AFTER `azure_client_secret`;