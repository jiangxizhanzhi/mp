# v5.3.7
alter table mp_cscene_cscene
    add column support_partial_start tinyint(1)  not null comment '是否支持部分启动';
alter table mp_cscene_cscene_instance
    add column support_partial_start tinyint(1)  not null comment '是否支持部分启动';