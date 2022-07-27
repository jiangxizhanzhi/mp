-- 实装设备在位状态表
create table mp_devops_cluster_equipment_position
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '实装设备在位状态编码',
    ch_name varchar(64) not null comment '实装设备在位状态中文名称',
    en_name varchar(64) not null comment '实装设备在位状态英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间'
)
    comment '实装设备在位状态表';


-- 实装设备使用状态表
create table mp_devops_cluster_equipment_use
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    code int not null comment '实装设备使用状态编码',
    ch_name varchar(64) not null comment '实装设备使用状态中文名称',
    en_name varchar(64) not null comment '实装设备使用状态英文名称',
    create_time  datetime NOT NULL COMMENT '创建时间',
    update_time  datetime NOT NULL COMMENT '更新时间'
)
    comment '实装设备使用状态表';