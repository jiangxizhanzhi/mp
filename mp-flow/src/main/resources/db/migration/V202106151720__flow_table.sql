CREATE TABLE `mp_flow_flow_info` (
         `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
         `id` varchar(64) NOT NULL COMMENT '流量id',
         `title` varchar(32) NOT NULL COMMENT '流量标题',
         `flow_desc` varchar(255) NOT NULL COMMENT '流量描述',
         `interactive_type` tinyint(2) NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量，2 全流量',
         `resource_type` tinyint(2) NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
         `pcap_file` varchar(255) NOT NULL COMMENT 'pcap文件地址',
         `script_file` varchar(255) NOT NULL COMMENT '脚本文件地址',
         `create_time` datetime NOT NULL COMMENT '创建时间',
         `update_time` datetime NOT NULL COMMENT '更新时间',
         PRIMARY KEY (`pk`),
         KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流量信息表';

CREATE TABLE `mp_flow_task` (
        `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务id',
        `title` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
        `task_desc` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务描述',
        `scene_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景id',
        `flow_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量id',
        `flow_platform` tinyint(4) NOT NULL COMMENT '流量发生平台：0 通用，1 北京流量发生器',
        `third_flow_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量第三方uuid',
        `third_flow_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量第三方名称',
        `third_task_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量任务第三方uuid',
        `count` int(10) unsigned NOT NULL COMMENT '执行次数',
        `delay` int(10) unsigned NOT NULL COMMENT '首次执行延迟时间，单位：秒(可以为0)',
        `interval_time` int(10) unsigned NOT NULL COMMENT '周期执行间隔，单位：秒',
        `task_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止',
        `origin_ips` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '源ip范围',
        `target_ips` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标ip范围',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `update_time` datetime NOT NULL COMMENT '更新时间',
        PRIMARY KEY (`pk`),
        KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流量任务表';

CREATE TABLE `mp_flow_actuator` (
        `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器id',
        `title` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器名称',
        `scene_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景id',
        `ip` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器ip地址',
        `network_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网络id',
        `instance_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例id',
        `server_ip` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务端ip',
        `server_mac` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'trex服务端mac',
        `server_gateway` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务端网关',
        `client_ip` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ip',
        `client_mac` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'trex客户端mac',
        `client_gateway` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端网关',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `update_time` datetime NOT NULL COMMENT '更新时间',
        PRIMARY KEY (`pk`),
        UNIQUE KEY `sceneId_index` (`scene_id`) USING BTREE,
        KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流量执行器表';
