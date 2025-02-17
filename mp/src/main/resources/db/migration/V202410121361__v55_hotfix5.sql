-- 前台沙盒攻防赛 -> 'AWDP Contest'
update mp_menu_template_info set en_name = 'AWDP Contest' where menu_id = 'ct-awdp-contest-list' ;
-- 后台沙盒攻防赛 -> 'AWDP'
update mp_menu_template_info set en_name = 'AWDP' where menu_id = 'ct-awdp-list' ;
-- 沙盒攻防题 -> 'AWDP Topic'
update mp_menu_template_info set en_name = 'AWDP Topic' where menu_id = 'ct-awdp-question-list' ;