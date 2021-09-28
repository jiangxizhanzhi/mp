CREATE TABLE `mp_devops_target_logo` (
                                         `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'id',
                                         `url` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'url',
                                         `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
                                         `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
                                         PRIMARY KEY (`pk`),
                                         UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='靶标默认logo';


CREATE TABLE `mp_devops_target` (
                                    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `id` varchar(64) NOT NULL COMMENT '镜像ID',
                                    `uuid` varchar(64) NOT NULL COMMENT '镜像UUID',
                                    `name` varchar(64) NOT NULL COMMENT '镜像名称',
                                    `role` tinyint(8) DEFAULT NULL COMMENT '角色',
                                    `main_category` int(8) DEFAULT NULL COMMENT '一级分类',
                                    `sub_category` int(8) DEFAULT NULL COMMENT '二级分类',
                                    `third_category` int(8) DEFAULT NULL COMMENT '三级分类',
                                    `image_type` varchar(32) DEFAULT NULL COMMENT '镜像类型',
                                    `is_public` tinyint(1) NOT NULL COMMENT '是否公开，0：私有，1：自定义，2：公开',
                                    `quick_create_support` tinyint(1) NOT NULL COMMENT '是否支持快速构建，0：不支持，1：支持',
                                    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                    `in_position` tinyint(1) DEFAULT NULL COMMENT '在位状态',
                                    `reference_status` int(8) NOT NULL DEFAULT '0' COMMENT '引用状态',
                                    `use_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用状态',
                                    `system_type` int(8) DEFAULT NULL COMMENT '系统类型',
                                    `system_architecure` varchar(16) DEFAULT NULL COMMENT '系统架构',
                                    `language` varchar(16) DEFAULT NULL COMMENT '系统语言',
                                    `image_status` tinyint(2) DEFAULT NULL COMMENT '镜像状态',
                                    `base_image_support` tinyint(1) DEFAULT NULL COMMENT '是否支持基础镜像,0:不支持，1：支持',
                                    PRIMARY KEY (`pk`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='镜像管理';


CREATE TABLE `mp_devops_target_auth` (
                                         `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `id` varchar(64) NOT NULL COMMENT 'id',
                                         `tenant` varchar(64) DEFAULT NULL COMMENT '租户',
                                         `owner` varchar(64) NOT NULL COMMENT '用户ID',
                                         `target` varchar(64) NOT NULL COMMENT '镜像UUID',
                                         `can_read` tinyint(1) DEFAULT NULL COMMENT '可读',
                                         `can_create` tinyint(1) DEFAULT NULL COMMENT '可创建',
                                         `can_update` tinyint(1) DEFAULT NULL COMMENT '可更新',
                                         `can_delete` tinyint(1) DEFAULT NULL COMMENT '可删除',
                                         `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                         `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                         PRIMARY KEY (`pk`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='镜像授权表';