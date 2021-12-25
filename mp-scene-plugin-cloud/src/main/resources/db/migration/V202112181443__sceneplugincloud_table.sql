ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `host_routes` varchar(2048) NOT NULL COMMENT '虚拟交换机静态路由';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `auxiliary_network_host_routes` varchar(2048) NOT NULL COMMENT '辅助网络静态路由';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `host_routes` varchar(2048) NOT NULL COMMENT '虚拟交换机静态路由';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `auxiliary_network_host_routes` varchar(2048) NOT NULL COMMENT '辅助网络静态路由';

CREATE TABLE mp_sceneplugincloud_scene_connection (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `scene` varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `scene_element_port` varchar(64) NOT NULL COMMENT '端口id',
    `access_mode_id` varchar(32) NOT NULL COMMENT '接入id',
    `protocol` varchar(64) NOT NULL COMMENT '协议',
    `port` int(10) NOT NULL COMMENT '端口号',
    `username` varchar(255) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `token` varchar(2048) NOT NULL COMMENT '协议接入token',
    `access_ip` varchar(32) NOT NULL COMMENT '接入ip',
    `access_port` int(10) NOT NULL COMMENT '接入端口号',
    `access_user` varchar(255) NOT NULL COMMENT '接入用户',
    `access_pass` varchar(255) NOT NULL COMMENT '接入用户密码',
    `access_token` varchar(2048) NOT NULL COMMENT '接入用户token',
    `access_token_expire_time` datetime(6) NOT NULL COMMENT '接入用户token过期时间',
    `connection_id` varchar(64) NOT NULL COMMENT '接入id',
    `connection_url` varchar(2048) NOT NULL COMMENT '接入url',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`scene`, `access_mode_id`, `access_user`)
);
