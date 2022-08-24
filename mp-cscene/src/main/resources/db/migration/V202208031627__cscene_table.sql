create table mp_cscene_snapshot
(
    pk                   int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    id                   varchar(64)  not null comment 'id',
    cscene_id            varchar(64)  not null comment '场景模板',
    cscene_instance_id   varchar(64)  not null comment '场景实例',
    name                 varchar(255) not null comment '名称',
    source_name          varchar(255) not null comment '源快照名称',
    node_id              varchar(64)  not null comment '描述',
    snapshot_id          varchar(64)  not null comment '场景数据库快照id',
    snapshot_resource_id varchar(64)  not null comment '快照资源id',
    deleted              tinyint(1) NOT NULL COMMENT '是否删除',
    create_time          datetime(6) not null comment '创建时间',
    update_time          datetime(6) not null comment '更新时间',
    PRIMARY KEY (`pk`)
);
CREATE INDEX cscene_id on mp_cscene_snapshot (cscene_id);