-- 实装设备使用状态表
create table mp_devops_cluster_equipment_use
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '实装设备使用状态编码',
    name varchar(64) not null comment '实装设备使用状态名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间',
    constraint mp_sita_tel_probe_id unique (id)
)
    comment '实装设备使用状态表';