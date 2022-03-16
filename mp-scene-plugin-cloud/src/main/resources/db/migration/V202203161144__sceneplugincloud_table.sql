ALTER TABLE mp_sceneplugincloud_scene_config_element MODIFY COLUMN `firewall_rules` text NOT NULL COMMENT '虚拟防火墙规则';
ALTER TABLE mp_sceneplugincloud_scene_config_element_port MODIFY COLUMN `firewall_rules` text NOT NULL COMMENT '虚拟防火墙规则';

ALTER TABLE mp_sceneplugincloud_scene_element MODIFY COLUMN `firewall_rules` text NOT NULL COMMENT '虚拟防火墙规则';
ALTER TABLE mp_sceneplugincloud_scene_element_port MODIFY COLUMN `firewall_rules` text NOT NULL COMMENT '虚拟防火墙规则';