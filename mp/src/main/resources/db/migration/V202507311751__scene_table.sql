alter table mp_sys_config add column `type` varchar(64) not null default 'text' comment '对应前端组件类型';
alter table mp_sys_config add column `options` text not null  default '' comment '对应前端组件的选项';
alter table mp_sys_config add column `range` varchar(64) not null default '' comment '数值组件的范围';
alter table mp_sys_config add column `unit` varchar(64) not null default '' comment '数值组件的单位';