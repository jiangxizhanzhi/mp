CREATE TABLE mp_sceneplugingns3_scene_config (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`)
);

CREATE TABLE mp_sceneplugingns3_scene_config_element (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `static_routes` text NOT NULL COMMENT '虚拟路由静态路由',
    `image` varchar(64) NOT NULL COMMENT '镜像',
    `platform` varchar(64) NOT NULL COMMENT '平台',
    `ram` int(10) NOT NULL COMMENT '内存大小',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `scene_element`)
);

CREATE TABLE mp_sceneplugingns3_scene_config_element_port (
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


CREATE TABLE mp_sceneplugingns3_scene (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `resource_id` varchar(64) NOT NULL COMMENT '',
    `bridge_resource_id` varchar(64) NOT NULL COMMENT '',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`)
);

CREATE TABLE mp_sceneplugingns3_scene_element (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `static_routes` text NOT NULL COMMENT '虚拟路由静态路由',
    `image` varchar(64) NOT NULL COMMENT '镜像',
    `platform` varchar(64) NOT NULL COMMENT '平台',
    `ram` int(10) NOT NULL COMMENT '内存大小',
    `resource_id` varchar(64) NOT NULL COMMENT '资源id',
    `dynamic_id` int(10) NOT NULL COMMENT '路由身份id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `scene_element`)
);

CREATE TABLE mp_sceneplugingns3_scene_element_port (
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
