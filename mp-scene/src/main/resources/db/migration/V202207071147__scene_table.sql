alter table mp_scene_scene_config add column `public_net_proxy` tinyint(1) NOT NULL COMMENT '开启外网代理';
alter table mp_scene_scene_config add column `public_net_proxy_ip` varchar(32) NOT NULL COMMENT '外网代理ip';
alter table mp_scene_scene_config add column `public_net_proxy_port` varchar(32) NOT NULL COMMENT '外网代理端口';
alter table mp_scene_scene_config_element add column `public_net_proxy` tinyint(1) NOT NULL COMMENT '开启外网代理';
alter table mp_scene_scene_config_element add column `public_net_proxy_ip` varchar(32) NOT NULL COMMENT '外网代理ip';
alter table mp_scene_scene_config_element add column `public_net_proxy_port` varchar(32) NOT NULL COMMENT '外网代理端口';
alter table mp_scene_scene_config_element_port add column `public_net_proxy`  tinyint(1) NULL COMMENT '开启外网代理';
alter table mp_scene_scene_config_element_port add column `public_net_proxy_ip` varchar(32) NOT NULL COMMENT '外网代理ip';
alter table mp_scene_scene_config_element_port add column `public_net_proxy_port` varchar(32) NOT NULL COMMENT '外网代理端口';

alter table mp_scene_scene add column `public_net_proxy` tinyint(1) NOT NULL COMMENT '开启外网代理';
alter table mp_scene_scene add column `public_net_proxy_ip` varchar(32) NOT NULL COMMENT '外网代理ip';
alter table mp_scene_scene add column `public_net_proxy_port` varchar(32) NOT NULL COMMENT '外网代理端口';
alter table mp_scene_scene_element add column `public_net_proxy` tinyint(1) NOT NULL COMMENT '开启外网代理';
alter table mp_scene_scene_element add column `public_net_proxy_ip` varchar(32) NOT NULL COMMENT '外网代理ip';
alter table mp_scene_scene_element add column `public_net_proxy_port` varchar(32) NOT NULL COMMENT '外网代理端口';
alter table mp_scene_scene_element_port add column `public_net_proxy`  tinyint(1) NULL COMMENT '开启外网代理';
alter table mp_scene_scene_element_port add column `public_net_proxy_ip` varchar(32) NOT NULL COMMENT '外网代理ip';
alter table mp_scene_scene_element_port add column `public_net_proxy_port` varchar(32) NOT NULL COMMENT '外网代理端口';