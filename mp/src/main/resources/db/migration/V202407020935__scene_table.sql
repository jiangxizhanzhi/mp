create table mp_cscene_element_soft_version
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `eid`         varchar(64)  NOT NULL COMMENT '节点eid',
    `soft`        varchar(128) NOT NULL COMMENT '软件名',
    `version`     varchar(64)  NOT NULL COMMENT '版本号',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`eid`)
) COMMENT ='节点软件版本信息表';