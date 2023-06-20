# v5.4
##修改name长度和业务保持一致
alter table mp_cscene_cscene_role modify column name varchar(256) NOT NULL COMMENT '名称';

alter table mp_cscene_cscene_instance add column logo  varchar(2048)  not null comment '综合场景模板logo' ;

CREATE TABLE `mp_sceneplugincollection_scene_data` (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64) NOT NULL COMMENT 'ID',
       `scene` varchar(64) NOT NULL COMMENT '场景实例id',
       `scene_element` varchar(64) NOT NULL COMMENT '节点id',
       `type` int(11) NOT NULL COMMENT '数据类型',
       `data` text NOT NULL COMMENT '数据',
       `update_time` datetime(6) NOT NULL COMMENT '更新时间',
       `create_time` datetime(6) NOT NULL COMMENT '创建时间',
       PRIMARY KEY (`pk`),
       KEY `scene` (`scene`,`scene_element`)
) ENGINE=InnoDB AUTO_INCREMENT=134473 DEFAULT CHARSET=utf8 COMMENT='场景实例数据采集插件信息表';