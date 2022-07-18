CREATE TABLE `mp_devops_system_alarm` (
          `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
          `id` varchar(64) NOT NULL COMMENT '告警id',
          `alarm_type` tinyint(2) NOT NULL COMMENT '告警类型：0 云平台服务, 1 业务服务 2 服务器',
          `alarm_msg` varchar(256) NOT NULL COMMENT '告警信息',
          `serverName` varchar(128) NOT NULL COMMENT '设备名称',
          `running_time` int(11) NOT NULL COMMENT '持续时间',
          `alarm_status` tinyint(2) NOT NULL COMMENT '告警状态：0 生效， 1失效',
          `create_time` datetime NOT NULL COMMENT '创建时间',
          `update_time` datetime NOT NULL COMMENT '更新时间',
          PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警信息表';