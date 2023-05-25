# v5.4
##修改name长度和业务保持一致
alter table mp_cscene_cscene_role modify column name varchar(256) NOT NULL COMMENT '名称';