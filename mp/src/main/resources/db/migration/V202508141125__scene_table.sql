CREATE TABLE `attack_command_history_model`
(
    `pk`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64) NOT NULL COMMENT 'ID',
    `cscene_id`         varchar(64) NOT NULL COMMENT '场景ID',
    `category`          varchar(64) NOT NULL COMMENT '指令类型',
    `attack_desc`       varchar(64) NOT NULL COMMENT '具体攻击指令描述',
    `attack_target`     varchar(64) NOT NULL COMMENT '攻击目标名称',
    `attack_target_id`  varchar(64) NOT NULL COMMENT '攻击目标ID',
    `attack_source_id`  varchar(64) NOT NULL COMMENT '攻击源头ID',
    `attack_command`    varchar(64) NOT NULL COMMENT '具体执行的攻击命令',
    `attack_expected_effect` varchar(256) NOT NULL COMMENT '攻击的预期效果',
    `command_status`     varchar(64) NOT NULL COMMENT '命令执行状态',
    `attack_command_output` text NOT NULL COMMENT '攻击命令的原始输出',
    `need_manual_check_attack_effect` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否需要人工检查攻击效果',
    `attack_effected_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '攻击是否生效',
    `create_time`      datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`      datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='攻击指令历史记录';