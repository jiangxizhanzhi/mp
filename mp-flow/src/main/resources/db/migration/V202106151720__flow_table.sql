CREATE TABLE `mp_flow_flow_info` (
         `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
         `id` varchar(64) NOT NULL COMMENT '流量id',
         `title` varchar(64) NOT NULL COMMENT '流量标题',
         `flow_desc` varchar(320) NOT NULL COMMENT '流量描述',
         `interactive_type` tinyint(2) NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量，2 全流量',
         `resource_type` tinyint(2) NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
         `run_cmd` varchar(128) NOT NULL COMMENT '攻击脚本运行命令',
         `category` tinyint(4) NOT NULL COMMENT '分类   0:"测试测量",1:"应用升级",2:"网络管理",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"',
         `file_name` varchar(128) NOT NULL COMMENT '文件名称',
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
        `title` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
        `task_desc` varchar(320) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务描述',
        `actuator_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量执行器id',
        `flow_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量id',
        `flow_port_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量网口id',
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
        `title` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器名称',
        `ip` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器ip地址',
        `network_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网络id',
        `instance_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例id',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `update_time` datetime NOT NULL COMMENT '更新时间',
        PRIMARY KEY (`pk`),
        KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流量执行器表';

CREATE TABLE `mp_flow_port` (
        `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网口id',
        `server_ip` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务端ip',
        `server_mac` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务端mac',
        `server_port_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务端portId',
        `server_gateway` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务端网关',
        `client_ip` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ip',
        `client_mac` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端mac',
        `client_port_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端portId',
        `client_gateway` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端网关',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `update_time` datetime NOT NULL COMMENT '更新时间',
        PRIMARY KEY (`pk`),
        KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流量执行网口表';

CREATE TABLE `mp_flow_collection` (
          `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
          `id` varchar(64) NOT NULL COMMENT '集合id',
          `title` varchar(64) NOT NULL COMMENT '集合标题',
          `description` varchar(320) NOT NULL COMMENT '集合描述',
          `category` tinyint(4) NOT NULL COMMENT '集合类型   0:"测试测量",1:"应用升级",2:"网络管理",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"',
          `background_flow_count` int(11) NOT NULL COMMENT '背景流量数量',
          `attack_flow_count` int(11) NOT NULL COMMENT '攻击流量数量',
          `create_time` datetime NOT NULL COMMENT '创建时间',
          `update_time` datetime NOT NULL COMMENT '更新时间',
          PRIMARY KEY (`pk`),
          KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流量集合表';

CREATE TABLE `mp_flow_collection_rel` (
          `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
          `id` varchar(64) NOT NULL COMMENT '关联id',
          `collection_id` varchar(64) NOT NULL COMMENT '流量集合id',
          `flow_id` varchar(64) NOT NULL COMMENT '流量id',
          `create_time` datetime NOT NULL COMMENT '创建时间',
          `update_time` datetime NOT NULL COMMENT '更新时间',
          PRIMARY KEY (`pk`),
          UNIQUE KEY `unique_index` (`collection_id`,`flow_id`) USING HASH,
          KEY `id_index` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流量关联集合表';

CREATE TABLE `mp_flow_script_demo` (
           `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
           `id` varchar(64) NOT NULL COMMENT 'id',
           `script_name` varchar(64) NOT NULL COMMENT '脚本名称',
           `url` varchar(1024) NOT NULL COMMENT '脚本url',
           `create_time` datetime NOT NULL COMMENT '创建时间',
           `update_time` datetime NOT NULL COMMENT '更新时间',
           PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流量脚本模板表';

