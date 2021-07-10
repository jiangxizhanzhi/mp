#v5.4
alter table mp_sceneplugincloud_scene_element
    add column iso_attach_filename varchar(64) not null comment '挂载iso文件名' ,
    add column iso_tmpdir_uuid varchar(64) not null comment '挂载iso临时文件地址' ,
    add column virtual_usbs varchar(512) not null comment '虚拟usb列表',
    add column entity_usbs varchar(512) not null comment '实体usb列表',
    add column docker_params text not null comment 'docker参数',
    add column platform varchar(64) not null comment 'gns3 platform';

alter table mp_sceneplugincloud_scene_config_element
    add column iso_attach_filename varchar(64) not null comment '挂载iso文件名' ,
    add column iso_tmpdir_uuid varchar(64) not null comment '挂载iso临时文件地址' ,
    add column virtual_usbs varchar(512) not null comment '虚拟usb列表',
    add column entity_usbs varchar(512) not null comment '实体usb列表',
    add column docker_params text not null comment 'docker参数',
    add column platform varchar(64) not null comment 'gns3 platform';

alter table mp_sceneplugincloud_scene_element
    add column snmp_config text null comment 'snmp相关配置' ,
    add column mib_data text null comment 'mib文件数据';

alter table mp_sceneplugincloud_scene_config_element
    add column snmp_config text null comment 'snmp相关配置' ,
    add column mib_data text null comment 'mib文件数据';

alter table mp_sceneplugincloud_scene_element add column fwdns_ips text comment '云平台用于分布式部署时需要的ip' ;