alter table mp_sceneplugincloud_scene_element add column vlan_switch_url text not null comment '多vlan交换机默认访问url';

alter table mp_sceneplugincloud_scene_config_element add column vlan_switch_url text not null comment '多vlan交换机默认访问url';