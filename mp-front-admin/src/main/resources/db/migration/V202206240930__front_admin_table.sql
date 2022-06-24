alter table mp_front_admin_activ_ctl modify sys_run_seconds varchar(256) default 0 null comment '系统运行秒数';
alter table mp_front_admin_activ_ctl add sys_lifetime varchar(256) null comment '系统寿命' after sys_run_seconds;
