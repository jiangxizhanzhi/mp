# v5.3.5
CREATE TABLE `mp_sceneplugincollection_scene`
(
    `pk`                   int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) NOT NULL COMMENT 'ID',
    `scene`                varchar(64) NOT NULL COMMENT '场景实例id',
    `enable_flow_collect`  tinyint(1)  NOT NULL COMMENT '开启流量采集',
    `flow_collector`       varchar(64) NOT NULL COMMENT '流量采集器id',
    `create_time`          datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NOT NULL COMMENT '更新时间',
    `probe_collector`      varchar(64) NOT NULL COMMENT '流量采集器id',
    `enable_probe_collect` tinyint(1)  NOT NULL COMMENT '开启探针采集',
    `probe_router`         varchar(64) NOT NULL COMMENT '采集路由id',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`)
) COMMENT ='场景实例数据采集插件信息表';


CREATE TABLE `mp_sceneplugincollection_scene_config`
(
    `pk`                   int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) NOT NULL COMMENT 'ID',
    `scene`                varchar(64) NOT NULL COMMENT '场景模板id',
    `enable_flow_collect`  tinyint(1)  NOT NULL COMMENT '开启流量采集',
    `create_time`          datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NOT NULL COMMENT '更新时间',
    `enable_probe_collect` tinyint(1)  NOT NULL COMMENT '开启探针采集',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`)
) COMMENT ='场景拓扑数据采集插件信息表';



CREATE TABLE `mp_sceneplugincollection_scene_config_element`
(
    `pk`                   int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) NOT NULL COMMENT 'ID',
    `scene`                varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element`        varchar(64) NOT NULL COMMENT '节点id',
    `probes`               longtext    NOT NULL COMMENT '探针列表',
    `create_time`          datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NOT NULL COMMENT '更新时间',
    `enable_probe_collect` tinyint(1)  NOT NULL COMMENT '开启探针采集',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element`)
) COMMENT ='场景拓扑数据采集插件节点信息表';



CREATE TABLE `mp_sceneplugincollection_scene_config_element_port`
(
    `pk`                  int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                  varchar(64) NOT NULL COMMENT 'ID',
    `scene`               varchar(64) NOT NULL COMMENT '场景模板id',
    `scene_element_port`  varchar(64) NOT NULL COMMENT '端口id',
    `enable_flow_collect` tinyint(1)  NOT NULL COMMENT '开启流量采集',
    `create_time`         datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`         datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element_port`)
) COMMENT ='场景拓扑数据采集插件节点端口信息表';



CREATE TABLE `mp_sceneplugincollection_scene_element`
(
    `pk`                   int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) NOT NULL COMMENT 'id',
    `scene`                varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element`        varchar(64) NOT NULL COMMENT '节点id',
    `probes`               longtext    NOT NULL COMMENT '探针列表',
    `create_time`          datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NOT NULL COMMENT '更新时间',
    `enable_probe_collect` tinyint(1)  NOT NULL COMMENT '开启探针采集',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element`)
) COMMENT ='场景实例数据采集插件节点信息表';



CREATE TABLE `mp_sceneplugincollection_scene_element_port`
(
    `pk`                  int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                  varchar(64) NOT NULL COMMENT 'ID',
    `scene`               varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element_port`  varchar(64) NOT NULL COMMENT '端口id',
    `enable_flow_collect` tinyint(1)  NOT NULL COMMENT '开启流量采集',
    `create_time`         datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`         datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`scene`, `scene_element_port`)
) COMMENT ='场景实例数据采集插件节点端口信息表';



CREATE TABLE `mp_sceneplugincollection_scene_probe_router_port`
(
    `pk`            int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT 'ID',
    `scene`         varchar(64) NOT NULL COMMENT '场景实例id',
    `scene_element` varchar(64) NOT NULL COMMENT '节点id',
    `network_id`    varchar(64) NOT NULL COMMENT '网络id',
    `ip`            varchar(32) NOT NULL COMMENT 'ip',
    `create_time`   datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='场景实例数据采集插件探针路由端口表';



CREATE TABLE `mp_sceneplugincollection_wazuh_rule`
(
    `pk`               int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `file`             varchar(64)  NOT NULL COMMENT '文件',
    `relative_dirname` varchar(64)  NOT NULL COMMENT '相对目录名',
    `id`               varchar(64)  NOT NULL COMMENT '规则id',
    `level`            int(11)      NOT NULL COMMENT '规则等级',
    `status`           varchar(64)  NOT NULL COMMENT '状态',
    `details`          varchar(256) DEFAULT NULL COMMENT '详细描述',
    `pci_dss`          varchar(256) DEFAULT NULL COMMENT 'pci_dss',
    `gpg13`            varchar(256) DEFAULT NULL COMMENT 'gpg13',
    `gdpr`             varchar(256) DEFAULT NULL COMMENT 'gdpr',
    `hipaa`            varchar(256) DEFAULT NULL COMMENT 'hipaa',
    `nist`             varchar(256) DEFAULT NULL COMMENT 'nist_800_53',
    `tsc`              varchar(256) DEFAULT NULL COMMENT 'tsc',
    `mitre`            varchar(256) DEFAULT NULL COMMENT 'mitre',
    `groups`           varchar(256) NOT NULL COMMENT '分组',
    `description`      varchar(256) DEFAULT NULL COMMENT '描述',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `update_time`      datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='wazuh规则表';



CREATE TABLE `mp_sceneplugincollection_wazuh_rule_type`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        varchar(64) NOT NULL COMMENT '类型名称',
    `id`          varchar(64) NOT NULL COMMENT '类型id',
    `code`        varchar(64) NOT NULL COMMENT '编号',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='wazuh类型表';



CREATE TABLE `mp_sceneplugincollection_wazuh_rule_type_relation`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) DEFAULT NULL COMMENT 'id',
    `rule_id`     varchar(64) NOT NULL COMMENT '规则id',
    `type_id`     varchar(64) NOT NULL COMMENT '类型id',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`rule_id`, `type_id`)
) COMMENT ='wazuh规则类型关系表';
