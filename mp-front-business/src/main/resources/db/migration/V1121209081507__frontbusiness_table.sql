# v5.3.5
CREATE TABLE `mp_auth_url_ac`
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `url`         varchar(500) NOT NULL COMMENT 'URL pattern',
    `is_pattern`  int(11)      NOT NULL DEFAULT 0 COMMENT '是否包含模式字符 1:是 0:否',
    `methods`     varchar(100) NOT NULL COMMENT '支持的请求方法，如get,post',
    `desc`        varchar(500)          DEFAULT NULL COMMENT '描述',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`url`)
) COMMENT ='URL访问控制表';



CREATE TABLE `mp_auth_url_ac_target`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `urlac_id`    varchar(64) NOT NULL COMMENT 'URL访问控制表ID',
    `target`      varchar(64) NOT NULL COMMENT '权限ID',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`urlac_id`),
    KEY (`target`)
) COMMENT ='URL访问控制-权限关系表';
