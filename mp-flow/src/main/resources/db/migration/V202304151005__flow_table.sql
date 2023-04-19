# 流量集合表新增分类字段
ALTER TABLE `mp_flow_collection`
    ADD COLUMN `type_flag` tinyint UNSIGNED NOT NULL DEFAULT 2 COMMENT '集合分类：0背景流量，1攻击流量，2混合流量' AFTER `category`;

# 新增流量协议表
CREATE TABLE `mp_flow_protocol`
(
    `pk`            bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`            varchar(64)         NOT NULL COMMENT '协议id',
    `flow_id`       varchar(64)         NOT NULL COMMENT '流量id',
    `protocol_name` varchar(64)         NOT NULL COMMENT '协议名称',
    `rate`          float               NOT NULL COMMENT '比例',
    `marked`        tinyint(2) UNSIGNED NOT NULL COMMENT '是否勾选：0 否，1 是',
    `create_time`   datetime            NOT NULL COMMENT '创建时间',
    `update_time`   datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `flow_protocol_id` (`id`, `flow_id`) USING HASH
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量协议表';

# 新增流量包
CREATE TABLE `mp_flow_pcap`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)         NOT NULL COMMENT '流量包id',
    `flow_id`     varchar(64)         NOT NULL COMMENT '流量id',
    `duration`    bigint(20) UNSIGNED NOT NULL COMMENT '时长(单位um)',
    `pcap_size`   bigint(10) UNSIGNED NOT NULL COMMENT '大小(单位Byte)',
    `pkt_num`     bigint(10) UNSIGNED NOT NULL COMMENT '包数(报文数量)',
    `protocols`   varchar(128)        NOT NULL COMMENT '包含协议',
    `pcap_file`   varchar(255)        NOT NULL COMMENT 'pcap文件地址',
    `create_time` datetime            NOT NULL COMMENT '创建时间',
    `update_time` datetime            NOT NULL COMMENT '更新时间',
    `create_user` varchar(64)         NOT NULL COMMENT '创建用户',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `flow_id` (`flow_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量包表';

# 新增流量任务配置表
CREATE TABLE `mp_task_config`
(
    `pk`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64)         NOT NULL COMMENT '配置id',
    `task_id`        varchar(64)         NOT NULL COMMENT '任务id',
    `resource_type`  tinyint(2)          NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
    `keep_running`   tinyint(2) UNSIGNED NOT NULL COMMENT '持续运行：0 否，1 是',
    `rate_unlimited` tinyint(2) UNSIGNED NOT NULL COMMENT '速率无限制：0 否，1 是',
    `replay`         tinyint(2) UNSIGNED NOT NULL COMMENT '是否流量重放：0 否，1 是',
    `conf`           text                NOT NULL COMMENT '配置',
    `create_time`    datetime            NOT NULL COMMENT '创建时间',
    `update_time`    datetime            NOT NULL COMMENT '更新时间',
    `create_user`    varchar(64)         NOT NULL COMMENT '创建用户',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `task_id` (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务配置表';
