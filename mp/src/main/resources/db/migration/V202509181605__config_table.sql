insert into mp_sys_config(id, tenant, key_name, value, init_value, create_time, update_time) values ("4a3d
2ecc528e4b77b3d518a81bd0133s", "default", "sub_system_name", "某某教学安全演训平台", "某某教学安全演训平台", "2025-08-11
 17:46:58", "2025-09-18 16:00:44");


alter table mp_auth_role add column biz_role int not null default 0 comment '业务角色0-学生 1-老师';