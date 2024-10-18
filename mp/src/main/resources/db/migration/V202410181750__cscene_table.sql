alter table mp_devops_target add column `support_qga` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否支持qga' ;
alter table mp_devops_target add column `operate_machine` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是操作机' ;
alter table mp_devops_target add column `can_participants_use` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否可以参训人员使用' ;
alter table mp_devops_target add column `ext` varchar(512) NOT NULL DEFAULT '' COMMENT '扩展字段' ;