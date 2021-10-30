-- auto-generated definition
create table mp_situation_probe
(
    pk               int auto_increment
        primary key,
    id               varchar(64)   not null,
    probe_name       varchar(32)   not null comment '探针名称',
    des              varchar(64)   null comment '描述',
    command_interval varchar(10)   not null comment '定时任务配置',
    probe_category   int default 0 not null comment '0:信息类 1:日志类 2: 事件类',
    source_category  int default 0 not null comment '0:默认探针 1:定制探针',
    script_content   blob          null,
    create_time      datetime      null,
    update_time      datetime      null,
    del_flag         int default 0 null comment '1:删除 0:未删除',
    OS_type          int default 0 not null comment '操作系统类型 0:linux 1:windows',
    probe_status     int default 0 null comment '0:未开始 1：运行中 2：已关闭',
    script_type      int default 0 null comment '0:shell 1:python 2:bat',
    constraint mp_situation_probe_id_uindex
        unique (id)
)
    comment '探针基本信息';



-- auto-generated definition
create table mp_situation_probe_param
(
    pk          int auto_increment
        primary key,
    id          varchar(64)   not null,
    probe_id    varchar(64)   not null comment '探针管理id',
    param_type  int           null comment '0:number 1:string 2:bool 3:ip',
    default_val varchar(32)   null comment '默认值',
    param_des   varchar(64)   null comment '参数说明',
    create_time datetime      not null,
    update_time datetime      null,
    del_flag    int default 0 not null,
    param_name  varchar(32)   null,
    constraint mp_situation_probe_param_id_uindex
        unique (id)
)
    comment '探针的参数';


-- auto-generated definition
create table mp_situation_script
(
    pk           int auto_increment
        primary key,
    probe_id     varchar(64) not null comment '探针id',
    content      blob        null,
    probe_type   int         null,
    probe_name   varchar(64) null comment '探针名称',
    os_type      int         null,
    `interval`   varchar(10) null,
    command_line varchar(64) null,
    tag          varchar(64) null,
    create_time  datetime    not null comment '探针创建时间',
    constraint mp_situation_script_probe_id_uindex
        unique (probe_id)
)
    comment '探针元信息表,中台不使用';

alter table mp_situation_probe_param modify default_val varchar(50) null comment '默认值';

