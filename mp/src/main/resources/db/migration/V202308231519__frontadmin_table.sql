CREATE TABLE `mp_sys_model_version`
(
    `pk`              int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`              varchar(64)   NOT NULL COMMENT 'ID',
    `model_name`      varchar(64)   NOT NULL DEFAULT '' COMMENT '模块名称',
    `model_version`   varchar(64)   NOT NULL DEFAULT '' COMMENT '模块版本号',
    `visible`         tinyint(1)    NOT NULL COMMENT '是否可见',
    `create_time`     datetime      NOT NULL COMMENT '创建时间',
    `update_time`     datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='模块版本信息表';
