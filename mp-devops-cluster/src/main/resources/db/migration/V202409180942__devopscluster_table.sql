CREATE TABLE `mp_devops_task_node_target` (
                                              `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `id` varchar(64) NOT NULL COMMENT 'ID',
                                              `record_id` varchar(64) NOT NULL COMMENT '报告ID',
                                              `record_name` varchar(128) DEFAULT NULL COMMENT '报告名称',
                                              `node` varchar(64) NOT NULL COMMENT '节点',
                                              `images` text DEFAULT NULL COMMENT '绑定镜像ID列表',
                                              `create_time` datetime(6) NOT NULL COMMENT '创建时间',
                                              `update_time` datetime(6) NOT NULL COMMENT '更新时间',
                                              PRIMARY KEY (`pk`) USING BTREE,
                                              UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='扫描任务节点关联镜像表';