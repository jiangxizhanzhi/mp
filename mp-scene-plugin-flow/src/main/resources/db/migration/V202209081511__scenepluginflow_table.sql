# v5.3.5

CREATE TABLE `mp_scenepluginflow_scene`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `scene`       varchar(64) NOT NULL COMMENT '场景实例id',
    `executor`    varchar(64) NOT NULL COMMENT '流量发生器id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`)
) COMMENT ='场景实例流量插件信息表';



CREATE TABLE `mp_scenepluginflow_scene_config`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `scene`       varchar(64) NOT NULL COMMENT '场景模板id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`)
) COMMENT ='场景拓扑流量插件信息表';



CREATE TABLE `mp_scenepluginflow_scene_config_element`
(
    `pk`            int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT 'ID',
    `scene`         varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `flows`         longtext    NOT NULL COMMENT '流量列表',
    `create_time`   datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element`)
) COMMENT ='场景拓扑流量插件节点信息表';



CREATE TABLE `mp_scenepluginflow_scene_config_element_port`
(
    `pk`                 int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                 varchar(64) NOT NULL COMMENT 'ID',
    `scene`              varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element_port` varchar(64) NOT NULL COMMENT '端口id',
    `create_time`        datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`        datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element_port`)
) COMMENT ='场景拓扑流量插件节点端口信息表';



CREATE TABLE `mp_scenepluginflow_scene_element`
(
    `pk`            int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT 'ID',
    `scene`         varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `flows`         longtext    NOT NULL COMMENT '流量列表',
    `create_time`   datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element`)
) COMMENT ='场景实例流量插件节点信息表';



CREATE TABLE `mp_scenepluginflow_scene_element_port`
(
    `pk`                 int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                 varchar(64) NOT NULL COMMENT 'ID',
    `scene`              varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element_port` varchar(64) NOT NULL COMMENT '端口id',
    `create_time`        datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`        datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element_port`)
) COMMENT ='场景实例流量插件节点端口信息表';
