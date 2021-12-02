# 流量任务表新增实际执行次数字段
ALTER TABLE `mp_flow_task`
    ADD COLUMN `running_count` int NOT NULL DEFAULT 0 COMMENT '实际执行次数' AFTER `task_status`;
