-- 选项表
CREATE TABLE mp_common_option (
    `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64) NOT NULL COMMENT 'id',
    `code` varchar(255) NOT NULL COMMENT '选项编码',
    `zh_name` varchar(255) NOT NULL COMMENT '选项中文名称',
    `en_name` varchar(255) NOT NULL COMMENT '选项英文名称',
    `sort` int(10) NOT NULL COMMENT '排序字段',
    `type` varchar(255) NOT NULL COMMENT '选项类型',
    `create_time`  datetime NOT NULL COMMENT '创建时间',
    `update_time`  datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) COMMENT '选项表';