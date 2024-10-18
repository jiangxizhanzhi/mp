CREATE TABLE `mp_scene_external_instance_network`
(
    `pk`                        int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                        varchar(64) NOT NULL COMMENT 'ID',
    `scene_id`                  varchar(64) NOT NULL COMMENT '场景实例id(cscene id)',
    `node_id`                   varchar(64) NOT NULL COMMENT '节点id',
    `port_id`                   varchar(64) NOT NULL COMMENT '端口id',
    `external_scene_id`         varchar(64) NOT NULL COMMENT '外部场景id',
    `external_node_id`          varchar(64) NOT NULL COMMENT '外部节点id',
    `external_node_resource_id` varchar(64) DEFAULT '' COMMENT '外部节点的云资源id',
    `network_id`                varchar(64) DEFAULT '' COMMENT '外部节点接入的场景中的网络id',
    `interface_id`              varchar(64) DEFAULT '' COMMENT '外部节点的接口id',
    `create_time`               datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`               datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `id`(`id`) USING BTREE,
    KEY                         `scene`(`scene_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '场景外部节点实例的网络接口信息';