# v5.3.5
CREATE TABLE `mp_situation_collection_template`
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `name`        varchar(200) NOT NULL COMMENT '模板名称',
    `creator`     varchar(100) DEFAULT NULL COMMENT '创建者用户名',
    `description` varchar(600) DEFAULT NULL COMMENT '描述',
    `OS_type`     int(11)      DEFAULT NULL COMMENT '(单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`name`),
    KEY (`OS_type`),
    KEY (`create_time`)
) COMMENT ='采集模板表';



CREATE TABLE `mp_situation_collection_template_probe`
(
    `pk`             int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64) NOT NULL COMMENT 'ID',
    `template_id`    varchar(64) NOT NULL COMMENT '采集模板id',
    `probe_id`       varchar(64) NOT NULL COMMENT '探针id',
    `name`           varchar(32) NOT NULL COMMENT '探针名称',
    `description`    varchar(400)         DEFAULT NULL COMMENT '描述',
    `probe_category` int(11)     NOT NULL DEFAULT 0 COMMENT '0:信息类 1:事件类 2:日志类 ',
    `type`           int(11)     NOT NULL DEFAULT 0 COMMENT '0:通用探针 1:自定义探针',
    `output_format`  varchar(10) NOT NULL DEFAULT 'none' COMMENT '自定义探针输出格式: none, json, plain',
    `engine`         varchar(40)          DEFAULT NULL COMMENT '分析引擎',
    `OS_type`        varchar(20)          DEFAULT NULL COMMENT '(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other',
    `creator`        varchar(100)         DEFAULT NULL COMMENT '创建者用户名',
    `disabled`       int(11)     NOT NULL DEFAULT 0 COMMENT '0:不启用，1:启用',
    `script_type`    int(11)     NOT NULL COMMENT '0:shell 1:python 2:bat',
    `script_url`     varchar(400)         DEFAULT NULL COMMENT '探针脚本内容fdfs地址，采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板',
    `interval_sec`   int(11)     NOT NULL DEFAULT 180 COMMENT '探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec',
    `config`         varchar(4000)        DEFAULT NULL COMMENT '探针配置，json串，数组',
    `parameter`      varchar(2000)        DEFAULT NULL COMMENT '探针参数',
    `create_time`    datetime    NOT NULL COMMENT '创建时间',
    `update_time`    datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`template_id`, `probe_id`)
) COMMENT ='采集模板探针关系表';



CREATE TABLE `mp_situation_flow_classification`
(
    `pk`          bigint(8) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(32)        NOT NULL COMMENT 'ID',
    `name`        varchar(32)        NOT NULL COMMENT '名称',
    `category_en` varchar(255)       NOT NULL COMMENT '分类英文名称',
    `category_zh` varchar(255)       NOT NULL COMMENT '分类中文名称',
    `severity`    tinyint(3)         NOT NULL COMMENT '严重性',
    `create_time` datetime           NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量分类表';



CREATE TABLE `mp_situation_flow_port_protocol`
(
    `pk`          bigint(8)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(32) NOT NULL COMMENT 'ID',
    `port`        int(11)     NOT NULL COMMENT '端口',
    `protocol`    varchar(32) NOT NULL COMMENT '协议',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量端口协议表';



CREATE TABLE `mp_situation_probe`
(
    `pk`             int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64)  NOT NULL COMMENT 'ID',
    `name`           varchar(200) NOT NULL COMMENT '探针名称',
    `description`    varchar(600)          DEFAULT NULL COMMENT '描述',
    `probe_category` int(11)      NOT NULL DEFAULT 0 COMMENT '0:信息类 1:事件类 2:日志类 ',
    `type`           int(11)      NOT NULL DEFAULT 0 COMMENT '0:通用探针 1:自定义探针',
    `creator`        varchar(100)          DEFAULT NULL COMMENT '创建者用户名',
    `interval_sec`   int(11)      NOT NULL DEFAULT 180 COMMENT '探针执行间隔，为大于1的数值，单位秒',
    `output_format`  varchar(10)  NOT NULL DEFAULT 'none' COMMENT '自定义探针输出格式: none, json, plain',
    `engine`         varchar(40)           DEFAULT NULL COMMENT '分析引擎',
    `is_deleted`     int(11)               DEFAULT 0 COMMENT '1:删除 0:未删除',
    `OS_type`        varchar(20)           DEFAULT NULL COMMENT '(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other',
    `create_time`    datetime     NOT NULL COMMENT '创建时间',
    `update_time`    datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`name`),
    KEY (`create_time`),
    KEY (`type`, `probe_category`, `create_time`)
) COMMENT ='探针基本信息';



CREATE TABLE `mp_situation_probe_ability`
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'ID',
    `probe_id`    varchar(64)  NOT NULL COMMENT '探针id',
    `name`        varchar(200) NOT NULL COMMENT '功能名称',
    `engine`      varchar(40)  DEFAULT NULL COMMENT '分析引擎',
    `OS_type`     varchar(20)  DEFAULT NULL COMMENT '比探针的os_type种类要多，0:Windows, 1:Unix, 2:Linux, 3:BSD, 5:cisco-ios, 6:HP, 7:junos, 8:openbsd',
    `description` varchar(400) DEFAULT NULL COMMENT '描述',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='探针功能项信息';



CREATE TABLE `mp_situation_probe_param`
(
    `pk`            int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT 'ID',
    `probe_id`      varchar(64)  DEFAULT NULL COMMENT '探针id',
    `template_id`   varchar(64)  DEFAULT NULL COMMENT '采集模板id',
    `name`          varchar(50) NOT NULL COMMENT '探针名称',
    `type`          int(11)     NOT NULL COMMENT '参数类型 0:number 1:string 2:bool 3:ip',
    `default_value` varchar(600) DEFAULT NULL COMMENT '默认值',
    `description`   varchar(600) DEFAULT NULL COMMENT '描述',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`template_id`, `probe_id`)
) COMMENT ='探针或者采集模板的参数';



CREATE TABLE `mp_situation_probe_script`
(
    `pk`             int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64) NOT NULL COMMENT 'ID',
    `probe_id`       varchar(64)          DEFAULT NULL COMMENT '探针id',
    `script_content` blob                 DEFAULT NULL COMMENT '探针脚本内容',
    `script_url`     varchar(400)         DEFAULT NULL COMMENT '探针脚本内容url地址',
    `script_type`    int(11)     NOT NULL COMMENT '脚本类型 0:shell 1:python 2:bat',
    `version`        int(11)     NOT NULL DEFAULT 1 COMMENT '脚本版本',
    `status`         int(11)     NOT NULL DEFAULT 1 COMMENT '脚本状态',
    `checksum`       varchar(100)         DEFAULT NULL COMMENT '脚本内容md5',
    `create_time`    datetime    NOT NULL COMMENT '创建时间',
    `update_time`    datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`probe_id`, `script_type`)
) COMMENT ='探针脚本';



CREATE TABLE `mp_situation_traffic_group`
(
    `pk`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `traffic_group_id` varchar(64) NOT NULL COMMENT '流量索引前缀名',
    `create_time`      datetime    NOT NULL COMMENT '创建时间',
    `del_flag`         int(11) DEFAULT 0 COMMENT '1:删除 0:未删除',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`traffic_group_id`)
) COMMENT ='流量索引表';
