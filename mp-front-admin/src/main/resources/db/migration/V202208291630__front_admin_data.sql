-- 去掉create_time和update_time中的DEFAULT CURRENT_TIMESTAMP
ALTER TABLE mp_front_admin_menu_extra_info modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_front_admin_menu_extra_info modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_front_admin_activ_ctl modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_front_admin_activ_ctl modify update_time datetime NOT NULL COMMENT '更新时间';