alter table mp_sceneplugincloud_scene_element
    add column rip_lookback varchar(64)  not null comment 'rip组网的lookback';
alter table mp_sceneplugincloud_scene_config_element
    add column rip_lookback varchar(64)  not null comment 'rip组网的lookback';

alter table mp_sceneplugincloud_scene_element_port
    add column rip_ip   varchar(32)  not null comment 'rip组网的ip',
    add column rip_pear varchar(32)  not null comment 'rip组网的邻居';

alter table mp_sceneplugincloud_scene_config_element_port
    add column rip_ip   varchar(32)  not null comment 'rip组网的ip',
    add column rip_pear varchar(32)  not null comment 'rip组网的邻居';