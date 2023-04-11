alter table mp_cscene_cscene add column app_type int(11) not null comment '场景应用类型' ;
alter table mp_cscene_cscene_instance add column app_type int(11) not null comment '场景应用类型' ;
alter table mp_cscene_cscene add column biz_type int(11) not null comment '场景类型' ;
alter table mp_cscene_cscene_instance add column biz_type int(11) not null comment '场景类型' ;