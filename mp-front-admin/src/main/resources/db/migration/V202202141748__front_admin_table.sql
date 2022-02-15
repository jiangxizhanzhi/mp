# 菜单附加属性
CREATE TABLE mp_front_admin_activ_ctl
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '所属租户' default '',
    `salt`        varchar(64)   NULL COMMENT '随机盐',
    `sys_timestamp`    long  NOT NULL COMMENT '系统当前时间戳，用于防止服务器时间回拨',
    `active_timestamp`  long  NOT NULL COMMENT '激活时间戳，用于每次生成sys-uuid',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '系统激活相关限制信息';
