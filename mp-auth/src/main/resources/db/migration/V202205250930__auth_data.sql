# admin密码初始化
# UPDATE `mp_auth_user`
# SET
#     `tenant` = 'default',
#     `username` = 'admin',
#     `password` = '1b002d29849f7f46c2de25cb47468ded9ff6f64091d6cb6854566f4ca2ab9864984976f09d3e978c',
#     `token_secret` = '1b0bedd747d346d49fdab9355a7cfe3d',
#     `name` = '管理员1',
#     `nickname` = '管理员',
#     `avatar` = '/api/mp/avatar/9.png',
#     `birthday` = '2021-06-23',
#     `email` = 'example@123.com',
#     `is_enabled` = 1,
#     `is_audited` = 1,
#     `init_check` = 0,
#     `description` = '1212126111',
#     `date_joined` = '2022-01-12 11:02:18',
#     `expired_time` = '2121-07-29 23:59:59',
#     `last_login` = NULL,
#     `res` = NULL,
#     `create_time` = '2022-01-12 11:02:18',
#     `update_time` = '2022-07-26 13:50:31'
# WHERE
#         `id` = '3f655d87112f4ae094bbaf70830e20aa';

ALTER TABLE `mp_auth_user`
    ADD COLUMN `third_token` text CHARACTER SET utf8 NOT NULL COMMENT '第三方token';

# 默认教员、学员角色
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description)
    VALUES ('7454c07d331841f4a58b17e4fb933b94', 'default', 'default', 'TEACHER', '教员', '教员角色');
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description)
    VALUES ('1e9748bd16c4406f8a5790b645b6c5d1', 'default', 'default', 'STUDENT', '学员', '学员角色');

ALTER TABLE `mp_auth_user`
    ADD COLUMN `third_user_id` varchar(64) NOT NULL COMMENT '第三方用户id' AFTER `third_token`;
