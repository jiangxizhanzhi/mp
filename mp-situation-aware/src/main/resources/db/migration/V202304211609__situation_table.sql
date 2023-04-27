# v5.4
CREATE TABLE `mp_situation_suricatarule`
(
    `pk`                   int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) NOT NULL COMMENT 'ID',
    `action`               varchar(64) NOT NULL COMMENT '匹配规则时进行的操作',
    `protocol`             varchar(64) NOT NULL COMMENT '协议',
    `source`               varchar(1024) NOT NULL COMMENT '源ip',
    `source_ports`         varchar(1024) NOT NULL COMMENT '源端口',
    `direction`            varchar(32) NOT NULL COMMENT '方向',
    `destination`          varchar(1024) NOT NULL COMMENT '目的ip',
    `destination_ports`    varchar(1024) NOT NULL COMMENT '目的端口',
    `rule_msg`             text NOT NULL COMMENT '规则体',
    `rule_name`            varchar(1024) NOT NULL COMMENT '规则名称',
    `sid`                  varchar(64) NOT NULL COMMENT '签名id',
    `attck`                varchar(64) NULL COMMENT 'Att&ck',
    `create_time`          datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`rule_name`),
    KEY (`protocol`),
    KEY (`attck`)
) COMMENT ='流量分析规则表';
