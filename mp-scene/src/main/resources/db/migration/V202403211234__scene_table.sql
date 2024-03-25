create table `mp_cyber_range_register`
(
    `pk`                     int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                     varchar(64)   NOT NULL COMMENT 'ID',
    `range_tag`              varchar(64)    NOT NULL COMMENT '靶场标识',
    `range_ip`              varchar(64)     NOT NULL COMMENT '靶场ip',
    `range_port`            varchar(64)     NOT NULL COMMENT '靶场端口',
    `range_status`          int             NOT NULL COMMENT '靶场状态0-下线 1-上线',
    `create_time`            datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`            datetime(6)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='分靶场注册表';


alter table mp_scene_scene add column `scene_create_range_tag` varchar(50) comment '当前拓扑创建的靶场标识';
alter table mp_scene_scene_config add column `scene_create_range_tag` varchar(50) comment '当前拓扑创建的靶场标识';

alter table mp_scene_scene add column `multi_scene_type` int comment '当前拓扑的多靶场类型0-普通靶场1-多靶场';
alter table mp_scene_scene_config add column `multi_scene_type` int comment '当前拓扑的多靶场类型0-普通靶场1-多靶场';

alter table mp_scene_scene_config_element add column `range_tag` varchar(50) comment '节点归属靶场的标识';
alter table mp_scene_scene_element add column `range_tag` varchar(50) comment '节点归属靶场的标识';

alter table mp_scene_scene_config_element_port add column `vlan_number` int comment '多靶场端口vlan';
alter table mp_scene_scene_element_port add column `vlan_number` int comment '多靶场端口vlan';



