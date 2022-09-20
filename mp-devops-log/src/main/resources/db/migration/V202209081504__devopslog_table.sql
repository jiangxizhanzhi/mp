# v5.3.5
CREATE TABLE `mp_log_report`
(
    `pk`           int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(32)  NOT NULL COMMENT 'ID',
    `file_name`    varchar(32)  NOT NULL COMMENT '文件名称',
    `type`         varchar(20)  NOT NULL COMMENT '日志类型（CR/MP/AD/CP)',
    `file_size`    bigint(20)   NOT NULL COMMENT '文件大小',
    `download_url` varchar(255) NOT NULL COMMENT '文件下载地址',
    `host_ip`      varchar(20)  NOT NULL DEFAULT '' COMMENT '节点ip',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='日志上报表';



-- 审计日志表
CREATE TABLE mp_devops_log_operation (
     `pk` int NOT NULL AUTO_INCREMENT COMMENT '主键',
     `id` varchar(64) NOT NULL COMMENT 'ID',
     `site_type` int NOT NULL COMMENT '站点类型：应用、中台、前端、平台',
     `from` varchar(255) NOT NULL COMMENT '日志来源',
     `log_type` int NOT NULL COMMENT '日志类型：设备日志、系统日志、业务日志、用户日志、场景日志',
     `op_module` varchar(255) NOT NULL COMMENT '操作模块',
     `op_module_id` int NOT NULL COMMENT '操作模块ID',
     `op_type` varchar(255) NOT NULL COMMENT '操作类型',
     `op_type_id` int NOT NULL COMMENT '操作类型ID',
     `op_target` varchar(255) NOT NULL COMMENT '操作对象',
     `op_target_id` int NOT NULL COMMENT '操作对象ID',
     `title` varchar(255) NOT NULL COMMENT '主题',
     `user_id` varchar(64) NOT NULL COMMENT '用户ID',
     `user_name` varchar(64) NOT NULL COMMENT '用户名',
     `user_type` varchar(64) NOT NULL COMMENT '用户类型（用户，客户端）',
     `content` text NOT NULL COMMENT '内容',
     `description` varchar(1024) NOT NULL COMMENT '描述',
     `op_time` datetime NOT NULL COMMENT '操作时间',
     `op_ip` varchar(64) NOT NULL COMMENT '客户端IP',
     `create_time`  datetime NOT NULL COMMENT '创建时间',
     `update_time`  datetime NOT NULL COMMENT '更新时间',
     PRIMARY KEY (`pk`),
     UNIQUE KEY (`id`),
     KEY (`op_module_id`, `op_type_id`, `user_name`, `op_target`, `create_time`),
     KEY (`user_id`, `create_time`)
) COMMENT '审计日志表';
