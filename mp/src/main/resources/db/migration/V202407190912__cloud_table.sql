alter table mp_sceneplugincloud_scene add column port_mapping_instance_id varchar(128) not null default '' comment '端口映射代理虚拟机id' ;

CREATE TABLE `mp_router_network`
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `router_id`   varchar(128) NOT NULL COMMENT '路由id',
    `network_id`  varchar(128) NOT NULL COMMENT '网络id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`router_id`)
) COMMENT ='路由网络关系表';