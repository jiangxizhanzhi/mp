DROP TABLE IF EXISTS `mp_devops_cloud_target`;
CREATE TABLE `mp_devops_cloud_target`  (
                                           `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                           `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                           `image_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '镜像类型(container、vm)',
                                           `cloud_service_type` tinyint(8) NULL DEFAULT NULL COMMENT '云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )',
                                           `operating_system` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                                           `operating_system_status` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统状态',
                                           `generation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代系',
                                           `version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
                                           `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                           `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                           PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 310 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_devops_cloud_target` VALUES (303, 'f9b2750e-ea2c-405a-955e-19e442d640a1', 'samples/cyberpeace/arkime', 'container', 3, 'linux', '专业化', '第一代', '0.0.1', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');
REPLACE INTO `mp_devops_cloud_target` VALUES (304, 'd34622c9-28eb-402b-ab02-a3f30498fbda', 'samples/hello-world', 'container', 3, 'linux', '专业化', '第一代', '0.0.1', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');
REPLACE INTO `mp_devops_cloud_target` VALUES (305, '1765811f-b8b2-4fc0-8b4f-11d544d090ac', 'samples/mp_elasticsearch', 'container', 3, 'linux', '专业化', '第一代', '0.0.1', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');
REPLACE INTO `mp_devops_cloud_target` VALUES (306, '0eae349d-1823-4773-bb71-22dd5c9c2bd3', 'samples/nginx', 'container', 3, 'linux', '专业化', '第一代', '0.0.1', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');
REPLACE INTO `mp_devops_cloud_target` VALUES (307, 'f96f306a-6316-47ae-8053-545b8d2352a6', 'netfoundryinc', 'vm', 2, 'Linux', '专业化', '第一代', '2.1.20230614', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');
REPLACE INTO `mp_devops_cloud_target` VALUES (308, 'cb187205-ba43-446a-810b-ac37781f3aae', 'enterpriseworx-it', 'vm', 2, 'Windows', '专业化', '第一代', '1.0.0', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');
REPLACE INTO `mp_devops_cloud_target` VALUES (309, 'a1c5aaa5-6a02-465f-ae46-1ac6d1526d8d', 'bluetalon', 'vm', 2, 'Linux', '专业化', '第一代', '1.0.0', '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000');

DROP TABLE IF EXISTS `mp_auth_cloud_user`;
CREATE TABLE `mp_auth_cloud_user`  (
                                       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `cloud_type` tinyint(8) NULL DEFAULT NULL COMMENT '云类型(1: 微软公有云 2:微软私有云 3:微软混合云)',
                                       `azure_tenant_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'tenant_id',
                                       `azure_subscription_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'subscription_id',
                                       `azure_client_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'client_id',
                                       `azure_client_secret` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'client_secret',
                                       `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                       `creator` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                       `version` tinyint(4) NULL DEFAULT 1 COMMENT '版本号',
                                       PRIMARY KEY (`pk`) USING BTREE,
                                       UNIQUE INDEX `tenant`(`cloud_type`, `azure_subscription_id`) USING BTREE,
                                       UNIQUE INDEX `tenant_2`(`cloud_type`, `azure_client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2070 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '微软云账号信息' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_auth_cloud_user` VALUES (1, 1, 'e1039762-f748-4410-a19d-eb16ba072acd', 'c87ba926-f7c2-4d53-8b8e-89245dc92e26', 'a8982e4d-099d-4ab0-b6c8-178f50d16fb0', '26V8Q~gGDhjyHIJLopU-.mws2agepHucAD~hqb7_', '2024-03-25 11:10:26', '123', 1);
REPLACE INTO `mp_auth_cloud_user` VALUES (2, 2, 'e1039762-f748-4410-a19d-eb16ba072acd', 'c87ba926-f7c2-4d53-8b8e-89245dc92e26', 'a8982e4d-099d-4ab0-b6c8-178f50d16fb0', '26V8Q~gGDhjyHIJLopU-.mws2agepHucAD~hqb7_', '2024-03-25 11:10:26', '123', 1);
REPLACE INTO `mp_auth_cloud_user` VALUES (3, 3, 'e1039762-f748-4410-a19d-eb16ba072acd', 'c87ba926-f7c2-4d53-8b8e-89245dc92e26', 'a8982e4d-099d-4ab0-b6c8-178f50d16fb0', '26V8Q~gGDhjyHIJLopU-.mws2agepHucAD~hqb7_', '2024-03-25 11:10:26', '123', 1);

DROP TABLE IF EXISTS `mp_sys_code_enum`;
CREATE TABLE `mp_sys_code_enum`  (
                                     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'code编码',
                                     `code_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
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
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统码表' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_sys_code_enum` VALUES (24, '1005', '状态码', NULL, NULL, NULL, NULL, 1, 1, NULL, '2024-04-11 11:22:51');
REPLACE INTO `mp_sys_code_enum` VALUES (25, '100501', '载荷执行结果页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-11 11:23:16');
REPLACE INTO `mp_sys_code_enum` VALUES (26, '10050101', '执行结果', NULL, NULL, 'result_status', '100501', 3, 1, NULL, '2024-04-11 11:24:05');
REPLACE INTO `mp_sys_code_enum` VALUES (27, '1005010101', '成功', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (28, '1005010102', '失败', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (29, '100502', '载荷页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-25 17:29:35');
REPLACE INTO `mp_sys_code_enum` VALUES (30, '10050201', '作用类型', '', NULL, 'action_type', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
REPLACE INTO `mp_sys_code_enum` VALUES (31, '1005020101', '逆向分析', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (32, '1005020102', '信息探测', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (33, '1005020103', '漏洞利用', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (34, '10050202', '运行平台', NULL, NULL, 'run_table', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
REPLACE INTO `mp_sys_code_enum` VALUES (35, '1005020201', 'docker', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (36, '1005020202', 'VMware', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (37, '1005020203', 'VM', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (38, '10050203', '控制类型', NULL, NULL, 'control_type', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
REPLACE INTO `mp_sys_code_enum` VALUES (39, '1005020301', 'shell', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (40, '1005020302', 'web', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (41, '1005020303', '其他', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
REPLACE INTO `mp_sys_code_enum` VALUES (80, '100503', '载荷同步设置code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-30 17:56:04');
REPLACE INTO `mp_sys_code_enum` VALUES (81, '10050301', '同步协议', NULL, NULL, 'synchronization_protocol', '100503', 3, 1, NULL, '2024-04-30 17:56:36');
REPLACE INTO `mp_sys_code_enum` VALUES (82, '1005030101', 'rest', NULL, NULL, NULL, '10050301', 4, 1, NULL, '2024-04-30 17:57:10');
REPLACE INTO `mp_sys_code_enum` VALUES (83, '100504', '云靶标镜像页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-05-06 10:28:07');
REPLACE INTO `mp_sys_code_enum` VALUES (84, '10050401', '操作系统', NULL, NULL, 'operating_system', '100504', 3, 1, NULL, '2024-05-06 10:28:39');
REPLACE INTO `mp_sys_code_enum` VALUES (85, '1005040101', 'windows', NULL, NULL, NULL, '10050401', 4, 1, NULL, '2024-05-06 10:29:17');
REPLACE INTO `mp_sys_code_enum` VALUES (86, '1005040102', 'linux', NULL, NULL, NULL, '10050401', 4, 1, NULL, '2024-05-06 10:31:25');
REPLACE INTO `mp_sys_code_enum` VALUES (87, '10050102', '结果类型', NULL, NULL, 'result_type', '100501', 3, 1, NULL, '2024-05-06 15:32:58');
REPLACE INTO `mp_sys_code_enum` VALUES (88, '1005010201', '自动上报', NULL, NULL, NULL, '10050102', 4, 1, NULL, '2024-05-06 15:33:47');
REPLACE INTO `mp_sys_code_enum` VALUES (89, '1005010202', '人工填报', NULL, NULL, NULL, '10050102', 4, 1, NULL, '2024-05-06 15:33:47');
REPLACE INTO `mp_sys_code_enum` VALUES (90, '100504010101', 'WindowsXP', NULL, NULL, NULL, '1005040101', 5, 1, NULL, '2024-05-08 09:57:59');
REPLACE INTO `mp_sys_code_enum` VALUES (91, '100504010102', 'Windows8', NULL, NULL, NULL, '1005040101', 5, 1, NULL, '2024-05-08 09:57:59');
REPLACE INTO `mp_sys_code_enum` VALUES (92, '10050402', '操作系统状态', NULL, NULL, 'operating_system_status', '100504', 3, 1, NULL, '2024-05-08 10:38:54');
REPLACE INTO `mp_sys_code_enum` VALUES (93, '1005040201', '通用化', NULL, NULL, NULL, '10050402', 4, 1, NULL, '2024-05-08 10:39:51');
REPLACE INTO `mp_sys_code_enum` VALUES (94, '1005040202', '专业化', NULL, NULL, NULL, '10050402', 4, 1, NULL, '2024-05-08 10:39:51');
REPLACE INTO `mp_sys_code_enum` VALUES (99, '1005010103', '暂无结果', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-05-20 08:53:15');

DROP TABLE IF EXISTS `mp_devops_load`;
CREATE TABLE `mp_devops_load`  (
                                   `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ID',
                                   `tool_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具名称',
                                   `image_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '镜像名称',
                                   `tool_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具描述',
                                   `tool_platform` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具平台',
                                   `action_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作用类型',
                                   `run_table` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '运行平台',
                                   `control_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '控制类型',
                                   `control_port` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '控制端口',
                                   `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源(1载荷平台 2本地)',
                                   `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面存储路径',
                                   `cover_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面名称',
                                   `attachment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件存储路径',
                                   `attachment_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件名称',
                                   `read_me` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帮助文档存储路径',
                                   `read_me_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帮助文档名称',
                                   `default_cmd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '默认命令',
                                   `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                   `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                   `synchronization_time` datetime(0) NULL DEFAULT NULL COMMENT '同步时间',
                                   `creator_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
                                   PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 287 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷同步表' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_devops_load` VALUES (283, '35', 'testvmware', NULL, 'testvmwarefsajkfsajflasj', 'vmware', '1005020102', '1005020202', '1005020302', '80', '1', '/api/mp/file/370/763/592a8536d5f4eeb0c0f0ff72c5403809.JPG', '场景页面.JPG', '/api/mp/file/762/673/98f8a59b6e7cd07e3597b50c1d8cb9c4.zip', 'ct-web-5.4.1-build0003.zip', '/api/mp/file/878/044/ba02665a50a3a77a2a4bdea39c4beef6.pdf', '系统虚拟化：原理与实现.pdf', '', '2024-05-17 11:42:01', '2023-11-29 10:58:45', '2024-05-17 11:42:01', '0');
REPLACE INTO `mp_devops_load` VALUES (284, '28a47231-141f-11ef-b1e4-0242ac1e0018', '11231231231212', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-17 15:29:13', '2024-05-17 15:29:13', NULL, NULL);
REPLACE INTO `mp_devops_load` VALUES (285, 'f2c2cca6-1432-11ef-b1e4-0242ac1e0018', '1123123123adadw1212', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-05-17 17:53:31', '2024-05-17 17:53:31', NULL, NULL);
REPLACE INTO `mp_devops_load` VALUES (286, '31c03939-164a-11ef-b1e4-0242ac1e0018', '测试用载荷', NULL, '测试下描述', '工具平台', '控制类型', '运行平台', '控制类型', '控制端口', '2', '***.jpg', '', '***.zip', '', '***.txt', '', 'cmd', '2024-05-20 09:44:58', '2024-05-20 09:44:58', NULL, NULL);

DROP TABLE IF EXISTS `mp_devops_load_scene_result_log`;
CREATE TABLE `mp_devops_load_scene_result_log`  (
                                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具id',
                                                    `instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工具实例id',
                                                    `scene_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景id',
                                                    `stage_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阶段id',
                                                    `task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id',
                                                    `result_log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回日志',
                                                    `result_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果状态',
                                                    `result_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果类型',
                                                    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                                    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                                    `creator_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
                                                    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷执行结果表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_load_source`;
CREATE TABLE `mp_devops_load_source`  (
                                          `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '载荷主机',
                                          `synchronization_protocol` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '同步协议',
                                          `authentication_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '认证方式(1:密码认证 2:cookie)',
                                          `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                                          `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                                          `cookie` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cookie',
                                          `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                          `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                          `creator_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
                                          PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷同步设置表' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_devops_load_source` VALUES (1, 'http://10.108.1.10:5002', 'synchronizationProtocol', '1', 'userName', '1', '123', '2024-05-17 15:04:17', '2024-04-24 10:29:33', '1');
REPLACE INTO `mp_devops_load_source` VALUES (21, 'http://10.108.1.10:5002', '0', '2', '', NULL, '1111', NULL, '2024-04-30 17:50:09', '');
REPLACE INTO `mp_devops_load_source` VALUES (22, 'http://10.108.1.10:5002', '0', '1', '111', NULL, '', NULL, '2024-04-30 17:50:52', NULL);
REPLACE INTO `mp_devops_load_source` VALUES (23, 'http://10.108.1.10:5002', '0', '1', '111', NULL, '', NULL, '2024-04-30 17:51:17', NULL);
REPLACE INTO `mp_devops_load_source` VALUES (24, 'http://10.108.1.10:5002', '1005030101', '1', '11', NULL, '', NULL, '2024-04-30 18:04:09', NULL);
REPLACE INTO `mp_devops_load_source` VALUES (25, 'http://10.108.1.10:5002', '1005030101', '2', '', NULL, '12321312', NULL, '2024-05-08 09:16:27', NULL);
REPLACE INTO `mp_devops_load_source` VALUES (26, 'http://10.108.1.10:5002', '1005030101', '1', '1', NULL, '', NULL, '2024-05-09 10:41:32', NULL);
REPLACE INTO `mp_devops_load_source` VALUES (27, 'http://10.108.1.10:5002', 'synchronizationProtocol', '1', 'userName1', 'password1', NULL, '2024-05-20 09:52:49', '2024-05-17 15:06:09', NULL);

DROP TABLE IF EXISTS `mp_sys_url_config`;
CREATE TABLE `mp_sys_url_config`  (
                                      `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'key',
                                      `service_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务地址',
                                      `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`pk`) USING BTREE,
                                      UNIQUE INDEX `key`(`code`) USING BTREE COMMENT 'key唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置信息表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_calculate`;
CREATE TABLE `mp_devops_cloud_target_calculate`  (
                                                     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                     `link_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                                     `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                                     `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                                     `publisher` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'publisher',
                                                     `offer` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'offer',
                                                     `sku` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku',
                                                     `sub_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子类型（虚拟机，vm应用程序定义）',
                                                     `calculate_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计算类型（laas，paas）',
                                                     `operating_system` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                                                     `operating_system_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统状态',
                                                     `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                                     `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                                     `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                                     `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                     `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                                     `version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
                                                     `generation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代系',
                                                     PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 230 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-计算表' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_devops_cloud_target_calculate` VALUES (227, 'f96f306a-6316-47ae-8053-545b8d2352a6', 'netfoundryinc', 1, 'netfoundryinc', 'ziti-edge-router', 'ziti-edge-router', 'vm', 'paas', 'Linux', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '2.1.20230614', '第一代');
REPLACE INTO `mp_devops_cloud_target_calculate` VALUES (228, 'cb187205-ba43-446a-810b-ac37781f3aae', 'enterpriseworx-it', 1, 'enterpriseworx-it', 'c4sql_central_v2', 'c4sql-v2', 'vm', 'paas', 'Windows', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '1.0.0', '第一代');
REPLACE INTO `mp_devops_cloud_target_calculate` VALUES (229, 'a1c5aaa5-6a02-465f-ae46-1ac6d1526d8d', 'bluetalon', 1, 'bluetalon', 'bluetalon', 'bluetalon', 'vm', 'paas', 'Linux', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '1.0.0', '第一代');

DROP TABLE IF EXISTS `mp_devops_cloud_target_analysis`;
CREATE TABLE `mp_devops_cloud_target_analysis`  (
                                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                                    `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                                    `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                                    `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                                    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                                    `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                                    `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                                    `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                    `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                    `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                                    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-分析表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_container`;
CREATE TABLE `mp_devops_cloud_target_container`  (
                                                     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                     `link_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联id',
                                                     `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                                     `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                                     `container_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '容器类型(1:容器基础结构 2:容器管理 3:容器化应用程序)',
                                                     `sub_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子类型(1:容器实例 2:容器注册表 3:Kubernetes 服务)',
                                                     `upload_target_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户上传镜像名称',
                                                     `operating_system` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
                                                     `operating_system_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统状态',
                                                     `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                                     `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                                     `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                                     `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                     `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                                     `version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
                                                     `generation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代系',
                                                     PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 155 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-容器表' ROW_FORMAT = Dynamic;

REPLACE INTO `mp_devops_cloud_target_container` VALUES (151, 'f9b2750e-ea2c-405a-955e-19e442d640a1', 'samples/cyberpeace/arkime', 2, NULL, NULL, 'samples/cyberpeace/arkime', 'linux', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '0.0.1', '第一代');
REPLACE INTO `mp_devops_cloud_target_container` VALUES (152, 'd34622c9-28eb-402b-ab02-a3f30498fbda', 'samples/hello-world', 2, NULL, NULL, 'samples/hello-world', 'linux', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '0.0.1', '第一代');
REPLACE INTO `mp_devops_cloud_target_container` VALUES (153, '1765811f-b8b2-4fc0-8b4f-11d544d090ac', 'samples/mp_elasticsearch', 2, NULL, NULL, 'samples/mp_elasticsearch', 'linux', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '0.0.1', '第一代');
REPLACE INTO `mp_devops_cloud_target_container` VALUES (154, '0eae349d-1823-4773-bb71-22dd5c9c2bd3', 'samples/nginx', 2, NULL, NULL, 'samples/nginx', 'linux', '专业化', 2, 1, 0, 0, '2024-05-16 09:56:59.000000', '2024-05-16 09:56:59.000000', '/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png', '0.0.1', '第一代');

DROP TABLE IF EXISTS `mp_devops_cloud_target_storage`;
CREATE TABLE `mp_devops_cloud_target_storage`  (
                                                   `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                   `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                                   `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                                   `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                                   `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                                   `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                                   `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                                   `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                                   `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                   `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                   `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                                   PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-存储表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_database`;
CREATE TABLE `mp_devops_cloud_target_database`  (
                                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                                    `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                                    `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                                    `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                                    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                                    `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                                    `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                                    `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                    `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                    `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                                    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-数据库表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_net`;
CREATE TABLE `mp_devops_cloud_target_net`  (
                                               `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `link_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                               `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                               `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                               `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                               `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                               `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                               `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                               `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                               `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                               PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-网络表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_devops_cloud_target_security`;
CREATE TABLE `mp_devops_cloud_target_security`  (
                                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务ID',
                                                    `name` varchar(164) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名称',
                                                    `source_type` tinyint(8) NULL DEFAULT NULL COMMENT '源类型(1:云平台 2:用户上传)',
                                                    `is_public` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                                    `quick_create_support` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持快速构建，0：不支持，1：支持',
                                                    `is_import` tinyint(1) NULL DEFAULT 0 COMMENT '是否导入，0：false,1:true',
                                                    `is_hide` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏，0：false,1:true',
                                                    `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                    `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                    `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
                                                    PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '云靶标信息表-安全性表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mp_sys_code_menu`;
CREATE TABLE `mp_sys_code_menu`  (
                                     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'code编码',
                                     `code_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
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

REPLACE INTO `mp_sys_code_menu` VALUES (1, '1002', '混合云配置项菜单', '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
REPLACE INTO `mp_sys_code_menu` VALUES (2, '100201', '混合云', '01', '', '', '1002', 2, 1, '', '2024-03-28 14:00:09');
REPLACE INTO `mp_sys_code_menu` VALUES (3, '100202', '载荷', '01', '', '', '1002', 2, 1, '', '2024-03-28 15:20:26');
REPLACE INTO `mp_sys_code_menu` VALUES (4, '100203', '边界网络设备', '01', '', '', '1002', 2, 1, '', '2024-03-28 15:20:24');
REPLACE INTO `mp_sys_code_menu` VALUES (5, '10020101', '网络', '01', 'target.cloud_service_type = \'1\'', '1', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (6, '10020102', '计算', '01', 'target.cloud_service_type = \'2\'', '2', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (7, '10020103', '容器', '01', 'target.cloud_service_type = \'3\'', '3', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (8, '10020104', '数据库', '', 'target.cloud_service_type = \'4\'', '4', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (9, '10020105', '分析', '01', 'target.cloud_service_type = \'5\'', '5', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (10, '10020106', '安全性', '01', 'target.cloud_service_type = \'6\'', '6', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (11, '10020107', '存储', '01', 'target.cloud_service_type = \'7\'', '7', '100201', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (12, '1002010201', '关键字搜索', '02', 'name like \'**\'', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (13, '1002010202', '类型', '01', '', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (14, '1002010203', '操作系统', '01', '', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (15, '100201020201', '全部', '', '', '', '1002010202', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (16, '100201020202', 'paas', '', 'calculate_type = \'paas\'', '', '1002010202', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (17, '100201020301', '全部', '', '', '', '1002010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (18, '100201020302', 'windows', '', 'operating_system = \'windows\'', '', '1002010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (19, '100201020303', 'linux', '', 'operating_system = \'linux\'', '', '1002010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (20, '1002010204', '子类型', '01', '', '', '10020102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (21, '100201020401', '虚拟机', '', 'sub_type = \'vm\'', '', '1002010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (22, '100201020402', 'VM应用程序定义', '', 'sub_type = \'vmApplicationDefinition\'', '', '1002010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (23, '100201020403', '应用程序服务', '', 'sub_type = \'applicationService\'', '', '1002010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (24, '1003', '私有云云配置项菜单', '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
REPLACE INTO `mp_sys_code_menu` VALUES (25, '100301', '私有云', '01', '', '', '1003', 2, 1, '', '2024-03-28 14:00:09');
REPLACE INTO `mp_sys_code_menu` VALUES (26, '100302', '载荷', '01', '', '', '1003', 2, 1, '', '2024-03-28 15:20:26');
REPLACE INTO `mp_sys_code_menu` VALUES (27, '100303', '边界网络设备', '01', '', '', '1003', 2, 1, '', '2024-03-28 15:20:24');
REPLACE INTO `mp_sys_code_menu` VALUES (28, '10030101', '网络', '01', 'target.cloud_service_type = \'1\'', '1', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (29, '10030102', '计算', '01', 'target.cloud_service_type = \'2\'', '2', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (30, '10030103', '容器', '01', 'target.cloud_service_type = \'3\'', '3', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (31, '10030104', '数据库', '', 'target.cloud_service_type = \'4\'', '4', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (32, '10030105', '分析', '01', 'target.cloud_service_type = \'5\'', '5', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (33, '10030106', '安全性', '01', 'target.cloud_service_type = \'6\'', '6', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (34, '10030107', '存储', '01', 'target.cloud_service_type = \'7\'', '7', '100301', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (35, '1003010201', '关键字搜索', '02', 'name like \'**\'', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (36, '1003010202', '类型', '01', '', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (37, '1003010203', '操作系统', '01', '', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (38, '100301020201', '全部', '', '', '', '1003010202', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (39, '100301020202', 'paas', '', 'calculate_type = \'paas\'', '', '1003010202', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (40, '100301020301', '全部', '', '', '', '1003010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (41, '100301020302', 'windows', '', 'operating_system = \'windows\'', '', '1003010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (42, '100301020303', 'linux', '', 'operating_system = \'linux\'', '', '1003010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (43, '1003010204', '子类型', '01', '', '', '10030102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (44, '100301020401', '虚拟机', '', 'sub_type = \'vm\'', '', '1003010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (45, '100301020402', 'VM应用程序定义', '', 'sub_type = \'vmApplicationDefinition\'', '', '1003010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (46, '100301020403', '应用程序服务', '', 'sub_type = \'applicationService\'', '', '1003010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (47, '1004', '公有云配置项菜单', '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
REPLACE INTO `mp_sys_code_menu` VALUES (48, '100401', '公有云', '01', '', '', '1004', 2, 1, '', '2024-03-28 14:00:09');
REPLACE INTO `mp_sys_code_menu` VALUES (49, '100402', '载荷', '01', '', '', '1004', 2, 1, '', '2024-03-28 15:20:26');
REPLACE INTO `mp_sys_code_menu` VALUES (50, '100403', '边界网络设备', '01', '', '', '1004', 2, 1, '', '2024-03-28 15:20:24');
REPLACE INTO `mp_sys_code_menu` VALUES (51, '10040101', '网络', '01', 'target.cloud_service_type = \'1\'', '1', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (52, '10040102', '计算', '01', 'target.cloud_service_type = \'2\'', '2', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (53, '10040103', '容器', '01', 'target.cloud_service_type = \'3\'', '3', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (54, '10040104', '数据库', '', 'target.cloud_service_type = \'4\'', '4', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (55, '10040105', '分析', '01', 'target.cloud_service_type = \'5\'', '5', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (56, '10040106', '安全性', '01', 'target.cloud_service_type = \'6\'', '6', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (57, '10040107', '存储', '01', 'target.cloud_service_type = \'7\'', '7', '100401', 3, 1, '', '2024-03-28 15:20:58');
REPLACE INTO `mp_sys_code_menu` VALUES (58, '1004010201', '关键字搜索', '02', 'name like \'**\'', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (59, '1004010202', '类型', '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (60, '1004010203', '操作系统', '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (61, '100401020201', '全部', '', '', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (62, '100401020202', 'paas', '', 'calculate_type = \'paas\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (63, '100401020301', '全部', '', '', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (64, '100401020302', 'windows', '', 'operating_system = \'windows\'', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (65, '100401020303', 'linux', '', 'operating_system = \'linux\'', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (66, '1004010204', '子类型', '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
REPLACE INTO `mp_sys_code_menu` VALUES (67, '100401020401', '虚拟机', '', 'sub_type = \'vm\'', '', '1004010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (68, '100401020402', 'VM应用程序定义', '', 'sub_type = \'vmApplicationDefinition\'', '', '1004010204', 5, 1, '', '2024-03-29 10:00:37');
REPLACE INTO `mp_sys_code_menu` VALUES (69, '100401020403', '应用程序服务', '', 'sub_type = \'applicationService\'', '', '1004010204', 5, 1, '', '2024-03-29 10:00:37');

REPLACE INTO `mp`.`mp_auth_url_ac`(`pk`, `id`, `url`, `is_pattern`, `group`, `methods`, `desc`, `create_time`, `update_time`) VALUES (706, '33b99e69569711ed91a0000ec67b0395', '/api/mp/load/load/addLoadExecutionOutResult', 0, 'out-load-service', '*', '载荷平台数据入库', '2024-05-20 11:34:00', '2024-05-20 11:34:02');

REPLACE INTO `mp`.`mp_auth_url_ac_target`(`pk`, `id`, `group`, `target`, `create_time`, `update_time`) VALUES (1180, '33b99e69569711ed91a0000ec67b0395', 'out-load-service', 'no_auth', '2024-05-20 11:31:56', '2024-05-20 11:31:56');
