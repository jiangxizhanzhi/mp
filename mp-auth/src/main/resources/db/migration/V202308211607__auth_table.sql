ALTER TABLE mp_auth_user
    ADD COLUMN delay_remind_license_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '延期提醒时间' AFTER init_check;

INSERT INTO mp_common_option (`id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES ('fe6dd6f30f0411ed996d0242ac110002', '5', '', '授权', 'licence', 5, 'notice_type', '{\"activKeys\":\"LCS-FUNC-CR-SHIXUN\",\"visible\":1}', '2023-04-20 19:17:37', '2023-04-20 19:17:37');
