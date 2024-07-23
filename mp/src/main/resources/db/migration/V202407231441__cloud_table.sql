alter table mp_sceneplugincloud_scene_element add column config_ip varchar(128) not null default '' comment '靶标在配置网络上分到的ip' ;

CREATE TABLE `mp_config_network`
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `scene`       varchar(128) NOT NULL COMMENT '场景 scene id',
    `network_id`  varchar(128) NOT NULL COMMENT '网络id',
    `ip`          varchar(128) NOT NULL COMMENT 'ip',
    `status`      int(11) NOT NULL COMMENT '0 已释放 1 使用中',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`network_id`),
    KEY (`scene`)
) COMMENT ='config网络使用表';