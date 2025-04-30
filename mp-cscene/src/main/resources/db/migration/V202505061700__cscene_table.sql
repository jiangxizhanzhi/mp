create table mp_sceneplugincloud_cve_data
(
    `pk`             int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64) NOT NULL COMMENT 'ID',
    `device_version` varchar(64) NOT NULL COMMENT '机器型号，对应 mp_sceneplugincloud_scene_config_element 的 image ',
    `content`        text comment 'cve内容',
    `create_time`    datetime    NOT NULL COMMENT '创建时间',
    `update_time`    datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`)
) COMMENT ='网络设备和安全设备cve信息存储表';