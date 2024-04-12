# v5.3.5
CREATE TABLE `mp_cscene_cscene`
(
    `pk`                  int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                  varchar(64)   NOT NULL COMMENT 'ID',
    `name`                varchar(255)  NOT NULL DEFAULT '' COMMENT '名称',
    `type`                int(10)       NOT NULL COMMENT '综合场景模板类型',
    `auth_type`           int(10)       NOT NULL COMMENT '授权类型',
    `reference_type`      int(10)       NOT NULL COMMENT '综合场景模板引用类型',
    `reference_meta_data` varchar(2048) NOT NULL COMMENT '综合场景模板引用元数据',
    `logo`                varchar(2048) NOT NULL COMMENT '综合场景模板logo',
    `scene_config`        varchar(64)   NOT NULL COMMENT '场景模板id',
    `create_user`         varchar(64)   NOT NULL COMMENT '创建用户id',
    `create_username`     varchar(64)   NOT NULL COMMENT '创建用户名称',
    `deleted`             tinyint(1)    NOT NULL COMMENT '是否删除',
    `create_time`         datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`         datetime(6)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='场景信息表';



CREATE TABLE `mp_cscene_cscene_instance`
(
    `pk`                  int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                  varchar(64)   NOT NULL COMMENT 'ID',
    `name`                varchar(255)  NOT NULL DEFAULT '' COMMENT '名称',
    `cscene`              varchar(64)   NOT NULL COMMENT '综合场景id',
    `type`                int(10)       NOT NULL COMMENT '综合场景实例类型',
    `reference_type`      int(10)       NOT NULL COMMENT '综合场景实例引用类型',
    `reference_meta_data` varchar(2048) NOT NULL COMMENT '综合场景实例引用元数据',
    `scene`               varchar(64)   NOT NULL COMMENT '场景实例id',
    `scene_ttl`           int(10)       NOT NULL COMMENT '场景实例存活时间',
    `scene_attr`          int(10)       NOT NULL COMMENT '场景实例属性',
    `create_user`         varchar(64)   NOT NULL COMMENT '创建用户id',
    `create_username`     varchar(64)   NOT NULL COMMENT '创建用户名称',
    `deleted`             tinyint(1)    NOT NULL COMMENT '是否删除',
    `create_time`         datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`         datetime(6)   NOT NULL COMMENT '更新时间',
    `pass_through`        text                   DEFAULT NULL COMMENT '透传字段',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='场景实例表';



CREATE TABLE `mp_cscene_cscene_instance_role`
(
    `pk`            int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64)   NOT NULL COMMENT 'ID',
    `cscene`        varchar(64)   NOT NULL COMMENT '综合场景实例id',
    `parent`        varchar(64)   NOT NULL COMMENT '父角色id',
    `key`           varchar(64)   NOT NULL COMMENT '角色key',
    `name`          varchar(64)   NOT NULL COMMENT '名称',
    `type`          varchar(64)   NOT NULL COMMENT '类型',
    `asset_nodes`   varchar(2048) NOT NULL COMMENT '资产节点',
    `visible_nodes` varchar(2048) NOT NULL COMMENT '可见节点',
    `users`         varchar(2048) NOT NULL COMMENT '包含用户',
    `create_time`   datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='场景实例角色表';



CREATE TABLE `mp_cscene_cscene_role`
(
    `pk`            int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64)   NOT NULL COMMENT 'ID',
    `cscene`        varchar(64)   NOT NULL COMMENT '综合场景id',
    `parent`        varchar(64)   NOT NULL COMMENT '父角色id',
    `key`           varchar(64)   NOT NULL COMMENT '角色key',
    `name`          varchar(64)   NOT NULL COMMENT '名称',
    `type`          varchar(64)   NOT NULL COMMENT '类型',
    `asset_nodes`   varchar(2048) NOT NULL COMMENT '资产节点',
    `visible_nodes` varchar(2048) NOT NULL COMMENT '可见节点',
    `users`         varchar(2048) NOT NULL COMMENT '包含用户',
    `create_time`   datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='场景角色表';



CREATE TABLE `mp_cscene_snapshot`
(
    `pk`                   int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64)  NOT NULL COMMENT 'ID',
    `cscene_id`            varchar(64)  NOT NULL COMMENT '场景模板',
    `cscene_instance_id`   varchar(64)  NOT NULL COMMENT '场景实例',
    `name`                 varchar(255) NOT NULL COMMENT '名称',
    `source_name`          varchar(255) NOT NULL COMMENT '源快照名称',
    `node_id`              varchar(64)  NOT NULL COMMENT '描述',
    `snapshot_id`          varchar(64)  NOT NULL COMMENT '场景数据库快照id',
    `snapshot_resource_id` varchar(64)  NOT NULL COMMENT '快照资源id',
    `deleted`              tinyint(1)   NOT NULL COMMENT '是否删除',
    `create_time`          datetime(6)  NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`cscene_id`)
) COMMENT ='场景快照表';



CREATE TABLE `mp_cscene_cscene_auth`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) DEFAULT NULL COMMENT '租户',
    `owner`       varchar(64) NOT NULL COMMENT '用户ID',
    `target`      varchar(64) NOT NULL COMMENT '场景模板id',
    `can_read`    tinyint(1)  DEFAULT NULL COMMENT '可读',
    `can_create`  tinyint(1)  DEFAULT NULL COMMENT '可创建',
    `can_update`  tinyint(1)  DEFAULT NULL COMMENT '可更新',
    `can_delete`  tinyint(1)  DEFAULT NULL COMMENT '可删除',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE KEY (`id`)
) COMMENT ='场景模板授权信息表';