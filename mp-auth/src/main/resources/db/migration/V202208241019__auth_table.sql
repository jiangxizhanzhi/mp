ALTER TABLE mp_auth_user ADD COLUMn mobile varchar(64) NOT NULL COMMENT '手机号';
ALTER TABLE mp_auth_user ADD COLUMN id_card varchar(64) NOT NULL COMMENT '身份证号';
ALTER TABLE mp_auth_user ADD COLUMN address varchar(255) NOT NULL COMMENT '地址';
ALTER TABLE mp_auth_user ADD COLUMN identifier varchar(64) NULL COMMENT '编号';
ALTER TABLE mp_auth_user ADD INDEX identifier(`identifier`);
ALTER TABLE mp_auth_user DROP COLUMN res;

