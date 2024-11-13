CREATE TABLE `mp_flow_actuator`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT '执行器id',
    `title`       varchar(64) NOT NULL COMMENT '执行器名称',
    `ip`          varchar(32) NOT NULL COMMENT '执行器ip地址',
    `network_id`  varchar(64) NOT NULL COMMENT '网络id',
    `instance_id` varchar(64) NOT NULL COMMENT '实例id',
    `cscene_id`   varchar(64) NOT NULL COMMENT '实例id',
    `status`      int(11) NOT NULL COMMENT '运行状态',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量执行器表';



CREATE TABLE `mp_flow_collection`
(
    `pk`                    bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                    varchar(64)  NOT NULL COMMENT '集合id',
    `title`                 varchar(64)  NOT NULL COMMENT '集合标题',
    `description`           varchar(320) NOT NULL COMMENT '集合描述',
    `category`              tinyint(4) NOT NULL COMMENT '集合类型   0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"',
    `type_flag`             tinyint UNSIGNED NOT NULL DEFAULT 2 COMMENT '集合分类：0背景流量，1攻击流量，2混合流量',
    `background_flow_count` int(11) NOT NULL COMMENT '背景流量数量',
    `attack_flow_count`     int(11) NOT NULL COMMENT '攻击流量数量',
    `create_time`           datetime     NOT NULL COMMENT '创建时间',
    `update_time`           datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量集合表';



CREATE TABLE `mp_flow_collection_rel`
(
    `pk`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT '关联id',
    `collection_id` varchar(64) NOT NULL COMMENT '流量集合id',
    `flow_id`       varchar(64) NOT NULL COMMENT '流量id',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`collection_id`, `flow_id`) USING HASH,
    UNIQUE KEY (`id`)
) COMMENT ='流量关联集合表';



CREATE TABLE `mp_flow_flow_info`
(
    `pk`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64)  NOT NULL COMMENT '流量id',
    `title`            varchar(64)  NOT NULL COMMENT '流量标题',
    `flow_desc`        varchar(320) NOT NULL COMMENT '流量描述',
    `interactive_type` tinyint(2) NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量，2 全流量',
    `resource_type`    tinyint(2) NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
    `run_cmd`          varchar(128) NOT NULL COMMENT '攻击脚本运行命令',
    `category`         tinyint(4) NOT NULL COMMENT '分类   0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"',
    `file_name`        varchar(128) NOT NULL COMMENT '文件名称',
    `pcap_file`        varchar(255) NOT NULL COMMENT 'pcap文件地址',
    `script_file`      varchar(255) NOT NULL COMMENT '脚本文件地址',
    `attack_flow_type` int default 1 comment '攻击流量攻击类别,对应common_option中attack_flow_type',
    `create_time`      datetime     NOT NULL COMMENT '创建时间',
    `update_time`      datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`title`)
) COMMENT ='流量信息表';



CREATE TABLE `mp_flow_port`
(
    `pk`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64) NOT NULL COMMENT '网口id',
    `server_ip`      varchar(32) NOT NULL COMMENT '服务端ip',
    `server_mac`     varchar(32) NOT NULL COMMENT '服务端mac',
    `server_port_id` varchar(40) NOT NULL COMMENT '服务端portId',
    `server_gateway` varchar(32) NOT NULL COMMENT '服务端网关',
    `client_ip`      varchar(32) NOT NULL COMMENT '客户端ip',
    `client_mac`     varchar(32) NOT NULL COMMENT '客户端mac',
    `client_port_id` varchar(40) NOT NULL COMMENT '客户端portId',
    `client_gateway` varchar(32) NOT NULL COMMENT '客户端网关',
    `network_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '网络id',
    `actuator_id`    varchar(64) NOT NULL DEFAULT '' COMMENT '流量执行器id',
    `port_type`      tinyint(4) NOT NULL DEFAULT 0 COMMENT '网口类型：0 pcap包，1 脚本',
    `create_time`    datetime    NOT NULL COMMENT '创建时间',
    `update_time`    datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量执行网口表';



CREATE TABLE `mp_flow_script_demo`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `script_name` varchar(64)   NOT NULL COMMENT '脚本名称',
    `url`         varchar(1024) NOT NULL COMMENT '脚本url',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    `type`        int(11) DEFAULT 1 COMMENT '示例脚本类型 1:流量 2:探针',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量脚本模板表';



CREATE TABLE `mp_flow_script_param`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT '脚本参数id',
    `flow_id`     varchar(64)   NOT NULL COMMENT '流量id',
    `param_key`   varchar(50)   NOT NULL COMMENT '参数名',
    `param_value` varchar(64)   NOT NULL COMMENT '参数值',
    `param_type`  tinyint(4) NOT NULL COMMENT '参数类型：0 string,1 IP,2 number,3 bool',
    `param_desc`  varchar(2048) NOT NULL COMMENT '参数说明',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE KEY (`id`, `flow_id`) USING HASH
) COMMENT ='流量脚本参数表';



CREATE TABLE `mp_flow_task`
(
    `pk`              bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`              varchar(64)   NOT NULL COMMENT '任务id',
    `title`           varchar(64)   NOT NULL COMMENT '任务标题',
    `task_desc`       varchar(320)  NOT NULL COMMENT '任务描述',
    `actuator_id`     varchar(64)   NOT NULL COMMENT '流量执行器id',
    `flow_id`         varchar(64)   NOT NULL COMMENT '流量id',
    `flow_port_id`    varchar(64)   NOT NULL COMMENT '流量网口id',
    `flow_platform`   tinyint(4) NOT NULL COMMENT '流量发生平台：0 通用，1 北京流量发生器',
    `third_flow_id`   varchar(64)   NOT NULL COMMENT '流量第三方uuid',
    `third_flow_name` varchar(128)  NOT NULL COMMENT '流量第三方名称',
    `third_task_id`   varchar(64)   NOT NULL COMMENT '流量任务第三方uuid',
    `count`           int(10) unsigned NOT NULL COMMENT '执行次数',
    `delay`           int(10) unsigned NOT NULL COMMENT '首次执行延迟时间，单位：秒(可以为0)',
    `interval_time`   int(10) unsigned NOT NULL COMMENT '周期执行间隔，单位：秒',
    `task_status`     tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止',
    `running_count`   int(11) NOT NULL DEFAULT 0 COMMENT '实际执行次数',
    `origin_ips`      varchar(32)   NOT NULL COMMENT '源ip范围',
    `target_ips`      varchar(32)   NOT NULL COMMENT '目标ip范围',
    `node_id`         varchar(64)   NOT NULL COMMENT '节点id',
    `custom_flag`     tinyint(4) NOT NULL DEFAULT 0 COMMENT '自定义流量任务标志 0：否， 1：是',
    `flow_protocol`   varchar(512)  NOT NULL COMMENT '流量协议',
    `content`         varchar(1024) NOT NULL COMMENT '流量内容',
    `mult`            int(11) NOT NULL DEFAULT 1 COMMENT '倍数:大于等于1的整数',
    `create_time`     datetime      NOT NULL COMMENT '创建时间',
    `update_time`     datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量任务表';

CREATE TABLE `mp_flow_protocol`
(
    `pk`            bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `id`            varchar(64) NOT NULL COMMENT '协议id',
    `flow_id`       varchar(64) NOT NULL COMMENT '流量id',
    `protocol_name` varchar(64) NOT NULL COMMENT '协议名称',
    `rate`          float       NOT NULL COMMENT '比例',
    `marked`        tinyint(2) UNSIGNED NOT NULL COMMENT '是否勾选：0 否，1 是',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `flow_protocol_id` (`id`, `flow_id`) USING HASH
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量协议表';

CREATE TABLE `mp_flow_pcap`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT '流量包id',
    `flow_id`     varchar(64)  NOT NULL COMMENT '流量id',
    `duration`    bigint(20) UNSIGNED NOT NULL COMMENT '时长(单位ms)',
    `pcap_size`   bigint(10) UNSIGNED NOT NULL COMMENT '大小(单位Byte)',
    `pkt_num`     bigint(10) UNSIGNED NOT NULL COMMENT '包数(报文数量)',
    `protocols`   varchar(128) NOT NULL COMMENT '包含协议',
    `pcap_file`   varchar(255) NOT NULL COMMENT 'pcap文件地址',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `flow_id` (`flow_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量包表';

CREATE TABLE `mp_flow_task_conf`
(
    `pk`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`             varchar(64) NOT NULL COMMENT '配置id',
    `task_id`        varchar(64) NOT NULL COMMENT '任务id',
    `keep_running`   tinyint(2) UNSIGNED NOT NULL COMMENT '持续运行：0 否，1 是',
    `rate_unlimited` tinyint(2) UNSIGNED NOT NULL COMMENT '速率无限制：0 否，1 是',
    `replay`         tinyint(2) UNSIGNED NOT NULL COMMENT '是否流量重放：0 否，1 是',
    `conf`           text        NOT NULL COMMENT '配置',
    `create_time`    datetime    NOT NULL COMMENT '创建时间',
    `update_time`    datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `task_id` (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务配置表';

CREATE TABLE `mp_flow_task_task`
(
    `pk`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64) NOT NULL COMMENT '任务id',
    `scene_id`         varchar(64) NOT NULL COMMENT '场景实例id',
    `actuator_id`      varchar(64) NOT NULL COMMENT '流量执行器id',
    `resource_type`    tinyint(2) NOT NULL COMMENT '资源类型：0 pcap包，1 脚本',
    `interactive_type` tinyint(2) NOT NULL COMMENT '交互类型：0 背景流量，1 攻击流量',
    `flow_id`          varchar(64) NOT NULL COMMENT '流量id',
    `port_id`          varchar(64) NOT NULL COMMENT '网口id',
    `success_count`    int(11) NOT NULL DEFAULT 0 COMMENT '成功执行次数',
    `error_count`      int(11) NOT NULL DEFAULT 0 COMMENT '失败执行次数',
    `task_status`      tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败',
    `node_id`          varchar(64) NOT NULL COMMENT '目标节点id',
    `node_ip`          varchar(24) NOT NULL COMMENT '目标节点ip',
    `node_name`        varchar(64) NOT NULL COMMENT '目标节点名称',
    `create_time`      datetime    NOT NULL COMMENT '创建时间',
    `update_time`      datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量执行任务表';

CREATE TABLE `mp_flow_task_port`
(
    `pk`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT '网口id',
    `network_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '网络id',
    `actuator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '流量执行器id',
    `mac`         varchar(64) NOT NULL COMMENT '网口mac',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务网口表';

CREATE TABLE `mp_flow_task_record`
(
    `pk`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) NOT NULL COMMENT '记录id',
    `actuator_id`   varchar(64) NOT NULL COMMENT '流量执行器id',
    `task_id`       varchar(64) NOT NULL COMMENT '任务id',
    `tx_bps`        bigint(20) NOT NULL COMMENT '实时发送速率(单位byte/s)',
    `tx_pps`        bigint(20) NOT NULL COMMENT '实时发包数',
    `latency`       bigint(10) NOT NULL COMMENT '延迟(单位ms)',
    `jitter`        bigint(10) NOT NULL COMMENT '抖动(单位ms)',
    `obytes`        bigint(20) NOT NULL COMMENT '发包总大小(单位byte)',
    `oerrors`       bigint(20) NOT NULL COMMENT '发包错误数',
    `total_tx_L2`   bigint(20) NOT NULL COMMENT '发送速率L2(单位byte/s)',
    `total_tx_L1`   bigint(20) NOT NULL COMMENT '发送速率L1(单位byte/s)',
    `total_pps`     bigint(20) NOT NULL COMMENT '总发包数',
    `total_rx`      bigint(20) NOT NULL COMMENT '接收速率(单位byte/s)',
    `queue_full`    bigint(20) NOT NULL COMMENT '阻塞队列大小(单位byte)',
    `drop_rate`     bigint(20) NOT NULL COMMENT '丢包率(单位byte/s)',
    `record_status` tinyint(2) NOT NULL COMMENT '是否有效：0 无效，1 有效',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY `id` (`id`),
    KEY             `task_id` (`task_id`),
    KEY             `create_time` (`create_time`),
    KEY             `task_id_and_create_time` (`task_id`, `create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='流量任务记录表';

CREATE TABLE `mp_common_option`
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'ID',
    `code`        varchar(255)  NOT NULL COMMENT '选项编码',
    `parent_code` varchar(255)  NOT NULL COMMENT '父级选项编码',
    `zh_name`     varchar(255)  NOT NULL COMMENT '选项中文名称',
    `en_name`     varchar(255)  NOT NULL COMMENT '选项英文名称',
    `sort`        int(10) NOT NULL COMMENT '排序字段',
    `type`        varchar(255)  NOT NULL COMMENT '选项类型',
    `meta`        varchar(2000) NOT NULL COMMENT '元数据',
    `create_time` datetime      NOT NULL COMMENT '创建时间',
    `update_time` datetime      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`code`, `type`)
) COMMENT ='选项表';