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
    `path` varchar(512) NOT NULL COMMENT '组织结构路径',
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
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `identifier`)
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
    `status` int(10) NOT NULL COMMENT '账号状态 1：正常  2：禁用  3：挂起（待审批） 4:过期',
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
    client_id VARCHAR(128),
    resource_ids VARCHAR(256),
    client_secret VARCHAR(256),
    type int not null default 1 comment '类型 1:Oauth2渠道 2:免认证渠道',
    scope VARCHAR(256),
    authorized_grant_types VARCHAR(256),
    web_server_redirect_uri VARCHAR(256),
    authorities VARCHAR(256),
    access_token_validity INTEGER,
    refresh_token_validity INTEGER,
    additional_information VARCHAR(4096),
    autoapprove VARCHAR(256),
    PRIMARY KEY (`pk`),
    UNIQUE (`client_id`)
) COMMENT '渠道认证配置表';


# 系统配置信息表
CREATE TABLE mp_sys_config (
         `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
         `id` varchar(64)  NOT NULL COMMENT 'id',
         `tenant` varchar(64) NOT NULL COMMENT '租户',
         `key_name` varchar(256)  NULL COMMENT '键',
         `value` varchar(1024)  NULL COMMENT '值',
         `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
         `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
         PRIMARY KEY (`pk`),
         UNIQUE (`id`)
) COMMENT '其他配置信息表';


