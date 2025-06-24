-- 添加自定义探针示例
update mp_auth_target set `name` = '评估模型' where `name` = '指标项管理' ;
update mp_menu_template_info set `name` = '评估模型' where `name` = '指标项管理' ;