# yz4
alter table mp_devops_target add column business_tag int(11) not null default 1 comment '业务标签,1:终端防护靶标,2:路由交换靶标,3:边界安全防护设备靶标,4:流量分析靶标,5:应用服务靶标' ;
alter table mp_devops_target add column reference_nums int(11) not null default 0 comment '引用过的次数' ;
alter table mp_devops_target add column use_duration int(11) not null default 0 comment '靶标管理中启动过的时长' ;
alter table mp_scene_scene_element add column physical_location varchar(128) not null default '' comment '物理位置' ;
alter table mp_scene_scene_config_element add column physical_location varchar(128) not null default '' comment '物理位置' ;
alter table mp_devops_target add column last_create_time datetime(6) COMMENT '上一次启动时间' ;

INSERT INTO mp_sys_config (id, tenant, key_name, value, init_value, create_time, update_time)VALUES ('8b4191259f6c405ca103abcc5c00c001', 'default', 'screenshot_del_conf', '6', '6', now(), now());
INSERT INTO mp_sys_config (id, tenant, key_name, value, init_value, create_time, update_time)VALUES ('8b4191259f6c405ca103abcc5c00c002', 'default', 'image_del_config', '{"referenceNums":10,"useDuration":10}','{"referenceNums":10,"useDuration":10}', now(), now());
INSERT INTO mp_auth_url_ac (id, url, is_pattern, `group`, methods, `desc`, create_time, update_time) VALUES ('74a772c8569411ed90a0000ec67b0396', '/api/mp/devops/cluster/screenshot/update_delete_config', 0, '74a772bb569411ed90a0000ec67b0395', 'post', '更新截屏自动删除配置', now(), now());
INSERT INTO mp_auth_url_ac (id, url, is_pattern, `group`, methods, `desc`, create_time, update_time) VALUES ('74a772c8569411ed90a0000ec67b0397', '/api/mp/devops/cluster/screenshot/get_delete_config', 0, '74a772bb569411ed90a0000ec67b0395', 'get', '查询截屏自动删除配置', now(), now());

