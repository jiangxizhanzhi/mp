-- 探针类型表
create table mp_situation_probe_type
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '探针类型编码',
    name varchar(64) not null comment '探针类型名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间',
    constraint mp_sita_tel_probe_id unique (id)
)
    comment '探针类型表';