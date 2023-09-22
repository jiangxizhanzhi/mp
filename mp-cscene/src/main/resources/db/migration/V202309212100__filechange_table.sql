-- 文件和进程变更对应告警信息配置表
create TABLE `host_collect_map_warning_config` (
       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
       `id` varchar(64) NOT NULL COMMENT 'ID',
       `warning_name` varchar(64) NOT NULL COMMENT '告警名称',
       `warning_target` int(2) NOT NULL COMMENT '告警方向，0-关键文件变化 1-系统典型差异',
       `warning_class` int(2) NOT NULL COMMENT '告警等级，0-低危 1-中危 2-高危',
       `warning_movements` varchar(64) NOT NULL COMMENT '告警动作，可能会有多个，0-新增，1-变更，2-删除',
       `system_type` int(2) NOT NULL COMMENT '系统类型，0-windows 1-linux',
       `process_names` varchar(1024) NOT NULL default '' COMMENT '进程名称，多个用,隔开',
       `process_detail_features` varchar(1024) NOT NULL default '' COMMENT '进程内容包含字段，多个用,隔开',
       `process_status_list` varchar(64) NOT NULL default '' COMMENT '进程状态，0-running 1-sleeping，多个用,隔开',
       `file_paths` varchar(2048) NOT NULL default '' COMMENT '文件路径，多个用,隔开',
       `file_suffixes` varchar(1024) NOT NULL default '' COMMENT '文件后缀，多个用,隔开',
       `file_size_min` varchar(64) NOT NULL default '' COMMENT '文件体积下限单位是MB',
       `register_paths` varchar(1024) NOT NULL default '' COMMENT '注册表路径',
       `update_time` datetime(6) NOT NULL COMMENT '更新时间',
       `create_time` datetime(6) NOT NULL COMMENT '创建时间',
       PRIMARY KEY (`pk`),
       UNIQUE KEY (`id`)
) COMMENT '主机采集信息匹配告警配置表，目前主要匹配文件和进程变更'






