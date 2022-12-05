# v5.3.6
alter table mp_devops_target add column `support_export` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否支持导出，0：false,1:true' ;