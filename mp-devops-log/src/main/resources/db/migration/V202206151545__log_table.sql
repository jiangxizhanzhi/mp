-- auto-generated definition
create table mp_log_report
(
    pk           int auto_increment comment '主键'
        primary key,
    id           varchar(32)  not null comment '编号',
    file_name    varchar(32)  not null comment '文件名称',
    type         varchar(20)  not null comment '日志类型（CR/MP/AD/CP)',
    file_size    bigint       not null comment '文件大小',
    download_url varchar(255) not null comment '文件下载地址',
    create_time  datetime     not null comment '创建时间',
    update_time  datetime     not null comment '更新时间',
    constraint mp_log_report_id_uindex
        unique (id)
)
    comment '日志上报表';

