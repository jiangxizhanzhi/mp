# url访问控制表
CREATE TABLE mp_auth_url_ac
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `url`         varchar(500)   NOT NULL COMMENT 'URL pattern',
    `methods`     varchar(100)   NOT NULL COMMENT '支持的请求方法，如get,post',
    `desc`     varchar(500)      NULL COMMENT '描述',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment 'url访问控制表';
create index idx_url_ac_url on mp_auth_url_ac (url);



# url访问控制-权限关系表
CREATE TABLE mp_auth_url_ac_target
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `urlac_id`    varchar(64)   NOT NULL COMMENT 'urlac id',
    `target`      varchar(64)   NOT NULL COMMENT 'target id',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment 'url访问控制-权限关系表';
create index idx_urlact_uid on mp_auth_url_ac_target (urlac_id);
create index idx_urlact_tid on mp_auth_url_ac_target (target);

