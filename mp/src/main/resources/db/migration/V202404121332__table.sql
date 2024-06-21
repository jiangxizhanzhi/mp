-- v5.5
ALTER TABLE mp_sceneplugincollection_scene_config_element ADD COLUMN edited TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有添加过探针' ;

create table mp_event_tracking_record
(
    pk          int auto_increment comment '主键' primary key,
    id          varchar(64) not null comment 'ID',
    module      varchar(256) not null comment '模块',
    category    varchar(256) not null comment '分类',
    action      varchar(1024) not null comment '动作',
    target      varchar(1024) not null comment '操作目标',
    create_time datetime(6) not null comment '创建时间',
    update_time datetime(6) not null comment '更新时间',
    index       module_cat (`module`, `category`)
) comment '埋点表';

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

alter table mp_scene_scene_config_area add column type tinyint(2) comment '区域类型 0:普通区域 1:层级区域' default 0 after open;
alter table mp_scene_scene_area add column type tinyint(2) comment '区域类型 0:普通区域 1:层级区域' default 0 after open;
alter table mp_scene_scene_element add outer_position2 varchar(1024) NOT NULL comment '外部2d坐标';
alter table mp_scene_scene_config_element add outer_position2 varchar(1024) NOT NULL  comment '外部2d坐标';
alter table mp_scene_scene_area add outer_position2 varchar(1024) NOT NULL comment '外部2d坐标';
alter table mp_scene_scene_config_area add outer_position2 varchar(1024) NOT NULL comment '外部2d坐标';
alter table mp_scene_scene_piece add outer_position2 varchar(1024) NOT NULL comment '外部2d坐标';
alter table mp_scene_scene_config_piece add outer_position2 varchar(1024) NOT NULL comment '外部2d坐标';
alter table mp_scene_scene_element_port add column usable tinyint(1) comment '是否可用' default 1 after type;
alter table mp_scene_scene_config_element_port add column usable tinyint(1) comment '是否可用' default 1 after type;
alter table `mp_situation_suricatarule` add column class_type_cn varchar(1024) NOT NULL DEFAULT '' COMMENT "流量规则分类类型中文";
alter table `mp_situation_suricatarule` add column class_type_en varchar(1024) NOT NULL DEFAULT '' COMMENT "流量规则分类类型英文";
alter table `mp_situation_suricatarule` add column rule_gen_type int(2) NOT NULL DEFAULT 0 COMMENT "规则生成类型0-预定义，用户不能编辑 1-自定义，用户可以编辑";

DROP INDEX IF EXISTS id on 	mp_cscene_cscene_role ;
DROP INDEX IF EXISTS id on 	mp_cscene_cscene_instance_role ;
alter table mp_cscene_cscene_role add UNIQUE KEY cscene_id (`cscene`,`id`) ;
alter table mp_cscene_cscene_instance_role add UNIQUE KEY cscene_id (`cscene`,`id`) ;

ALTER TABLE `mp_scene_scene_config_element_port_connection` ADD COLUMN `info` text COMMENT '其他信息,前端可自由定义';
ALTER TABLE `mp_scene_scene_element_port_connection` ADD COLUMN `info` text COMMENT '其他信息,前端可自由定义';
ALTER TABLE `mp_scene_scene_config_node_connection` ADD COLUMN `type` varchar(64) COMMENT '连线类型';
ALTER TABLE `mp_scene_scene_config_node_connection` ADD COLUMN `vertices` text COMMENT '路径点';
ALTER TABLE `mp_scene_scene_node_connection` ADD COLUMN `type` varchar(64) COMMENT '连线类型';
ALTER TABLE `mp_scene_scene_node_connection` ADD COLUMN `vertices` text COMMENT '路径点';