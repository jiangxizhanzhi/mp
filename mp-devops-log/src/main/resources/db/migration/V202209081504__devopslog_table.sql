# v5.3.5
CREATE TABLE `mp_log_report`
(
    `pk`           int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(32)  NOT NULL COMMENT 'ID',
    `file_name`    varchar(32)  NOT NULL COMMENT '文件名称',
    `type`         varchar(20)  NOT NULL COMMENT '日志类型（CR/MP/AD/CP)',
    `file_size`    bigint(20)   NOT NULL COMMENT '文件大小',
    `download_url` varchar(255) NOT NULL COMMENT '文件下载地址',
    `host_ip`      varchar(20)  NOT NULL DEFAULT '' COMMENT '节点ip',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='日志上报表';
