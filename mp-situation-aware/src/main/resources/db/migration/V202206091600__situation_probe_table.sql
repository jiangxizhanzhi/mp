alter table mp_situation_probe modify name varchar(50) not null comment '探针名称';
alter table mp_situation_probe modify description varchar(600) null comment '描述';

alter table mp_situation_probe_param modify name varchar(50) not null comment '探针名称';
alter table mp_situation_probe_param modify description varchar(600) null comment '描述';
alter table mp_situation_probe_param modify default_value varchar(600) null comment '默认值';

alter table mp_situation_collection_template modify description varchar(600) null comment '描述';
