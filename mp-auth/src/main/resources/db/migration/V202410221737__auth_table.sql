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
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统码表' ROW_FORMAT = Dynamic;

INSERT INTO `mp_sys_code_enum` VALUES (1, '1005', '状态码', NULL, NULL, NULL, NULL, 1, 1, NULL, '2024-04-11 11:22:51');
INSERT INTO `mp_sys_code_enum` VALUES (2, '100501', '载荷执行结果页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum` VALUES (3, '10050101', '执行结果', NULL, NULL, 'result_status', '100501', 3, 1, NULL, '2024-04-11 11:24:05');
INSERT INTO `mp_sys_code_enum` VALUES (4, '1005010101', '成功', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (5, '1005010102', '失败', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (6, '1005010103', '暂无结果', NULL, NULL, NULL, '10050101', 4, 1, NULL, '2024-05-20 08:53:15');
INSERT INTO `mp_sys_code_enum` VALUES (7, '10050102', '结果类型', NULL, NULL, 'result_type', '100501', 3, 1, NULL, '2024-05-06 15:32:58');
INSERT INTO `mp_sys_code_enum` VALUES (8, '1005010201', '自动上报', NULL, NULL, NULL, '10050102', 4, 1, NULL, '2024-05-06 15:33:47');
INSERT INTO `mp_sys_code_enum` VALUES (9, '1005010202', '人工填报', NULL, NULL, NULL, '10050102', 4, 1, NULL, '2024-05-06 15:33:47');
INSERT INTO `mp_sys_code_enum` VALUES (10, '100502', '载荷页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-25 17:29:35');
INSERT INTO `mp_sys_code_enum` VALUES (11, '10050201', '作用类型', '', NULL, 'action_type', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum` VALUES (12, '1005020101', '逆向分析', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (13, '1005020102', '信息探测', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (14, '1005020103', '漏洞自动化利用', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (15, '10050202', '运行平台', NULL, NULL, 'run_table', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum` VALUES (16, '1005020201', 'docker', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (17, '1005020202', 'VMware', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (18, '1005020203', 'VM', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (19, '1005020204', '其他', NULL, NULL, NULL, '10050202', 4, 1, NULL, '2100-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum` VALUES (20, '10050203', '控制类型', NULL, NULL, 'control_type', '100502', 3, 1, NULL, '2024-04-11 11:23:16');
INSERT INTO `mp_sys_code_enum` VALUES (21, '1005020301', 'shell', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (22, '1005020302', 'web', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (23, '1005020303', '其他', NULL, NULL, NULL, '10050203', 4, 1, NULL, '2100-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (24, '100503', '载荷同步设置code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-04-30 17:56:04');
INSERT INTO `mp_sys_code_enum` VALUES (25, '10050301', '同步协议', NULL, NULL, 'synchronization_protocol', '100503', 3, 1, NULL, '2024-04-30 17:56:36');
INSERT INTO `mp_sys_code_enum` VALUES (26, '1005030101', 'rest', NULL, NULL, NULL, '10050301', 4, 1, NULL, '2024-04-30 17:57:10');
INSERT INTO `mp_sys_code_enum` VALUES (27, '100504', '云靶标镜像页面code码', NULL, NULL, NULL, '1005', 2, 1, NULL, '2024-05-06 10:28:07');
INSERT INTO `mp_sys_code_enum` VALUES (28, '10050401', '操作系统', NULL, NULL, 'operating_system', '100504', 3, 1, NULL, '2024-05-06 10:28:39');
INSERT INTO `mp_sys_code_enum` VALUES (29, '1005040101', 'Windows', NULL, NULL, NULL, '10050401', 4, 1, NULL, '2024-05-06 10:29:17');
INSERT INTO `mp_sys_code_enum` VALUES (30, '1005040102', 'Linux', NULL, NULL, NULL, '10050401', 4, 1, NULL, '2024-05-06 10:31:25');
INSERT INTO `mp_sys_code_enum` VALUES (31, '10050402', '操作系统状态', NULL, NULL, 'operating_system_status', '100504', 3, 1, NULL, '2024-05-08 10:38:54');
INSERT INTO `mp_sys_code_enum` VALUES (32, '1005040201', '通用化', NULL, NULL, NULL, '10050402', 4, 1, NULL, '2024-05-08 10:39:51');
INSERT INTO `mp_sys_code_enum` VALUES (33, '1005040202', '专业化', NULL, NULL, NULL, '10050402', 4, 1, NULL, '2024-05-08 10:39:51');
INSERT INTO `mp_sys_code_enum` VALUES (34, '1005020104', '其他', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2100-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (101, '1005020105', '资产扫描', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (102, '1005020106', '资产发现', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (103, '1005020107', '弱口令扫描', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');
INSERT INTO `mp_sys_code_enum` VALUES (104, '1005020108', '漏洞扫描', NULL, NULL, NULL, '10050201', 4, 1, NULL, '2024-04-11 11:24:40');

DROP TABLE IF EXISTS `mp_sys_code_menu`;
CREATE TABLE `mp_sys_code_menu`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 290 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

INSERT INTO `mp_sys_code_menu` VALUES (1, '1004', '公有云配置项菜单', '', '01', '', '', '', 1, 1, '', '2024-03-28 13:59:49');
INSERT INTO `mp_sys_code_menu` VALUES (2, '100401', '公有云', '', '01', '', '', '1004', 2, 1, '', '2024-03-28 14:00:09');
INSERT INTO `mp_sys_code_menu` VALUES (3, '10040101', '网络', '', '01', 'target.cloud_service_type = \'1\'', '1', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (4, '1004010101', '关键字搜索', '', '02', 'target.name like \'%**%\'', '', '10040101', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (5, '1004010102', '类型', '', '01', '', '', '10040101', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (6, '100401010201', '全部', '', '01', '', '', '1004010102', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (7, '10040101020101', '虚拟网络', 'VNet', '', 'sub_type = \'VNet\'', '', '100401010201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (8, '10040101020102', '防火墙', 'fireWall', '', 'sub_type = \'fireWall\'', '', '100401010201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (9, '10040101020103', 'WEB应用程序防火墙策略', 'WAF', '', 'sub_type = \'WAF\'', '', '100401010201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (10, '100401010202', '网络基础', 'basicNetwork', '01', 'net_type = \'basicNetwork\'', '', '1004010102', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (11, '10040101020201', '虚拟网络', 'VNet', '', 'sub_type = \'VNet\'', '', '100401010202', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (12, '100401010203', '网络安全', 'networkSecurity', '01', 'net_type = \'networkSecurity\'', '', '1004010102', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (13, '10040101020301', '防火墙', 'fireWall', '', 'sub_type = \'fireWall\'', '', '100401010203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (14, '10040101020302', 'WEB应用程序防火墙策略', 'WAF', '', 'sub_type = \'WAF\'', '', '100401010203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (15, '10040102', '计算', '', '01', 'target.cloud_service_type = \'2\'', '2', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (16, '1004010201', '关键字搜索', '', '02', 'target.name like \'%**%\'', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (17, '1004010202', '类型', '', '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (18, '100401020201', '全部', '', '01', '', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (19, '10040102020102', '虚拟机', 'vm', '', 'sub_type = \'vm\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (20, '10040102020103', 'VM应用程序定义', 'vmApplicationDefinition', '', 'sub_type = \'vmApplicationDefinition\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (21, '10040102020104', '应用程序服务', 'applicationService', '', 'sub_type = \'applicationService\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (22, '10040102020105', 'VM映像版本', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (23, '10040102020106', '云服务(外延支持)', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (24, '10040102020107', 'SAP解决方案虚拟实例', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (25, '10040102020108', 'Azure Spring Apps', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (26, '10040102020109', 'Azure VMware 解决方案', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (27, '10040102020110', '函数应用', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (28, '10040102020111', '容器应用', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (29, '10040102020112', '容器应用环境', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (30, '10040102020113', 'Kubernetes 服务', '', '', 'sub_type = \'\'', '', '100401020201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (31, '100401020202', 'laas', 'laas', '01', 'calculate_type = \'laas\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (32, '10040102020201', '虚拟机', '', '', 'sub_type = \'\'', '', '100401020202', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (33, '10040102020202', '应用程序服务', '', '', 'sub_type = \'\'', '', '100401020202', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (34, '10040102020203', 'VM映像版本', '', '', 'sub_type = \'\'', '', '100401020202', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (35, '100401020203', 'paas', 'paas', '01', 'calculate_type = \'paas\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (36, '10040102020301', 'VM应用程序定义', '', '', 'sub_type = \'\'', '', '100401020203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (37, '10040102020302', '云服务(外延支持)', '', '', 'sub_type = \'\'', '', '100401020203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (38, '10040102020303', 'SAP解决方案虚拟实例', '', '', 'sub_type = \'\'', '', '100401020203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (39, '10040102020304', 'Azure Spring Apps', '', '', 'sub_type = \'\'', '', '100401020203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (40, '10040102020305', 'Azure VMware 解决方案', '', '', 'sub_type = \'\'', '', '100401020203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (41, '100401020204', '微服务和无服务器', 'microservice', '01', 'calculate_type = \'microservice\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (42, '10040102020401', '函数应用', '', '', 'sub_type = \'\'', '', '100401020204', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (43, '10040102020402', '容器应用', '', '', 'sub_type = \'\'', '', '100401020204', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (44, '10040102020403', '容器应用环境', '', '', 'sub_type = \'\'', '', '100401020204', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (45, '10040102020404', 'Kubernetes 服务', '', '', 'sub_type = \'\'', '', '100401020204', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (46, '100401020205', '高性能计算', 'HPComputing', '01', 'calculate_type = \'HPComputing\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (47, '100401020206', '公有云', 'mixCloud', '01', 'calculate_type = \'mixCloud\'', '', '1004010202', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (48, '1004010203', '操作系统', '', '01', '', '', '10040102', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (49, '100401020301', '全部', '', '', '', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (50, '100401020302', 'windows', 'windows', '', 'operating_system = \'windows\'', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (51, '100401020303', 'linux', 'linux', '', 'operating_system = \'linux\'', '', '1004010203', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (52, '10040103', '容器', '', '01', 'target.cloud_service_type = \'3\'', '3', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (53, '1004010301', '关键字搜索', '', '02', 'target.name like \'%**%\'', '', '10040103', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (54, '1004010302', '类型', '', '01', '', '', '10040103', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (55, '100401030201', '全部', '', '01', '', '', '1004010302', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (56, '10040103020101', '全部', '', '', '', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (57, '10040103020102', '容器实例', 'containerInstance', '', 'sub_type = \'containerInstance\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (58, '10040103020103', '容器注册表', 'containerRegistry', '', 'sub_type = \'containerRegistry\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (59, '10040103020104', 'Kubernetes 服务', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (60, '10040103020105', 'Kubernetes 舰队管理器', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (61, '10040103020106', 'Azure Red Hat OpenShift 群集', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (62, '10040103020107', 'Service Fabric 群集', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (63, '10040103020108', 'Service Fabric 托管群集', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (64, '10040103020109', '应用程序配置', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (65, '10040103020110', '容器应用', '', '', 'sub_type = \'\'', '', '100401030201', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (66, '100401030202', '容器基础结构', 'containerInfrastructure', '01', 'container_type = \'containerInfrastructure\'', '', '1004010302', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (67, '10040103020201', '容器实例', 'containerRegistry', '', 'sub_type = \'containerRegistry\'', '', '100401030202', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (68, '10040103020202', '容器注册表', '', '', 'sub_type = \'\'', '', '100401030202', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (69, '100401030203', '容器管理', 'containerManagement', '01', 'container_type = \'containerManagement\'', '', '1004010302', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (70, '10040103020301', 'Kubernetes 服务', '', '', 'sub_type = \'\'', '', '100401030203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (71, '10040103020302', 'Kubernetes 舰队管理器', '', '', 'sub_type = \'\'', '', '100401030203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (72, '10040103020303', 'Azure Red Hat OpenShift 群集', '', '', 'sub_type = \'\'', '', '100401030203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (73, '10040103020304', 'Service Fabric 群集', '', '', 'sub_type = \'\'', '', '100401030203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (74, '10040103020305', 'Service Fabric 托管群集', '', '', 'sub_type = \'\'', '', '100401030203', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (75, '100401030204', '容器化应用程序', 'containerizedApplication', '01', 'container_type = \'containerizedApplication\'', '', '1004010302', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (76, '10040103020401', '应用程序配置', '', '', 'sub_type = \'\'', '', '100401030204', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (77, '10040103020402', '容器应用', '', '', 'sub_type = \'\'', '', '100401030204', 6, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (78, '1004010303', '操作系统', '', '01', '', '', '10040103', 4, 1, '', '2024-03-29 09:45:43');
INSERT INTO `mp_sys_code_menu` VALUES (79, '100401030301', '全部', '', '', '', '', '1004010303', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (80, '100401030302', 'windows', 'windows', '', 'operating_system = \'windows\'', '', '1004010303', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (81, '100401030303', 'linux', 'linux', '', 'operating_system = \'linux\'', '', '1004010303', 5, 1, '', '2024-03-29 10:00:37');
INSERT INTO `mp_sys_code_menu` VALUES (82, '10040104', '数据库', '', '01', 'target.cloud_service_type = \'4\'', '4', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (83, '10040105', '分析', '', '01', 'target.cloud_service_type = \'5\'', '5', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (84, '10040106', '安全性', '', '01', 'target.cloud_service_type = \'6\'', '6', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (85, '10040107', '存储', '', '01', 'target.cloud_service_type = \'7\'', '7', '100401', 3, 1, '', '2024-03-28 15:20:58');
INSERT INTO `mp_sys_code_menu` VALUES (86, '100402', '载荷', '', '01', '', '', '1004', 2, 1, '', '2024-03-28 15:20:26');
INSERT INTO `mp_sys_code_menu` VALUES (87, '100403', '边界网络设备', '', '01', '', '', '1004', 2, 1, '', '2024-03-28 15:20:24');
INSERT INTO `mp_sys_code_menu` VALUES (283, '10040301', '设备类型', NULL, '01', NULL, NULL, '100403', 3, 1, NULL, '2024-07-05 15:59:20');
INSERT INTO `mp_sys_code_menu` VALUES (284, '1004030101', '蜜罐', 'honeyPot', '', '', '', '10040301', 4, 1, '', '2024-07-05 15:59:20');
INSERT INTO `mp_sys_code_menu` VALUES (285, '1004030102', '交换机', 'exchange', '', '', '', '10040301', 4, 1, '', '2024-07-05 15:59:20');
INSERT INTO `mp_sys_code_menu` VALUES (286, '1004030103', '路由器', 'router', '', '', '', '10040301', 4, 1, '', '2024-07-05 15:59:20');
INSERT INTO `mp_sys_code_menu` VALUES (287, '1004030104', 'IDS', 'IDS', '', '', '', '10040301', 4, 1, '', '2024-07-05 15:59:20');
INSERT INTO `mp_sys_code_menu` VALUES (288, '1004030105', 'IPS', 'IPS', '', '', '', '10040301', 4, 1, '', '2024-07-05 15:59:20');
INSERT INTO `mp_sys_code_menu` VALUES (289, '1004030106', '防火墙', 'fireWall', '', '', '', '10040301', 4, 1, '', '2024-07-05 15:59:20');

DROP TABLE IF EXISTS `mp_devops_load_scene_upload_log_file`;
CREATE TABLE `mp_devops_load_scene_upload_log_file`  (
                                                         `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                         `scene_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景id',
                                                         `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
                                                         `file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
                                                         `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                                         `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                                         `creator_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人Id',
                                                         PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '载荷执行结果文件上传表' ROW_FORMAT = Dynamic;

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

INSERT INTO `mp_sys_url_config` VALUES (1, 'getBindLoopholes', 'http://10.108.1.10:80/api/vuln/admin/vuln/load_code', '2024-07-04 14:58:35', '2024-07-04 14:58:37');
INSERT INTO `mp_sys_url_config` VALUES (2, 'getStagesBySceneId', 'http://10.108.1.10:80/api/vuln/admin/vuln/load_code', '2024-07-04 14:58:35', '2024-07-04 14:58:37');
INSERT INTO `mp_sys_url_config` VALUES (3, 'addUser', 'http://ip:port/v1/gxpt/addUser', '2024-07-25 11:51:02', '2024-07-25 11:51:05');
INSERT INTO `mp_sys_url_config` VALUES (4, 'deteleUser', 'http://ip:9088/xinan-server/mhUser/deteleUser', '2024-07-25 14:26:31', '2024-07-25 14:26:33');
INSERT INTO `mp_sys_url_config` VALUES (5, 'addTarget', 'http://ip:port/ testApi/zySb/insert', '2024-07-25 14:37:50', '2024-07-25 14:37:52');
INSERT INTO `mp_sys_url_config` VALUES (6, 'deleteTarget', 'http://ip:port/ /testApi/zySb/delete', '2024-07-25 15:22:04', '2024-07-25 15:22:07');
INSERT INTO `mp_sys_url_config` VALUES (7, 'loadTransformCode', 'http://ip:port', '2024-10-09 09:48:10', '2024-10-09 09:48:13');

ALTER TABLE mp_cscene_cscene_instance
    ADD COLUMN log_set_cycle int(8) NULL COMMENT '日志收集周期' AFTER logo;

ALTER TABLE mp_devops_load
    MODIFY COLUMN action_type varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作用类型' AFTER tool_platform;

