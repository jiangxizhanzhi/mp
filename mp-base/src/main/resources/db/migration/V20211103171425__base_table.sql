CREATE TABLE mp_base_local_file_mapping (
    `pk` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id` varchar(64)  NOT NULL COMMENT 'id',
    `group` varchar(64) NOT NULL COMMENT '分组',
    `seq` int(10) NOT NULL COMMENT '序号',
    `name` varchar(255) NOT NULL COMMENT '名称',
    `local_file` varchar(2048) NOT NULL COMMENT '本地文件路径',
    `local_file_modified_time` bigint(20) NOT NULL COMMENT '本地文件修改时间',
    `remote_file` varchar(2048) NOT NULL COMMENT '远程文件路径',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
);
