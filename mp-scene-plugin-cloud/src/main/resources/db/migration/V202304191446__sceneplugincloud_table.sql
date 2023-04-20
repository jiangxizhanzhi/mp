# v5.4
alter table mp_sceneplugincloud_scene_element
    add column iso_attach_filename varchar(64)  not null comment '挂载iso文件名' ,
    add column iso_tmpdir_uuid varchar(64) not null comment '挂载iso临时文件地址' ;
alter table mp_sceneplugincloud_scene_config_element
    add column iso_attach_filename varchar(64)  not null comment '挂载iso文件名' ,
    add column iso_tmpdir_uuid varchar(64) not null comment '挂载iso临时文件地址' ;

alter table mp_sceneplugincloud_scene_element add column virtual_usbs varchar(512) not null comment '虚拟usb列表' ;
alter table mp_sceneplugincloud_scene_config_element add column virtual_usbs varchar(512) not null comment '虚拟usb列表' ;