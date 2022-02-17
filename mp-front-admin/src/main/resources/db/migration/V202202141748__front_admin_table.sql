# 系统激活相关限制信息
CREATE TABLE mp_front_admin_activ_ctl
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '所属租户' default '',
    `salt`        varchar(64)   NULL COMMENT '随机盐',
    `sys_uuid_suffix`        varchar(10)   NULL COMMENT '系统UUID后缀，6位随机数字+字母，当前表中每次新增一条数据，则随机产生一个suffix',
    `license_code`        varchar(256)   NULL COMMENT '（密文）激活码',
    `first_active_date`   varchar(256)  NULL COMMENT '（密文）系统首次激活日期。如果同一个license_code多次激活，最终的过期日期以第一次激活日期为准',
    `last_active_date`   varchar(256)  NULL COMMENT '（密文）系统最近一次激活日期，同一个激活码的情况下',
    `expire_date`   varchar(256)  NULL COMMENT '（密文）过期日期',
    `active_count`    int NOT NULL DEFAULT 1 COMMENT '同一个激活码的激活次数',
    `sys_timestamp`   long NULL COMMENT '定时任务记录的系统时间戳，发生时间回拨，则该值也回到回拨时间，用于累计系统运行秒数',
    `sys_run_seconds`   long NULL COMMENT '系统运行秒数',
    `status`  int  NOT NULL DEFAULT 1 COMMENT '状态 1:有效 0:无效',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '系统激活相关限制信息';
