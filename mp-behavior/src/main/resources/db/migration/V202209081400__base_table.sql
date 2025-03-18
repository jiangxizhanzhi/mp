-- ----------------------------
-- Table structure for mp_behavior_type
-- ----------------------------
DROP TABLE IF EXISTS `mp_behavior_type`;
CREATE TABLE `mp_behavior_type`  (
                                     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                     `create_time` datetime(0) NOT NULL COMMENT '创建时间',
                                     `update_time` datetime(0) NOT NULL COMMENT '更新时间',
                                     `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类名称',
                                     `code` int(5) NULL DEFAULT NULL COMMENT '编号',
                                     PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mp_behavior_type
-- ----------------------------
INSERT INTO `mp_behavior_type` VALUES (1, '1', '2025-03-18 14:21:00', '2025-03-18 14:21:00', 'Web访问', 0);
INSERT INTO `mp_behavior_type` VALUES (2, '2', '2025-03-18 14:21:00', '2025-03-18 14:21:00', '办公门户访问', 1);
INSERT INTO `mp_behavior_type` VALUES (3, '3', '2025-03-18 14:21:00', '2025-03-18 14:21:00', '视频会议召开', 2);
INSERT INTO `mp_behavior_type` VALUES (4, '4', '2025-03-18 14:21:00', '2025-03-18 14:21:00', 'FTP访问', 3);
INSERT INTO `mp_behavior_type` VALUES (5, '5', '2025-03-18 14:21:00', '2025-03-18 14:21:00', '流媒体访问', 4);
INSERT INTO `mp_behavior_type` VALUES (6, '6', '2025-03-18 14:21:00', '2025-03-18 14:21:00', '即时通讯访问', 5);
INSERT INTO `mp_behavior_type` VALUES (7, '7', '2025-03-18 14:21:00', '2025-03-18 14:21:00', '指挥信息系统', 6);

DROP TABLE IF EXISTS `mp_network_behavior`;
CREATE TABLE `mp_network_behavior` (
                                       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `id` varchar(64) NOT NULL COMMENT 'ID',
                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                       `update_time` datetime NOT NULL COMMENT '更新时间',
                                       `name` varchar(255) NOT NULL COMMENT '行为名称',
                                       `description` varchar(255) DEFAULT NULL COMMENT '行为描述',
                                       `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
                                       `file_path` varchar(255) DEFAULT NULL COMMENT '文件路径',
                                       `params` text DEFAULT NULL COMMENT '参数配置',
                                       `target` varchar(255) DEFAULT NULL COMMENT '目标服务',
                                       `type` tinyint(2) DEFAULT NULL COMMENT '分类',
                                       `step` tinyint(5) DEFAULT NULL COMMENT '步骤',
                                       PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `mp_user_behavior`;
CREATE TABLE `mp_user_behavior` (
                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'ID',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `name` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '行为名称',
                                    `description` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '行为描述',
                                    `start_time` time DEFAULT NULL COMMENT '开始时间',
                                    `end_time` time DEFAULT NULL COMMENT '结束时间',
                                    `behaviors` varchar(255) DEFAULT NULL COMMENT '行为列表',
                                    PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



