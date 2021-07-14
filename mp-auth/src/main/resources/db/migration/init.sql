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


-- 系统配置
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (62, 'ec8bda8f239d4967b9ace3d425c829fb', 'default', 'system_name', '浙江能源', '2021-07-05 11:41:29.435932', '2021-07-05 11:41:29.435944');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (63, '4a3d2ecc528e4b77b3d518a81bd01335', 'default', 'copyright', '@2021-2023，南京赛宁信息技术有限公司，版权所有', '2021-07-05 11:41:29.436008', '2021-07-05 11:41:29.436010');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (64, 'bbd330a0fa7646fc937c8a875996ee8c', 'default', 'env_per_person', '12', '2021-07-05 11:41:29.436067', '2021-07-05 11:41:29.436070');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (65, '9efaf1686331402f814e266f740cba74', 'default', 'public_register', '3', '2021-07-05 11:41:29.436128', '2021-07-07 17:33:35.000000');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (66, 'b64cfb718fe64d7797d361aa8f806743', 'default', 'user_audit', '1', '2021-07-05 11:41:29.436235', '2021-07-14 15:13:46.000000');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (67, '3c20e18b430c4387aa3ac98dd0bebbfe', 'default', 'flag_length', '8', '2021-07-05 11:41:29.436292', '2021-07-05 11:41:29.436295');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (68, 'ecf0293f69d84dbdb430ebf96e323404', 'default', 'flag_prefix', '12345', '2021-07-05 11:41:29.436337', '2021-07-05 11:41:29.436339');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (69, '308666a1b1614a0bae80f838f35fff97', 'default', 'vlan_bucket', '100-200', '2021-07-05 11:41:29.436392', '2021-07-14 15:22:20.320849');
