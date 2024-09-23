create table mp_azure_vm_os_disk
(
    pk                              int auto_increment comment '主键'
        primary key,
    id                              varchar(64)            not null comment 'ID',
    name                            varchar(32) default '' not null comment '磁盘类型名称',
    size                            varchar(32) default '' not null comment '性能等级名称',
    max_size_gi_b                   varchar(32) default '' null comment '磁盘容量GB',
    max_size                        varchar(32) default '' not null comment '磁盘最大容量',
    max_size_unit                   varchar(5) null comment '磁盘大小单位',
    iops                            varchar(32) default '' null comment 'IOPS',
    iops_performance_plus           varchar(32) default '' null comment '扩展IOPS',
    bandwidth_mbps                  varchar(32) default '' null comment '磁盘吞吐量',
    bandwidth_mbps_performance_plus varchar(32) default '' null comment '扩展吞吐量',
    max_burst_iops                  varchar(32) default '' null comment '最大突发IOPS',
    max_burst_bandwidth_mbps        varchar(32) default '' null comment '最大突发吞吐量',
    max_burst_duration_in_min       varchar(32) default '0' null comment '最大突发持续时间',
    create_user                     varchar(64) null comment '创建用户id',
    create_username                 varchar(64) null comment '创建用户名称',
    deleted                         tinyint(1) default 0 not null comment '是否删除',
    create_time                     datetime(6) not null comment '创建时间',
    update_time                     datetime(6) not null comment '更新时间',
    constraint id
        unique (id)
) comment '微软云虚拟机系统磁盘信息列表';

alter table mp_sceneplugincloud_scene_config_element
    add vm_os_disk_type varchar(32) null comment '虚拟机大小';

alter table mp_sceneplugincloud_scene_element
    add vm_os_disk_type varchar(32) null comment '虚拟机大小';

alter table mp_scene_scene_config_element
    add control_type varchar(64) null comment '控制类型';

alter table mp_scene_scene_config_element
    add default_cmd varchar(255) default '0' null comment '默认命令';

alter table mp_scene_scene_element
    add control_type varchar(64) null comment '控制类型';

alter table mp_scene_scene_element
    add default_cmd varchar(255) default '0' null comment '默认命令';

alter table mp_scene_scene_config_element
    add expansion_disk int(10) null comment '扩展磁盘大小';

alter table mp_scene_scene_element
    add expansion_disk int(10) null comment '扩展磁盘大小';

alter table mp_sceneplugincloud_scene_config_element
    add vm_expansion_disk_type varchar(32) null comment '虚拟机扩容磁盘类型';

alter table mp_sceneplugincloud_scene_element
    add vm_expansion_disk_type varchar(32) null comment '虚拟机扩容磁盘类型';

alter table mp_sceneplugincloud_scene_config_element
    add expansion tinyint(1) not null comment '是否扩容';

alter table mp_sceneplugincloud_scene_element
    add expansion tinyint(1) not null comment '是否扩容';

alter table mp_sceneplugincollection_scene
    modify flow_collector varchar (255) not null comment '流量采集器id';

create table mp_scene_element_situation_probe
(
    pk            int auto_increment comment '主键'
        primary key,
    id            varchar(64) not null comment 'ID',
    scene         varchar(64) not null comment '场景实例id',
    scene_element varchar(64) not null comment '节点id',
    probe_id      varchar(64) not null comment '探针id',
    status        int         not null comment '探针状态 0：未安装 1：运行 2：停止',
    create_time   datetime null comment '创建时间',
    update_time   datetime null comment '更新时间',
    deleted       tinyint(1) default 0 not null comment '是否删除',
    constraint id
        unique (id)
) comment '节点探针信息关联表';

alter table mp_sceneplugincloud_scene_config_element
    add service_type varchar(255) null comment '二级类型-云场景';

alter table mp_sceneplugincloud_scene_element
    add service_type varchar(255) null comment '二级类型-云场景';

alter table mp_sceneplugincloud_scene_config_element
    add sub_type varchar(255) null comment '三级类型-云场景';

alter table mp_sceneplugincloud_scene_element
    add sub_type varchar(255) null comment '三级类型-云场景';

alter table mp_sceneplugincloud_scene_config_element
    add cloud_service_type varchar(255) null comment '云服务类型-云场景';

alter table mp_sceneplugincloud_scene_element
    add cloud_service_type varchar(255) null comment '云服务类型-云场景';

alter table mp_sceneplugincloud_scene_config_element
    add source_type tinyint(1) DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)';

alter table mp_sceneplugincloud_scene_element
    add source_type tinyint(1) DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)';

alter table mp_sceneplugincloud_scene_config_element
    add upload_image_id varchar(255) DEFAULT NULL COMMENT '用户上传镜像id';

alter table mp_sceneplugincloud_scene_element
    add upload_image_id varchar(255) DEFAULT NULL COMMENT '用户上传镜像id';

alter table mp_sceneplugincloud_scene_config_element
    add username varchar(64) null comment '用户名';

alter table mp_sceneplugincloud_scene_element
    add username varchar(64) null comment '用户名';

alter table mp_sceneplugincloud_scene_config_element
    add password varchar(64) null comment '密码';

alter table mp_sceneplugincloud_scene_element
    add password varchar(64) null comment '密码';

alter table mp_sceneplugincloud_scene_config_element
    change vuln_code loophole_info varchar (255) null comment '漏洞code';

alter table mp_sceneplugincloud_scene_element
    change vuln_code loophole_info varchar (255) null comment '漏洞code';

alter table mp_sceneplugincloud_scene_config_element
    add default_launch_image_name varchar(64) null comment '默认可运行的镜像名称';

alter table mp_sceneplugincloud_scene_element
    add default_launch_image_name varchar(64) null comment '默认可运行的镜像名称';

alter table mp_scene_scene_config_element
    add action_type varchar(64) null comment '运行类型CODE';

alter table mp_scene_scene_element
    add action_type varchar(64) null comment '运行类型CODE';

alter table mp_scene_scene_config_element
    add action_type_name varchar(64) null comment '运行类型NAME';

alter table mp_scene_scene_element
    add action_type_name varchar(64) null comment '运行类型NAME';

alter table mp_scene_scene_config_element
    add control_type_name varchar(64) null comment '控制类型NAME';

alter table mp_scene_scene_element
    add control_type_name varchar(64) null comment '控制类型NAME';

alter table mp_scene_scene_config_element
    add run_table varchar(64) null comment '运行平台CODE';

alter table mp_scene_scene_element
    add run_table varchar(64) null comment '运行平台CODE';

alter table mp_scene_scene_config_element
    add run_table_name varchar(64) null comment '运行平台NAME';

alter table mp_scene_scene_element
    add run_table_name varchar(64) null comment '运行平台NAME';

alter table mp_sceneplugincloud_scene_config_element
    add is_public_ip tinyint(1) DEFAULT 0 COMMENT '公共ip标记 0否 1是';

alter table mp_sceneplugincloud_scene_element
    add is_public_ip tinyint(1) DEFAULT 0 COMMENT '公共ip标记 0否 1是';

alter table mp_sceneplugincloud_scene_config_element
    add public_ip_address varchar(64) null comment '公共ip';

alter table mp_sceneplugincloud_scene_element
    add public_ip_address varchar(64) null comment '公共ip';

alter table mp_scene_scene_config_element_port
    add protocols varchar(64) null comment '接入协议';

alter table mp_scene_scene_element_port
    add protocols varchar(64) null comment '接入协议';

alter table mp_sceneplugincloud_scene_element_port
    modify auxiliary_network_id varchar (255) not null comment '辅助网络id';

alter table mp_sceneplugincloud_scene_config_element
    add cidr_pools varchar(500) null comment '网段信息';

alter table mp_sceneplugincloud_scene_element
    add cidr_pools varchar(500) null comment '网段信息';

alter table mp_sceneplugincloud_scene_config_element
    add address varchar(32) null comment '边界设备-ip地址';

alter table mp_sceneplugincloud_scene_element
    add address varchar(32) null comment '边界设备-ip地址';

alter table mp_sceneplugincloud_scene_config_element
    add protocol varchar(32) null comment '边界设备-接入协议';

alter table mp_sceneplugincloud_scene_element
    add protocol varchar(32) null comment '边界设备-接入协议';

alter table mp_sceneplugincloud_scene_config_element
    add port varchar(32) null comment '边界设备-端口';

alter table mp_sceneplugincloud_scene_element
    add port varchar(32) null comment '边界设备-端口';

alter table mp_sceneplugincloud_scene_config_element
    add management_ip varchar(32) null comment '管理网络ip-用于远程访问';

alter table mp_sceneplugincloud_scene_element
    add management_ip varchar(32) null comment '管理网络ip-用于远程访问';


create table mp_wazuh_probe_data
(
    pk          bigint unsigned auto_increment comment '主键'
        primary key,
    id          varchar(64) charset utf8mb4 not null comment 'id',
    scene_id    varchar(64) charset utf8mb4 not null comment '场景id',
    node_name   varchar(255) null comment '主机名称',
    probe_name  varchar(255) null comment '探针名称',
    event_desc  varchar(255) null comment '详情',
    content     varchar(255) null comment '底层信息',
    time        datetime null comment '时间',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间'
) charset = utf8;

create table mp_element_log_list
(
    pk          bigint unsigned auto_increment comment '主键'
        primary key,
    id          varchar(64) not null comment 'id',
    scene_id    varchar(64) not null comment '场景id',
    node_name   varchar(255) null comment '主机名称',
    time        datetime null comment '时间',
    app_name    varchar(255) null comment '应用名',
    event_desc  varchar(500) null comment '事件描述',
    level       tinyint(3) null comment '级别',
    description varchar(255) null comment 'rule描述信息',
    catagory    varchar(255) null comment '分类',
    log_type    varchar(10) null comment '日志类型用于区分日志/告警信息',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    rule_id     varchar(32) null comment '规则id'
) row_format = DYNAMIC;


-- fu

DROP TABLE IF EXISTS `mp_devops_cloud_target`;
create table mp_devops_cloud_target
(
    pk                      int auto_increment comment '主键'
        primary key,
    id                      varchar(64) null comment '服务ID',
    other_name              varchar(255) null comment '别名',
    name                    varchar(255) null comment '服务名称',
    cloud_type              varchar(8) null comment '云类型',
    cloud_service_type      tinyint(8) null comment '云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )',
    operating_system        varchar(40) null comment '操作系统',
    operating_system_status varchar(40) null comment '操作系统状态',
    generation              varchar(64) null comment '代系',
    version                 varchar(64) null comment '版本号',
    create_time             datetime(6) null comment '创建时间',
    update_time             datetime(6) null comment '更新时间'
) comment '云靶标信息表' row_format = DYNAMIC;

DROP TABLE IF EXISTS `mp_auth_cloud_user`;
CREATE TABLE `mp_auth_cloud_user`
(
    `pk`                    int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `connect_ip`            varchar(255) null comment '连接地址',
    `cloud_type`            tinyint(8) NULL DEFAULT NULL COMMENT '云类型(1: 微软公有云 2:微软私有云 3:微软混合云)',
    `azure_tenant_id`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'tenant_id',
    `azure_subscription_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'subscription_id',
    `azure_client_id`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'client_id',
    `azure_client_secret`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'client_secret',
    `create_time`           datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `creator`               varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `version`               tinyint(4) NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `tenant`(`cloud_type`, `azure_subscription_id`) USING BTREE,
    UNIQUE INDEX `tenant_2`(`cloud_type`, `azure_client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2070 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '微软云账号信息' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_sys_code_enum`;
CREATE TABLE `mp_sys_code_enum`
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'code编码',
    `code_name`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `type`        varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型(01:下拉框 02:搜索框)',
    `supplement`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '补充',
    `remark`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `parent_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父级code',
    `level`       int(64) NULL DEFAULT NULL COMMENT '层级',
    `status`      tinyint(8) NULL DEFAULT NULL COMMENT '状态(1:使用 2:禁用)',
    `creator`     varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `code`(`code`) USING BTREE COMMENT 'code值唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mp_sys_code_enum
-- ----------------------------
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (24, '1005', '状态码', NULL, NULL, NULL, NULL, 1, 1, NULL, '2024-04-11 11:22:51');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (25, '100501', '载荷执行结果页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (26, '10050101', '执行结果', NULL, NULL, 'result_status', '100501', 3, 1, NULL, '2024-04-11 11:24:05');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (27, '1005010101', '成功', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (28, '1005010102', '失败', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (29, '100502', '载荷页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-25 17:29:35');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (30, '10050201', '作用类型', '', NULL, 'action_type', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (31, '1005020101', '逆向分析', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (32, '1005020102', '信息探测', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (33, '1005020103', '漏洞利用', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (34, '10050202', '运行平台', NULL, NULL, 'run_table', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (35, '1005020201', 'docker', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (36, '1005020202', 'VMware', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (37, '1005020203', 'VM', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (38, '10050203', '控制类型', NULL, NULL, 'control_type', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (39, '1005020301', 'shell', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (40, '1005020302', 'web', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (41, '1005020303', '其他', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (80, '100503', '载荷同步设置code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-30 17:56:04');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (81, '10050301', '同步协议', NULL, NULL, 'synchronization_protocol', '100503', 3, 1, NULL, '2024-04-30 17:56:36');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (82, '1005030101', 'rest', NULL, NULL, NULL, '10050301', 4, 1, NULL, '2024-04-30 17:57:10');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (83, '100504', '云靶标镜像页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-05-06 10:28:07');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (84, '10050401', '操作系统', NULL, NULL, 'operating_system', '100504', 3, 1, NULL, '2024-05-06 10:28:39');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (85, '1005040101', 'windows', NULL, NULL, NULL, '10050401', 4, 1, NULL, '2024-05-06 10:29:17');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (86, '1005040102', 'linux', NULL, NULL, NULL, '10050401', 4, 1, NULL, '2024-05-06 10:31:25');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (87, '10050102', '结果类型', NULL, NULL, 'result_type', '100501', 3, 1, NULL, '2024-05-06 15:32:58');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (88, '1005010201', '自动上报', NULL, NULL, NULL, '10050102', 4, 1, NULL, '2024-05-06 15:33:47');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (89, '1005010202', '人工填报', NULL, NULL, NULL, '10050102', 4, 1, NULL, '2024-05-06 15:33:47');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (90, '100504010101', 'WindowsXP', NULL, NULL, NULL, '1005040101', 5, 1, NULL, '2024-05-08 09:57:59');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (91, '100504010102', 'Windows8', NULL, NULL, NULL, '1005040101', 5, 1, NULL, '2024-05-08 09:57:59');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (92, '10050402', '操作系统状态', NULL, NULL, 'operating_system_status', '100504', 3, 1, NULL, '2024-05-08 10:38:54');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (93, '1005040201', '通用化', NULL, NULL, NULL, '10050402', 4, 1, NULL, '2024-05-08 10:39:51');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (94, '1005040202', '专业化', NULL, NULL, NULL, '10050402', 4, 1, NULL, '2024-05-08 10:39:51');
INSERT INTO `mp_sys_code_enum`(`pk`, `code`, `code_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (99, '1005010103', '暂无结果', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-05-20 08:53:15');


DROP TABLE IF EXISTS `mp_devops_load`;
CREATE TABLE `mp_devops_load`
(
    `pk`                   int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ID',
    `tool_name`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具名称',
    `image_name`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '镜像名称',
    `tool_description`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具描述',
    `tool_platform`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具平台',
    `action_type`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作用类型',
    `run_table`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '运行平台',
    `control_type`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '控制类型',
    `control_port`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '控制端口',
    `source`               varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源(1载荷平台 2本地)',
    `cover`                varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面存储路径',
    `cover_name`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面名称',
    `attachment`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件存储路径',
    `attachment_name`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件名称',
    `read_me`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帮助文档存储路径',
    `read_me_name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帮助文档名称',
    `default_cmd`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '默认命令',
    `update_time`          datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `create_time`          datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `synchronization_time` datetime(0) NULL DEFAULT NULL COMMENT '同步时间',
    `creator_id`           varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 287 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷同步表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_load_scene_result_log`;
CREATE TABLE `mp_devops_load_scene_result_log`
(
    `pk`            int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具id',
    `instance_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具实例id',
    `scene_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景id',
    `stage_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阶段id',
    `task_id`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id',
    `result_log`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回日志',
    `result_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果状态',
    `result_type`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果类型',
    `update_time`   datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `create_time`   datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `creator_id`    varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷执行结果表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_load_source`;
CREATE TABLE `mp_devops_load_source`
(
    `pk`                       int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ip`                       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '载荷主机',
    `synchronization_protocol` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '同步协议',
    `authentication_type`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '认证方式(1:密码认证 2:cookie)',
    `user_name`                varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
    `password`                 varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
    `cookie`                   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cookie',
    `update_time`              datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `create_time`              datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `creator_id`               varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷同步设置表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_sys_url_config`;
CREATE TABLE `mp_sys_url_config`
(
    `pk`          int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'key',
    `service_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务地址',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `key`(`code`) USING BTREE COMMENT 'key唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置信息表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_calculate`;
create table mp_devops_cloud_target_calculate
(
    pk                      int auto_increment comment '主键'
        primary key,
    link_id                 varchar(64) null comment '服务ID',
    name                    varchar(164) null comment '服务名称',
    cloud_type              varchar(8) null comment '云类型',
    source_type             tinyint(8) null comment '源类型(1:云平台 2:用户上传)',
    upload_image_id         varchar(255) null comment '用户上传镜像id',
    publisher               varchar(64) null comment 'publisher',
    offer                   varchar(64) null comment 'offer',
    sku                     varchar(64) null comment 'sku',
    sub_type                varchar(255) null comment '子类型（虚拟机，vm应用程序定义）',
    calculate_type          varchar(255) null comment '计算类型（laas，paas）',
    operating_system        varchar(20) null comment '操作系统',
    operating_system_status varchar(20) null comment '操作系统状态',
    is_public               tinyint(1) null comment '是否公开，0：私有，1：自定义，2：公开',
    quick_create_support    tinyint(1) default 0 null comment '是否支持快速构建，0：不支持，1：支持',
    is_import               tinyint(1) default 0 null comment '是否导入，0：false,1:true',
    is_hide                 tinyint(1) default 0 null comment '是否隐藏，0：false,1:true',
    create_time             datetime(6) null comment '创建时间',
    update_time             datetime(6) null comment '更新时间',
    logo                    varchar(255) null comment '图标地址',
    version                 varchar(64) null comment '版本号',
    generation              varchar(64) null comment '代系'
) comment '云靶标信息表-计算表' row_format = DYNAMIC;


DROP TABLE IF EXISTS `mp_devops_cloud_target_analysis`;
CREATE TABLE `mp_devops_cloud_target_analysis`
(
    `pk`                   int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
    `name`                 varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
    `source_type`          tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
    `is_public`            tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
    `is_import`            tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
    `is_hide`              tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
    `create_time`          datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
    `logo`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-分析表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_container`;
create table mp_devops_cloud_target_container
(
    pk                      int auto_increment comment '主键'
        primary key,
    link_id                 varchar(64) null comment '关联id',
    name                    varchar(164) null comment '服务名称',
    cloud_type              varchar(8) null comment '云类型',
    source_type             tinyint(8) null comment '源类型(1:云平台 2:用户上传)',
    container_type          varchar(255) null comment '容器类型(1:容器基础结构 2:容器管理 3:容器化应用程序)',
    sub_type                varchar(255) null comment '子类型(1:容器实例 2:容器注册表 3:Kubernetes 服务)',
    upload_target_name      varchar(1000) null comment '用户上传镜像名称',
    operating_system        varchar(20) null comment '操作系统',
    operating_system_status varchar(20) null comment '操作系统状态',
    is_public               tinyint(1) null comment '是否公开，0：私有，1：自定义，2：公开',
    quick_create_support    tinyint(1) default 0 null comment '是否支持快速构建，0：不支持，1：支持',
    is_import               tinyint(1) default 0 null comment '是否导入，0：false,1:true',
    is_hide                 tinyint(1) default 0 null comment '是否隐藏，0：false,1:true',
    create_time             datetime(6) null comment '创建时间',
    update_time             datetime(6) null comment '更新时间',
    logo                    varchar(255) null comment '图标地址',
    version                 varchar(64) null comment '版本号',
    generation              varchar(64) null comment '代系'
) comment '云靶标信息表-容器表' row_format = DYNAMIC;

DROP TABLE IF EXISTS `mp_devops_cloud_target_storage`;
CREATE TABLE `mp_devops_cloud_target_storage`
(
    `pk`                   int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
    `name`                 varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
    `source_type`          tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
    `is_public`            tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
    `is_import`            tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
    `is_hide`              tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
    `create_time`          datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
    `logo`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-存储表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_database`;
CREATE TABLE `mp_devops_cloud_target_database`
(
    `pk`                   int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
    `name`                 varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
    `source_type`          tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
    `is_public`            tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
    `is_import`            tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
    `is_hide`              tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
    `create_time`          datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
    `logo`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-数据库表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_net`;
create table mp_devops_cloud_target_net
(
    pk                   int auto_increment comment '主键'
        primary key,
    link_id              varchar(64) null comment '服务ID',
    name                 varchar(164) null comment '服务名称',
    source_type          tinyint(8) null comment '源类型(1:云平台 2:用户上传)',
    cloud_type           varchar(8) null comment '云类型',
    net_type             varchar(255) null comment '网络类型',
    sub_type             varchar(255) null comment '子类型',
    is_public            tinyint(1) null comment '是否公开，0：私有，1：自定义，2：公开',
    quick_create_support tinyint(1) default 0 null comment '是否支持快速构建，0：不支持，1：支持',
    is_import            tinyint(1) default 0 null comment '是否导入，0：false,1:true',
    is_hide              tinyint(1) default 0 null comment '是否隐藏，0：false,1:true',
    create_time          datetime(6) null comment '创建时间',
    update_time          datetime(6) null comment '更新时间',
    logo                 varchar(255) null comment '图标地址'
) comment '云靶标信息表-网络表' row_format = DYNAMIC;

DROP TABLE IF EXISTS `mp_devops_cloud_target_security`;
CREATE TABLE `mp_devops_cloud_target_security`
(
    `pk`                   int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
    `name`                 varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
    `source_type`          tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
    `is_public`            tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
    `is_import`            tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
    `is_hide`              tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
    `create_time`          datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`          datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
    `logo`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-安全性表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_sys_code_menu`;
CREATE TABLE `mp_sys_code_menu`
(
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'code编码',
    `code_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `code_other_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
    `type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型(01:下拉框 02:搜索框)',
    `supplement` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '补充',
    `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `parent_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父级code',
    `level` int(64) NULL DEFAULT NULL COMMENT '层级',
    `status` tinyint(8) NULL DEFAULT NULL COMMENT '状态(1:使用 2:禁用)',
    `creator` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`pk`) USING BTREE,
    UNIQUE INDEX `code`(`code`) USING BTREE COMMENT 'code值唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 99 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mp_sys_code_menu
-- ----------------------------
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (1, '1002', '混合云配置项菜单', NULL, '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (2, '100201', '混合云', NULL, '01', '', '', '1002', 2, 1, '', '2024-03-28 14:00:09');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (3, '100202', '载荷', NULL, '01', '', '', '1002', 2, 1, '', '2024-03-28 15:20:26');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (4, '100203', '边界网络设备', NULL, '01', '', '', '1002', 2, 1, '', '2024-03-28 15:20:24');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (5, '10020101', '网络', NULL, '01', 'target.cloud_service_type = \'1\'', '1', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (6, '10020102', '计算', NULL, '01', 'target.cloud_service_type = \'2\'', '2', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (7, '10020103', '容器', NULL, '01', 'target.cloud_service_type = \'3\'', '3', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (8, '10020104', '数据库', NULL, '', 'target.cloud_service_type = \'4\'', '4', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (9, '10020105', '分析', NULL, '01', 'target.cloud_service_type = \'5\'', '5', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (10, '10020106', '安全性', NULL, '01', 'target.cloud_service_type = \'6\'', '6', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (11, '10020107', '存储', NULL, '01', 'target.cloud_service_type = \'7\'', '7', '100201', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (12, '1002010201', '关键字搜索', NULL, '02', 'name like \' * *\'', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (13, '1002010202', '类型', NULL, '01', '', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (14, '1002010203', '操作系统', NULL, '01', '', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (15, '100201020201', '全部', NULL, '', '', '', '1002010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (16, '100201020202', 'paas', NULL, '', 'calculate_type = \'paas\'', '', '1002010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (17, '100201020301', '全部', NULL, '', '', '', '1002010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (18, '100201020302', 'windows', NULL, '', 'operating_system = \'windows\'', '', '1002010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (19, '100201020303', 'linux', NULL, '', 'operating_system = \'linux\'', '', '1002010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (20, '1002010204', '子类型', NULL, '01', '', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (21, '100201020401', '虚拟机', NULL, '', 'sub_type = \'vm\'', '', '1002010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (22, '100201020402', 'VM应用程序定义', NULL, '', 'sub_type = \'vmApplicationDefinition\'', '', '1002010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (23, '100201020403', '应用程序服务', NULL, '', 'sub_type = \'applicationService\'', '', '1002010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (24, '1003', '私有云云配置项菜单', NULL, '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (25, '100301', '私有云', NULL, '01', '', '', '1003', 2, 1, '', '2024-03-28 14:00:09');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (26, '100302', '载荷', NULL, '01', '', '', '1003', 2, 1, '', '2024-03-28 15:20:26');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (27, '100303', '边界网络设备', NULL, '01', '', '', '1003', 2, 1, '', '2024-03-28 15:20:24');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (28, '10030101', '网络', NULL, '01', 'target.cloud_service_type = \'1\'', '1', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (29, '10030102', '计算', NULL, '01', 'target.cloud_service_type = \'2\'', '2', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (30, '10030103', '容器', NULL, '01', 'target.cloud_service_type = \'3\'', '3', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (31, '10030104', '数据库', NULL, '', 'target.cloud_service_type = \'4\'', '4', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (32, '10030105', '分析', NULL, '01', 'target.cloud_service_type = \'5\'', '5', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (33, '10030106', '安全性', NULL, '01', 'target.cloud_service_type = \'6\'', '6', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (34, '10030107', '存储', NULL, '01', 'target.cloud_service_type = \'7\'', '7', '100301', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (35, '1003010201', '关键字搜索', NULL, '02', 'name like \' * *\'', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (36, '1003010202', '类型', NULL, '01', '', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (37, '1003010203', '操作系统', NULL, '01', '', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (38, '100301020201', '全部', NULL, '', '', '', '1003010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (39, '100301020202', 'paas', NULL, '', 'calculate_type = \'paas\'', '', '1003010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (40, '100301020301', '全部', NULL, '', '', '', '1003010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (41, '100301020302', 'windows', NULL, '', 'operating_system = \'windows\'', '', '1003010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (42, '100301020303', 'linux', NULL, '', 'operating_system = \'linux\'', '', '1003010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (43, '1003010204', '子类型', NULL, '01', '', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (44, '100301020401', '虚拟机', NULL, '', 'sub_type = \'vm\'', '', '1003010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (45, '100301020402', 'VM应用程序定义', NULL, '', 'sub_type = \'vmApplicationDefinition\'', '', '1003010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (46, '100301020403', '应用程序服务', NULL, '', 'sub_type = \'applicationService\'', '', '1003010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (47, '1004', '公有云配置项菜单', NULL, '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (48, '100401', '公有云', NULL, '01', '', '', '1004', 2, 1, '', '2024-03-28 14:00:09');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (49, '100402', '载荷', NULL, '01', '', '', '1004', 2, 1, '', '2024-03-28 15:20:26');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (50, '100403', '边界网络设备', NULL, '01', '', '', '1004', 2, 1, '', '2024-03-28 15:20:24');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (51, '10040101', '网络', NULL, '01', 'target.cloud_service_type = \'1\'', '1', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (52, '10040102', '计算', NULL, '01', 'target.cloud_service_type = \'2\'', '2', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (53, '10040103', '容器', NULL, '01', 'target.cloud_service_type = \'3\'', '3', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (54, '10040104', '数据库', NULL, '', 'target.cloud_service_type = \'4\'', '4', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (55, '10040105', '分析', NULL, '01', 'target.cloud_service_type = \'5\'', '5', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (56, '10040106', '安全性', NULL, '01', 'target.cloud_service_type = \'6\'', '6', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (57, '10040107', '存储', NULL, '01', 'target.cloud_service_type = \'7\'', '7', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (58, '1004010201', '关键字搜索', NULL, '02', 'name like \' * *\'', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (59, '1004010202', '类型', NULL, '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (60, '1004010203', '操作系统', NULL, '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (61, '100401020201', '全部', NULL, '', '', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (62, '100401020202', 'paas', NULL, '', 'calculate_type = \'paas\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (63, '100401020301', '全部', NULL, '', '', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (64, '100401020302', 'windows', NULL, '', 'operating_system = \'windows\'', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (65, '100401020303', 'linux', NULL, '', 'operating_system = \'linux\'', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (66, '1004010204', '子类型', NULL, '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (67, '100401020401', '虚拟机', NULL, '', 'sub_type = \'vm\'', '', '1004010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (68, '100401020402', 'VM应用程序定义', NULL, '', 'sub_type = \'vmApplicationDefinition\'', '', '1004010204', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu`(`pk`, `code`, `code_name`, `code_other_name`, `type`, `supplement`, `remark`, `parent_code`, `level`, `status`, `creator`, `create_time`) VALUES (69, '100401020403', '应用程序服务', NULL, '', 'sub_type = \'applicationService\'', '', '1004010204', 5, 1, '', '2024-03-29 10:00:37');


INSERT INTO `mp_auth_url_ac`(`pk`, `id`, `url`, `is_pattern`, `group`, `methods`, `desc`, `create_time`, `update_time`)
VALUES (706, '33b99e69569711ed91a0000ec67b0395', '/api/mp/load/load/addLoadExecutionOutResult', 0, 'out-load-service',
        '*', '载荷平台数据入库', '2024-05-20 11:34:00', '2024-05-20 11:34:02');

INSERT INTO `mp_auth_url_ac_target`(`pk`, `id`, `group`, `target`, `create_time`, `update_time`)
VALUES (1180, '33b99e69569711ed91a0000ec67b0395', 'out-load-service', 'no_auth', '2024-05-20 11:31:56',
        '2024-05-20 11:31:56');

INSERT INTO mp_sys_url_config (pk, code, service_url, create_time, update_time)
VALUES (1, 'getBindLoopholes', 'http://10.108.1.10:80/api/vuln/admin/vuln/load_code', '2024-07-04 14:58:35',
        '2024-07-04 14:58:37');
INSERT INTO mp_sys_url_config (pk, code, service_url, create_time, update_time)
VALUES (2, 'getStagesBySceneId', 'http://10.108.1.10:80/api/vuln/admin/vuln/load_code', '2024-07-04 14:58:35',
        '2024-07-04 14:58:37');
INSERT INTO mp_sys_url_config (pk, code, service_url, create_time, update_time)
VALUES (3, 'addUser', 'http://ip:port/v1/gxpt/addUser', '2024-07-25 11:51:02', '2024-07-25 11:51:05');
INSERT INTO mp_sys_url_config (pk, code, service_url, create_time, update_time)
VALUES (4, 'deteleUser', 'http://ip:9088/xinan-server/mhUser/deteleUser', '2024-07-25 14:26:31', '2024-07-25 14:26:33');
INSERT INTO mp_sys_url_config (pk, code, service_url, create_time, update_time)
VALUES (5, 'addTarget', 'http://ip:port/ testApi/zySb/insert', '2024-07-25 14:37:50', '2024-07-25 14:37:52');
INSERT INTO mp_sys_url_config (pk, code, service_url, create_time, update_time)
VALUES (6, 'deleteTarget', 'http://ip:port/ /testApi/zySb/delete', '2024-07-25 15:22:04', '2024-07-25 15:22:07');


alter table mp_auth_user
    add is_sychronized varchar(8) null comment '是否已同步共性平台';
alter table mp_auth_user
    add return_data varchar(1024) null comment '同步共性平台返回data';
alter table mp_auth_user
    add return_info varchar(255) null comment '同步共性平台返回info';

alter table mp_azure_vm_size modify create_user varchar (64) null comment '创建用户id';
alter table mp_azure_vm_size modify create_username varchar (64) null comment '创建用户名称';
alter table mp_azure_vm_size
    alter column deleted set default 0;

alter table mp_devops_target
    add is_sychronized varchar(8) null comment '是否已同步共性平台';
alter table mp_devops_target
    add return_data varchar(1024) null comment '同步共性平台返回data';
alter table mp_devops_target
    add return_id varchar(255) null comment '同步共性平台返回id';

create table mp_element_file_count
(
    pk          bigint unsigned auto_increment comment '主键'
        primary key,
    id          varchar(64) charset utf8mb4 not null comment 'id',
    scene_id    varchar(64) charset utf8mb4 not null comment '场景id',
    time        varchar(32) null comment '时间',
    count       int null comment '数量',
    `group`     varchar(255) null comment 'group',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间'
) charset = utf8;

create table mp_element_file_list
(
    pk          bigint unsigned auto_increment comment '主键'
        primary key,
    id          varchar(64) not null comment 'id',
    scene_id    varchar(64) not null comment '场景id',
    node_name   varchar(255) null comment '主机名称',
    time        datetime null comment '时间',
    path        varchar(255) null comment '文件路径',
    event_desc  varchar(255) null comment '事件描述',
    level       tinyint(3) null comment '级别',
    file_size   varchar(255) null comment '文件大小',
    `group`     varchar(255) null comment 'group',
    md5_before  varchar(255) null comment '变更前md5值',
    md5_after   varchar(255) null comment '变更后md5值',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间'
) row_format = DYNAMIC;

alter table mp_scene_scene_config
    add cloud_type int default 0 not null comment '0:赛宁云;1:公有云;2:私有云;3:混合云';
alter table mp_scene_scene_config
    add scene_create_range_tag varchar(50) null comment '当前拓扑创建的靶场标识';
alter table mp_scene_scene_config
    add multi_scene_type int null comment '当前拓扑的多靶场类型0-普通靶场1-多靶场';

alter table mp_scene_scene_config_element modify outer_position2 varchar (1024) not null comment '外部2d坐标' after run_table_name;

alter table mp_scene_scene_element modify outer_position2 varchar (1024) not null comment '外部2d坐标' after run_table_name;

alter table mp_scene_scene_config_element_port
    add port_connect_net_cidr varchar(32) null comment '网段信息';
alter table mp_scene_scene_element_port
    add port_connect_net_cidr varchar(32) null comment '网段信息';

alter table mp_sceneplugincloud_scene_config_element
    alter column expansion set default 0;
alter table mp_sceneplugincloud_scene_config_element
    modify source_type int (1) null comment '源类型(1:云平台 2:用户上传)';
alter table mp_sceneplugincloud_scene_config_element
    modify is_public_ip tinyint(1) default 0 null comment '公共ip标记 0否 1是' after public_ip_address;
alter table mp_sceneplugincloud_scene_config_element
    add protocols varchar(64) null comment '接入协议' after is_public_ip;

alter table mp_sceneplugincloud_scene_element
    modify resource_id varchar (500) not null comment '云平台虚拟化资源id';
alter table mp_sceneplugincloud_scene_element
    alter column expansion set default 0;
alter table mp_sceneplugincloud_scene_element
    modify source_type int (1) null comment '源类型(1:云平台 2:用户上传)';
alter table mp_sceneplugincloud_scene_element
    modify is_public_ip tinyint(1) default 0 null comment '公共ip标记 0否 1是' after public_ip_address;
alter table mp_sceneplugincloud_scene_element
    add protocols varchar(64) null comment '接入协议' after is_public_ip;
alter table mp_sceneplugincloud_scene_element_network
    modify resource_id varchar (500) not null comment '虚拟网络资源id';

alter table mp_sceneplugincollection_scene_probe_router_port modify network_id varchar (255) not null comment '虚拟网络资源id';

alter table mp_sys_code_menu
    add code_other_name varchar(64) null comment '别名' after code_name;

create table mp_devops_boundary_network_equipment
(
    pk                  int auto_increment comment '主键' primary key,
    id                  varchar(64) null comment '服务ID',
    equipment_type      varchar(255) null comment '设备类型',
    equipment_type_name varchar(255) null comment '设备类型名称',
    other_name          varchar(255) null comment '别名',
    name                varchar(255) null comment '服务名称',
    ip_connect          varchar(255) null comment 'ip地址',
    port                varchar(255) null comment '端口号',
    protocol            varchar(255) null comment '接入协议',
    user_name           varchar(255) null comment '用户名',
    password            varchar(255) null comment '密码',
    logo                varchar(255) null comment '图标',
    create_time         datetime(6) null comment '创建时间',
    update_time         datetime(6) null comment '更新时间'
) comment '边界网络设备' collate = utf8mb4_general_ci;



