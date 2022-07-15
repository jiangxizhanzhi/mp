# 云平台节点探针监控脚本安装信息表
CREATE TABLE mp_devops_cpnode_monitor_script_config
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `agent_name`  varchar(64) NOT NULL COMMENT '探针名称',
    `script_name` varchar(64) NOT NULL COMMENT '脚本名称',
    `hostname`    varchar(64) NOT NULL COMMENT '主机名称',
    `ip`          varchar(64) NOT NULL COMMENT 'IP地址',
    `status`      int         NOT NULL COMMENT '1:已完成安装 2:已发送指令-待确认安装成功 3:安装失败',
    `create_time`  datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '云平台节点探针监控脚本安装信息表';
