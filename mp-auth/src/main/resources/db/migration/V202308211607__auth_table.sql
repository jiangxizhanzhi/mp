ALTER TABLE mp_auth_user
    ADD COLUMN delay_remind_license_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '延期提醒时间' AFTER init_check;