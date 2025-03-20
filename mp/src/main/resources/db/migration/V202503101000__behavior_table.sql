CREATE TABLE `mp_network_behavior` (
                                                `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                                `create_time` datetime NOT NULL COMMENT '创建时间',
                                                `update_time` datetime NOT NULL COMMENT '更新时间',
                                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行为名称',
                                                `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '行为描述',
                                                `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名称',
                                                `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
                                                `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '参数配置',
                                                PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `mp_run_behavior_log` (
                                       `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                       `update_time` datetime NOT NULL COMMENT '更新时间',
                                       `log_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志名称',
                                       `log_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志路径',
                                       `behavior_template_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '行为模板id',
                                       `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '参数配置',
                                       `queue_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '队列名称',
                                       PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;