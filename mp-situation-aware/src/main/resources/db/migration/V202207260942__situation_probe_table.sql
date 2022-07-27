-- 操作系统类型表
create table mp_situation_os_type
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '操作系统类型编码',
    ch_name varchar(64) not null comment '操作系统类型中文名称',
    en_name varchar(64) not null comment '操作系统类型英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间'
)
    comment '操作系统类型表';

-- 探针类型表
create table mp_situation_probe_type
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '探针类型编码',
    ch_name varchar(64) not null comment '探针类型中文名称',
    en_name varchar(64) not null comment '探针类型英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间'
)
    comment '探针类型表';