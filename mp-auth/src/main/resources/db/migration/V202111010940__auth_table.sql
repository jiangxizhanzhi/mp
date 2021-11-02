# 菜单表
CREATE TABLE mp_auth_target_menu
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'id',
    `parent`      varchar(64)  NULL COMMENT '父菜单ID',
    `identifier`  varchar(64)  NOT NULL COMMENT '编号',
    `tenant`      varchar(64)  NOT NULL COMMENT '所属租户',
    `name`        varchar(64)  NOT NULL COMMENT '名称',
    `path`        varchar(255) NULL COMMENT '路径',
    `description` varchar(400) NOT NULL COMMENT '说明',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`)
) comment '权限（资源）表';


# api权限表
CREATE TABLE mp_auth_target_api
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'id',
    `identifier`  varchar(64)  NOT NULL COMMENT '编号',
    `tenant`      varchar(64)  NOT NULL COMMENT '所属租户',
    `name`        varchar(64)  NOT NULL COMMENT '名称',
    `path`        varchar(255) NULL COMMENT '路径',
    `description` varchar(400) NOT NULL COMMENT '说明',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`)
) comment '权限（资源）表';


# 数据权限表
CREATE TABLE mp_auth_target_data
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'id',
    `identifier`  varchar(64)  NOT NULL COMMENT '编号',
    `tenant`      varchar(64)  NOT NULL COMMENT '所属租户',
    `name`        varchar(64)  NOT NULL COMMENT '名称',
    `description` varchar(400) NOT NULL COMMENT '说明',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`)
) comment '权限（资源）表';


# 用户菜单资源权限表
CREATE TABLE mp_auth_user_permission_menu
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(用户)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
) comment '用户菜单权限表';


# 用户API资源权限表
CREATE TABLE mp_auth_user_permission_api
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(用户)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
) comment '用户api权限表';


# 用户DATA权限表
CREATE TABLE mp_auth_user_permission_data
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(用户)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
) comment '用户data权限表';
