CREATE TABLE `mp_scene_business_element`
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `scene`       varchar(64) NOT NULL COMMENT '场景实例id',
    `node_id`     varchar(64) NOT NULL COMMENT '节点id',
    `name`        varchar(255) DEFAULT '' COMMENT '名称',
    `cpu`         int(10) DEFAULT 0 COMMENT 'cpu数量',
    `ram`         int(10) DEFAULT 0 COMMENT '内存大小',
    `disk`        int(10) DEFAULT 0 COMMENT '磁盘大小',
    `resource_id` varchar(64)  DEFAULT '' COMMENT '节点id',
    `image_type`  varchar(64)  DEFAULT '' COMMENT '节点类型',
    `image`  varchar(64)  DEFAULT '' COMMENT '镜像名称',
    `image_uuid`  varchar(64)  DEFAULT '' COMMENT '虚拟机/容器镜像id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `id`(`id`) USING BTREE,
    UNIQUE INDEX `scene`(`scene`, `node_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '由业务单独创建的场景节点信息表';

CREATE TABLE `mp_scene_business_element_port`
(
    `pk`            int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT 'ID',
    `scene`         varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '所属网元id',
    `port_id`       varchar(64) NOT NULL COMMENT '端口id',
    `name`          varchar(255) DEFAULT '' COMMENT '名称',
    `network_id`    varchar(255) NOT NULL COMMENT '名称',
    `ip`            varchar(32)  DEFAULT '' COMMENT 'ip地址',
    `access_modes`  text        NOT NULL COMMENT '接入信息列表',
    `create_time`   datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `id`(`id`) USING BTREE,
    UNIQUE INDEX `scene_element`(`scene`,`scene_element`, `port_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '场景实例节点端口信息表';