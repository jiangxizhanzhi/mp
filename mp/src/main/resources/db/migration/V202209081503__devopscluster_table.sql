# v5.3.5
CREATE TABLE `mp_devops_alarm_statistics`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)         NOT NULL COMMENT '告警统计id',
    `alarm_type`  tinyint(2)          NOT NULL COMMENT '告警类型：0 云平台服务, 1 业务服务 2 服务器',
    `alarm_count` bigint(10)          NOT NULL COMMENT '告警数量',
    `alarm_date`  datetime            NOT NULL COMMENT '告警日期',
    `create_time` datetime            NOT NULL COMMENT '创建时间',
    `update_time` datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='告警统计信息表';



CREATE TABLE `mp_devops_cpnode_monitor_script_config`
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `agent_name`  varchar(64)   NOT NULL COMMENT '探针名称',
    `hostname`    varchar(64)   NOT NULL COMMENT '主机名称',
    `ip`          varchar(64)   NOT NULL COMMENT 'IP地址',
    `config`      varchar(2000) NOT NULL COMMENT '脚本配置JSON字符串，包括launchid, interval，scriptUrl等信息',
    `status`      int(11)       NOT NULL COMMENT '1:已完成安装 2:已发送指令-待确认安装成功 3:安装失败',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`agent_name`)
) COMMENT ='云平台节点探针监控脚本安装信息表';



CREATE TABLE `mp_devops_screenshot`
(
    `pk`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64)         NOT NULL COMMENT 'ID',
    `file`          varchar(256)        NOT NULL COMMENT '截屏文件地址',
    `file_name`     varchar(256)        NOT NULL COMMENT '截屏文件名称',
    `file_size`     bigint(20)          NOT NULL COMMENT '截屏文件大小，单位:B',
    `scene_name`    varchar(256)        NOT NULL COMMENT '场景名称',
    `user_id`       varchar(64)         NOT NULL COMMENT '用户id',
    `user_name`     varchar(64)         NOT NULL COMMENT '用户名称',
    `user_org_name` varchar(1024)       NOT NULL COMMENT '组织名称',
    `scene`         varchar(64)         NOT NULL DEFAULT '' COMMENT '场景id',
    `node_id`       varchar(64)         NOT NULL DEFAULT '' COMMENT '节点id',
    `create_time`   datetime            NOT NULL COMMENT '创建时间',
    `update_time`   datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='截屏文件管理表';



CREATE TABLE `mp_devops_system_alarm`
(
    `pk`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(64)         NOT NULL COMMENT 'ID',
    `alarm_type`   tinyint(2)          NOT NULL COMMENT '告警类型：0 云平台服务, 1 业务服务 2 服务器',
    `alarm_msg`    varchar(256)        NOT NULL COMMENT '告警信息',
    `hostname`     varchar(128)        NOT NULL COMMENT '节点名称',
    `servername`   varchar(128)        NOT NULL COMMENT '设备名称',
    `running_time` int(11)             NOT NULL COMMENT '持续时间',
    `alarm_status` tinyint(2)          NOT NULL COMMENT '告警状态：0 生效， 1失效',
    `create_time`  datetime            NOT NULL COMMENT '创建时间',
    `update_time`  datetime            NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='告警信息表';



CREATE TABLE `mp_devops_target`
(
    `pk`                   int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64)  NOT NULL COMMENT 'ID',
    `uuid`                 varchar(64)  NOT NULL COMMENT '镜像UUID',
    `name`                 varchar(164) NOT NULL COMMENT '镜像名称',
    `logo`                 varchar(255)          DEFAULT NULL COMMENT '图标地址',
    `role`                 tinyint(8)            DEFAULT NULL COMMENT '角色',
    `main_category`        int(8)                DEFAULT NULL COMMENT '一级分类',
    `sub_category`         int(8)                DEFAULT NULL COMMENT '二级分类',
    `third_category`       int(8)                DEFAULT NULL COMMENT '三级分类',
    `image_type`           varchar(32)           DEFAULT NULL COMMENT '镜像类型：vm，container,simple, entity',
    `is_public`            tinyint(1)   NOT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
    `quick_create_support` tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
    `in_position`          tinyint(1)            DEFAULT NULL COMMENT '在位状态',
    `reference_status`     int(8)       NOT NULL DEFAULT 0 COMMENT '引用状态',
    `use_status`           tinyint(1)   NOT NULL DEFAULT 0 COMMENT '使用状态',
    `parent_system_type`   int(8)                DEFAULT NULL COMMENT '一级系统类型',
    `system_type`          int(8)                DEFAULT NULL COMMENT '系统类型',
    `system_architecure`   varchar(16)           DEFAULT NULL COMMENT '系统架构',
    `language`             varchar(16)           DEFAULT NULL COMMENT '系统语言',
    `image_status`         tinyint(2)            DEFAULT NULL COMMENT '镜像状态',
    `base_image_support`   tinyint(1)            DEFAULT 0 COMMENT '是否支持基础镜像,0:不支持，1：支持',
    `init_support`         tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否支持Cloud init,0:不支持，1：支持',
    `create_time`          datetime(6)  NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6)  NOT NULL COMMENT '更新时间',
    `image_source`         tinyint(2)            DEFAULT NULL COMMENT '系统镜像类型，1：基础镜像，2：openstack已有镜像，3：自定义上传',
    `is_import`            tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
    `is_hide`              tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
    `instance_state`       tinyint(4)   NOT NULL DEFAULT 0 COMMENT '镜像状态 0:没有实例，1：创建中，2创建成功，3创建失败',
    `is_init`              tinyint(1)            DEFAULT 0 COMMENT '是否初始靶标，0：false，1：true',
    `instance_create_user` varchar(64)           DEFAULT NULL COMMENT '靶标实例创建用户',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE KEY (`id`),
    UNIQUE KEY (`name`) USING BTREE,
    KEY (`main_category`) USING BTREE,
    KEY (`sub_category`) USING BTREE,
    KEY (`role`) USING BTREE
) COMMENT ='靶标信息表';



CREATE TABLE `mp_devops_target_auth`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `tenant`      varchar(64) DEFAULT NULL COMMENT '租户',
    `owner`       varchar(64) NOT NULL COMMENT '用户ID',
    `target`      varchar(64) NOT NULL COMMENT '镜像UUID',
    `can_read`    tinyint(1)  DEFAULT NULL COMMENT '可读',
    `can_create`  tinyint(1)  DEFAULT NULL COMMENT '可创建',
    `can_update`  tinyint(1)  DEFAULT NULL COMMENT '可更新',
    `can_delete`  tinyint(1)  DEFAULT NULL COMMENT '可删除',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE KEY (`id`)
) COMMENT ='靶标授权表';
