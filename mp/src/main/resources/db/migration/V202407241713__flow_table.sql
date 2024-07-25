INSERT INTO mp_common_option (id, code, parent_code, zh_name, en_name, sort, `type`, meta, create_time, update_time)
VALUES ('er3fa9ef0f0411ed996d0242ac110002', '1', '', 'DDoS', 'Distributed Denial of Service', 1, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110003', '2', '', '蠕虫', 'worm virus', 2, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110004', '3', '', '垃圾邮件', 'spam email', 3, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110006', '4', '', 'DNS污染', 'dns poisoning', 4, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110007', '5', '', 'BGP劫持', 'BGP hijacking', 5, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110008', '6', '', '缓冲区溢出', 'buffer overflow', 6, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110009', '7', '', 'SQL注入', 'SQL injection', 7, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110010', '8', '', 'XSS', 'Cross-Site Scripting', 8, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14'),
       ('er3fa9ef0f0411ed996d0242ac110011', '9', '', '勒索病毒', 'Ransomware', 9, 'attack_flow_type',
        '{"activKeys":"no_auth","visible":1}', '2023-04-20 19:17:37', '2024-05-08 15:30:14');

alter table mp_flow_flow_info add column attack_flow_type int default 1 comment '攻击流量攻击类别,对应common_option中attack_flow_type';
alter table mp_sceneplugincloud_scene add column flow_mirror_ip varchar(128) not null default '' comment '流量镜像的ip' ;
alter table mp_sceneplugincloud_scene add column flow_mirror_id varchar(128) not null default '' comment '流量镜像的id' ;
alter table mp_sceneplugincloud_scene_config add column flow_mirror_ip varchar(128) not null default '' comment '流量镜像的ip' ;
alter table mp_sceneplugincloud_scene_config add column flow_mirror_id varchar(128) not null default '' comment '流量镜像的id' ;