alter table mp_sceneplugincollection_scene add column arkime_ip varchar(64) not null default '' comment 'arkime ip';

alter table mp_sceneplugincollection_scene_config add column flow_persistence_rule text not null default '' comment '流量持久化规则';
alter table mp_sceneplugincollection_scene add column flow_persistence_rule text not null default '' comment '流量持久化规则';


CREATE TABLE `arkime_flow_persistence_task`
(
    `pk`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64) NOT NULL COMMENT 'ID',
    `scene`            varchar(64) NOT NULL COMMENT '场景实例id',
    `arkime_ip`         varchar(64) NOT NULL COMMENT 'arkime ip',
    `valid`            tinyint(1) NOT NULL DEFAULT 0 COMMENT '任务是否有效，如果场景实例关闭，任务失效',
    `latest_execute_message` text NOT NULL COMMENT '最近的任务执行信息',
    `flow_persistence_rule` text NOT NULL DEFAULT '' COMMENT '流量持久化规则',
    `create_time`      datetime(6) NOT NULL COMMENT '创建时间',
    `update_time`      datetime(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE KEY (`id`)
) COMMENT ='流量留存任务记录表';


