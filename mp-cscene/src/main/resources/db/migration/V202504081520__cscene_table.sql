# 场景分层相关的表

alter table mp_scene_scene_config_area add column type tinyint(2) comment '区域类型 0:普通区域 1:层级区域' default 0 after open;
alter table mp_scene_scene_area add column type tinyint(2) comment '区域类型 0:普通区域 1:层级区域' default 0 after open;
alter table mp_scene_scene_element add outer_position2 varchar(1024) NOT NULL default '' comment '外部2d坐标';
alter table mp_scene_scene_config_element add outer_position2 varchar(1024) NOT NULL default ''  comment '外部2d坐标';
alter table mp_scene_scene_area add outer_position2 varchar(1024) NOT NULL default '' comment '外部2d坐标';
alter table mp_scene_scene_config_area add outer_position2 varchar(1024) NOT NULL default '' comment '外部2d坐标';
alter table mp_scene_scene_piece add outer_position2 varchar(1024) NOT NULL default '' comment '外部2d坐标';
alter table mp_scene_scene_config_piece add outer_position2 varchar(1024) NOT NULL default '' comment '外部2d坐标';
alter table mp_scene_scene_element_port add column usable tinyint(1) comment '是否可用' default 1 after type;
alter table mp_scene_scene_config_element_port add column usable tinyint(1) comment '是否可用' default 1 after type;
ALTER TABLE `mp_scene_scene_config_node_connection` ADD COLUMN `type` varchar(64) default 1 COMMENT '连线类型';
ALTER TABLE `mp_scene_scene_config_node_connection` ADD COLUMN `vertices` text default 1  COMMENT '路径点';
ALTER TABLE `mp_scene_scene_node_connection` ADD COLUMN `type` varchar(64) default 1 COMMENT '连线类型';
ALTER TABLE `mp_scene_scene_node_connection` ADD COLUMN `vertices` text default 1 COMMENT '路径点';

CREATE TABLE `mp_scene_scene_node_connection`
(
    `pk`                 int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                 varchar(64)   NOT NULL COMMENT 'ID',
    `scene`              varchar(64)   NOT NULL COMMENT '场景实例id',
    `source_node_id`     varchar(64)   NOT NULL COMMENT '源节点id(node_id)',
    `target_node_id`     varchar(64)   NOT NULL COMMENT '目标节点id(node_id)',
    `source_node`        varchar(64)   NOT NULL COMMENT '源节点id',
    `target_node`        varchar(64)   NOT NULL COMMENT '目标节点id',
    `connection_id`      varchar(64)   NOT NULL COMMENT '连接id',
    `name`               varchar(255)  NOT NULL COMMENT '名称',
    `create_time`        datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`        datetime(6)   NOT NULL COMMENT '更新时间',
    `display_context`    varchar(2048) NOT NULL COMMENT '显示上下文',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`source_node`, `target_node`),
    UNIQUE KEY (`scene`, `source_node_id`, `target_node_id`)
) COMMENT ='场景实例节点连线信息表';

CREATE TABLE `mp_scene_scene_config_node_connection`
(
    `pk`                 int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                 varchar(64)   NOT NULL COMMENT 'ID',
    `scene`              varchar(64)   NOT NULL COMMENT '场景实例id',
    `source_node_id`     varchar(64)   NOT NULL COMMENT '源节点id(node_id)',
    `target_node_id`     varchar(64)   NOT NULL COMMENT '目标节点id(node_id)',
    `source_node`        varchar(64)   NOT NULL COMMENT '源节点id',
    `target_node`        varchar(64)   NOT NULL COMMENT '目标节点id',
    `connection_id`      varchar(64)   NOT NULL COMMENT '连接id',
    `name`               varchar(255)  NOT NULL COMMENT '名称',
    `create_time`        datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`        datetime(6)   NOT NULL COMMENT '更新时间',
    `display_context`    varchar(2048) NOT NULL COMMENT '显示上下文',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`source_node`, `target_node`),
    UNIQUE KEY (`scene`, `source_node_id`, `target_node_id`)
) COMMENT ='场景实例节点连线信息表';