CREATE TABLE mp_cscene_cscene (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `type` int(10) NOT NULL COMMENT '综合场景模板类型',
    `scene_config` varchar(64) NOT NULL COMMENT '场景模板id',
    `deleted` tinyint(1) NOT NULL COMMENT '是否删除',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);

CREATE TABLE mp_cscene_cscene_role (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `cscene` varchar(64) NOT NULL COMMENT '综合场景id',
    `parent` varchar(64) NOT NULL COMMENT '父角色id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `asset_nodes` varchar(2048) NOT NULL COMMENT '资产节点',
    `visible_nodes` varchar(2048) NOT NULL COMMENT '可见节点',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);

CREATE TABLE mp_cscene_cscene_instance (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `cscene` varchar(64) NOT NULL COMMENT '综合场景id',
    `type` int(10) NOT NULL COMMENT '综合场景实例类型',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `deleted` tinyint(1) NOT NULL COMMENT '是否删除',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);

CREATE TABLE mp_cscene_cscene_instance_role (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `cscene` varchar(64) NOT NULL COMMENT '综合场景实例id',
    `parent` varchar(64) NOT NULL COMMENT '父角色id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `asset_nodes` varchar(2048) NOT NULL COMMENT '资产节点',
    `visible_nodes` varchar(2048) NOT NULL COMMENT '可见节点',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);
