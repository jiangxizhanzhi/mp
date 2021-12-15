alter table mp_scene_scene_config_area modify column `position2` varchar(128) NOT NULL COMMENT '2d坐标' ;
alter table mp_scene_scene_config_area modify column `position3` varchar(128) NOT NULL COMMENT '3d坐标' ;

alter table mp_scene_scene_area modify column `position2` varchar(128) NOT NULL COMMENT '2d坐标' ;
alter table mp_scene_scene_area modify column `position3` varchar(128) NOT NULL COMMENT '3d坐标' ;

CREATE TABLE mp_scene_scene_config_piece (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `node_id` varchar(64) NOT NULL COMMENT '节点id',
    `area` varchar(64) NOT NULL COMMENT '所属区域节点id',
    `icon2` varchar(2048) NOT NULL COMMENT '2d图标地址',
    `position2` varchar(128) NOT NULL COMMENT '2d坐标',
    `icon3` varchar(2048) NOT NULL COMMENT '3d图标地址',
    `position3` varchar(128) NOT NULL COMMENT '3d坐标',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `type` varchar(64) NOT NULL COMMENT '碎片类型',
    `display_context` varchar(2048) NOT NULL COMMENT '显示上下文',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `node_id`)
);


CREATE TABLE mp_scene_scene_piece (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `node_id` varchar(64) NOT NULL COMMENT '节点id',
    `area` varchar(64) NOT NULL COMMENT '所属区域节点id',
    `icon2` varchar(2048) NOT NULL COMMENT '2d图标地址',
    `position2` varchar(128) NOT NULL COMMENT '2d坐标',
    `icon3` varchar(2048) NOT NULL COMMENT '3d图标地址',
    `position3` varchar(128) NOT NULL COMMENT '3d坐标',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `type` varchar(64) NOT NULL COMMENT '碎片类型',
    `display_context` varchar(2048) NOT NULL COMMENT '显示上下文',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `node_id`)
);
