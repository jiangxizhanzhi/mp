drop table if exists mp_situation_probe;
drop table if exists mp_situation_probe_param;
drop table if exists mp_situation_script;

-- 探针表
create table mp_situation_probe
(
    pk int auto_increment
        primary key,
    id varchar(64) not null,
    probe_name varchar(32) not null comment '探针名称',
    des varchar(400) null comment '描述',
    command_interval varchar(10) not null comment '定时任务配置',
    probe_category int default 0 not null comment '0:信息类 1:事件类 2:日志类 ',
    source_category int default 0 not null comment '0:通用探针 1:自定义探针',
    script_content blob null,
    output_format varchar(10) default 'none' not null comment '自定义探针输出格式: none, json, plain',
    del_flag int default 0 null comment '1:删除 0:未删除',
    OS_type varchar(20) null comment '(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other',
    script_type int default 0 null comment '0:shell 1:python 2:bat',
    create_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    constraint mp_sita_probe_id unique (id)
)
    comment '探针基本信息';


-- 探针参数表
create table mp_situation_probe_param
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    probe_id varchar(64) not null comment '探针id',
    param_name varchar(32) null comment '参数名称',
    param_type int not null comment '0:number 1:string 2:bool 3:ip',
    default_val varchar(50) null comment '默认值',
    param_des varchar(64) null comment '参数说明',
    create_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    constraint mp_sita_probe_param_id unique (id)
)
    comment '探针的参数';



-- 采集模板表
create table mp_situation_collection_template
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    name varchar(200) not null comment '模板名称',
    des varchar(400) null comment '模板描述',
    OS_type int null comment '(单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other',
    create_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    constraint mp_sita_tel_id unique (id)
)
    comment '采集模板表';


-- 采集模板表-探针关系表
create table mp_situation_collection_template_probe
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    template_id varchar(64) not null comment '采集模板id',
    probe_id varchar(64) not null comment '探针id',
    enabled int default 1 not null comment '0:不启用，1:启用',
    config varchar(4000) null comment '探针配置，json串，数组',
    create_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    constraint mp_sita_tel_probe_id unique (id)
)
    comment '采集模板-探针关系表';
