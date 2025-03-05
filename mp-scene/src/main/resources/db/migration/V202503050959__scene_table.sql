alter table mp_scene_scene add column client_lan varchar(255) not null default '192.168.1.0/24' comment '红蓝对抗红方所处lan cidr';
alter table mp_scene_scene_config add column client_lan varchar(255) not null default '192.168.1.0/24' comment '红蓝对抗红方所处lan cidr';

alter table mp_scene_scene add column server_vpn_ip varchar(255) not null default '' comment '蓝方部署的vpn server端ip';
alter table mp_scene_scene_config add column server_vpn_ip varchar(255) not null default '' comment '蓝方部署的vpn server端ip';