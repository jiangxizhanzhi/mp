CREATE TABLE `mp_flow_script_param` (
        `pk` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '脚本参数id',
        `flow_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流量id',
        `param_key` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名',
        `param_value` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数值',
        `param_type` tinyint(4) NOT NULL COMMENT '参数类型：0 string,1 IP,2 number,3 bool',
        `param_desc` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数说明',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `update_time` datetime NOT NULL COMMENT '更新时间',
        PRIMARY KEY (`pk`) USING BTREE,
        UNIQUE KEY `id_index` (`id`,`flow_id`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT COMMENT='流量脚本参数表';