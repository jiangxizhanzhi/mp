alter table mp_sceneplugincloud_scene_config_element add column `wan_port_number` int(10) NOT NULL COMMENT 'wan口数量' after optical_port_type ;
alter table mp_sceneplugincloud_scene_config_element add column `wan_port_type`   varchar(8) NOT NULL COMMENT 'wan口类型' after wan_port_number ;
alter table mp_sceneplugincloud_scene_config_element add column `lan_port_number` int(10) NOT NULL COMMENT 'lan口数量' after wan_port_type ;
alter table mp_sceneplugincloud_scene_config_element add column `lan_port_type`   varchar(8) NOT NULL COMMENT 'lan口类型' after lan_port_number ;

alter table mp_sceneplugincloud_scene_element add column `wan_port_number` int(10) NOT NULL COMMENT 'wan口数量' after optical_port_type ;
alter table mp_sceneplugincloud_scene_element add column `wan_port_type`   varchar(8) NOT NULL COMMENT 'wan口类型' after wan_port_number ;
alter table mp_sceneplugincloud_scene_element add column `lan_port_number` int(10) NOT NULL COMMENT 'lan口数量' after wan_port_type ;
alter table mp_sceneplugincloud_scene_element add column `lan_port_type`   varchar(8) NOT NULL COMMENT 'lan口类型' after lan_port_number ;
alter table mp_sceneplugincloud_scene_element add column `host`  varchar(64) NOT NULL default '' COMMENT '宿主机名称' after mgmt_mac_addr ;