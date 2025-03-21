create table mp_scene_snapshot
(
    `pk`            int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64)  NOT NULL COMMENT 'ID',
    `cscene_id`     varchar(128) NOT NULL COMMENT '场景模板id',
    `cscene_instance_id`     varchar(128) NOT NULL COMMENT '场景模板id',
    `snapshot_name` varchar(128) NOT NULL COMMENT '场景快照名称',
    `content`       LONGTEXT     NOT NULL COMMENT '快照内容',
    `create_time`   datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`   datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    KEY (`cscene_id`)
) COMMENT ='场景快照';