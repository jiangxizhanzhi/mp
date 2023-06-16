#v5.4
alter table mp_sceneplugincloud_scene_element
    add column snmp_config text null comment 'snmp相关配置' ,
    add column mib_data text null comment 'mib文件数据';

alter table mp_sceneplugincloud_scene_config_element
    add column snmp_config text null comment 'snmp相关配置' ,
    add column mib_data text null comment 'mib文件数据';

alter table mp_sceneplugincloud_scene_element add column fwdns_ips text comment '云平台用于分布式部署时需要的ip' ;