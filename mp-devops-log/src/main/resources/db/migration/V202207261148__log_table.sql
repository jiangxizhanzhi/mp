-- 审计日志模块表
create table mp_devops_log_module
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '审计日志模块编码',
    ch_name varchar(64) not null comment '审计日志模块中文名称',
    en_name varchar(64) not null comment '审计日志模块英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间'
)
    comment '审计日志模块表';