# v5.3.7
alter table mp_devops_target add column `create_user` varchar(128) NOT NULL COMMENT '靶标创建用户' ;
alter table mp_devops_target add column `create_user_name` varchar(128) NOT NULL COMMENT '靶标创建用户名' ;