alter table mp_devops_target add column `application_num` int(11) NOT NULL DEFAULT 0 COMMENT '申请次数';
alter table mp_devops_target add column `is_occupy` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否占用';