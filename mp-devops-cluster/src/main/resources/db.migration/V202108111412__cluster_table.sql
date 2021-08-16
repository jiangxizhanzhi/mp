CREATE TABLE `mp_devops_services` (
                                      `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `id` varchar(64) NOT NULL COMMENT 'id',
                                      `name` varchar(42) NOT NULL COMMENT '名称',
                                      `description` varchar(1024) NOT NULL COMMENT '描述',
                                      `img_icon_url` varchar(255) NOT NULL COMMENT '图标',
                                      `type` varchar(32) NOT NULL COMMENT '服务类型：docker服务，Windows服务',
                                      `docker` varchar(255) DEFAULT NULL COMMENT 'docker的tar/zip包地址',
                                      `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                      `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`pk`),
                                      KEY `idx_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务管理表';


CREATE TABLE `mp_devops_services_category` (
                                               `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `id` varchar(64) NOT NULL COMMENT 'id',
                                               `name` varchar(64) NOT NULL COMMENT '服务-分类名称',
                                               `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                               `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                               PRIMARY KEY (`pk`),
                                               UNIQUE KEY `idx_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';


CREATE TABLE `mp_devops_services_link_category` (
                                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                    `id` varchar(64) NOT NULL COMMENT 'id',
                                                    `services_id` varchar(64) NOT NULL COMMENT '服务ID',
                                                    `services_category_id` varchar(64) NOT NULL COMMENT '服务分类ID',
                                                    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                                    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                                    PRIMARY KEY (`pk`),
                                                    KEY `idx_services_category_id` (`services_category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务关联分类表';


CREATE TABLE `mp_devops_sevices_start_parameters` (
                                                      `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                      `id` varchar(64) NOT NULL COMMENT 'id',
                                                      `type` tinyint(2) NOT NULL COMMENT '启动参数类型，环境变量/命令参数/端口映射',
                                                      `option` tinyint(2) DEFAULT NULL COMMENT '可选还是必选',
                                                      `name` varchar(32) DEFAULT NULL COMMENT '参数名称',
                                                      `default_val` varchar(64) DEFAULT NULL COMMENT '参数默认值',
                                                      `command` varchar(500) DEFAULT NULL COMMENT '命令',
                                                      `container_port` varchar(8) DEFAULT NULL COMMENT '容器端口',
                                                      `host_port` varchar(8) DEFAULT NULL COMMENT '主机端口',
                                                      `description` varchar(255) DEFAULT NULL COMMENT '描述',
                                                      `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                                      `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                                      PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务启动参数表';

INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (1, '03d332ebf9e311eb87910242ac140002', '数据分析', '2021-08-11 14:29:52.000000', '2021-08-11 14:29:52.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (2, '03d33570f9e311eb87910242ac140002', '应用框架', '2021-08-11 14:29:52.000000', '2021-08-11 14:29:52.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (3, '03d3376ef9e311eb87910242ac140002', '应用中间件', '2021-08-11 14:29:52.000000', '2021-08-11 14:29:52.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (4, '03d33862f9e311eb87910242ac140002', '应用服务', '2021-08-11 14:29:52.000000', '2021-08-11 14:29:52.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (5, '03d3390df9e311eb87910242ac140002', '基础镜像', '2021-08-11 14:29:52.000000', '2021-08-11 14:29:52.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (6, '68cb305ef9e311eb87910242ac140002', '数据库', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (7, '68cb3341f9e311eb87910242ac140002', 'DevOps工具', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (8, '68cb34cff9e311eb87910242ac140002', '功能镜像', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (9, '68cb3597f9e311eb87910242ac140002', '消息服务', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (10, '68cb3645f9e311eb87910242ac140002', '操作系统', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (11, '68cb36ecf9e311eb87910242ac140002', '程序语言', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (12, '68cb3796f9e311eb87910242ac140002', '安全', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');
INSERT INTO `mp_devops_services_category`(`pk`, `id`, `name`, `create_time`, `update_time`) VALUES (13, '68cb3841f9e311eb87910242ac140002', '存储', '2021-08-11 14:32:42.000000', '2021-08-11 14:32:42.000000');