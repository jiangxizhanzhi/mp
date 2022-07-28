-- 系统运维实例类型表
create table mp_cscene_type
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '系统运维实例类型编码',
    ch_name varchar(64) not null comment '系统运维实例类型中文名称',
    en_name varchar(64) not null comment '系统运维实例类型英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间',
    unique (id)
)
    comment '系统运维实例类型表';

-- 系统运维节点状态表
create table mp_cscene_status
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '系统运维节点状态编码',
    ch_name varchar(64) not null comment '系统运维节点状态中文名称',
    en_name varchar(64) not null comment '系统运维节点状态英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间',
    unique (id)
)
    comment '系统运维节点状态表';