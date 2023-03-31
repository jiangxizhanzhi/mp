alter table mp_devops_target add column `application_num` int(11) NOT NULL COMMENT '申请次数';
alter table mp_devops_target add column `is_occupy` tinyint(1) NOT NULL COMMENT '是否占用';