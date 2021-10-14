CREATE TABLE mp_sceneplugincollection_scene_config (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `enable_flow_collect` tinyint(1) NOT NULL COMMENT '开启流量采集',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`)
);

CREATE TABLE mp_sceneplugincollection_scene_config_element (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `probes` varchar(1024) NOT NULL COMMENT '探针列表',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `scene_element`)
);

CREATE TABLE mp_sceneplugincollection_scene_config_element_port (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element_port` varchar(64) NOT NULL COMMENT '端口id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `scene_element_port`)
);


CREATE TABLE mp_sceneplugincollection_scene (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `enable_flow_collect` tinyint(1) NOT NULL COMMENT '开启流量采集',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`)
);

CREATE TABLE mp_sceneplugincollection_scene_element (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `probes` varchar(1024) NOT NULL COMMENT '探针列表',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `scene_element`)
);

CREATE TABLE mp_sceneplugincollection_scene_element_port (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element_port` varchar(64) NOT NULL COMMENT '端口id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `scene_element_port`)
);
