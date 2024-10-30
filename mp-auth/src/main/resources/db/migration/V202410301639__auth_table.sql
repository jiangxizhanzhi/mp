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

 Date: 27/10/2024 15:37:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mp_auth_team
-- ----------------------------
DROP TABLE IF EXISTS `mp_auth_team`;
CREATE TABLE `mp_auth_team`  (
                                 `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                 `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '战队名称',
                                 `logo` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'logo',
                                 `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
                                 `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                 `user_count` int(10) UNSIGNED ZEROFILL NOT NULL DEFAULT 0000000000 COMMENT '战队人员数量',
                                 `creator` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
                                 PRIMARY KEY (`pk`) USING BTREE,
                                 UNIQUE INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


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

 Date: 27/10/2024 15:37:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mp_auth_personnel_management
-- ----------------------------
DROP TABLE IF EXISTS `mp_auth_personnel_management`;
CREATE TABLE `mp_auth_personnel_management`  (
                                                 `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                 `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                                 `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
                                                 `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '姓名',
                                                 `user_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
                                                 `team_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '战队id',
                                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                                 PRIMARY KEY (`pk`) USING BTREE,
                                                 UNIQUE INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
