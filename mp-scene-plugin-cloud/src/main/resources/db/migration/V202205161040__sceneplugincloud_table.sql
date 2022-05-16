CREATE TABLE mp_cscene_common_config (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `key` varchar(32) NOT NULL COMMENT '配置对应key',
    `content` text NOT NULL COMMENT '配置内容',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`key`)
);

ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `ospf_router_id` varchar(64)  NOT NULL COMMENT 'ospf网络下的routerID';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `ospf_router_id` varchar(16)  NOT NULL COMMENT 'ospf网络下的routerID';
ALTER TABLE mp_scene_scene_element ADD COLUMN `is_auxiliary` tinyint(1)  NOT NULL COMMENT '是否是辅助节点';
ALTER TABLE mp_scene_scene_config_element ADD COLUMN `is_auxiliary` tinyint(1)  NOT NULL COMMENT '是否是辅助节点';
ALTER TABLE mp_scene_scene_element_port ADD COLUMN `is_auxiliary` tinyint(1) NOT NULL COMMENT '是否是辅助端口';
ALTER TABLE mp_scene_scene_config_element_port ADD COLUMN `is_auxiliary` tinyint(1) NOT NULL COMMENT '是否是辅助端口';

ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `ospf_ip` varchar(64)  NOT NULL COMMENT 'ospf组网下的ip地址';
ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `ospf_area` varchar(16)  NOT NULL COMMENT 'ospf组网下的area';
ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `bgp_as` varchar(16)  NOT NULL COMMENT 'bgp组网下的as';
ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `bgp_ip` varchar(64)  NOT NULL COMMENT 'bgp组网下的ip地址';
ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `bgp_peer` varchar(64)  NOT NULL COMMENT 'bgp组网下的对端端口ip';
ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `bgp_peer_as` varchar(16)  NOT NULL COMMENT 'bgp组网下的对端端口所属AS';

ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `ospf_ip` varchar(64)  NOT NULL COMMENT 'ospf组网下的ip地址';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `ospf_area` varchar(16)  NOT NULL COMMENT 'ospf组网下的area';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `bgp_as` varchar(16)  NOT NULL COMMENT 'bgp组网下的as';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `bgp_ip` varchar(64)  NOT NULL COMMENT 'bgp组网下的ip地址';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `bgp_peer` varchar(64)  NOT NULL COMMENT 'bgp组网下的对端端口ip';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `bgp_peer_as` varchar(16)  NOT NULL COMMENT 'bgp组网下的对端端口所属AS';