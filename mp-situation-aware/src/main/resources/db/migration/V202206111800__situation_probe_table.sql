drop table mp_situation_collection_template_probe;

-- 采集模板表-探针关系表
create table mp_situation_collection_template_probe
(
    pk int auto_increment primary key,
    id varchar(64) not null,
    template_id varchar(64) not null comment '采集模板id',
    probe_id varchar(64) not null comment '探针id',
    name varchar(32) not null comment '探针名称',
    description varchar(400) null comment '描述',
    probe_category int default 0 not null comment '0:信息类 1:事件类 2:日志类 ',
    type int default 0 not null comment '0:通用探针 1:自定义探针',
    output_format varchar(10) default 'none' not null comment '自定义探针输出格式: none, json, plain',
    engine varchar(40) null comment '分析引擎',
    OS_type varchar(20) null comment '(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other',
    creator varchar(100) null comment '创建者用户名',
    disabled int default 0 not null comment '0:不启用，1:启用',
    script_url varchar(400) null comment '探针脚本内容fdfs地址，采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板',
    interval_sec int default 180 not null comment '探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec',
    config varchar(4000) null comment '探针配置，json串，数组',
    parameter varchar(2000) null comment '探针参数',
    create_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    constraint mp_sita_tel_probe_id unique (id)
)
    comment '采集模板-探针关系表';
create index idx_sit_coltempp_tid_pid on mp_situation_collection_template_probe (template_id, probe_id);
