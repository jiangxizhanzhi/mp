alter table mp_flow_flow_info add column create_user varchar(64) not null comment '创建用户' ;
alter table mp_flow_collection add column create_user varchar(64) not null comment '创建用户' ;