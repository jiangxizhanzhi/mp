# v5.3.7 clw
alter table mp_devops_target add column `model` varchar(128) NOT NULL COMMENT '型号' ;
alter table mp_devops_target add column `manufacturer` varchar(128) NOT NULL COMMENT '厂商' ;