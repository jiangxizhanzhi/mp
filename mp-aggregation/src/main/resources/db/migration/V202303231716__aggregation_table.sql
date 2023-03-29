create table mp_event_tracking_record
(
    pk             int auto_increment comment '主键'
        primary key,
    id             varchar(64) not null comment 'ID',
    module         varchar(64) not null comment '模块',
    category       varchar(64) not null comment '分类',
    action         varchar(64) not null comment '动作',
    target         varchar(64) not null comment '操作目标',
    operation_type varchar(64) not null comment '触发类型',
    current_url    varchar(64) not null comment '当前url',
    title          varchar(64) not null comment '按钮文本',
    parameter      text        not null comment '携带参数',
    user_id        varchar(64) not null comment '用户id',
    user_name      varchar(64) not null comment '用户名称',
    create_time    datetime(6) not null comment '创建时间',
    update_time    datetime(6) not null comment '更新时间',
    index          module_cat (`module`, `category`)
) comment '埋点表';

create table mp_role_operate_config
(
    pk          int auto_increment comment '主键'
        primary key,
    id          varchar(64)  not null comment 'ID',
    module      varchar(64)  not null comment '模块',
    `key`       varchar(64)  not null comment '模块',
    `url`       varchar(64)  not null comment 'url',
    `desc`      varchar(128) not null comment '描述',
    create_time datetime(6)  not null comment '创建时间',
    update_time datetime(6)  not null comment '更新时间',
    unique key (id)
) comment '试用角色操作权限配置表';