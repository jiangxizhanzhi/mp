-- 5.4-hjjydx 系统配置新增专业配置
CREATE TABLE `mp_auth_major`
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'ID',
    `major_name`    varchar(64) NOT NULL COMMENT '专业名称',
    `major_description` varchar(1024) NOT NULL DEFAULT '' COMMENT '专业描述',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`),
    UNIQUE KEY (`major_name`)
) COMMENT ='系统管理-专业配置表';

alter table `mp_auth_user` add column `student_number` varchar(64) NOT NULL default '' comment '学号';
alter table `mp_auth_user` add column `major_id`  varchar(64) NOT NULL comment '专业';