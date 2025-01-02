create table mp_local_load_execution_record
(
    pk               int auto_increment comment '主键'
        primary key,
    id               varchar(64)               not null comment 'ID',
    scene            varchar(64)               not null comment '场景',
    resource_id      varchar(64)               null comment 'ojj实例id',
    load_node        varchar(64)               not null comment '载荷节点',
    load_node_name   varchar(32)               not null,
    source_ip        varchar(32)               not null comment '源ip',
    destination_node varchar(64)               not null comment '目标节点',
    destination_ip   varchar(32)               not null comment '目标ip',
    status           int(2) unsigned default 0 not null comment '状态',
    create_time      datetime(6)               not null comment '创建时间',
    update_time      datetime(6)               not null comment '更新时间'
)
    comment '本地载荷执行记录表';