# 流量集合表新增分类字段
ALTER TABLE `mp_flow_collection`
    ADD COLUMN `type_flag` tinyint UNSIGNED NOT NULL DEFAULT 2 COMMENT '集合分类：0背景流量，1攻击流量，2混合流量' AFTER `category`;

