# v5.4
alter table mp_sceneplugincloud_scene_element
    add column iso_attach_filename varchar(64)  not null comment '挂载iso文件名' after firewall_rules,
    add column iso_tmpdir_uuid varchar(64) not null comment '挂载iso临时文件地址' after iso_attach_filename;
alter table mp_sceneplugincloud_scene_config_element
    add column iso_attach_filename varchar(64)  not null comment '挂载iso文件名' after firewall_rules,
    add column iso_tmpdir_uuid varchar(64) not null comment '挂载iso临时文件地址' after iso_attach_filename;
