ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `wan_port_number` int(10) NOT NULL COMMENT 'wan口数量';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `wan_port_type`   varchar(8) NOT NULL COMMENT 'wan口类型';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `lan_port_number` int(10) NOT NULL COMMENT 'lan口数量';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `lan_port_type`   varchar(8) NOT NULL COMMENT 'lan口类型';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `host`  varchar(64) NOT NULL default '' COMMENT '宿主机名称';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `category2` int(10) NOT NULL COMMENT '二级分类';
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD COLUMN `category3` int(10) NOT NULL COMMENT '三级分类';
ALTER TABLE mp_sceneplugincloud_scene_config_element MODIFY COLUMN `firewall_rules` varchar(2048) NOT NULL COMMENT '虚拟防火墙规则';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port ADD COLUMN `firewall_rules` varchar(2048) NOT NULL COMMENT '虚拟防火墙规则';

ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `wan_port_number` int(10) NOT NULL COMMENT 'wan口数量';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `wan_port_type`   varchar(8) NOT NULL COMMENT 'wan口类型';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `lan_port_number` int(10) NOT NULL COMMENT 'lan口数量';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `lan_port_type`   varchar(8) NOT NULL COMMENT 'lan口类型';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `host`  varchar(64) NOT NULL default '' COMMENT '宿主机名称';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `category2` int(10) NOT NULL COMMENT '二级分类';
ALTER TABLE mp_sceneplugincloud_scene_element ADD COLUMN `category3` int(10) NOT NULL COMMENT '三级分类';
ALTER TABLE mp_sceneplugincloud_scene_element MODIFY COLUMN `firewall_rules` varchar(2048) NOT NULL COMMENT '虚拟防火墙规则';
ALTER TABLE mp_sceneplugincloud_scene_element_port ADD COLUMN `firewall_rules` varchar(2048) NOT NULL COMMENT '虚拟防火墙规则';
