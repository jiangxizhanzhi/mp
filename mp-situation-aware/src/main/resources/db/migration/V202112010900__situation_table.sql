-- auto-generated definition
create table mp_situation_traffic_group
(
    pk               int auto_increment
        primary key,
    traffic_group_id varchar(64)   not null comment '流量索引前缀名',
    create_time      datetime      not null,
    del_flag         int default 0 null comment '1:删除 0:未删除',
    constraint mp_situation_traffic_group_group_id_uindex
        unique (traffic_group_id)
)
    comment ',';

