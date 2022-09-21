# v5.3.5
CREATE TABLE `mp_cscene_cscene_auth`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) DEFAULT NULL COMMENT '租户',
    `owner`       varchar(64) NOT NULL COMMENT '用户ID',
    `target`      varchar(64) NOT NULL COMMENT '场景模板id',
    `can_read`    tinyint(1)  DEFAULT NULL COMMENT '可读',
    `can_create`  tinyint(1)  DEFAULT NULL COMMENT '可创建',
    `can_update`  tinyint(1)  DEFAULT NULL COMMENT '可更新',
    `can_delete`  tinyint(1)  DEFAULT NULL COMMENT '可删除',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE KEY (`id`)
) COMMENT ='场景模板授权信息表';

ALTER table mp_cscene_cscene ADD column auth_count int(11) not NULL COMMENT '授权用户数量' after type;