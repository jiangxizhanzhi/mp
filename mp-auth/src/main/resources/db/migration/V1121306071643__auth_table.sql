# v5.4
alter table mp_auth_role add column visible tinyint default true not null comment '是否可见 1:可见，0:不可见';