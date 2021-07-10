-- v5.4
create table mp_menu_template_role
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `template_id` varchar(64) NOT NULL COMMENT 'ID',
    `role_id`     varchar(64) NOT NULL COMMENT 'ID',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`)
) COMMENT ='角色关联菜单模板表';

create table mp_menu_template_info
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `template_id` varchar(64) NOT NULL COMMENT 'ID',
    `menu_id`     varchar(64) NOT NULL COMMENT '菜单ID',
    `parent`      varchar(64)  DEFAULT NULL COMMENT '父权限ID',
    `identifier`  varchar(64) NOT NULL COMMENT '编号',
    `tenant`      varchar(64) not null comment '所属租户',
    `type`        int(11) NOT NULL COMMENT '资源类型 1:菜单, 2:API, 3:数据',
    `visible`     int(11) NOT NULL DEFAULT 1 COMMENT '是否可见 1:可见，0:不可见（受控于激活选项） 默认为可见',
    `name`        varchar(64) NOT NULL COMMENT '中文名称',
    `en_name`     varchar(64) NOT NULL COMMENT '英文名称',
    `path`        varchar(255) DEFAULT NULL COMMENT '路径',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='菜单模板表';
