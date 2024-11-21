CREATE TABLE `mp_token` (
                            `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `id` varchar(64) NOT NULL COMMENT 'ID',
                            `user_name` varchar(255) DEFAULT NULL,
                            `jwt` varchar(255) DEFAULT NULL,
                            `access_token` varchar(255) DEFAULT NULL,
                            `logon_token` varchar(255) DEFAULT NULL,
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_time` datetime NOT NULL COMMENT '更新时间',
                            PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;