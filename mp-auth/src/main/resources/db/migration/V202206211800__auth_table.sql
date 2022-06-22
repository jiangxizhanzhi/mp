# 随机生成一个admin的token_secret
update mp_auth_user set token_secret = (select replace(uuid(),"-","")) where id = '3f655d87112f4ae094bbaf70830e20aa';


# 系统认证配置表
CREATE TABLE mp_auth_authentication_config
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `jwt_secret`        varchar(64) NOT NULL COMMENT '秘钥',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '系统认证配置表';
