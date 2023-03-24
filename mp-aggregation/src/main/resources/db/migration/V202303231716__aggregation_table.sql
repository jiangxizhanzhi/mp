create table mp_event_tracking_record
(
    pk          int auto_increment comment '主键'
        primary key,
    id          varchar(64) not null comment 'ID',
    module      varchar(64) not null comment '模块',
    category    varchar(64) not null comment '分类',
    action      varchar(64) not null comment '动作',
    target      varchar(64) not null comment '操作目标',
    create_time datetime(6) not null comment '创建时间',
    update_time datetime(6) not null comment '更新时间',
    index       module_cat (`module`, `category`)
) comment '埋点表';