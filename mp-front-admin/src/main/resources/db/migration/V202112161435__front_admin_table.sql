# 菜单附加属性
CREATE TABLE mp_front_admin_menu_extra_info
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '所属租户' default '',
    `menu`        varchar(64)   NOT NULL COMMENT '菜单id',
    `editions`    varchar(200)  NOT NULL COMMENT '系统激活版本，多个用逗号隔开  0:标准版 1:专业版',
    `activ_keys`  varchar(1024) NOT NULL COMMENT '系统激活key，多个用逗号隔开',
    `per_status`  varchar(100)  NOT NULL COMMENT '权限状态',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`menu`)
) comment '菜单附加信息';
