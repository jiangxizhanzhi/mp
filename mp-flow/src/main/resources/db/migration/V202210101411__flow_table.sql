CREATE TABLE `mp_flow_flow`
(
    `pk`                bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`                varchar(64)         NOT NULL COMMENT '流量id',
    `name`              varchar(128)        NOT NULL COMMENT '流量名称',
    `description`       varchar(320)        NOT NULL COMMENT '流量描述',
    `category`          tinyint(2)          NOT NULL COMMENT '流量类型',
    `interactive_type`  tinyint(2)          NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量',
    `resource_type`     tinyint(2)          NOT NULL COMMENT '资源类型：0 pcap包，1 脚本, 2 协议流量, 3 IXIA流量',
    `protocols`         varchar(255)        NOT NULL COMMENT '协议',
    `pcap_file`         varchar(255)        NOT NULL COMMENT 'pcap文件地址',
    `file_name`         varchar(128)        NOT NULL COMMENT '文件名称',
    `protocol_configs`  text                NOT NULL COMMENT '协议配置',
    `protocol_flow_units` text                NOT NULL COMMENT '协议流量单元',
    `script_type`       varchar(10)         NOT NULL COMMENT '脚本类型: python, shell',
    `script_file`       varchar(255)        NOT NULL COMMENT '脚本文件地址',
    `script_params`     text                NOT NULL COMMENT '脚本参数',
    `ixia_flow_name`    varchar(128)        NOT NULL COMMENT 'ixia流量名称',
    `create_time`       datetime            NOT NULL COMMENT '创建时间',
    `update_time`       datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量表';

CREATE TABLE `mp_flow_flow_task`
(
    `pk`               bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`               varchar(64)         NOT NULL COMMENT '流量任务id',
    `name`             varchar(128)        NOT NULL COMMENT '流量任务名称',
    `description`      varchar(320)        NOT NULL COMMENT '流量任务描述',
    `category`         tinyint(2)          NOT NULL COMMENT '流量任务分类',
    `interactive_type` tinyint(2)          NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量',
    `bandwidth`        bigint(20)          NOT NULL COMMENT '带宽',
    `load_mode`         varchar(20)         NOT NULL COMMENT '负载模式：随机负载 random，二项分布 binomial，均匀分布 uniformity，指数分布
index，高斯分布 gauss',
    `filter_protocols` varchar(255)        NOT NULL COMMENT '协议类型',
    `flows`            text                NOT NULL COMMENT '关联流量',
    `create_time`      datetime            NOT NULL COMMENT '创建时间',
    `update_time`      datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务表';

CREATE TABLE `mp_flow_flow_generator`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`          varchar(64)         NOT NULL COMMENT 'id',
    `name`        varchar(128)        NOT NULL COMMENT '名称',
    `type`        varchar(20)         NOT NULL COMMENT '类型：cpcloud_trex、cpcloud_ixia、dragon_test',
    `resource_id` varchar(64)         NOT NULL COMMENT '云平台虚拟化资源id',
    `ip`          varchar(32)         NOT NULL COMMENT 'ip',
    `server`      varchar(128)        NOT NULL COMMENT '服务地址',
    `username`    varchar(32)         NOT NULL COMMENT '用户名',
    `password`    varchar(64)         NOT NULL COMMENT '密码',
    `token`       varchar(256)        NOT NULL COMMENT 'token',
    `nics`        text                NOT NULL COMMENT '网口信息',
    `create_time` datetime            NOT NULL COMMENT '创建时间',
    `update_time` datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量发生器表';

CREATE TABLE `mp_flow_flow_task_record`
(
    `pk`                bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`                varchar(64)         NOT NULL COMMENT 'id',
    `flow_generator_id` varchar(64)         NOT NULL COMMENT '流量发生器id',
    `target_ip`         varchar(32)         NOT NULL COMMENT '目标ip',
    `target_mac`        varchar(32)         NOT NULL COMMENT '目标mac',
    `status`            tinyint(2)          NOT NULL COMMENT '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止，4 暂停',
    `name`              varchar(128)        NOT NULL COMMENT '流量任务名称',
    `description`       varchar(320)        NOT NULL COMMENT '流量任务描述',
    `category`          tinyint(2)          NOT NULL COMMENT '流量任务分类',
    `bandwidth`         bigint(20)          NOT NULL COMMENT '带宽',
    `load_mode`         varchar(20)         NOT NULL COMMENT '负载模式：随机负载 random，二项分布 binomial，均匀分布 uniformity，指数分布
index，高斯分布 gauss',
    `filter_protocols`  varchar(255)        NOT NULL COMMENT '协议类型',
    `flows`             text                NOT NULL COMMENT '关联流量',
    `create_time`       datetime            NOT NULL COMMENT '创建时间',
    `update_time`       datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`flow_generator_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务记录表';
