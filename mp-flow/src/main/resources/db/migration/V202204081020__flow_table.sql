# 流量脚本参数表字段名长度调整
ALTER TABLE `mp_flow_script_param`
    MODIFY COLUMN `param_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数名' AFTER `flow_id`;
ALTER TABLE `mp_flow_script_param`
    MODIFY COLUMN `param_desc` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数说明' AFTER `param_type`;