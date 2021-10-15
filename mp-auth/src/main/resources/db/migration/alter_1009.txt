# 目前已经更新的环境有
# 2.21 对应10.100.0.101库
# 2.21 对应10.100.0.101库


alter table mp_auth_tenant
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_tenant
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_org
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_org
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_role
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_role
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_tenant_user
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_tenant_user
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_user_heartbeat
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_user_heartbeat
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_user_online_report
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_user_online_report
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_user_role
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_user_role
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_target
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_target
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_org_permission
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_org_permission
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_role_permission
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_role_permission
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_user_permission
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_user_permission
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';

alter table mp_auth_org_user
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_org_user
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';



alter table mp_auth_user
    modify create_time datetime default current_timestamp not null comment '创建时间';
alter table mp_auth_user
    modify update_time datetime default current_timestamp not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间';
alter table mp_auth_user
    modify date_joined datetime not null comment '加入时间';
alter table mp_auth_user
    modify expired_time datetime not null comment '过期时间';
alter table mp_auth_user
    modify last_login datetime not null comment '最后登录时间';


create index mp_auth_user_update_time on mp_auth_user (update_time);



