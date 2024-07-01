# v5.4
alter table mp_auth_user add column gateway varchar(64) default null comment 'gateway';
alter table mp_auth_user add column cidr varchar(64) default null comment 'cidr';
alter table mp_auth_user add column allocation_pools varchar(1024) default null comment 'allocation_pools';