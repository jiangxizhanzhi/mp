-- 默认管理员用户，首次登陆需修改密码
INSERT INTO mp_auth_user (pk, id, tenant, identifier, username, password, token_secret, name, nickname, avatar, birthday, email, mobile, id_card, address, is_enabled, is_audited, init_check, description, date_joined, expired_time, last_login, create_time, update_time) VALUES (1, '3f655d87112f4ae094bbaf70830e20aa', 'default', 'admin', 'admin', '1f670a02678a6cbcc9d38ce57f97f4a3724574dfa3a69b73f2930586110b4619265c87c9874df4cc', '351672ff8854438eaaa48c46efdfa947', '管理员', '管理员', 'http://10.20.3.9:8888/group1/M00/01/07/ChQDCWCZRs2AC3LWAACvtHWZ1-g868.png', '2021-06-23', 'abc@123.com', '13851121894', '', '11111', 1, 1, 1, '', '2021-06-23 09:37:29.084709', '2021-07-29 00:00:00.000000', '2021-06-23 09:37:29.084667', '2021-06-23 09:37:29.084805', '2021-07-08 20:16:47.878994');

-- 默认管理员角色
INSERT INTO mp_auth_role (pk, id, tenant, org, identifier, name, description, create_time, update_time) VALUES (1, 'adminid', 'default', 'default', 'ADMIN', '管理员', '管理员角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');

-- 用户-角色关系
INSERT INTO mp_auth_user_role (pk, id, tenant, user, role, create_time, update_time) VALUES (1, '791088e33dd44de3899f3c105597c354', 'default', '3f655d87112f4ae094bbaf70830e20aa', 'adminid', '2021-07-08 20:16:47.896726', '2021-07-08 20:16:47.896733');

-- 客户端授权
INSERT INTO mp_auth_client_details (pk, client_id, resource_ids, client_secret, type, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove) VALUES (1, '84261d495c414e', 'all', '2112f4b855264ad7b1a4209737fc7836', 2, 'all', 'all', null, null, null, null, null, null);


-- 默认头像
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (1, '1', '/fdfs/group1/M00/01/55/CmQO_GDtBPaAIqetAADFmq1O8YE839.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (2, '2', '/fdfs/group1/M00/01/55/CmQO_GDtBSuAZ9XXAACIQD_kUto650.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (3, '3', '/fdfs/group1/M00/01/55/CmQO_GDtBUaANenKAACvtHWZ1-g540.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (4, '4', '/fdfs/group1/M00/01/55/CmQO_GDtBYSAd6J6AAC3Z_Sm84k252.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (5, '5', '/fdfs/group1/M00/01/55/CmQO_GDtBZyAUZqgAADRF7kKXkQ783.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (6, '6', '/fdfs/group1/M00/01/55/CmQO_GDtBcOAAHg9AADCu1xFRVY798.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (7, '7', '/fdfs/group1/M00/01/55/CmQO_GDtBdyAXp1-AAC_trViz9A182.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (8, '8', '/fdfs/group1/M00/01/55/CmQO_GDtBfeAYTqrAADEtkPKBzM983.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
INSERT INTO mp_auth_avatar (pk, id, url, create_time, update_time) VALUES (9, '9', '/fdfs/group1/M00/01/55/CmQO_GDtBgqAW3jAAADZj_AdtGM862.png', '2021-07-03 10:09:05.000000', '2021-07-03 10:09:07.000000');
