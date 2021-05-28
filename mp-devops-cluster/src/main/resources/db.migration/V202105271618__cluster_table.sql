CREATE TABLE mp.`mp_devops_device` (
                                    `pk` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                    `id` varchar(64) NOT NULL COMMENT '设备ID',
                                    `crate_id` varchar(64) NOT NULL COMMENT '机箱编号',
                                    `group_id` varchar(64) NOT NULL COMMENT '设备组编号',
                                    `identifier` varchar(64) NOT NULL COMMENT '设备编号',
                                    `name` varchar(32) NOT NULL COMMENT '设备名称',
                                    `type` tinyint(2) unsigned NOT NULL COMMENT '设备类型',
                                    `os` tinyint(2) unsigned NOT NULL COMMENT '操作系统',
                                    `cpu` tinyint(3) unsigned NOT NULL COMMENT '处理器类型',
                                    `memory_size` int(11) unsigned NOT NULL COMMENT '内存大小',
                                    `disk_size` bigint(20) unsigned NOT NULL COMMENT '磁盘大小',
                                    `use_state` tinyint(2) unsigned NOT NULL COMMENT '使用状态',
                                    `run_state` tinyint(2) unsigned NOT NULL COMMENT '运行状态',
                                    `description` varchar(255) NOT NULL COMMENT '描述',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `create_person` varchar(50) NOT NULL COMMENT '创建人ID',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `update_person` varchar(50) DEFAULT NULL COMMENT '更新人ID',
                                    PRIMARY KEY (`pk`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维管控-集群管理-计算节点-设备管理表';


CREATE TABLE mp.`mp_devops_device_analyse` (
                                            `pk` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                            `id` varchar(64) NOT NULL COMMENT '设备参数分析ID',
                                            `device_id` varchar(64) NOT NULL COMMENT '设备ID',
                                            `cpu_use_rate` decimal(10,2) unsigned NOT NULL COMMENT '处理器使用率',
                                            `disk_use_size` bigint(20) unsigned NOT NULL COMMENT '磁盘已使用大小',
                                            `address_pool_use_rate` decimal(10,2) unsigned NOT NULL COMMENT '地址池使用率',
                                            `disk_io` decimal(10,2) NOT NULL COMMENT '磁盘IO大小',
                                            `network_io` decimal(10,2) unsigned NOT NULL COMMENT '网络IO大小',
                                            `mbps` int(8) unsigned NOT NULL COMMENT '带宽大小',
                                            `temperature` decimal(8,2) unsigned NOT NULL COMMENT '工作温度',
                                            `create_time` datetime NOT NULL COMMENT '创建时间',
                                            `create_person` varchar(50) NOT NULL COMMENT '创建人ID',
                                            PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维管控-集群管理-计算节点-设备参数分析表';


CREATE TABLE mp.`mp_devops_device_service` (
                                            `pk` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                            `id` varchar(64) NOT NULL COMMENT '设备服务ID',
                                            `device_id` varchar(64) NOT NULL COMMENT '设备ID',
                                            `vm_num` int(10) unsigned NOT NULL COMMENT '虚拟机数量',
                                            `container_num` int(10) unsigned NOT NULL COMMENT '容器数量',
                                            `real_device_num` int(10) unsigned NOT NULL COMMENT '实装装备数量',
                                            `npc_num` int(10) NOT NULL COMMENT 'NPC数量',
                                            `switch_num` int(10) NOT NULL COMMENT '交换机数量',
                                            `router_num` int(10) unsigned NOT NULL COMMENT '路由器数量',
                                            `firewall_num` int(10) NOT NULL COMMENT '防火墙数量',
                                            `bridge_num` int(10) unsigned NOT NULL COMMENT '网桥数量',
                                            `create_time` datetime NOT NULL COMMENT '创建时间',
                                            `create_person` varchar(50) NOT NULL COMMENT '创建人ID',
                                            `update_time` datetime NOT NULL COMMENT '更新时间',
                                            `update_person` varchar(50) DEFAULT NULL COMMENT '更新人ID',
                                            PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维管控-集群管理-计算节点-设备服务表';