## config cidr scope branch
alter table mp_scene_scene_config_element_port add column port_connect_net_cidr varchar(20) comment '路由端口网络cidr';
alter table mp_scene_scene_element_port add column port_connect_net_cidr varchar(20) comment '路由端口网络cidr';