CREATE TABLE `mp_behavior_task` (
                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'ID',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `simulate_id` varchar(64) DEFAULT NULL COMMENT '仪表id',
                                    `network_behavior_id` varchar(64) DEFAULT NULL COMMENT '行为模板id',
                                    `name` varchar(255) DEFAULT NULL COMMENT '行为名称',
                                    `description` varchar(255) DEFAULT NULL COMMENT '行为描述',
                                    `file_url` varchar(255) DEFAULT NULL COMMENT '文件下载路径',
                                    `params` text DEFAULT NULL COMMENT '参数配置',
                                    `target` varchar(255) DEFAULT NULL COMMENT '目标服务',
                                    `type` varchar(255) DEFAULT NULL COMMENT '分类',
                                    `step` varchar(255) DEFAULT NULL COMMENT '步骤',
                                    `run_time` datetime DEFAULT NULL COMMENT '运行时间',
                                    `delete` tinyint(1) DEFAULT NULL COMMENT '是否删除',
                                    PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `mp_behavior_simulate` (
                                        `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'ID',
                                        `create_time` datetime NOT NULL COMMENT '创建时间',
                                        `update_time` datetime NOT NULL COMMENT '更新时间',
                                        `scene_id` varchar(64) DEFAULT NULL COMMENT '场景id',
                                        `scene_name` varchar(255) DEFAULT NULL COMMENT '场景名称',
                                        `node_id` varchar(64) DEFAULT NULL COMMENT '节点id',
                                        `node_name` varchar(255) DEFAULT NULL COMMENT '节点名称',
                                        `status` tinyint(2) DEFAULT NULL COMMENT '状态',
                                        `delete` tinyint(1) DEFAULT NULL COMMENT '是否删除',
                                        `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                        `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                        `online` tinyint(1) DEFAULT NULL COMMENT '是否在线',
                                        PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `mp_behavior_scene` (
                                     `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'ID',
                                     `create_time` datetime NOT NULL COMMENT '创建时间',
                                     `update_time` datetime NOT NULL COMMENT '更新时间',
                                     `scene_id` varchar(64) DEFAULT NULL COMMENT '场景id',
                                     `node_id` varchar(64) DEFAULT NULL COMMENT '节点id',
                                     `node_config` text DEFAULT NULL COMMENT '节点配置',
                                     PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `mp_run_behavior_log` (
                                       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `id` varchar(64) NOT NULL COMMENT 'ID',
                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                       `update_time` datetime NOT NULL COMMENT '更新时间',
                                       `behavior_template_id` varchar(64) NOT NULL COMMENT '行为模板id',
                                       `params` text DEFAULT NULL COMMENT '参数配置',
                                       `queue_name` varchar(255) DEFAULT NULL COMMENT '队列名称',
                                       `log_content` text DEFAULT NULL COMMENT '日志内容',
                                       `behavior_template_name` varchar(255) DEFAULT NULL COMMENT '行为模板名称',
                                       PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;