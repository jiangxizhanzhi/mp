alter table `mp_situation_suricatarule` add column class_type_cn varchar(1024) NOT NULL DEFAULT '' COMMENT "流量规则分类类型中文";
alter table `mp_situation_suricatarule` add column class_type_en varchar(1024) NOT NULL DEFAULT '' COMMENT "流量规则分类类型英文";
alter table `mp_situation_suricatarule` add column rule_gen_type int(2) NOT NULL DEFAULT 0 COMMENT "规则生成类型0-预定义，用户不能编辑 1-自定义，用户可以编辑";
