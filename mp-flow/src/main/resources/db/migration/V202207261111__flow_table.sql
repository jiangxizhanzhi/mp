-- 流量分类表
create table mp_flow_category
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '流量分类编码',
    ch_name varchar(64) not null comment '流量分类中文名称',
    en_name varchar(64) not null comment '流量分类英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间',
    constraint mp_sita_tel_probe_id unique (id)
)
    comment '流量分类表';