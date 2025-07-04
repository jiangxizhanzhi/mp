alter table mp_devops_target
    add column is_attacker tinyint(1) default 0 null comment '是否是攻击机';
alter table mp_sceneplugincloud_scene_config_element
    add column is_attacker tinyint(1) default 0 null comment '是否是攻击机';
alter table mp_sceneplugincloud_scene_element
    add column is_attacker tinyint(1) default 0 null comment '是否是攻击机';