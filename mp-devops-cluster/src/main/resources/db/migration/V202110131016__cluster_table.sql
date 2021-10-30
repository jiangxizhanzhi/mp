CREATE TABLE `mp_devops_screenshot` (
        `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `id` varchar(64) NOT NULL COMMENT 'id',
        `file` varchar(256) NOT NULL COMMENT '截屏文件地址',
        `file_name` varchar(256) NOT NULL COMMENT '截屏文件名称',
        `file_size` bigint(20) NOT NULL COMMENT '截屏文件大小，单位:B',
        `scene_name` varchar(256) NOT NULL COMMENT '场景名称',
        `user_id` varchar(64) NOT NULL COMMENT '用户id',
        `user_name` varchar(64) NOT NULL COMMENT '用户名称',
        `user_org_name` varchar(1024) NOT NULL COMMENT '组织名称',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `update_time` datetime NOT NULL COMMENT '更新时间',
        PRIMARY KEY (`pk`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COMMENT='截屏文件管理表';