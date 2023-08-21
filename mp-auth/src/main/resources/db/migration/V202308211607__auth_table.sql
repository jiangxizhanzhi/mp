ALTER TABLE mp_auth_user
    ADD COLUMN delay_remind_license_time INT(11) NOT NULL DEFAULT -1 comment '延迟提醒license到期时间';