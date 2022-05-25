ALTER TABLE `mp_auth_user`
    ADD COLUMN `third_token` text CHARACTER SET utf8 NOT NULL COMMENT '第三方token';

# 默认教员、学员角色
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description)
    VALUES ('7454c07d331841f4a58b17e4fb933b94', 'default', 'default', 'TEACHER', '教员', '教员角色');
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description)
    VALUES ('1e9748bd16c4406f8a5790b645b6c5d1', 'default', 'default', 'STUDENT', '学员', '学员角色');