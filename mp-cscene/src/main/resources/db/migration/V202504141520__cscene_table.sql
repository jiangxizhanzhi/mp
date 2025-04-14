CREATE TABLE `mp_sceneplugincollection_scene_cycle_data`
(
    `pk`            int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT 'ID',
    `scene`         varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `type`          varchar(64) NOT NULL COMMENT '数据类型',
    `data`          text        NOT NULL COMMENT '数据',
    `update_time`   datetime(6) NOT NULL COMMENT '更新时间',
    `create_time`   datetime(6) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`pk`),
    KEY             `scene` (`scene`,`scene_element`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='按照周期查询的采集数据表';