CREATE TABLE `mp_devops_physic_node`
(
    `pk`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64) NOT NULL COMMENT 'ID',
    `node_name`       varchar(64) NOT NULL COMMENT '节点名称',
    `os`              varchar(64) NOT NULL COMMENT '操作系统',
    `rack_position`   varchar(255) NOT NULL COMMENT '机架位置',
    `cpu_info`          varchar(255) NOT NULL COMMENT 'CPU信息',
    `memory_info`     varchar(255) NOT NULL COMMENT '内存信息',
    `disk_info`        varchar(255) NOT NULL COMMENT '磁盘信息',
    `nic_info`         varchar(255) NOT NULL COMMENT '网卡信息',
    `create_time`      datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`      datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='攻击指令历史记录';