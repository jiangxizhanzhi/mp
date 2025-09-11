CREATE TABLE `mp_platform`
(
    `pk`               bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`               varchar(64)         NOT NULL COMMENT '平台id',
    `name`             varchar(64)         NOT NULL COMMENT '平台名称',
    `company`          varchar(64)         NOT NULL COMMENT '厂商',
    `params`           varchar(256)        NOT NULL COMMENT '参数',
    `system_type`      varchar(64)         NOT NULL COMMENT '架构类型',
    `in_position`      tinyint(2) UNSIGNED NOT NULL COMMENT '在位状态：0 否，1 是',
    `access_protocols` text                NOT NULL COMMENT '接入信息',
    `create_user`      varchar(64)         NOT NULL COMMENT '创建用户id',
    `create_username`  varchar(64)         NOT NULL COMMENT '创建用户名称',
    `create_time`      datetime            NOT NULL COMMENT '创建时间',
    `update_time`      datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='超融合平台管理表';