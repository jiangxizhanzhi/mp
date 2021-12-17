# 激活key和menu对应表
CREATE TABLE mp_front_admin_activ_key_menu
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'id',
    `tenant`      varchar(64)  NOT NULL COMMENT '所属租户' default '',
    `activ_key`   varchar(100) NOT NULL COMMENT '系统激活key',
    `menu`        varchar(64)  NOT NULL COMMENT '菜单id',
    `create_time` datetime     NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`activ_key`)
) comment '激活项菜单对应表';

# 激活key和menu对应表
CREATE TABLE mp_front_admin_activ_edition_menu
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户' default '',
    `edition`     varchar(64) NOT NULL COMMENT '系统激活版本',
    `menu`        varchar(64) NOT NULL COMMENT '菜单id',
    `create_time` datetime    NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`edition`)
) comment '激活版本菜单对应表';
