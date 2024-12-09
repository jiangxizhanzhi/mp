DROP TABLE IF EXISTS `mp_sceneplugincollection_scene_data_custom`;
CREATE TABLE `mp_sceneplugincollection_scene_data_custom`  (
                                                               `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                               `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                                               `cscene` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景实例id',
                                                               `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名称',
                                                               `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
                                                               `scene_element` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点id',
                                                               `scene_element_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点名称',
                                                               `size` int(10) NULL DEFAULT NULL COMMENT '文件大小',
                                                               `protocol` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '采集协议',
                                                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                               PRIMARY KEY (`pk`) USING BTREE,
                                                               INDEX `scene`(`size`, `protocol`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 134487 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '场景实例数据采集插件自定义信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;