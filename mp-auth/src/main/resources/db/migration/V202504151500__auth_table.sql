alter table mp_auth_cloud_user
    add wazuh_manager_ip varchar(32) null comment '采集服务器地址';
alter table mp_devops_load
    add delivery tinyint(1) null comment '是否投递';

alter table mp_auth_cloud_user
    add wazuh_manager_ip varchar(32) null comment '采集服务器地址';
alter table mp_devops_load
    add delivery tinyint(1) null comment '是否投递';
