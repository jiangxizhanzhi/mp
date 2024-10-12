/*
 Navicat Premium Data Transfer

 Source Server         : YZ4
 Source Server Type    : MySQL
 Source Server Version : 100334 (10.3.34-MariaDB-1:10.3.34+maria~focal-log)
 Source Host           : 10.108.1.10:9307
 Source Schema         : mp

 Target Server Type    : MySQL
 Target Server Version : 100334 (10.3.34-MariaDB-1:10.3.34+maria~focal-log)
 File Encoding         : 65001

 Date: 12/10/2024 14:41:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mp_auth_target_difference
-- ----------------------------
DROP TABLE IF EXISTS `mp_auth_target_difference`;
CREATE TABLE `mp_auth_target_difference`  (
                                              `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                              `create_time` datetime NOT NULL COMMENT '创建时间',
                                              `update_time` datetime NOT NULL COMMENT '更新时间',
                                              `difference` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '区分菜单',
                                              PRIMARY KEY (`pk`) USING BTREE,
                                              UNIQUE INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 702 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限（资源）表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mp_auth_target_difference
-- ----------------------------
INSERT INTO `mp_auth_target_difference` VALUES (1, '1', '2024-10-12 10:49:49', '2024-10-12 10:49:52', 'living-vulnerability，living-vulnerability-overview，living-vulnerability-intelligence，living-vulnerability-env，living-vulnerability-exploitation，living-vulnerability-analysis，cr-standard-device-folder，image-manage，real-manage，service-manage，front_all，back_all');
INSERT INTO `mp_auth_target_difference` VALUES (2, '2', '2024-10-12 10:53:51', '2024-10-12 10:53:54', 'range-test-self，recourse-center-folder，tool-magazine，vulnerability-library，tool-exp，tool-shell，ghdb-library，resource-manage-folder，tool，vulnerability，front_all，back_all');
INSERT INTO `mp_auth_target_difference` VALUES (3, '3', '2024-10-12 10:56:25', '2024-10-12 10:56:27', 'range-test-self，dashboard，assessment-system，assessment-template，assessment-algorithm，assessment-model，assessment-indicators，technical-battle，cr-standard-device-folder，image-manage，service-manage，topology-management，traffic-manage-folder，static-traffic-list，dynamic-traffic-list，traffic-scheme-list，front_all，back_all');
INSERT INTO `mp_auth_target_difference` VALUES (4, '4', '2024-10-12 11:00:39', '2024-10-12 11:00:42', 'range-test-self，cr-standard-device-folder，image-manage，real-manage，service-manage，cr-collection-system-folder，universal-probe，custom-probe，collection-template，traffic-manage-folder，static-traffic-list，dynamic-traffic-list，traffic-scheme-list，system-management-folder，system-config，audit-log，operate-log，system-log，user-manage，role-management，system-notice-folder，system-operation-folder，system-alarm，cloud-platform-monitoring，business-service-monitoring，instance-monitoring，cluster-management，network-management，real-equipment-management，resource-usage-management，front_all，back_all');

SET FOREIGN_KEY_CHECKS = 1;
