-- 前端下拉框选项表
CREATE TABLE mp_common_dropdown (
    `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64) NOT NULL COMMENT 'id',
    `code` int NOT NULL COMMENT '操作系统类型编码',
    `zh_name` varchar(64) NOT NULL COMMENT '操作系统类型中文名称',
    `en_name` varchar(64) NOT NULL COMMENT '操作系统类型英文名称',
    `sort` int(10) NOT NULL COMMENT '排序字段',
    `type` int(10) NOT NULL COMMENT '下拉选项类型',
    `create_time`  datetime NOT NULL COMMENT '创建时间',
    `update_time`  datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) COMMENT '前端下拉框选项表';