# v5.3.5
CREATE TABLE `mp_common_option`
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `code`        varchar(255)  NOT NULL COMMENT '选项编码',
    `parent_code` varchar(255)  NOT NULL COMMENT '父级选项编码',
    `zh_name`     varchar(255)  NOT NULL COMMENT '选项中文名称',
    `en_name`     varchar(255)  NOT NULL COMMENT '选项英文名称',
    `sort`        int(10)       NOT NULL COMMENT '排序字段',
    `type`        varchar(255)  NOT NULL COMMENT '选项类型',
    `meta`        varchar(2000) NOT NULL COMMENT '元数据',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`code`, `type`)
) COMMENT ='选项表';
