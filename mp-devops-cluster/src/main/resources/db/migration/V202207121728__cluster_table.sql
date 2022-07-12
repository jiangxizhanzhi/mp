ALTER TABLE `mp_devops_target`
    ADD COLUMN `instance_state` tinyint NOT NULL DEFAULT 0 COMMENT '镜像状态 0:没有实例，1：创建中，2创建成功，3创建失败';