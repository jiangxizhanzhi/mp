update mp_common_option set zh_name = '已启动' where type = 'cscene_instance_status' and code = 3 ;
update mp_common_option set zh_name = '未启动' where type = 'cscene_instance_status' and code = 0 ;