CREATE TABLE mp_scene_scene_config (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `type` int(10) NOT NULL COMMENT '模板类型',
    `source` varchar(64) NOT NULL COMMENT '源实例id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `file` varchar(2048) NOT NULL COMMENT '场景文件',
    `deleted` tinyint(1) NOT NULL COMMENT '是否删除',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);

CREATE TABLE mp_scene_scene_config_area (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `node_id` varchar(64) NOT NULL COMMENT '节点id',
    `area` varchar(64) NOT NULL COMMENT '所属区域节点id',
    `icon2` varchar(2048) NOT NULL COMMENT '2d图标地址',
    `position2` varchar(64) NOT NULL COMMENT '2d坐标',
    `icon3` varchar(2048) NOT NULL COMMENT '3d图标地址',
    `position3` varchar(64) NOT NULL COMMENT '3d坐标',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `open` tinyint(1) NOT NULL COMMENT '是否展开',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `node_id`)
);

CREATE TABLE mp_scene_scene_config_element (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `node_id` varchar(64) NOT NULL COMMENT '节点id',
    `area` varchar(64) NOT NULL COMMENT '所属区域节点id',
    `icon2` varchar(2048) NOT NULL COMMENT '2d图标地址',
    `position2` varchar(64) NOT NULL COMMENT '2d坐标',
    `icon3` varchar(2048) NOT NULL COMMENT '3d图标地址',
    `position3` varchar(64) NOT NULL COMMENT '3d坐标',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `type` int(10) NOT NULL COMMENT '网元类型',
    `file` varchar(2048) NOT NULL COMMENT '节点文件',
    `system_type` varchar(64) NOT NULL COMMENT '系统类型',
    `system_architecture` varchar(16) NOT NULL COMMENT '系统架构',
    `system_language` varchar(8) NOT NULL COMMENT '系统语言',
    `flavor` varchar(32) NOT NULL COMMENT '硬件规格',
    `access_modes` varchar(1024) NOT NULL COMMENT '接入信息列表',
    `init_commands` varchar(2048) NOT NULL COMMENT '初始化命令',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `node_id`)
);


CREATE TABLE mp_scene_scene_config_element_port (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene_element` varchar(64) NOT NULL COMMENT '所属网元id',
    `port_id` varchar(64) NOT NULL COMMENT '端口id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `type` int(10) NOT NULL COMMENT '端口类型',
    `mac_addr` varchar(32) NOT NULL COMMENT 'mac地址',
    `ip` varchar(32) NOT NULL COMMENT 'ip地址',
    `netmask` varchar(32) NOT NULL COMMENT '子网掩码',
    `gateway` varchar(32) NOT NULL COMMENT '网关',
    `dhcp` tinyint(1) NOT NULL COMMENT '是否开启dhcp',
    `dns` varchar(255) NOT NULL COMMENT 'dns列表',
    `egress` int(11) NOT NULL COMMENT '上行带宽',
    `ingress` int(11) NOT NULL COMMENT '下行带宽',
    `delay` int(10) NOT NULL COMMENT '延迟',
    `loss` int(10) NOT NULL COMMENT '丢包',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene_element`, `port_id`)
);

CREATE TABLE mp_scene_scene_config_element_port_connection (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `source_port` varchar(64) NOT NULL COMMENT '源端口id',
    `target_port` varchar(64) NOT NULL COMMENT '目标端口id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `unidirectional` tinyint(1) NOT NULL COMMENT '是否单向',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`source_port`, `target_port`)
);

CREATE TABLE mp_scene_scene_config_element_plugin (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element` varchar(64) NOT NULL COMMENT '所属网元id',
    `plugin_key` varchar(32) NOT NULL COMMENT '插件代号',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene_element`)
);


CREATE TABLE mp_scene_scene (
   `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `id` varchar(64)  NOT NULL COMMENT 'id',
   `source` varchar(64) NOT NULL COMMENT '源场景模板',
   `name` varchar(64) NOT NULL COMMENT '名称',
   `description` varchar(1024) NOT NULL COMMENT '描述',
   `file` varchar(2048) NOT NULL COMMENT '场景文件',
   `event` int(10) NOT NULL COMMENT '当前事件',
   `event_status` int(10) NOT NULL COMMENT '当前事件状态',
   `process` int(10) NOT NULL COMMENT '当前事件进程',
   `process_status` int(10) NOT NULL COMMENT '当前事件进程状态',
   `process_message` varchar(2048) NOT NULL COMMENT '当前事件进程描述',
   `deleted` tinyint(1) NOT NULL COMMENT '是否删除',
   `create_time` datetime(6) NOT NULL COMMENT '创建时间',
   `update_time` datetime(6) NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`pk`),
   UNIQUE (`id`)
);

CREATE TABLE mp_scene_scene_area (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `node_id` varchar(64) NOT NULL COMMENT '节点id',
    `area` varchar(64) NOT NULL COMMENT '所属区域节点id',
    `icon2` varchar(2048) NOT NULL COMMENT '2d图标地址',
    `position2` varchar(64) NOT NULL COMMENT '2d坐标',
    `icon3` varchar(2048) NOT NULL COMMENT '3d图标地址',
    `position3` varchar(64) NOT NULL COMMENT '3d坐标',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `open` tinyint(1) NOT NULL COMMENT '是否展开',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `node_id`)
);

CREATE TABLE mp_scene_scene_element (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `node_id` varchar(64) NOT NULL COMMENT '节点id',
    `area` varchar(64) NOT NULL COMMENT '所属区域节点id',
    `icon2` varchar(2048) NOT NULL COMMENT '2d图标地址',
    `position2` varchar(64) NOT NULL COMMENT '2d坐标',
    `icon3` varchar(2048) NOT NULL COMMENT '3d图标地址',
    `position3` varchar(64) NOT NULL COMMENT '3d坐标',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `type` int(10) NOT NULL COMMENT '网元类型',
    `file` varchar(2048) NOT NULL COMMENT '节点文件',
    `system_type` varchar(64) NOT NULL COMMENT '系统类型',
    `system_architecture` varchar(16) NOT NULL COMMENT '系统架构',
    `system_language` varchar(8) NOT NULL COMMENT '系统语言',
    `flavor` varchar(32) NOT NULL COMMENT '硬件规格',
    `access_modes` varchar(1024) NOT NULL COMMENT '接入信息列表',
    `init_commands` varchar(2048) NOT NULL COMMENT '初始化命令',
    `event` int(10) NOT NULL COMMENT '当前事件',
    `event_status` int(10) NOT NULL COMMENT '当前事件状态',
    `process` int(10) NOT NULL COMMENT '当前事件进程',
    `process_status` int(10) NOT NULL COMMENT '当前事件进程状态',
    `process_message` varchar(2048) NOT NULL COMMENT '当前事件进程描述',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `node_id`)
);


CREATE TABLE mp_scene_scene_element_port (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene_element` varchar(64) NOT NULL COMMENT '所属网元id',
    `port_id` varchar(64) NOT NULL COMMENT '端口id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `type` int(10) NOT NULL COMMENT '端口类型',
    `mac_addr` varchar(32) NOT NULL COMMENT 'mac地址',
    `ip` varchar(32) NOT NULL COMMENT 'ip地址',
    `netmask` varchar(32) NOT NULL COMMENT '子网掩码',
    `gateway` varchar(32) NOT NULL COMMENT '网关',
    `dhcp` tinyint(1) NOT NULL COMMENT '是否开启dhcp',
    `dns` varchar(255) NOT NULL COMMENT 'dns列表',
    `egress` int(11) NOT NULL COMMENT '上行带宽',
    `ingress` int(11) NOT NULL COMMENT '下行带宽',
    `delay` int(10) NOT NULL COMMENT '延迟',
    `loss` int(10) NOT NULL COMMENT '丢包',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene_element`, `port_id`)
);

CREATE TABLE mp_scene_scene_element_port_connection (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `source_port` varchar(64) NOT NULL COMMENT '源端口id',
    `target_port` varchar(64) NOT NULL COMMENT '目标端口id',
    `name` varchar(64) NOT NULL COMMENT '名称',
    `unidirectional` tinyint(1) NOT NULL COMMENT '是否单向',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`source_port`, `target_port`)
);

CREATE TABLE mp_scene_scene_element_snapshot (
     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `id` varchar(64)  NOT NULL COMMENT 'id',
     `scene_element` varchar(64) NOT NULL COMMENT '所属网元id',
     `name` varchar(64) NOT NULL COMMENT '名称',
     `description` varchar(1024) NOT NULL COMMENT '描述',
     `address` varchar(255) NOT NULL COMMENT '快照地址',
     `create_time` datetime(6) NOT NULL COMMENT '创建时间',
     `update_time` datetime(6) NOT NULL COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE (`id`)
);

CREATE TABLE mp_scene_scene_element_plugin (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '所属网元id',
    `plugin_key` varchar(32) NOT NULL COMMENT '插件代号',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene_element`)
);
