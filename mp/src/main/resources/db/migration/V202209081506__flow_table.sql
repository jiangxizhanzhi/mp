ALTER TABLE `mp_flow_task_task`
    ADD COLUMN `msg` varchar(1024) NOT NULL DEFAULT '' COMMENT '错误信息' AFTER `task_status`;