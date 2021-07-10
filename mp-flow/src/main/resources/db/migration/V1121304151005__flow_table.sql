# v5.4
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

# 新增流量包表
CREATE TABLE `mp_flow_pcap`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)         NOT NULL COMMENT '流量包id',
    `flow_id`     varchar(64)         NOT NULL COMMENT '流量id',
    `duration`    bigint(20) UNSIGNED NOT NULL COMMENT '时长(单位ms)',
    `pcap_size`   bigint(10) UNSIGNED NOT NULL COMMENT '大小(单位Byte)',
    `pkt_num`     bigint(10) UNSIGNED NOT NULL COMMENT '包数(报文数量)',
    `protocols`   varchar(128)        NOT NULL COMMENT '包含协议',
    `pcap_file`   varchar(255)        NOT NULL COMMENT 'pcap文件地址',
    `create_time` datetime            NOT NULL COMMENT '创建时间',
    `update_time` datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `flow_id` (`flow_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量包表';

# 新增流量任务配置表
CREATE TABLE `mp_flow_task_conf`
(
    `pk`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64)         NOT NULL COMMENT '配置id',
    `task_id`        varchar(64)         NOT NULL COMMENT '任务id',
    `keep_running`   tinyint(2) UNSIGNED NOT NULL COMMENT '持续运行：0 否，1 是',
    `rate_unlimited` tinyint(2) UNSIGNED NOT NULL COMMENT '速率无限制：0 否，1 是',
    `replay`         tinyint(2) UNSIGNED NOT NULL COMMENT '是否流量重放：0 否，1 是',
    `conf`           text                NOT NULL COMMENT '配置',
    `create_time`    datetime            NOT NULL COMMENT '创建时间',
    `update_time`    datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `task_id` (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务配置表';

# 新增流量执行任务表
CREATE TABLE `mp_flow_task_task`
(
    `pk`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64)         NOT NULL COMMENT '任务id',
    `scene_id`         varchar(64)         NOT NULL COMMENT '场景实例id',
    `actuator_id`      varchar(64)         NOT NULL COMMENT '流量执行器id',
    `resource_type`    tinyint(2)          NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
    `interactive_type` tinyint(2)          NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量',
    `flow_id`          varchar(64)         NOT NULL COMMENT '流量id',
    `port_id`          varchar(64)         NOT NULL COMMENT '网口id',
    `success_count`    int(11)             NOT NULL DEFAULT 0 COMMENT '成功执行次数',
    `error_count`      int(11)             NOT NULL DEFAULT 0 COMMENT '失败执行次数',
    `task_status`      tinyint(4)          NOT NULL DEFAULT 0 COMMENT '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败',
    `node_id`          varchar(64)         NOT NULL COMMENT '目标节点id',
    `node_ip`          varchar(24)         NOT NULL COMMENT '目标节点ip',
    `node_name`        varchar(64)         NOT NULL COMMENT '目标节点名称',
    `create_time`      datetime            NOT NULL COMMENT '创建时间',
    `update_time`      datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量执行任务表';

# 新增流量任务网口表
CREATE TABLE `mp_flow_task_port`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)         NOT NULL COMMENT '网口id',
    `network_id`  varchar(64)         NOT NULL DEFAULT '' COMMENT '网络id',
    `actuator_id` varchar(64)         NOT NULL DEFAULT '' COMMENT '流量执行器id',
    `mac`         varchar(64)         NOT NULL COMMENT '网口mac',
    `create_time` datetime            NOT NULL COMMENT '创建时间',
    `update_time` datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务网口表';

# 新增流量任务记录表
CREATE TABLE `mp_flow_task_record`
(
    `pk`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64)         NOT NULL COMMENT '记录id',
    `actuator_id`   varchar(64)         NOT NULL COMMENT '流量执行器id',
    `task_id`       varchar(64)         NOT NULL COMMENT '任务id',
    `tx_bps`        bigint(20)          NOT NULL COMMENT '实时发送速率(单位byte/s)',
    `tx_pps`        bigint(20)          NOT NULL COMMENT '实时发包数',
    `latency`       bigint(10)          NOT NULL COMMENT '延迟(单位ms)',
    `jitter`        bigint(10)          NOT NULL COMMENT '抖动(单位ms)',
    `obytes`        bigint(20)          NOT NULL COMMENT '发包总大小(单位byte)',
    `oerrors`       bigint(20)          NOT NULL COMMENT '发包错误数',
    `total_tx_L2`   bigint(20)          NOT NULL COMMENT '发送速率L2(单位byte/s)',
    `total_tx_L1`   bigint(20)          NOT NULL COMMENT '发送速率L1(单位byte/s)',
    `total_pps`     bigint(20)          NOT NULL COMMENT '总发包数',
    `total_rx`      bigint(20)          NOT NULL COMMENT '接收速率(单位byte/s)',
    `queue_full`    bigint(20)          NOT NULL COMMENT '阻塞队列大小(单位byte)',
    `drop_rate`     bigint(20)          NOT NULL COMMENT '丢包率(单位byte/s)',
    `record_status` tinyint(2)          NOT NULL COMMENT '是否有效：0 无效，1 有效',
    `create_time`   datetime            NOT NULL COMMENT '创建时间',
    `update_time`   datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    KEY `task_id` (`task_id`),
    KEY `create_time` (`create_time`),
    KEY `task_id_and_create_time` (`task_id`, `create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务记录表';