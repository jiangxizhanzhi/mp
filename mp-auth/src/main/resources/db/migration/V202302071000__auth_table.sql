alter table mp_auth_user add type int default 1 not null comment '用户类型 1:系统用户 2:域用户';
