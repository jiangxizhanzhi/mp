alter table `mp_auth_user`
    add column menus_content longtext not null default '' comment '用户的菜单';