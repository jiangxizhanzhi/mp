CREATE TABLE `mp_range`
(
    `pk`                     int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                     varchar(64)   NOT NULL COMMENT 'ID',
    `range_name`             varchar(255)  NOT NULL COMMENT '靶场名称',
    `range_ip`               varchar(255)  NOT NULL COMMENT '靶场IP',
    `create_time`            datetime(6)   NOT NULL COMMENT '创建时间',
    `update_time`            datetime(6)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='靶场表';
