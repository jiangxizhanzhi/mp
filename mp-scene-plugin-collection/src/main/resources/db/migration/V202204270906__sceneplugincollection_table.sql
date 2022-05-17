CREATE TABLE mp_sceneplugincollection_scene_log_probe_location (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64) NOT NULL COMMENT 'id',
    `probe_id` varchar(64) NOT NULL COMMENT '探针id',
    `os_type` varchar(64) NOT NULL COMMENT '系统类型',
    `log_format` varchar(64) NOT NULL COMMENT '日志格式',
    `location` varchar(256) NOT NULL COMMENT '日志地址',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);