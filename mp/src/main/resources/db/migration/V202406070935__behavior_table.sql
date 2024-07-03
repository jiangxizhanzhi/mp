CREATE TABLE `mp_network_behavior` (
                                       `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `id` varchar(64) NOT NULL COMMENT 'ID',
                                       `name` varchar(64) NOT NULL COMMENT '网络行为名称',
                                       `description` varchar(255) NOT NULL COMMENT '网络行为描述',
                                       `type` tinyint(2) NOT NULL COMMENT '网络行为类型',
                                       `file_url` varchar(255) DEFAULT NULL COMMENT '文件路径',
                                       `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                       `update_time` datetime NOT NULL COMMENT '更新时间',
                                       PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网络行为表';

CREATE TABLE `mp_user_behavior` (
                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `id` varchar(64) NOT NULL COMMENT 'ID',
                                    `name` varchar(64) NOT NULL COMMENT '用户行为名称',
                                    `description` varchar(255) NOT NULL COMMENT '用户行为描述',
                                    `type` tinyint(2) NOT NULL COMMENT '用户行为类型',
                                    `network_ids` text NOT NULL COMMENT '关联的网络行为id列表',
                                    `start_time` datetime NOT NULL COMMENT '开始时间',
                                    `end_time` datetime NOT NULL COMMENT '结束时间',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

CREATE TABLE `mp_cscene_network_behavior` (
                                              `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `id` varchar(64) NOT NULL COMMENT 'ID',
                                              `name` varchar(64) NOT NULL COMMENT '网络行为名称',
                                              `description` varchar(255) NOT NULL COMMENT '网络行为描述',
                                              `type` tinyint(2) NOT NULL COMMENT '网络行为类型',
                                              `file_url` varchar(255) DEFAULT NULL COMMENT '文件路径',
                                              `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
                                              `node_id` varchar(64) NOT NULL COMMENT '节点id',
                                              `url` varchar(255) DEFAULT NULL COMMENT 'url配置',
                                              `network_behavior_id` varchar(64) NOT NULL COMMENT '网络行为id',
                                              `create_time` datetime NOT NULL COMMENT '创建时间',
                                              `update_time` datetime NOT NULL COMMENT '更新时间',
                                              `scene_id` varchar(64) DEFAULT NULL COMMENT '场景id',
                                              PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景网络行为表';

CREATE TABLE `mp_cscene_user_behavior` (
                                           `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `id` varchar(64) NOT NULL COMMENT 'ID',
                                           `name` varchar(64) NOT NULL COMMENT '用户行为名称',
                                           `description` varchar(255) NOT NULL COMMENT '用户行为描述',
                                           `type` tinyint(2) NOT NULL COMMENT '用户行为类型',
                                           `network_ids` text NOT NULL COMMENT '关联的网络行为id列表',
                                           `start_time` datetime NOT NULL COMMENT '开始时间',
                                           `end_time` datetime NOT NULL COMMENT '结束时间',
                                           `node_id` varchar(64) NOT NULL COMMENT '节点id',
                                           `user_behavior_id` varchar(64) NOT NULL COMMENT '用户行为id',
                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                           `update_time` datetime NOT NULL COMMENT '更新时间',
                                           `scene_id` varchar(64) DEFAULT NULL COMMENT '场景id',
                                           PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景用户行为表';


CREATE TABLE `mp_cscene_common_collect` (
                                            `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `id` varchar(64) NOT NULL COMMENT 'ID',
                                            `cscene_id` varchar(64) NOT NULL COMMENT '场景id',
                                            `node_id` varchar(64) NOT NULL COMMENT '节点id',
                                            `host_name` varchar(255) NOT NULL COMMENT '节点名称',
                                            `agreement` varchar(255) NOT NULL COMMENT '协议',
                                            `time` datetime NOT NULL COMMENT '采集时间',
                                            `detail` text DEFAULT NULL COMMENT '详情',
                                            `create_time` datetime NOT NULL COMMENT '创建时间',
                                            `update_time` datetime NOT NULL COMMENT '更新时间',
                                            PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用协议采集数据表';