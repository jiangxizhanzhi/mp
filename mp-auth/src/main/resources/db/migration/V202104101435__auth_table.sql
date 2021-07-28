# 租户表
CREATE TABLE mp_auth_tenant (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `identifier` varchar(64)  NOT NULL COMMENT '编号',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`)
);

# 组织表
CREATE TABLE mp_auth_org (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户' default '',
    `parent` varchar(64)  NOT NULL COMMENT '父组织' default '',
    `identifier` varchar(64)  NOT NULL COMMENT '编号',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `path` varchar(180) NOT NULL COMMENT '组织结构路径',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `identifier`),
    UNIQUE (`path`)
);

# 角色表
CREATE TABLE mp_auth_role (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户' default '',
    `org` varchar(64)  NOT NULL COMMENT '所属组织' default '',
    `identifier` varchar(64)  NOT NULL COMMENT '编号',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `identifier`),
    UNIQUE (`tenant`, `name`)
);

# 用户表
CREATE TABLE mp_auth_user (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '原生租户' default '',
    `identifier` varchar(64)  NOT NULL COMMENT '编号',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `token_secret` varchar(128) NOT NULL COMMENT 'token密钥',
    `name` varchar(128) NOT NULL COMMENT '姓名',
    `nickname` varchar(128) NOT NULL COMMENT '昵称',
    `avatar` varchar(2048) NOT NULL COMMENT '头像',
    `birthday` date NOT NULL COMMENT '生日',
    `email` varchar(255) NOT NULL COMMENT '邮箱',
    `mobile` varchar(64) NOT NULL COMMENT '手机号',
    `id_card` varchar(64) NOT NULL COMMENT '身份证号',
    `address` varchar(255) NOT NULL COMMENT '地址',
    `is_enabled` int NOT NULL COMMENT '1:启用 0：禁用',
    `is_audited` int NOT NULL COMMENT '1:已审核 0：待审核',
    `init_check` int NOT NULL DEFAULT 0 COMMENT '是否首次登陆需要强制修改密码 0:否，1:是',
    `description` varchar(1024) NULL COMMENT '描述',
    `date_joined` datetime(6) NOT NULL COMMENT '加入时间',
    `expired_time` datetime(6) NOT NULL COMMENT '过期时间',
    `last_login` datetime(6) NOT NULL COMMENT '最后登录时间',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`),
    UNIQUE (`username`)
);

# 租户用户
CREATE TABLE mp_auth_tenant_user (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
    `org` varchar(64)  NOT NULL COMMENT '所属组织',
    `user` varchar(64)  NOT NULL COMMENT '用户',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `user`)
);


# 用户心跳表
CREATE TABLE mp_auth_user_heartbeat (
     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `id` varchar(64)  NOT NULL COMMENT 'id',
     `user` varchar(64)  NOT NULL COMMENT '用户id',
     `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
     `create_time` datetime(6) NOT NULL COMMENT '创建时间',
     `update_time` datetime(6) NOT NULL COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE (`id`)
);
create index idx_user_hb_uid on mp_auth_user_heartbeat (user);
create index idx_user_hb_time on mp_auth_user_heartbeat (create_time);


# 用户在线状态统计表
CREATE TABLE mp_auth_user_online_report (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `user` varchar(64)  NOT NULL COMMENT '用户id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `name` varchar(64) NOT NULL COMMENT '用户姓名',
    `org` varchar(200) NOT NULL COMMENT '组织',
    `action` varchar(64) NOT NULL COMMENT '动作',
    `subject` varchar(64) NOT NULL COMMENT '对象',
    `total_online_hours` float NOT NULL DEFAULT 0 COMMENT '累计在线时长',
    `monitor` varchar(64) NOT NULL COMMENT '监控',
    `is_online` int DEFAULT 0 COMMENT '是否在线 0:不在线 1:在线',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`user`)
);
create index idx_user_olrpt_uname on mp_auth_user_online_report (username);



# 用户角色表
CREATE TABLE mp_auth_user_role (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
    `user` varchar(64)  NOT NULL COMMENT '用户',
    `role` varchar(64)  NOT NULL COMMENT '角色',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `user`, `role`)
);

# 资源目标表(有哪些资源)
CREATE TABLE mp_auth_target (
   `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `id` varchar(64)  NOT NULL COMMENT 'id',
   `parent` varchar(64) NULL COMMENT '父权限ID',
   `identifier` varchar(64)  NOT NULL COMMENT '编号',
   `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
   `name` varchar(64) NOT NULL COMMENT '名称',
   `path` varchar(255) NULL COMMENT '路径',
   `type` int NOT NULL COMMENT '资源类型 1:菜单, 2:API, 3:数据',
   `create_time` datetime(6) NOT NULL COMMENT '创建时间',
   `update_time` datetime(6) NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`pk`),
   UNIQUE (`id`),
   UNIQUE (`identifier`)
);

# 组织资源权限表
CREATE TABLE mp_auth_org_permission (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
    `owner` varchar(64)  NOT NULL COMMENT '权限所有者(组织)',
    `target` varchar(64) NOT NULL COMMENT '资源目标',
    `can_read` tinyint(1) NOT NULL COMMENT '读权限',
    `can_create` tinyint(1) NOT NULL COMMENT '创建权限',
    `can_update` tinyint(1) NOT NULL COMMENT '更新权限',
    `can_delete` tinyint(1) NOT NULL COMMENT '删除权限',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
);

# 角色资源权限表
CREATE TABLE mp_auth_role_permission (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
    `owner` varchar(64)  NOT NULL COMMENT '权限所有者(角色)',
    `target` varchar(64)  NOT NULL COMMENT '资源目标',
    `can_read` tinyint(1) NOT NULL COMMENT '读权限',
    `can_create` tinyint(1) NOT NULL COMMENT '创建权限',
    `can_update` tinyint(1) NOT NULL COMMENT '更新权限',
    `can_delete` tinyint(1) NOT NULL COMMENT '删除权限',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
);

# 用户资源权限表
CREATE TABLE mp_auth_user_permission (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '所属租户',
    `owner` varchar(64)  NOT NULL COMMENT '权限所有者(用户)',
    `target` varchar(64)  NOT NULL COMMENT '资源目标',
    `can_read` tinyint(1) NOT NULL COMMENT '读权限',
    `can_create` tinyint(1) NOT NULL COMMENT '创建权限',
    `can_update` tinyint(1) NOT NULL COMMENT '更新权限',
    `can_delete` tinyint(1) NOT NULL COMMENT '删除权限',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
);



# 组织-用户关系表
CREATE TABLE mp_auth_org_user (
     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `id` varchar(64)  NOT NULL COMMENT 'id',
     `tenant` varchar(64)  NOT NULL COMMENT '所属租户' default '',
     `org` varchar(64)  NOT NULL COMMENT '组织id',
     `user` varchar(64)  NOT NULL COMMENT '账号id',
     `create_time` datetime(6) NOT NULL COMMENT '创建时间',
     `update_time` datetime(6) NOT NULL COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE (`id`)
);


create table mp_auth_client_details (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    client_id VARCHAR(128) comment '客户端ID',
    resource_ids VARCHAR(256) comment '资源id',
    client_secret VARCHAR(256) comment '客户端秘钥',
    type int not null default 1 comment '类型 1:Oauth2渠道 2:免认证渠道',
    scope VARCHAR(256) comment '权限范围',
    authorized_grant_types VARCHAR(256) comment '授权类型',
    web_server_redirect_uri VARCHAR(256) comment '回跳地址',
    authorities VARCHAR(256) comment '权限',
    access_token_validity INTEGER comment 'access_token的有效期（秒）',
    refresh_token_validity INTEGER comment 'refresh_token的有效期（秒）',
    additional_information VARCHAR(4096) comment '预留字段',
    autoapprove VARCHAR(256) comment '设置用户是否自动approve操作',
    PRIMARY KEY (`pk`),
    UNIQUE (`client_id`)
) COMMENT '渠道认证配置表';


# 用户默认头像
CREATE TABLE mp_auth_avatar (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64)  NOT NULL COMMENT 'id',
       `url` varchar(1024)  NULL COMMENT '头像url',
       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
       PRIMARY KEY (`pk`),
       UNIQUE (`id`)
) COMMENT '默认头像';


# 系统配置信息表
CREATE TABLE mp_sys_config (
     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `id` varchar(64)  NOT NULL COMMENT 'id',
     `tenant` varchar(64) NOT NULL COMMENT '租户',
     `key_name` varchar(100)  NULL COMMENT '键',
     `value` varchar(1024)  NULL COMMENT '值',
     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE (`id`),
     UNIQUE (`tenant`, `key_name`)
) COMMENT '其他配置信息表';











-- 默认管理员用户，首次登陆需修改密码
INSERT INTO mp_auth_user (id, tenant, identifier, username, password, token_secret, name, nickname, avatar, birthday, email, mobile, id_card, address, is_enabled, is_audited, init_check, description, date_joined, expired_time, last_login, create_time, update_time) VALUES ('defaultuserid', 'default', 'admin', 'admin', '1f670a02678a6cbcc9d38ce57f97f4a3724574dfa3a69b73f2930586110b4619265c87c9874df4cc', '351672ff8854438eaaa48c46efdfa947', '管理员', '管理员', '', '2021-06-23', 'example@123.com', '13988888888', '', '北京路101号', 1, 1, 1, '', '2021-06-23 09:37:29.084709', '2021-07-29 00:00:00.000000', '2021-06-23 09:37:29.084667', '2021-06-23 09:37:29.084805', '2021-07-08 20:16:47.878994');

-- 默认管理员角色
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description, create_time, update_time) VALUES ('adminid', 'default', 'default', 'ADMIN', '管理员', '管理员角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description, create_time, update_time) VALUES ('superuserid', 'default', 'default', 'SUPERUSER', '超级用户', '超级用户角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');

-- 用户-角色关系
INSERT INTO mp_auth_user_role (id, tenant, user, role, create_time, update_time) VALUES ('791088e33dd44de3899f3c105597c354', 'default', 'defaultuserid', 'adminid', '2021-07-08 20:16:47.896726', '2021-07-08 20:16:47.896733');
INSERT INTO mp_auth_user_role (id, tenant, user, role, create_time, update_time) VALUES ('088e79133dd44de105597c3543899f3c', 'default', 'defaultuserid', 'superuserid', '2021-07-08 20:16:47.896726', '2021-07-08 20:16:47.896733');

-- 客户端授权
INSERT INTO mp_auth_client_details (client_id, resource_ids, client_secret, type, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove) VALUES ('84261d495c414e', 'all', '2112f4b855264ad7b1a4209737fc7836', 2, 'all', 'all', null, null, null, null, null, null);

-- 组织
INSERT INTO mp_auth_org (id, tenant, parent, identifier, name, path, description, create_time, update_time) VALUES ('defaultorgid', 'default', '', 'default-org', '一级组织', '一级组织', '一级组织', '2021-07-21 09:36:33.835950', '2021-07-21 10:13:33.281078');

-- 用户-组织
INSERT INTO mp_auth_org_user (id, tenant, org, user, create_time, update_time) VALUES ('06c3e74d568a4937812c8f9ba6ef5df9', 'default', 'defaultorgid', 'defaultuserid', '2021-07-21 21:47:14.517567', '2021-07-21 21:47:14.517572');

-- 租户
INSERT INTO mp_auth_tenant (id, identifier, name, create_time, update_time) VALUES ('defaulttenantid', 'default', '默认租户', '2021-06-23 07:14:03.000000', '2021-06-23 07:14:05.000000');

-- 系统配置
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (1, 'ec8bda8f239d4967b9ace3d425c829fb', 'default', 'system_name', '浙江能源', '2021-07-05 11:41:29.435932', '2021-07-05 11:41:29.435944');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (2, '4a3d2ecc528e4b77b3d518a81bd01335', 'default', 'copyright', '@2021-2023，南京赛宁信息技术有限公司，版权所有', '2021-07-05 11:41:29.436008', '2021-07-05 11:41:29.436010');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (3, 'bbd330a0fa7646fc937c8a875996ee8c', 'default', 'env_per_person', '12', '2021-07-05 11:41:29.436067', '2021-07-05 11:41:29.436070');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (4, '9efaf1686331402f814e266f740cba74', 'default', 'public_register', '3', '2021-07-05 11:41:29.436128', '2021-07-07 17:33:35.000000');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (5, 'b64cfb718fe64d7797d361aa8f806743', 'default', 'user_audit', '1', '2021-07-05 11:41:29.436235', '2021-07-14 15:13:46.000000');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (6, '3c20e18b430c4387aa3ac98dd0bebbfe', 'default', 'flag_length', '8', '2021-07-05 11:41:29.436292', '2021-07-05 11:41:29.436295');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (7, 'ecf0293f69d84dbdb430ebf96e323404', 'default', 'flag_prefix', '12345', '2021-07-05 11:41:29.436337', '2021-07-05 11:41:29.436339');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (8, '308666a1b1614a0bae80f838f35fff97', 'default', 'vlan_bucket', '100-200', '2021-07-05 11:41:29.436392', '2021-07-14 15:22:20.320849');



-- 菜单
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (1, 'course-folder', 'front_all', 'course-folder', 'default', '课程训练', '前台/课程训练', 1, '2021-07-28 14:08:10.750826000', '2021-07-28 14:08:10.750848000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (2, 'oj-practice', 'practice-folder', 'oj-practice', 'default', '理论题', '前台/攻防实训/理论题', 1, '2021-07-28 14:08:10.761360000', '2021-07-28 14:08:10.761383000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (3, 'oj-practice-capture-flag', 'practice-folder', 'oj-practice-capture-flag', 'default', '实操题', '前台/攻防实训/实操题', 1, '2021-07-28 14:08:10.765731000', '2021-07-28 14:08:10.765753000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (4, 'practice-folder', 'front_all', 'practice-folder', 'default', '攻防实训', '前台/攻防实训', 1, '2021-07-28 14:08:10.770376000', '2021-07-28 14:08:10.770398000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (5, 'competition-folder', 'front_all', 'competition-folder', 'default', '竞技演练', '前台/竞技演练', 1, '2021-07-28 14:08:10.775432000', '2021-07-28 14:08:10.775458000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (6, 'software-folder', 'front_all', 'software-folder', 'default', '靶场测试', '前台/靶场测试', 1, '2021-07-28 14:08:10.779732000', '2021-07-28 14:08:10.779755000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (7, 'red-vs-blue-folder', 'front_all', 'red-vs-blue-folder', 'default', '红蓝对抗', '前台/红蓝对抗', 1, '2021-07-28 14:08:10.784052000', '2021-07-28 14:08:10.784073000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (8, 'tool-magazine', 'recourse-center-folder', 'tool-magazine', 'default', '工具库', '前台/资源中心/工具库', 1, '2021-07-28 14:08:10.788443000', '2021-07-28 14:08:10.788465000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (9, 'vulnerability-library', 'recourse-center-folder', 'vulnerability-library', 'default', '漏洞库', '前台/资源中心/漏洞库', 1, '2021-07-28 14:08:10.792139000', '2021-07-28 14:08:10.792163000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (10, 'tool-exp', 'recourse-center-folder', 'tool-exp', 'default', 'EXP库', '前台/资源中心/EXP库', 1, '2021-07-28 14:08:10.795776000', '2021-07-28 14:08:10.795797000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (11, 'tool-shell', 'recourse-center-folder', 'tool-shell', 'default', 'ShellCode库', '前台/资源中心/ShellCode库', 1, '2021-07-28 14:08:10.799004000', '2021-07-28 14:08:10.799024000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (12, 'ghdb-library', 'recourse-center-folder', 'ghdb-library', 'default', 'GHDB库', '前台/资源中心/GHDB库', 1, '2021-07-28 14:08:10.802435000', '2021-07-28 14:08:10.802457000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (13, 'recourse-center-folder', 'front_all', 'recourse-center-folder', 'default', '资源中心', '前台/资源中心', 1, '2021-07-28 14:08:10.805727000', '2021-07-28 14:08:10.805745000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (14, 'personal-folder', 'front_all', 'personal-folder', 'default', '个人中心', '前台/个人中心', 1, '2021-07-28 14:08:10.809452000', '2021-07-28 14:08:10.809471000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (15, 'front_all', '', 'front_all', 'default', '前台', '前台/*', 1, '2021-07-28 14:08:10.813240000', '2021-07-28 14:08:10.813258000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (16, 'index', 'back_all', 'index', 'default', '仪表盘', '后台/仪表盘', 1, '2021-07-28 14:08:10.816309000', '2021-07-28 14:08:10.816327000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (17, 'courses', 'courses-manage-folder', 'courses', 'default', '课程管理', '后台/课程管理/课程管理', 1, '2021-07-28 14:08:10.820097000', '2021-07-28 14:08:10.820116000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (18, 'class-card', 'courses-manage-folder', 'class-card', 'default', '课程表', '后台/课程管理/课程表', 1, '2021-07-28 14:08:10.823106000', '2021-07-28 14:08:10.823125000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (19, 'learn-statistics', 'courses-manage-folder', 'learn-statistics', 'default', '学习统计', '后台/课程管理/学习统计', 1, '2021-07-28 14:08:10.826879000', '2021-07-28 14:08:10.826898000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (20, 'career-system', 'courses-manage-folder', 'career-system', 'default', '职业体系', '后台/课程管理/职业体系', 1, '2021-07-28 14:08:10.830046000', '2021-07-28 14:08:10.830062000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (21, 'courses-manage-folder', 'back_all', 'courses-manage-folder', 'default', '课程管理', '后台/课程管理', 1, '2021-07-28 14:08:10.832736000', '2021-07-28 14:08:10.832751000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (22, 'exercises', 'practices-folder', 'exercises', 'default', '习题集管理', '后台/练习题库/习题集管理', 1, '2021-07-28 14:08:10.836089000', '2021-07-28 14:08:10.836110000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (23, 'theory-base', 'practices-folder', 'theory-base', 'default', '理论基础', '后台/练习题库/理论基础', 1, '2021-07-28 14:08:10.839559000', '2021-07-28 14:08:10.839578000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (24, 'flags-solve', 'practices-folder', 'flags-solve', 'default', '实践操作', '后台/练习题库/实践操作', 1, '2021-07-28 14:08:10.843211000', '2021-07-28 14:08:10.843230000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (25, 'practices-folder', 'back_all', 'practices-folder', 'default', '练习题库', '后台/练习题库', 1, '2021-07-28 14:08:10.846357000', '2021-07-28 14:08:10.846373000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (26, 'ad-test-paper-list', 'item-bank-folder', 'ad-test-paper-list', 'default', '试卷库', '后台/竞赛题库/试卷库', 1, '2021-07-28 14:08:10.849356000', '2021-07-28 14:08:10.849378000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (27, 'ad-theory-list', 'item-bank-folder', 'ad-theory-list', 'default', '理论赛题目', '后台/竞赛题库/理论赛题目', 1, '2021-07-28 14:08:10.852522000', '2021-07-28 14:08:10.852539000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (28, 'ad-jeopardy-list', 'item-bank-folder', 'ad-jeopardy-list', 'default', '解题赛题目', '后台/竞赛题库/解题赛题目', 1, '2021-07-28 14:08:10.855413000', '2021-07-28 14:08:10.855429000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (29, 'ad-defense-list', 'item-bank-folder', 'ad-defense-list', 'default', '攻防赛题目', '后台/竞赛题库/攻防赛题目', 1, '2021-07-28 14:08:10.859401000', '2021-07-28 14:08:10.859420000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (30, 'ad-Dev-ops-list', 'item-bank-folder', 'ad-Dev-ops-list', 'default', '运维赛题目', '后台/竞赛题库/运维赛题目', 1, '2021-07-28 14:08:10.862544000', '2021-07-28 14:08:10.862562000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (31, 'ad-rw-list', 'item-bank-folder', 'ad-rw-list', 'default', 'RealWorld题目', '后台/竞赛题库/RealWorld题目', 1, '2021-07-28 14:08:10.865539000', '2021-07-28 14:08:10.865554000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (32, 'ad-penetration-list', 'item-bank-folder', 'ad-penetration-list', 'default', '渗透赛题目', '后台/竞赛题库/渗透赛题目', 1, '2021-07-28 14:08:10.868352000', '2021-07-28 14:08:10.868368000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (33, 'item-bank-folder', 'back_all', 'item-bank-folder', 'default', '竞赛题库', '后台/竞赛题库', 1, '2021-07-28 14:08:10.871899000', '2021-07-28 14:08:10.871917000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (34, 'image-manage', 'cr-standard-device-folder', 'image-manage', 'default', '镜像管理', '后台/靶标管理/镜像管理', 1, '2021-07-28 14:08:10.875820000', '2021-07-28 14:08:10.875840000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (35, 'real-manage', 'cr-standard-device-folder', 'real-manage', 'default', '实装管理', '后台/靶标管理/实装管理', 1, '2021-07-28 14:08:10.879286000', '2021-07-28 14:08:10.879305000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (36, 'TGM-manage', 'cr-standard-device-folder', 'TGM-manage', 'default', 'TGM管理', '后台/靶标管理/TGM管理', 1, '2021-07-28 14:08:10.882338000', '2021-07-28 14:08:10.882355000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (37, 'service-manage', 'cr-standard-device-folder', 'service-manage', 'default', '服务管理', '后台/靶标管理/服务管理', 1, '2021-07-28 14:08:10.885597000', '2021-07-28 14:08:10.885616000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (38, 'cr-standard-device-folder', 'back_all', 'cr-standard-device-folder', 'default', '靶标管理', '后台/靶标管理', 1, '2021-07-28 14:08:10.888535000', '2021-07-28 14:08:10.888550000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (39, 'cr-mission-arrange-folder', 'back_all', 'cr-mission-arrange-folder', 'default', '任务方案', '后台/任务方案', 1, '2021-07-28 14:08:10.892098000', '2021-07-28 14:08:10.892118000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (40, 'static-traffic-list', 'traffic-manage-folder', 'static-traffic-list', 'default', '背景流量', '后台/流量管理/背景流量', 1, '2021-07-28 14:08:10.895519000', '2021-07-28 14:08:10.895539000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (41, 'dynamic-traffic-list', 'traffic-manage-folder', 'dynamic-traffic-list', 'default', '攻击流量', '后台/流量管理/攻击流量', 1, '2021-07-28 14:08:10.898475000', '2021-07-28 14:08:10.898489000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (42, 'traffic-scheme-list', 'traffic-manage-folder', 'traffic-scheme-list', 'default', '流量集合', '后台/流量管理/流量集合', 1, '2021-07-28 14:08:10.901210000', '2021-07-28 14:08:10.901225000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (43, 'traffic-manage-folder', 'back_all', 'traffic-manage-folder', 'default', '流量管理', '后台/流量管理', 1, '2021-07-28 14:08:10.904252000', '2021-07-28 14:08:10.904270000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (44, 'cr-collection-system-folder', 'back_all', 'cr-collection-system-folder', 'default', '探针管理', '后台/探针管理', 1, '2021-07-28 14:08:10.907376000', '2021-07-28 14:08:10.907404000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (45, 'trainingScene', 'scene-folder', 'trainingScene', 'default', '训练场景编排', '后台/场景管理/训练场景编排', 1, '2021-07-28 14:08:10.910764000', '2021-07-28 14:08:10.910785000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (46, 'ad-scene-list', 'scene-folder', 'ad-scene-list', 'default', '渗透场景编排', '后台/场景管理/渗透场景编排', 1, '2021-07-28 14:08:10.913589000', '2021-07-28 14:08:10.913605000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (47, 'template', 'scene-folder', 'template', 'default', '对抗拓扑模板', '后台/场景管理/对抗拓扑模板', 1, '2021-07-28 14:08:10.916577000', '2021-07-28 14:08:10.916592000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (48, 'cr-event-list', 'scene-folder', 'cr-event-list', 'default', '对抗场景编排', '后台/场景管理/对抗场景编排', 1, '2021-07-28 14:08:10.919819000', '2021-07-28 14:08:10.919838000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (49, 'scene-folder', 'back_all', 'scene-folder', 'default', '场景管理', '后台/场景管理', 1, '2021-07-28 14:08:10.922675000', '2021-07-28 14:08:10.922693000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (50, 'ad-match-list', 'competition-terrain-folder', 'ad-match-list', 'default', '竞赛管理', '后台/赛事管理/竞赛管理', 1, '2021-07-28 14:08:10.925892000', '2021-07-28 14:08:10.925911000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (51, 'ad-theory-list-match', 'competition-terrain-folder', 'ad-theory-list-match', 'default', '理论赛赛场', '后台/赛事管理/理论赛赛场', 1, '2021-07-28 14:08:10.928975000', '2021-07-28 14:08:10.928991000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (52, 'ad-jeopardy-list-match', 'competition-terrain-folder', 'ad-jeopardy-list-match', 'default', '解题赛赛场', '后台/赛事管理/解题赛赛场', 1, '2021-07-28 14:08:10.931708000', '2021-07-28 14:08:10.931723000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (53, 'ad-layered-list-match', 'competition-terrain-folder', 'ad-layered-list-match', 'default', '闯关赛赛场', '后台/赛事管理/闯关赛赛场', 1, '2021-07-28 14:08:10.934720000', '2021-07-28 14:08:10.934737000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (54, 'ad-defense-list-match', 'competition-terrain-folder', 'ad-defense-list-match', 'default', '攻防赛赛场', '后台/赛事管理/攻防赛赛场', 1, '2021-07-28 14:08:10.937685000', '2021-07-28 14:08:10.937704000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (55, 'ad-Dev-ops-list-match', 'competition-terrain-folder', 'ad-Dev-ops-list-match', 'default', '运维赛赛场', '后台/赛事管理/运维赛赛场', 1, '2021-07-28 14:08:10.941064000', '2021-07-28 14:08:10.941081000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (56, 'ad-penetration-list-match', 'competition-terrain-folder', 'ad-penetration-list-match', 'default', '渗透赛赛场', '后台/赛事管理/渗透赛赛场', 1, '2021-07-28 14:08:10.944203000', '2021-07-28 14:08:10.944221000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (57, 'ad-rw-list-match', 'competition-terrain-folder', 'ad-rw-list-match', 'default', 'RealWorld赛场', '后台/赛事管理/RealWorld赛场', 1, '2021-07-28 14:08:10.946964000', '2021-07-28 14:08:10.946980000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (58, 'competition-terrain-folder', 'back_all', 'competition-terrain-folder', 'default', '赛事管理', '后台/赛事管理', 1, '2021-07-28 14:08:10.949463000', '2021-07-28 14:08:10.949480000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (59, 'tool', 'resource-manage-folder', 'tool', 'default', '工具库', '后台/资源管理/工具库', 1, '2021-07-28 14:08:10.952536000', '2021-07-28 14:08:10.952552000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (60, 'vulnerability', 'resource-manage-folder', 'vulnerability', 'default', '漏洞库', '后台/资源管理/漏洞库', 1, '2021-07-28 14:08:10.955351000', '2021-07-28 14:08:10.955368000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (61, 'resource-manage-folder', 'back_all', 'resource-manage-folder', 'default', '资源管理', '后台/资源管理', 1, '2021-07-28 14:08:10.958594000', '2021-07-28 14:08:10.958611000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (62, 'system-config', 'system-management-folder', 'system-config', 'default', '系统配置', '后台/系统管理/系统配置', 1, '2021-07-28 14:08:10.961869000', '2021-07-28 14:08:10.961886000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (63, 'audit-log', 'system-management-folder', 'audit-log', 'default', '审计日志', '后台/系统管理/审计日志', 1, '2021-07-28 14:08:10.964659000', '2021-07-28 14:08:10.964673000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (64, 'system-log', 'system-management-folder', 'system-log', 'default', '运行日志', '后台/系统管理/运行日志', 1, '2021-07-28 14:08:10.967286000', '2021-07-28 14:08:10.967300000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (65, 'user-manage', 'system-management-folder', 'user-manage', 'default', '用户管理', '后台/系统管理/用户管理', 1, '2021-07-28 14:08:10.970384000', '2021-07-28 14:08:10.970399000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (66, 'role-management', 'system-management-folder', 'role-management', 'default', '角色管理', '后台/系统管理/角色管理', 1, '2021-07-28 14:08:10.973651000', '2021-07-28 14:08:10.973667000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (67, 'ad-env-config', 'system-management-folder', 'ad-env-config', 'default', '赛题环境', '后台/系统管理/赛题环境', 1, '2021-07-28 14:08:10.977274000', '2021-07-28 14:08:10.977290000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (68, 'ad-team-list', 'system-management-folder', 'ad-team-list', 'default', '战队管理', '后台/系统管理/战队管理', 1, '2021-07-28 14:08:10.980367000', '2021-07-28 14:08:10.980380000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (69, 'system-management-folder', 'back_all', 'system-management-folder', 'default', '系统管理', '后台/系统管理', 1, '2021-07-28 14:08:10.983148000', '2021-07-28 14:08:10.983160000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (70, 'cluster-management', 'system-operation-folder', 'cluster-management', 'default', '计算实例管理', '后台/系统运维/计算实例管理', 1, '2021-07-28 14:08:10.986555000', '2021-07-28 14:08:10.986570000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (71, 'network-management', 'system-operation-folder', 'network-management', 'default', '网路实例管理', '后台/系统运维/网路实例管理', 1, '2021-07-28 14:08:10.989945000', '2021-07-28 14:08:10.989961000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (72, 'instance-monitoring', 'system-operation-folder', 'instance-monitoring', 'default', '靶场实例监控', '后台/系统运维/靶场实例监控', 1, '2021-07-28 14:08:10.993581000', '2021-07-28 14:08:10.993598000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (73, 'resource-usage', 'system-operation-folder', 'resource-usage', 'default', '资源用量管理', '后台/系统运维/资源用量管理', 1, '2021-07-28 14:08:10.996700000', '2021-07-28 14:08:10.996714000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (74, 'ad-MAC-manage', 'system-operation-folder', 'ad-MAC-manage', 'default', 'MAC地址管理', '后台/系统运维/MAC地址管理', 1, '2021-07-28 14:08:10.999396000', '2021-07-28 14:08:10.999409000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (75, 'resource-usage-management', 'system-operation-folder', 'resource-usage-management', 'default', '资源用量管理', '后台/系统运维/资源用量管理', 1, '2021-07-28 14:08:11.002597000', '2021-07-28 14:08:11.002612000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (76, 'cloud-platform-monitoring', 'system-operation-folder', 'cloud-platform-monitoring', 'default', '云平台监控', '后台/系统运维/云平台监控', 1, '2021-07-28 14:08:11.005647000', '2021-07-28 14:08:11.005661000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (77, 'system-operation-folder', 'back_all', 'system-operation-folder', 'default', '系统运维', '后台/系统运维', 1, '2021-07-28 14:08:11.009495000', '2021-07-28 14:08:11.009514000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (78, 'system-notice-folder', 'back_all', 'system-notice-folder', 'default', '系统公告', '后台/系统公告', 1, '2021-07-28 14:08:11.012814000', '2021-07-28 14:08:11.012827000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time) VALUES (79, 'back_all', '', 'back_all', 'default', '后台', '后台/*', 1, '2021-07-28 14:08:11.015776000', '2021-07-28 14:08:11.015789000');
