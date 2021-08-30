CREATE TABLE `mp_devops_target_logo` (
                                         `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'id',
                                         `url` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'url',
                                         `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
                                         `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
                                         PRIMARY KEY (`pk`),
                                         UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='靶标默认logo';