create table tenant (
    pk int(11) not null comment '主键',
    id varchar(64)  not null comment 'id',
    identifier varchar(64)  not null comment '编号',
    name varchar(64) not null comment '名称',
    primary key (pk),
    unique (identifier)
);

create table org (
    pk int(11) not null comment '主键',
    id varchar(64)  not null comment 'id',
    identifier varchar(64)  not null comment '编号',
    name varchar(64) not null comment '名称',
    primary key (pk),
    unique (identifier)
);

create table role (
    pk int(11) not null comment '主键',
    id varchar(64)  not null comment 'id',
    identifier varchar(64)  not null comment '编号',
    name varchar(64) not null comment '名称',
    primary key (pk),
    unique (identifier)
);

create table user (
    pk int(11) not null comment '主键',
    id varchar(64)  not null comment 'id',
    identifier varchar(64)  not null comment '编号',
    username varchar(64) not null comment '用户名',
    password varchar(128) not null comment '密码',
    name varchar(128) not null comment '姓名',
    primary key (pk),
    unique (identifier),
    unique (username)
);
