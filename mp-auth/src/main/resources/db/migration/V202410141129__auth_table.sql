/*
 Navicat Premium Data Transfer

 Source Server         : YZ3
 Source Server Type    : MySQL
 Source Server Version : 100334 (10.3.34-MariaDB-1:10.3.34+maria~focal-log)
 Source Host           : 10.108.1.10:9306
 Source Schema         : mp

 Target Server Type    : MySQL
 Target Server Version : 100334 (10.3.34-MariaDB-1:10.3.34+maria~focal-log)
 File Encoding         : 65001

 Date: 14/10/2024 11:29:03
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
) ENGINE = InnoDB AUTO_INCREMENT = 702 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限（资源）表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mp_auth_target_difference
-- ----------------------------
INSERT INTO `mp_auth_target_difference` VALUES (1, '1', '2024-10-12 17:18:59', '2024-10-12 17:19:01', 'verification-tasks,load-management,group-training,personal-center,dashboard,cloud-scene-template,cr-team-training,assessment-system,assessment-algorithm,assessment-model,assessment-indicators,traffic-manage-folder,static-traffic-list,dynamic-traffic-list,traffic-scheme-list,front_all,back_all');
INSERT INTO `mp_auth_target_difference` VALUES (2, '2', '2024-10-12 17:26:40', '2024-10-12 17:26:43', 'dashboard,cloud-scene-template,verification-tasks,personal-center,front_all,back_all');
INSERT INTO `mp_auth_target_difference` VALUES (3, '3', '2024-10-12 17:28:00', '2024-10-12 17:28:02', 'cr-standard-device-folder,image-manage,real-manage,cloud-image-manage,cr-collection-system-folder,traffic-manage-folder,traffic-scheme-list,dynamic-traffic-list,static-traffic-list,cloud-scene-template,cr-team-training,load-management,verification-tasks,personal-center,front_all,back_all');
INSERT INTO `mp_auth_target_difference` VALUES (4, '4', '2024-10-12 17:31:47', '2024-10-12 17:31:49', 'dashboard,resource-manage-folder,GHDB-library-manage,shellcode-library-management,exp-library-management,vulnerability,system-management-folder,system-notice-folder,role-management,user-manage,system-log,audit-log,system-config,system-operation-folder,resource-usage-management,real-equipment-management,network-management,cluster-management,instance-monitoring,business-service-monitoring,cloud-platform-monitoring,system-alarm');

SET FOREIGN_KEY_CHECKS = 1;
