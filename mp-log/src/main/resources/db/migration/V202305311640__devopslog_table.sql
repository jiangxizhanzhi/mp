# v5.4
CREATE TABLE `mp_log_level`
(
    `pk`           int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(256)  COMMENT 'ID',
    `log_name`    varchar(256)  NOT NULL COMMENT '日志名称(一般为包路径)',
    `log_level`   varchar(8)  NOT NULL COMMENT '日志级别(DEBUG/INFO/WARN/ERROR)',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`log_name`)
) COMMENT ='系统日志级别配置表';
