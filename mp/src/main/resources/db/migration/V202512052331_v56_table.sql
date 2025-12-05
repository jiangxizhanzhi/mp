alter table mp_scene_scene_element add npc_protocol varchar(255) default '' NOT NULL comment 'npc_protocol';
alter table mp_scene_scene_config_element add npc_protocol varchar(255) default '' NOT NULL comment 'npc_protocol';

alter table mp_scene_scene_element add npc_url varchar(255) default '' NOT NULL comment 'npc_url';
alter table mp_scene_scene_config_element add npc_url varchar(255) default '' NOT NULL comment 'npc_url';

alter table mp_scene_scene_element add npc_port varchar(255) default '' NOT NULL comment 'npc_port';
alter table mp_scene_scene_config_element add npc_port varchar(255) default '' NOT NULL comment 'npc_port';

alter table mp_scene_scene_element add npc_username varchar(255) default '' NOT NULL comment 'npc_username';
alter table mp_scene_scene_config_element add npc_username varchar(255) default '' NOT NULL comment 'npc_username';

alter table mp_scene_scene_element add npc_password varchar(255) default '' NOT NULL comment 'npc_password';
alter table mp_scene_scene_config_element add npc_password varchar(255) default '' NOT NULL comment 'npc_password';





