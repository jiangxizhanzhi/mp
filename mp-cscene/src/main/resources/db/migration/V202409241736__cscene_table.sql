INSERT `mp_auth_url_ac`( `id`, `url`, `is_pattern`, `group`, `methods`, `desc`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0394', '/api/mp/load/load/getLoadList', 0, 'out-load-service',
        '*', '载荷页面', '2024-05-20 11:34:00', '2024-05-20 11:34:02');

INSERT INTO `mp_auth_url_ac_target`( `id`, `group`, `target`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0394', 'out-load-service', 'no_auth', '2024-05-20 11:31:56',
        '2024-05-20 11:31:56');

INSERT INTO `mp_auth_url_ac`( `id`, `url`, `is_pattern`, `group`, `methods`, `desc`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0396', '/api/mp/common/commonCode/getNextAllTreeDataByCode', 0, 'out-load-service',
        '*', '根据code查找下级所有', '2024-05-20 11:34:00', '2024-05-20 11:34:02');

INSERT INTO `mp_auth_url_ac_target`( `id`, `group`, `target`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0396', 'out-load-service', 'no_auth', '2024-05-20 11:31:56',
        '2024-05-20 11:31:56');


INSERT INTO `mp_auth_url_ac`( `id`, `url`, `is_pattern`, `group`, `methods`, `desc`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0393', '/api/mp/common/commonCode/getNextLevelDataByCode', 0, 'out-load-service',
        '*', '根据code查找下级', '2024-05-20 11:34:00', '2024-05-20 11:34:02');

INSERT INTO `mp_auth_url_ac_target`( `id`, `group`, `target`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0393', 'out-load-service', 'no_auth', '2024-05-20 11:31:56',
        '2024-05-20 11:31:56');

INSERT INTO `mp_auth_url_ac`( `id`, `url`, `is_pattern`, `group`, `methods`, `desc`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0398', '/api/mp/agg/dashboard/user_resource', 0, 'out-load-service',
        '*', '用户资源', '2024-05-20 11:34:00', '2024-05-20 11:34:02');

INSERT INTO `mp_auth_url_ac_target`( `id`, `group`, `target`, `create_time`, `update_time`)
VALUES ( '33b99e69569711ed91a0000ec67b0398', 'out-load-service', 'no_auth', '2024-05-20 11:31:56',
        '2024-05-20 11:31:56');