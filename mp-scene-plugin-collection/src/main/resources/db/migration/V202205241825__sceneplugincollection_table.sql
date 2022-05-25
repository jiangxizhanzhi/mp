CREATE TABLE mp_sceneplugincollection_wazuh_rule
(
    `pk`               int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `file`             varchar(64)  NOT NULL COMMENT '文件',
    `relative_dirname` varchar(64)  NOT NULL COMMENT '相对目录名',
    `id`               varchar(64)  NOT NULL COMMENT '规则id',
    `level`            int(11)      NOT NULL COMMENT '规则等级',
    `status`           varchar(64)  NOT NULL COMMENT '状态',
    `details`          varchar(256) default NULL COMMENT '详细描述',
    `pci_dss`          varchar(256) default NULL COMMENT 'pci_dss',
    `gpg13`            varchar(256) default NULL COMMENT 'gpg13',
    `gdpr`             varchar(256) default NULL COMMENT 'gdpr',
    `hipaa`            varchar(256) default NULL COMMENT 'hipaa',
    `nist`             varchar(256) default NULL COMMENT 'nist_800_53',
    `tsc`              varchar(256) default NULL COMMENT 'tsc',
    `mitre`            varchar(256) default NULL COMMENT 'mitre',
    `groups`           varchar(256) NOT NULL COMMENT '分组',
    `description`      varchar(256) default NULL COMMENT '描述',
    `create_time`      datetime(6)  default LOCALTIME COMMENT '创建时间',
    `update_time`      datetime(6)  default LOCALTIME COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);
CREATE TABLE mp_sceneplugincollection_wazuh_rule_type
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        varchar(64) NOT NULL COMMENT '类型名称',
    `id`          varchar(64) NOT NULL COMMENT '类型id',
    `create_time` datetime(6) default LOCALTIME COMMENT '创建时间',
    `update_time` datetime(6) default LOCALTIME COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);
CREATE TABLE mp_sceneplugincollection_wazuh_rule_type_relation
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) default NULL COMMENT 'id',
    `rule_id`     varchar(64) NOT NULL COMMENT '规则id',
    `type_id`     varchar(64) NOT NULL COMMENT '类型id',
    `create_time` datetime(6) default LOCALTIME COMMENT '创建时间',
    `update_time` datetime(6) default LOCALTIME COMMENT '更新时间',
    PRIMARY KEY (`pk`)
)