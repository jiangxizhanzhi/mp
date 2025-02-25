alter table mp_sceneplugincloud_scene_element add column `config_network_ip` varchar(64) NOT NULL DEFAULT '' COMMENT '配采网的ip' ;
alter table mp_sceneplugincloud_scene_config_element add column `config_network_ip` varchar(64) NOT NULL DEFAULT '' COMMENT '配采网的ip' ;
alter table mp_sceneplugincloud_scene_element add column `config_network_id` varchar(64) NOT NULL DEFAULT '' COMMENT '配采网的id' ;
alter table mp_sceneplugincloud_scene_config_element add column `config_network_id` varchar(64) NOT NULL DEFAULT '' COMMENT '配采网的id' ;
alter table mp_sceneplugincloud_scene_element add column `config_network_next_hop_ip` varchar(64) NOT NULL DEFAULT '' COMMENT '配采网的snrouter下一跳' ;
alter table mp_sceneplugincloud_scene_config_element add column `config_network_next_hop_ip` varchar(64) NOT NULL DEFAULT '' COMMENT '配采网的snrouter下一跳' ;

create table mp_sceneplugincloud_config_network
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `scene`         varchar(64)  NOT NULL COMMENT '场景 scene id',
    `cidr`     varchar(64)  NOT NULL COMMENT '配采网cidr',
    `cidr_ip`     varchar(64)  NOT NULL COMMENT '配采网cidr的网络段',
    `cidr_ip_long`     BIGINT  NOT NULL COMMENT '配采网cidr的网络段的数值形式',
    `gateway_ip`      varchar(64)  NOT NULL COMMENT '配采网的网关ip',
    `next_hop_ip`      varchar(64)  NOT NULL COMMENT '配采网接入的SN router的下一跳地址',
    `sn_router_id`      varchar(64)  NOT NULL COMMENT '配采网接入的SN router id',
    `resource_id`     varchar(64)  NOT NULL COMMENT '配采网的网络资源id',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`,`cidr`),
    UNIQUE KEY (`cidr`)
) COMMENT ='场景配采网信息表' ;