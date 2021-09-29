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
) comment '租户表';

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
) comment '组织表';

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
) comment '角色表';

# 用户表
CREATE TABLE mp_auth_user (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64)  NOT NULL COMMENT '原生租户' default '',
    `identifier` varchar(64)  NULL COMMENT '编号',
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
    `res` varchar(255) NULL COMMENT '保留字段',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`),
    UNIQUE (`username`)
) comment '用户表';

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
) comment '租户用户表';


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
) comment '用户心跳表';
create index idx_user_hb_uid on mp_auth_user_heartbeat (user);
create index idx_user_hb_time on mp_auth_user_heartbeat (create_time);


# 用户在线报告统计表
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
) comment '用户在线报告统计表';
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
) comment '用户角色表';

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
) comment '权限（资源）表';

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
) comment '组织权限表';

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
) comment '角色权限表';

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
) comment '用户权限表';



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
) comment '组织用户表';


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
) comment '客户端认证配置表';


# 用户默认头像
CREATE TABLE mp_auth_avatar (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64)  NOT NULL COMMENT 'id',
       `url` varchar(1024)  NULL COMMENT '头像url',
       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
       PRIMARY KEY (`pk`),
       UNIQUE (`id`)
) COMMENT '头像表';


# 系统配置信息表
CREATE TABLE mp_sys_config
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '租户',
    `key_name`    varchar(100)  NULL COMMENT '键',
    `value`       varchar(8000) NULL COMMENT '值',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `key_name`)
) COMMENT '系统配置信息表';




# 系统公告
CREATE TABLE mp_sys_notice (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64)  NOT NULL COMMENT 'id',
       `tenant` varchar(64) NOT NULL COMMENT '租户',
       `content` text NULL COMMENT '公告内容',
       `title` varchar(150) NOT NULL COMMENT '标题',
       `publisher` varchar(64) NOT NULL COMMENT '发布人',
       `is_public` int NOT NULL default 0 COMMENT '是否公开，对所有人可见  0:否 1:是 默认是0',
       `type` int NOT NULL default 1 COMMENT '公告类型 1:系统、2:课程、3:练习、4:考试',
       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
       PRIMARY KEY (`pk`),
       UNIQUE (`id`),
       KEY (`title`)
) COMMENT '系统公告';



# 系统公告-组织表
CREATE TABLE mp_sys_notice_org (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64)  NOT NULL COMMENT 'id',
       `tenant` varchar(64) NOT NULL COMMENT '租户',
       `notice_id` varchar(64) NOT NULL COMMENT '公告ID',
       `org` varchar(64) NULL COMMENT '组织id',
       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
       PRIMARY KEY (`pk`),
       UNIQUE (`id`),
       KEY (`org`, `notice_id`)
) COMMENT '系统公告-组织表';


# 系统公告-角色表
CREATE TABLE mp_sys_notice_role (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64)  NOT NULL COMMENT 'id',
       `tenant` varchar(64) NOT NULL COMMENT '租户',
       `notice_id` varchar(64) NOT NULL COMMENT '公告ID',
       `role` varchar(64) NULL COMMENT '角色id',
       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
       PRIMARY KEY (`pk`),
       UNIQUE (`id`),
       KEY (`role`, `notice_id`)
) COMMENT '系统公告-角色表';


# 系统公告消息(已读/推送)统计表
CREATE TABLE mp_sys_notice_read (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `tenant` varchar(64) NOT NULL COMMENT '租户',
    `notice_id` varchar(64) NOT NULL COMMENT '公告ID',
    `user` varchar(64) NULL COMMENT '读者',
    `is_readed` int NOT NULL DEFAULT 1 COMMENT '是否已读 1:已读 0:未读  首次创建一条数据即为已读',
    `is_need_push` int NOT NULL DEFAULT 1 COMMENT '是否推送本条公告 1:是（需要推送）； 0:否（不再推送）默认为1',
    `read_count` int NOT NULL DEFAULT 1 COMMENT '已读次数 首次创建一条数据即为已读次数为1',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE (`id`),
     KEY (`notice_id`),
     KEY (`user`, `notice_id`)
) COMMENT '系统公告消息(已读/推送)统计表';






-- 默认管理员用户，首次登陆需修改密码
INSERT INTO mp_auth_user (id, tenant, identifier, username, password, token_secret, name, nickname, avatar, birthday, email, mobile, id_card, address, is_enabled, is_audited, init_check, description, date_joined, expired_time, last_login, create_time, update_time) VALUES ('3f655d87112f4ae094bbaf70830e20aa', 'default', 'admin', 'admin', '1f670a02678a6cbcc9d38ce57f97f4a3724574dfa3a69b73f2930586110b4619265c87c9874df4cc', '351672ff8854438eaaa48c46efdfa947', '管理员', '管理员', '', '2021-06-23', 'example@123.com', '13988888888', '', '北京路101号', 1, 1, 1, '', '2021-06-23 09:37:29.084709', '2121-07-29 00:00:00.000000', '2021-06-23 09:37:29.084667', '2021-06-23 09:37:29.084805', '2021-07-08 20:16:47.878994');

-- 默认管理员角色
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description, create_time, update_time) VALUES ('5827ff7847504ffd806e0a85c417228d', 'default', 'default', 'ADMIN', '管理员', '管理员角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description, create_time, update_time) VALUES ('e12bb08a9bc545bbba46a2fb3c92533a', 'default', 'default', 'SUPERUSER', '超级用户', '超级用户角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');
-- 特定需求角色？
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description, create_time, update_time) VALUES ('7454c07d331841f4a58b17e4fb933b94', 'default', 'default', 'TEACHER', '教员', '教员角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');
INSERT INTO mp_auth_role (id, tenant, org, identifier, name, description, create_time, update_time) VALUES ('1e9748bd16c4406f8a5790b645b6c5d1', 'default', 'default', 'STUDENT', '学员', '学员角色', '2021-06-23 06:20:05.000000', '2021-07-08 20:59:14.037209');


-- 用户-角色关系
INSERT INTO mp_auth_user_role (id, tenant, user, role, create_time, update_time) VALUES ('791088e33dd44de3899f3c105597c354', 'default', '3f655d87112f4ae094bbaf70830e20aa', '5827ff7847504ffd806e0a85c417228d', '2021-07-08 20:16:47.896726', '2021-07-08 20:16:47.896733');
INSERT INTO mp_auth_user_role (id, tenant, user, role, create_time, update_time) VALUES ('088e79133dd44de105597c3543899f3c', 'default', '3f655d87112f4ae094bbaf70830e20aa', 'e12bb08a9bc545bbba46a2fb3c92533a', '2021-07-08 20:16:47.896726', '2021-07-08 20:16:47.896733');

-- 客户端授权
INSERT INTO mp_auth_client_details (client_id, resource_ids, client_secret, type, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove) VALUES ('84261d495c414e', 'all', '2112f4b855264ad7b1a4209737fc7836', 2, 'all', 'all', null, null, null, null, null, null);

-- 组织
INSERT INTO mp_auth_org (id, tenant, parent, identifier, name, path, description, create_time, update_time) VALUES ('a13262d600fe40c799d63dce5c1485af', 'default', '', 'default-org', '一级组织', '一级组织', '一级组织', '2021-07-21 09:36:33.835950', '2021-07-21 10:13:33.281078');

-- 用户-组织
INSERT INTO mp_auth_org_user (id, tenant, org, user, create_time, update_time) VALUES ('06c3e74d568a4937812c8f9ba6ef5df9', 'default', 'a13262d600fe40c799d63dce5c1485af', '3f655d87112f4ae094bbaf70830e20aa', '2021-07-21 21:47:14.517567', '2021-07-21 21:47:14.517572');

-- 租户
INSERT INTO mp_auth_tenant (id, identifier, name, create_time, update_time) VALUES ('defaulttenantid', 'default', '默认租户', '2021-06-23 07:14:03.000000', '2021-06-23 07:14:05.000000');

-- 系统配置
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (1, 'ec8bda8f239d4967b9ace3d425c829fb', 'default', 'system_name', '浙江能源', '2021-07-05 11:41:29.435932', '2021-07-05 11:41:29.435944');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time) VALUES (2, '4a3d2ecc528e4b77b3d518a81bd01335', 'default', 'copyright', '@2021-2023，南京赛宁信息技术有限公司，版权所有', '2021-07-05 11:41:29.436008', '2021-07-05 11:41:29.436010');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (3, 'bbd330a0fa7646fc937c8a875996ee8c', 'default', 'env_per_person', '12', '2021-07-05 11:41:29.436067',
        '2021-07-05 11:41:29.436070');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (4, '9efaf1686331402f814e266f740cba74', 'default', 'public_register', '0', '2021-07-05 11:41:29.436128',
        '2021-07-07 17:33:35.000000');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (5, 'b64cfb718fe64d7797d361aa8f806743', 'default', 'user_audit', '1', '2021-07-05 11:41:29.436235',
        '2021-07-14 15:13:46.000000');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (6, '3c20e18b430c4387aa3ac98dd0bebbfe', 'default', 'flag_length', '8', '2021-07-05 11:41:29.436292',
        '2021-07-05 11:41:29.436295');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (7, 'ecf0293f69d84dbdb430ebf96e323404', 'default', 'flag_prefix', '12345', '2021-07-05 11:41:29.436337',
        '2021-07-05 11:41:29.436339');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (8, '308666a1b1614a0bae80f838f35fff97', 'default', 'vlan_bucket', '100-200', '2021-07-05 11:41:29.436392',
        '2021-07-14 15:22:20.320849');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (9, 'x23fasdbda1b161bae80f833ac98dd0b', 'default', 'logo', '', '2021-07-05 11:41:29', '2021-07-14 15:22:20');
INSERT INTO mp_sys_config (pk, id, tenant, key_name, value, create_time, update_time)
VALUES (10, 'q03a9asdbd0f833ac98dd0ba1b161ba8', 'default', 'vpn_packages', '', '2021-07-05 11:41:29.000000',
        '2021-07-14 15:22:20.000000');

-- 菜单
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (1, 'course-folder', 'front_all', 'course-folder', 'default', '课程训练', '前台/课程训练', 1,
        '2021-09-29 13:51:14.285853000', '2021-09-29 13:51:14.285896000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (2, 'oj-practice', 'practice-folder', 'oj-practice', 'default', '理论题', '前台/攻防实训/理论题', 1,
        '2021-09-29 13:51:14.332790000', '2021-09-29 13:51:14.332814000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (3, 'oj-practice-capture-flag', 'practice-folder', 'oj-practice-capture-flag', 'default', '实操题', '前台/攻防实训/实操题',
        1, '2021-09-29 13:51:14.340784000', '2021-09-29 13:51:14.340810000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (4, 'practice-folder', 'front_all', 'practice-folder', 'default', '攻防实训', '前台/攻防实训', 1,
        '2021-09-29 13:51:14.348462000', '2021-09-29 13:51:14.348487000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (5, 'single-list', 'comprehensive-training-folder', 'single-list', 'default', '单兵训练', '前台/综合训练/单兵训练', 1,
        '2021-09-29 13:51:14.356754000', '2021-09-29 13:51:14.356778000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (6, 'group-training', 'comprehensive-training-folder', 'group-training', 'default', '团队训练', '前台/综合训练/团队训练', 1,
        '2021-09-29 13:51:14.365174000', '2021-09-29 13:51:14.365200000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (7, 'comprehensive-training-folder', 'front_all', 'comprehensive-training-folder', 'default', '综合训练', '前台/综合训练',
        1, '2021-09-29 13:51:14.372786000', '2021-09-29 13:51:14.372810000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (8, 'red-vs-blue-web-folder', 'front_all', 'red-vs-blue-web-folder', 'default', '红蓝对抗', '前台/红蓝对抗', 1,
        '2021-09-29 13:51:14.381474000', '2021-09-29 13:51:14.381496000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (9, 'software-folder', 'front_all', 'software-folder', 'default', '靶场测试', '前台/靶场测试', 1,
        '2021-09-29 13:51:14.389240000', '2021-09-29 13:51:14.389268000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (10, 'tool-magazine', 'recourse-center-folder', 'tool-magazine', 'default', '工具库', '前台/资源中心/工具库', 1,
        '2021-09-29 13:51:14.396406000', '2021-09-29 13:51:14.396427000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (11, 'vulnerability-library', 'recourse-center-folder', 'vulnerability-library', 'default', '漏洞库', '前台/资源中心/漏洞库',
        1, '2021-09-29 13:51:14.403882000', '2021-09-29 13:51:14.403904000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (12, 'tool-exp', 'recourse-center-folder', 'tool-exp', 'default', 'EXP库', '前台/资源中心/EXP库', 1,
        '2021-09-29 13:51:14.412630000', '2021-09-29 13:51:14.412650000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (13, 'tool-shell', 'recourse-center-folder', 'tool-shell', 'default', 'ShellCode库', '前台/资源中心/ShellCode库', 1,
        '2021-09-29 13:51:14.417673000', '2021-09-29 13:51:14.417695000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (14, 'ghdb-library', 'recourse-center-folder', 'ghdb-library', 'default', 'GHDB库', '前台/资源中心/GHDB库', 1,
        '2021-09-29 13:51:14.429950000', '2021-09-29 13:51:14.429970000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (15, 'recourse-center-folder', 'front_all', 'recourse-center-folder', 'default', '资源中心', '前台/资源中心', 1,
        '2021-09-29 13:51:14.439282000', '2021-09-29 13:51:14.439302000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (16, 'personal-folder', 'front_all', 'personal-folder', 'default', '个人中心', '前台/个人中心', 1,
        '2021-09-29 13:51:14.447953000', '2021-09-29 13:51:14.447972000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (17, 'front_all', '', 'front_all', 'default', '前台', '前台/*', 1, '2021-09-29 13:51:14.455891000',
        '2021-09-29 13:51:14.455911000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (18, 'index', 'back_all', 'index', 'default', '仪表盘', '后台/仪表盘', 1, '2021-09-29 13:51:14.463106000',
        '2021-09-29 13:51:14.463129000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (19, 'courses', 'courses-manage-folder', 'courses', 'default', '课程管理', '后台/课程管理/课程管理', 1,
        '2021-09-29 13:51:14.471292000', '2021-09-29 13:51:14.471313000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (20, 'class-card', 'courses-manage-folder', 'class-card', 'default', '课程表', '后台/课程管理/课程表', 1,
        '2021-09-29 13:51:14.478586000', '2021-09-29 13:51:14.478607000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (21, 'learn-statistics', 'courses-manage-folder', 'learn-statistics', 'default', '学习统计', '后台/课程管理/学习统计', 1,
        '2021-09-29 13:51:14.486469000', '2021-09-29 13:51:14.486491000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (22, 'career-system', 'courses-manage-folder', 'career-system', 'default', '职业框架管理', '后台/课程管理/职业框架管理', 1,
        '2021-09-29 13:51:14.493632000', '2021-09-29 13:51:14.493652000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (23, 'courses-manage-folder', 'back_all', 'courses-manage-folder', 'default', '课程管理', '后台/课程管理', 1,
        '2021-09-29 13:51:14.500476000', '2021-09-29 13:51:14.500497000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (24, 'exercises', 'practices-folder', 'exercises', 'default', '习题集管理', '后台/练习题库/习题集管理', 1,
        '2021-09-29 13:51:14.505097000', '2021-09-29 13:51:14.505117000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (25, 'theory-base', 'practices-folder', 'theory-base', 'default', '理论基础', '后台/练习题库/理论基础', 1,
        '2021-09-29 13:51:14.509781000', '2021-09-29 13:51:14.509802000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (26, 'flags-solve', 'practices-folder', 'flags-solve', 'default', '实践操作', '后台/练习题库/实践操作', 1,
        '2021-09-29 13:51:14.514220000', '2021-09-29 13:51:14.514250000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (27, 'practices-folder', 'back_all', 'practices-folder', 'default', '练习题库', '后台/练习题库', 1,
        '2021-09-29 13:51:14.519652000', '2021-09-29 13:51:14.519674000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (28, 'image-manage', 'cr-standard-device-folder', 'image-manage', 'default', '镜像管理', '后台/靶标管理/镜像管理', 1,
        '2021-09-29 13:51:14.524167000', '2021-09-29 13:51:14.524188000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (29, 'real-manage', 'cr-standard-device-folder', 'real-manage', 'default', '实装管理', '后台/靶标管理/实装管理', 1,
        '2021-09-29 13:51:14.528387000', '2021-09-29 13:51:14.528406000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (30, 'service-manage', 'cr-standard-device-folder', 'service-manage', 'default', '服务管理', '后台/靶标管理/服务管理', 1,
        '2021-09-29 13:51:14.533072000', '2021-09-29 13:51:14.533095000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (31, 'cr-standard-device-folder', 'back_all', 'cr-standard-device-folder', 'default', '靶标管理', '后台/靶标管理', 1,
        '2021-09-29 13:51:14.538039000', '2021-09-29 13:51:14.538060000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (32, 'cr-mission-arrange-folder', 'back_all', 'cr-mission-arrange-folder', 'default', '任务方案', '后台/任务方案', 1,
        '2021-09-29 13:51:14.542325000', '2021-09-29 13:51:14.542345000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (33, 'static-traffic-list', 'traffic-manage-folder', 'static-traffic-list', 'default', '背景流量', '后台/流量管理/背景流量', 1,
        '2021-09-29 13:51:14.546173000', '2021-09-29 13:51:14.546190000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (34, 'dynamic-traffic-list', 'traffic-manage-folder', 'dynamic-traffic-list', 'default', '攻击流量', '后台/流量管理/攻击流量',
        1, '2021-09-29 13:51:14.550694000', '2021-09-29 13:51:14.550717000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (35, 'traffic-scheme-list', 'traffic-manage-folder', 'traffic-scheme-list', 'default', '流量集合', '后台/流量管理/流量集合', 1,
        '2021-09-29 13:51:14.555368000', '2021-09-29 13:51:14.555390000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (36, 'traffic-manage-folder', 'back_all', 'traffic-manage-folder', 'default', '流量管理', '后台/流量管理', 1,
        '2021-09-29 13:51:14.559516000', '2021-09-29 13:51:14.559535000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (37, 'cr-collection-system-folder', 'back_all', 'cr-collection-system-folder', 'default', '探针管理', '后台/探针管理', 1,
        '2021-09-29 13:51:14.565776000', '2021-09-29 13:51:14.565800000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (38, 'cr-individual-training', 'cr-training-list-folder', 'cr-individual-training', 'default', '单兵训练',
        '后台/综合训练/单兵训练', 1, '2021-09-29 13:51:14.570526000', '2021-09-29 13:51:14.570582000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (39, 'cr-team-training', 'cr-training-list-folder', 'cr-team-training', 'default', '团队训练', '后台/综合训练/团队训练', 1,
        '2021-09-29 13:51:14.575137000', '2021-09-29 13:51:14.575157000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (40, 'cr-training-list-folder', 'back_all', 'cr-training-list-folder', 'default', '综合训练', '后台/综合训练', 1,
        '2021-09-29 13:51:14.579086000', '2021-09-29 13:51:14.579103000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (41, 'red-vs-blue-folder', 'back_all', 'red-vs-blue-folder', 'default', '红蓝对抗', '后台/红蓝对抗', 1,
        '2021-09-29 13:51:19.559162000', '2021-09-29 13:51:19.559184000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (42, 'tool', 'resource-manage-folder', 'tool', 'default', '工具库', '后台/资源管理/工具库', 1,
        '2021-09-29 13:51:19.566995000', '2021-09-29 13:51:19.567016000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (43, 'vulnerability', 'resource-manage-folder', 'vulnerability', 'default', '漏洞库', '后台/资源管理/漏洞库', 1,
        '2021-09-29 13:51:19.572319000', '2021-09-29 13:51:19.572339000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (44, 'resource-manage-folder', 'back_all', 'resource-manage-folder', 'default', '资源管理', '后台/资源管理', 1,
        '2021-09-29 13:51:19.577464000', '2021-09-29 13:51:19.577483000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (45, 'system-config', 'system-management-folder', 'system-config', 'default', '系统配置', '后台/系统管理/系统配置', 1,
        '2021-09-29 13:51:19.582122000', '2021-09-29 13:51:19.582142000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (46, 'audit-log', 'system-management-folder', 'audit-log', 'default', '审计日志', '后台/系统管理/审计日志', 1,
        '2021-09-29 13:51:19.586907000', '2021-09-29 13:51:19.586925000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (47, 'system-log', 'system-management-folder', 'system-log', 'default', '运行日志', '后台/系统管理/运行日志', 1,
        '2021-09-29 13:51:19.591756000', '2021-09-29 13:51:19.591774000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (48, 'user-manage', 'system-management-folder', 'user-manage', 'default', '用户管理', '后台/系统管理/用户管理', 1,
        '2021-09-29 13:51:19.596781000', '2021-09-29 13:51:19.596799000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (49, 'role-management', 'system-management-folder', 'role-management', 'default', '角色管理', '后台/系统管理/角色管理', 1,
        '2021-09-29 13:51:19.599804000', '2021-09-29 13:51:19.599831000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (50, 'system-management-folder', 'back_all', 'system-management-folder', 'default', '系统管理', '后台/系统管理', 1,
        '2021-09-29 13:51:19.602403000', '2021-09-29 13:51:19.602428000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (51, 'cluster-management', 'system-operation-folder', 'cluster-management', 'default', '计算实例管理',
        '后台/系统运维/计算实例管理', 1, '2021-09-29 13:51:19.604722000', '2021-09-29 13:51:19.604737000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (52, 'network-management', 'system-operation-folder', 'network-management', 'default', '网路实例管理',
        '后台/系统运维/网路实例管理', 1, '2021-09-29 13:51:19.606707000', '2021-09-29 13:51:19.606721000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (53, 'real-equipment-management', 'system-operation-folder', 'real-equipment-management', 'default', '实装设备管理',
        '后台/系统运维/实装设备管理', 1, '2021-09-29 13:51:19.608643000', '2021-09-29 13:51:19.608657000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (54, 'instance-monitoring', 'system-operation-folder', 'instance-monitoring', 'default', '靶场实例监控',
        '后台/系统运维/靶场实例监控', 1, '2021-09-29 13:51:19.610476000', '2021-09-29 13:51:19.610490000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (55, 'cloud-platform-monitoring', 'system-operation-folder', 'cloud-platform-monitoring', 'default', '云平台监控',
        '后台/系统运维/云平台监控', 1, '2021-09-29 13:51:19.612459000', '2021-09-29 13:51:19.612473000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (56, 'resource-usage-management', 'system-operation-folder', 'resource-usage-management', 'default', '资源用量管理',
        '后台/系统运维/资源用量管理', 1, '2021-09-29 13:51:19.614466000', '2021-09-29 13:51:19.614483000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (57, 'system-operation-folder', 'back_all', 'system-operation-folder', 'default', '系统运维', '后台/系统运维', 1,
        '2021-09-29 13:51:19.616683000', '2021-09-29 13:51:19.616699000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (58, 'system-notice-folder', 'back_all', 'system-notice-folder', 'default', '系统公告', '后台/系统公告', 1,
        '2021-09-29 13:51:19.618715000', '2021-09-29 13:51:19.618732000');
INSERT INTO mp_auth_target (pk, id, parent, identifier, tenant, name, path, type, create_time, update_time)
VALUES (59, 'back_all', '', 'back_all', 'default', '后台', '后台/*', 1, '2021-09-29 13:51:19.620815000',
        '2021-09-29 13:51:19.620828000');
