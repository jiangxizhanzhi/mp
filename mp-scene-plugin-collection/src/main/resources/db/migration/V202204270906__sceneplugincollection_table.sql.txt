CREATE TABLE mp_sceneplugincollection_scene_log_probe_location (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64) NOT NULL COMMENT 'id',
    `probe_id` varchar(64) NOT NULL COMMENT '探针id',
    `os_type` varchar(64) NOT NULL COMMENT '系统类型',
    `log_format` varchar(64) NOT NULL COMMENT '日志格式',
    `location` varchar(256) NOT NULL COMMENT '日志地址',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);

INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('727666d63f0446db94f7174a4ffc100f','security','windows','eventlog','Security','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('ecc34515d70343968ba34492ce436ea1','os','windows','eventchannel','Microsoft-Windows-PrintService/Operational','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('993a75b618ad47e8abf19b430d57df49','security','windows','eventchannel','Application','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('12fdc75abee64ceca69cd7074ee7c3ab','security','windows','eventchannel','Security','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('85ae2da8da7b48da9cd9fdbf29ce88e0','os','windows','eventchannel','System','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('584812a25c5640e4b004b1e74dde6df3','os','windows','eventchannel','Microsoft-Windows-Sysmon/Operational','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('a0ebcdf1f24249ea8c8681fc3267cfdc','security','windows','eventchannel','Microsoft-Windows-Windows Defender/Operational','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('d3bef780aac243cab23681a557efa2d6','application','windows','eventchannel','File Replication Service','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('e643ad4f6c6140258e483e85192892d5','application','windows','eventchannel','Service Microsoft-Windows-TerminalServices-RemoteConnectionManager','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('20057caabbc64cff8822962cc3ad09c7','security','linux','syslog','/var/log/clamav.log','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('c81eb432db994b69bf2a63e6c918958e','security','linux','syslog','/var/log/secure','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('0628b4fe4a944138ac7817b8f60bebc8','application','linux','syslog','/var/log/maillog','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('c4794f022270402698e4857bfab5c417','application','linux','syslog','/var/log/httpd','2022-4-27 09:43:59','2022-4-27 09:43:59');
INSERT INTO mp_sceneplugincollection_scene_log_probe_location (id, probe_id, os_type, log_format, location,create_time,update_time)
VALUES ('a24917df628140c2b85742040dfc65ef','os','linux','syslog','/var/log/syslog','2022-4-27 09:43:59','2022-4-27 09:43:59');