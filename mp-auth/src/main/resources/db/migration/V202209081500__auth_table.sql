# v5.3.5
CREATE TABLE `mp_auth_client_details`
(
    `pk`                      int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `client_id`               varchar(128)     DEFAULT NULL COMMENT '客户端ID',
    `resource_ids`            varchar(256)     DEFAULT NULL COMMENT '资源id',
    `client_secret`           varchar(256)     DEFAULT NULL COMMENT '客户端秘钥',
    `type`                    int(11) NOT NULL DEFAULT 1 COMMENT '类型 1:Oauth2渠道 2:免认证渠道',
    `scope`                   varchar(256)     DEFAULT NULL COMMENT '权限范围',
    `authorized_grant_types`  varchar(256)     DEFAULT NULL COMMENT '授权类型',
    `web_server_redirect_uri` varchar(256)     DEFAULT NULL COMMENT '回跳地址',
    `authorities`             varchar(256)     DEFAULT NULL COMMENT '权限',
    `access_token_validity`   int(11)          DEFAULT NULL COMMENT 'access_token的有效期（秒）',
    `refresh_token_validity`  int(11)          DEFAULT NULL COMMENT 'refresh_token的有效期（秒）',
    `additional_information`  varchar(4096)    DEFAULT NULL COMMENT '预留字段',
    `autoapprove`             varchar(256)     DEFAULT NULL COMMENT '设置用户是否自动approve操作',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`client_id`)
) COMMENT ='客户端认证配置表';



CREATE TABLE `mp_auth_org`
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `tenant`      varchar(64)   NOT NULL DEFAULT '' COMMENT '所属租户',
    `parent`      varchar(64)   NOT NULL DEFAULT '' COMMENT '父组织',
    `identifier`  varchar(64)   NOT NULL COMMENT '编号',
    `name`        varchar(64)   NOT NULL COMMENT '名称',
    `path`        varchar(180)  NULL COMMENT '组织结构路径',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `identifier`)
) COMMENT ='组织表';



CREATE TABLE `mp_auth_org_permission`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(组织)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `owner`, `target`)
) COMMENT ='组织权限关系表';



CREATE TABLE `mp_auth_tenant_user`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL DEFAULT '' COMMENT '所属租户',
    `org`         varchar(64) NOT NULL COMMENT '组织ID',
    `user`        varchar(64) NOT NULL COMMENT '用户ID',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`user`, `org`),
    KEY (`org`, `user`)
) COMMENT ='组织用户关系表';



CREATE TABLE `mp_auth_role`
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `tenant`      varchar(64)   NOT NULL DEFAULT '' COMMENT '所属租户',
    `org`         varchar(64)   NOT NULL DEFAULT '' COMMENT '所属组织',
    `identifier`  varchar(64)   NOT NULL COMMENT '编号',
    `name`        varchar(64)   NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `identifier`),
    UNIQUE KEY (`tenant`, `name`)
) COMMENT ='角色表';



CREATE TABLE `mp_auth_role_permission`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(角色ID)',
    `target`      varchar(64) NOT NULL COMMENT '权限ID',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `owner`, `target`)
) COMMENT ='角色权限关系表';



CREATE TABLE `mp_auth_target`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `parent`      varchar(64)          DEFAULT NULL COMMENT '父权限ID',
    `identifier`  varchar(64) NOT NULL COMMENT '编号',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `name`        varchar(64) NOT NULL COMMENT '名称',
    `path`        varchar(255)         DEFAULT NULL COMMENT '路径',
    `type`        int(11)     NOT NULL COMMENT '资源类型 1:菜单, 2:API, 3:数据',
    `visible`     int(11)     NOT NULL DEFAULT 1 COMMENT '是否可见 1:可见，0:不可见（受控于激活选项） 默认为可见',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`identifier`)
) COMMENT ='权限（资源）表';



CREATE TABLE `mp_auth_tenant`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `identifier`  varchar(64) NOT NULL COMMENT '编号',
    `name`        varchar(64) NOT NULL COMMENT '名称',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`identifier`)
) COMMENT ='租户表';



CREATE TABLE `mp_auth_user`
(
    `pk`           int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(64)   NOT NULL COMMENT 'ID',
    `tenant`       varchar(64)   NOT NULL DEFAULT '' COMMENT '原生租户',
    `username`     varchar(64)   NOT NULL COMMENT '用户名',
    `identifier`   varchar(64)            COMMENT '编号',
    `password`     varchar(128)  NOT NULL COMMENT '密码',
    `token_secret` varchar(128)  NOT NULL COMMENT 'token密钥',
    `name`         varchar(128)  NOT NULL COMMENT '姓名',
    `nickname`     varchar(128)  NOT NULL COMMENT '昵称',
    `avatar`       varchar(2048) NOT NULL COMMENT '头像',
    `birthday`     date          NOT NULL COMMENT '生日',
    `email`        varchar(255)  NOT NULL COMMENT '邮箱',
    `mobile`       varchar(64)   NOT NULL COMMENT '手机号',
    `id_card`      varchar(64)   NOT NULL COMMENT '身份证号',
    `address`      varchar(255)  NOT NULL COMMENT '地址',
    `is_enabled`   int(11)       NOT NULL COMMENT '1:启用 0：禁用',
    `is_audited`   int(11)       NOT NULL COMMENT '1:已审核 0：待审核',
    `init_check`   int(11)       NOT NULL DEFAULT 0 COMMENT '是否首次登陆需要强制修改密码 0:否，1:是',
    `description`  varchar(1024)          DEFAULT NULL COMMENT '描述',
    `date_joined`  datetime               DEFAULT NULL COMMENT '加入时间',
    `expired_time` datetime      NOT NULL COMMENT '过期时间',
    `last_login`   datetime               DEFAULT NULL COMMENT '最后登录时间',
    `create_time`  datetime      NOT NULL COMMENT '创建时间',
    `update_time`  datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`username`),
    KEY (`update_time`),
    KEY (`identifier`)
) COMMENT ='用户表';



CREATE TABLE `mp_auth_user_online_record`
(
    `pk`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64) NOT NULL COMMENT 'ID',
    `user`             varchar(64) NOT NULL COMMENT '用户ID',
    `tenant`           varchar(64) NOT NULL COMMENT '所属租户',
    `start_time`       datetime    NOT NULL COMMENT '开始时间',
    `end_time`         datetime             COMMENT '结束时间',
    `duration_minutes` int(11)     NOT NULL DEFAULT 0 COMMENT '本轮累计在线分钟数',
    `status`           int(11)     NOT NULL DEFAULT 0 COMMENT '记录完成与否 0:未完成 1:已完成',
    `create_time`      datetime    NOT NULL COMMENT '创建时间',
    `update_time`      datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`user`),
    KEY (`end_time`, `status`),
    KEY (`start_time`, `end_time`, `status`),
    KEY (`user`, `end_time`)
) COMMENT ='用户在线记录表';



CREATE TABLE `mp_auth_user_permission`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(用户ID)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `owner`, `target`)
) COMMENT ='用户权限关系表';



CREATE TABLE `mp_auth_user_role`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `user`        varchar(64) NOT NULL COMMENT '用户ID',
    `role`        varchar(64) NOT NULL COMMENT '角色ID',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `user`, `role`),
    KEY (`user`, `role`),
    KEY (`role`, `user`)
) COMMENT ='用户角色关系表';
