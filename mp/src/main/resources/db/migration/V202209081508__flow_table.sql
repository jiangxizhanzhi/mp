CREATE TABLE `mp_cscene_config_flow_task`
(
    `pk`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64) NOT NULL COMMENT '任务id',
    `cscene_id`      varchar(64) NOT NULL COMMENT '场景模板id',
    `cscene_node_id` varchar(64) NOT NULL COMMENT '节点id',
    `content`        text COMMENT 'json格式的流量内容',
    `create_time`    datetime    NOT NULL COMMENT '创建时间',
    `update_time`    datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='场景模板流量任务表';