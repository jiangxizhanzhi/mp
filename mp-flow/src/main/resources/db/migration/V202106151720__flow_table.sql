CREATE TABLE `mp_flow_flow_info` (
       `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
       `id` varchar(64) NOT NULL COMMENT '流量id',
       `title` varchar(32) NOT NULL COMMENT '流量标题',
       `third_id` varchar(64) NOT NULL COMMENT '第三方uuid',
       `third_name` varchar(64) NOT NULL COMMENT '第三方文件名称',
       `flow_desc` varchar(255) NOT NULL COMMENT '流量描述',
       `interactive_type` tinyint(2) NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量，2 全流量',
       `flow_platform` tinyint(2) NOT NULL COMMENT '流量发生平台：0 通用，1 Trex流量发生器',
       `resource_type` tinyint(2) NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
       `pcap_file` varchar(255) NOT NULL COMMENT 'pcap文件地址',
       `script_file` varchar(255) NOT NULL COMMENT '脚本文件地址',
       `create_time` datetime NOT NULL COMMENT '创建时间',
       `update_time` datetime NOT NULL COMMENT '更新时间',
       PRIMARY KEY (`pk`),
       UNIQUE KEY `flow_id_index` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流量信息表';

