CREATE TABLE `mp_sceneplugincloud_scene_element_logical_switch`
(
    `pk`                 int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                 varchar(64)   NOT NULL COMMENT 'ID',
    `scene`             varchar(64)     NOT NULL COMMENT '场景实例id',
    `name`              varchar(64)     NOT NULL COMMENT '逻辑交换机的名称',
    `physical_ports`    varchar(64)     NOT NULL COMMENT '绑定的物理端口名称',
    `physical_switch_name`  varchar(64) NOT NULL COMMENT 'sdn物理交换机名称',
    `create_time`        datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`        datetime(6)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='虚实结合创建的逻辑交换机';