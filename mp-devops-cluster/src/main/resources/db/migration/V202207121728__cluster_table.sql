ALTER TABLE `mp_devops_target`
    ADD COLUMN `instance_state` tinyint NOT NULL DEFAULT 0 COMMENT '镜像状态 0:没有实例，1：创建中，2创建成功，3创建失败';



# 云平台节点探针监控脚本安装信息表
CREATE TABLE mp_devops_cpnode_monitor_script_config
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `agent_name`  varchar(64) NOT NULL COMMENT '探针名称',
    `script_name` varchar(64) NOT NULL COMMENT '脚本名称',
    `hostname`    varchar(64) NOT NULL COMMENT '主机名称',
    `ip`          varchar(64) NOT NULL COMMENT 'IP地址',
    `status`      int         NOT NULL COMMENT '1:已完成安装 2:已发送指令-待确认安装成功 3:安装失败',
    `create_time`  datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '云平台节点探针监控脚本安装信息表';



CREATE TABLE `mp_devops_system_alarm` (
      `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
      `id` varchar(64) NOT NULL COMMENT '告警id',
      `alarm_type` tinyint(2) NOT NULL COMMENT '告警类型：0 云平台服务, 1 业务服务 2 服务器',
      `alarm_msg` varchar(256) NOT NULL COMMENT '告警信息',
      `hostname` varchar(128) NOT NULL COMMENT '节点名称',
      `servername` varchar(128) NOT NULL COMMENT '设备名称',
      `running_time` int(11) NOT NULL COMMENT '持续时间',
      `alarm_status` tinyint(2) NOT NULL COMMENT '告警状态：0 生效， 1失效',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`pk`),
      UNIQUE (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警信息表';


CREATE TABLE `mp_devops_alarm_statistics` (
      `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
      `id` varchar(64) NOT NULL COMMENT '告警统计id',
      `alarm_type` tinyint(2) NOT NULL COMMENT '告警类型：0 云平台服务, 1 业务服务 2 服务器',
      `alarm_count` bigint(10) NOT NULL COMMENT '告警数量',
      `alarm_date` datetime NOT NULL COMMENT '告警日期',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`pk`),
      UNIQUE (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警统计信息表';
