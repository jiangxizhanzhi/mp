CREATE TABLE `mp_sys_deploy_log`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `system`      varchar(100) NOT NULL COMMENT '系统',
    `version`     varchar(100)  DEFAULT NULL COMMENT '版本',
    `description` varchar(4000) DEFAULT NULL COMMENT '描述',
    `status`      int(11)     NOT NULL DEFAULT 1 COMMENT '状态 0:失败 1:成功',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='系统部署日志表';
