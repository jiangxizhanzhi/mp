# v5.4
alter table mp_auth_user add column mac_addr varchar(256) default '' not null comment 'mac地址';