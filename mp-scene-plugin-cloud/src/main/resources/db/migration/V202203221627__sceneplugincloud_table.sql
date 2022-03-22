alter table mp_sceneplugincloud_scene_element_port add COLUMN `logic_network_id` varchar(64) NOT NULL COMMENT '网络id'
CREATE TABLE `mp_sceneplugincloud_scene_element_network`
(
    `pk`                                   int           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                                   varchar(64)   NOT NULL COMMENT 'id',
    `scene`                                varchar(64)   NOT NULL COMMENT '场景实例id',
    `scene_element`                        varchar(64)   NOT NULL COMMENT '场景实例节点id',
    `cidr`                                 varchar(32)   NOT NULL COMMENT '虚拟网络cidr地址段',
    `gateway`                              varchar(32)   NOT NULL COMMENT '虚拟网络网关',
    `dns_nameservers`                      varchar(255)  NOT NULL COMMENT '虚拟网络dns',
    `dhcp`                                 tinyint(1) NOT NULL COMMENT '虚拟网络是否开启dhcp',
    `allocation_pools`                     varchar(1024) NOT NULL COMMENT '虚拟网络地址池',
    `vlan`                                 tinyint(1) NOT NULL COMMENT '虚拟网络vlan模式',
    `vlan_id`                              int           NOT NULL COMMENT '虚拟网络vlanId',
    `host_routes`                          varchar(1024) NOT NULL COMMENT '虚拟路由静态路由',
    `resource_id`                          varchar(64)   NOT NULL COMMENT '虚拟网络资源id',
    `external`                             tinyint(1) NOT NULL COMMENT '虚拟网络vlan模式',
    `create_time`                          datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`                          datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    `auxiliary_network_router_on`          tinyint(1) NOT NULL COMMENT '是否开启辅助网络辅助路由',
    `auxiliary_network_router_resource_id` varchar(64)   NOT NULL COMMENT '辅助网络辅助路由id',
    `auxiliary_network_router_ip`          varchar(32)   NOT NULL COMMENT '辅助网络辅助路由ip',
    PRIMARY KEY (`pk`),
    UNIQUE INDEX `id` (`id`),
    INDEX                                  `scene_ele` (`scene`, `scene_element`)
) COMMENT '节点网络';