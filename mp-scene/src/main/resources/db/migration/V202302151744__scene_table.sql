# wh9c

create table mp_scene_instance_start_his
(
    pk              int auto_increment comment '主键',
    id              varchar(64)   not null comment 'ID',
    source_scene_id varchar(64)   not null comment '场景模板id',
    instance_id     varchar(64)   not null comment '实例id',
    `name`          varchar(256)  not null comment '场景名称',
    reference_type  int(10)       not null comment '场景所属业务类型',
    event           int(10)       not null comment '场景事件',
    event_status    int(10)       not null comment '场景事件状态',
    process_message varchar(2048) not null comment '场景过程描述',
    create_time     datetime(6)   not null comment '创建时间',
    update_time     datetime(6)   not null comment '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`instance_id`)
)