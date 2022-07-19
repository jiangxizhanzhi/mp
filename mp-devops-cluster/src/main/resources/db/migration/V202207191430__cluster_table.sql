CREATE TABLE `mp_devops_alarm_statistics` (
          `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
          `id` varchar(64) NOT NULL COMMENT '告警统计id',
          `alarm_type` tinyint(2) NOT NULL COMMENT '告警类型：0 云平台服务, 1 业务服务 2 服务器',
          `alarm_count` bigint(10) NOT NULL COMMENT '告警数量',
          `alarm_date` datetime NOT NULL COMMENT '告警日期',
          `create_time` datetime NOT NULL COMMENT '创建时间',
          `update_time` datetime NOT NULL COMMENT '更新时间',
          PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警统计信息表';