CREATE TABLE `mp_scene_business_element_command`
(
    `pk`           int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(64) NOT NULL COMMENT 'ID',
    `scene`        varchar(64) NOT NULL COMMENT '场景模板id',
    `node_id`      varchar(64) NOT NULL COMMENT '节点id',
    `business_key` varchar(64) NOT NULL COMMENT '节点命令功能标识，不同功能需不同的key',
    `file`         varchar(128) DEFAULT '' COMMENT '命令使用的附件',
    `command`      varchar(255) DEFAULT '' COMMENT '命令使用的附件',
    `create_time`  datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`  datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `id`(`id`) USING BTREE,
    UNIQUE INDEX `scene`(`scene`, `node_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '节点命令';