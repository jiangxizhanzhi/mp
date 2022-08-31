-- 去掉create_time和update_time中的DEFAULT CURRENT_TIMESTAMP
ALTER TABLE mp_auth_url_ac modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_url_ac modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_url_ac_target modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_url_ac_target modify update_time datetime NOT NULL COMMENT '更新时间';