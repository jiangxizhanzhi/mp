CREATE TABLE `mp_cscene_probeoperate_log`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `scene`       varchar(64) NOT NULL DEFAULT '' COMMENT '场景id',
    `type`        varchar(64) NOT NULL DEFAULT '' COMMENT '主机采集数据类型(changeEventState,originLog,logEvent,customCollection)',
    `operate`     int(11)     NOT NULL COMMENT '操作 1:开启, 0:关闭',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    KEY (`scene`, `type`)
) COMMENT ='主机采集数据开关操作记录表';