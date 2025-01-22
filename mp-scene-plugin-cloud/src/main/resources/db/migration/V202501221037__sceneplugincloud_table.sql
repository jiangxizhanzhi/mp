/*
 Navicat Premium Data Transfer

 Source Server         : hfdy
 Source Server Type    : MySQL
 Source Server Version : 100334 (10.3.34-MariaDB-1:10.3.34+maria~focal-log)
 Source Host           : 10.100.12.1:9310
 Source Schema         : mp

 Target Server Type    : MySQL
 Target Server Version : 100334 (10.3.34-MariaDB-1:10.3.34+maria~focal-log)
 File Encoding         : 65001

 Date: 22/01/2025 10:36:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mp_scene_switch_info
-- ----------------------------
DROP TABLE IF EXISTS `mp_scene_switch_info`;
CREATE TABLE `mp_scene_switch_info`  (
                                         `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                         `create_time` datetime NOT NULL COMMENT '创建时间',
                                         `update_time` datetime NOT NULL COMMENT '更新时间',
                                         `scene_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景id',
                                         `node_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点id',
                                         `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
                                         `user_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户真实ip',
                                         PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务管理表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
