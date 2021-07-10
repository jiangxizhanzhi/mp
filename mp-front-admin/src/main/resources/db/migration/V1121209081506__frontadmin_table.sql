# v5.3.5
CREATE TABLE `mp_sys_config`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '租户',
    `key_name`    varchar(100)  DEFAULT NULL COMMENT '键',
    `value`       varchar(8000) DEFAULT NULL COMMENT '值',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`tenant`, `key_name`)
) COMMENT ='系统配置信息表';



CREATE TABLE `mp_sys_notice`
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `tenant`      varchar(64)  NOT NULL COMMENT '租户',
    `content`     text                  DEFAULT NULL COMMENT '公告内容',
    `title`       varchar(150) NOT NULL COMMENT '标题',
    `publisher`   varchar(64)  NOT NULL COMMENT '发布人',
    `is_public`   int(11)      NOT NULL DEFAULT 0 COMMENT '是否公开，对所有人可见  0:否 1:是 默认是0',
    `type`        int(11)      NOT NULL DEFAULT 1 COMMENT '公告类型 1:系统、2:课程、3:练习、4:考试',
    `top`         bigint       NOT NULL COMMENT '置顶数字(采用当前时间戳，越大越靠前)',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`title`)
) COMMENT ='系统公告';



CREATE TABLE `mp_sys_notice_org`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '租户',
    `notice_id`   varchar(64) NOT NULL COMMENT '公告ID',
    `org`         varchar(64) DEFAULT NULL COMMENT '组织id',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`org`, `notice_id`)
) COMMENT ='系统公告组织关系表';



CREATE TABLE `mp_sys_notice_read`
(
    `pk`           int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(64) NOT NULL COMMENT 'ID',
    `tenant`       varchar(64) NOT NULL COMMENT '租户',
    `notice_id`    varchar(64) NOT NULL COMMENT '公告ID',
    `user`         varchar(64)          DEFAULT NULL COMMENT '用户ID(读者)',
    `is_readed`    int(11)     NOT NULL DEFAULT 1 COMMENT '是否已读 1:已读 0:未读  首次创建一条数据即为已读',
    `is_need_push` int(11)     NOT NULL DEFAULT 1 COMMENT '是否推送本条公告 1:是（需要推送）； 0:否（不再推送）默认为1',
    `read_count`   int(11)     NOT NULL DEFAULT 1 COMMENT '已读次数 首次创建一条数据即为已读次数为1',
    `create_time`  datetime    NOT NULL COMMENT '创建时间',
    `update_time`  datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`notice_id`),
    KEY (`user`, `notice_id`)
) COMMENT ='系统公告消息(已读/推送)统计表';



CREATE TABLE `mp_sys_notice_role`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '租户',
    `notice_id`   varchar(64) NOT NULL COMMENT '公告ID',
    `role`        varchar(64) DEFAULT NULL COMMENT '角色ID',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`role`, `notice_id`)
) COMMENT ='系统公告角色关系表';



CREATE TABLE `mp_sys_notice_user`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) NOT NULL COMMENT '租户',
    `notice_id`   varchar(64) NOT NULL COMMENT '公告ID',
    `user`        varchar(64) DEFAULT NULL COMMENT '用户ID',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`user`, `notice_id`)
) COMMENT ='系统公告用户关系表';



CREATE TABLE `mp_front_admin_menu_extra_info`
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `tenant`      varchar(64)   NOT NULL DEFAULT '' COMMENT '所属租户',
    `menu`        varchar(64)   NOT NULL COMMENT '菜单id',
    `editions`    varchar(200)  NOT NULL COMMENT '系统激活版本，多个用逗号隔开  0:标准版 1:专业版',
    `activ_keys`  varchar(1024) NOT NULL COMMENT '系统激活key，多个用逗号隔开',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`menu`)
) COMMENT ='菜单附加信息';


CREATE TABLE `mp_front_admin_activ_ctl`
(
    `pk`                int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                varchar(64) NOT NULL COMMENT 'ID',
    `tenant`            varchar(64) NOT NULL DEFAULT '' COMMENT '所属租户',
    `salt`              varchar(64)          DEFAULT NULL COMMENT '随机盐',
    `suffix`            varchar(10)          DEFAULT NULL COMMENT '6位随机数字+字母，当前表中每次新增一条数据，则随机产生一个suffix',
    `license_code`      varchar(256)         DEFAULT NULL COMMENT '（密文）激活码',
    `first_active_date` varchar(256)         DEFAULT NULL COMMENT '（密文）系统首次激活日期。如果同一个license_code多次激活，最终的过期日期以第一次激活日期为准',
    `last_active_date`  varchar(256)         DEFAULT NULL COMMENT '（密文）系统最近一次激活日期，同一个激活码的情况下',
    `expire_date`       varchar(256)         DEFAULT NULL COMMENT '（密文）过期日期',
    `active_count`      int(11)     NOT NULL DEFAULT 1 COMMENT '同一个激活码的激活次数',
    `sys_timestamp`     mediumtext           DEFAULT NULL COMMENT '定时任务记录的系统时间戳，发生时间回拨，则该值也回到回拨时间，用于累计系统运行秒数',
    `sys_run_seconds`   varchar(256)         DEFAULT '0' COMMENT '系统运行秒数',
    `sys_lifetime`      varchar(256)         DEFAULT NULL COMMENT '系统寿命',
    `status`            int(11)     NOT NULL DEFAULT 1 COMMENT '状态 1:有效 0:无效',
    `create_time`       datetime    NOT NULL COMMENT '创建时间',
    `update_time`       datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='系统激活信息表';
