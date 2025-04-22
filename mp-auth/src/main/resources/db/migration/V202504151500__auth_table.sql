alter table mp_auth_cloud_user
    add wazuh_manager_ip varchar(32) null comment '采集服务器地址';
alter table mp_devops_load
    add delivery tinyint(1) null comment '是否投递';

alter table mp_auth_cloud_user
    add wazuh_manager_user varchar(64) null comment '采集服务器用户',
    add wazuh_manager_password varchar(64) null comment '采集服务器密码';
-- 清空原载荷文件
update mp_devops_load
set script_file_path = null,
    script_file      = null
where 1=1;
