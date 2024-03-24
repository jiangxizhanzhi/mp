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