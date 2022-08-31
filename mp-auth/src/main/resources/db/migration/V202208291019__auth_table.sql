-- 加回mobile id_card address identifier，删除res
ALTER TABLE mp_auth_user ADD COLUMn mobile varchar(64) NOT NULL COMMENT '手机号' after last_login;
ALTER TABLE mp_auth_user ADD COLUMN id_card varchar(64) NOT NULL COMMENT '身份证号' after mobile;
ALTER TABLE mp_auth_user ADD COLUMN address varchar(255) NOT NULL COMMENT '地址' after id_card;
ALTER TABLE mp_auth_user ADD COLUMN identifier varchar(64) NULL COMMENT '编号' after address;
ALTER TABLE mp_auth_user ADD INDEX identifier(`identifier`);
ALTER TABLE mp_auth_user DROP COLUMN res;

-- 去掉create_time和update_time中的DEFAULT CURRENT_TIMESTAMP
ALTER TABLE mp_auth_tenant modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_tenant modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_org modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_org modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_role modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_role modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_user modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_user modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_tenant_user modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_tenant_user modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_user_online_report modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_user_online_report modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_user_online_record modify start_time datetime NOT NULL COMMENT '开始时间';
ALTER TABLE mp_auth_user_online_record modify end_time datetime COMMENT '结束时间';
ALTER TABLE mp_auth_user_online_record modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_user_online_record modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_user_role modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_user_role modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_target modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_target modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_org_permission modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_org_permission modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_role_permission modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_role_permission modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_user_permission modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_user_permission modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_org_user modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_org_user modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sys_config modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sys_config modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sys_notice modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sys_notice modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sys_notice_org modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sys_notice_org modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sys_notice_role modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sys_notice_role modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sys_notice_read modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sys_notice_read modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_auth_authentication_config modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_auth_authentication_config modify update_time datetime NOT NULL COMMENT '更新时间';
