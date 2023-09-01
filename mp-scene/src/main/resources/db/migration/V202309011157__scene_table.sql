#
v5.4.1

ALTER TABLE `mp_scene_scene`
    ADD COLUMN `stage` varchar(64) NOT NULL COMMENT '当前阶段',
    ADD COLUMN `stage_name` varchar(64) NOT NULL  COMMENT '当前阶段名称',
    ADD COLUMN `stage_percentage` int(32)   NOT NULL  COMMENT '当前阶段百分比',
    ADD COLUMN `stage_list` varchar(2048) NOT NULL  COMMENT '阶段列表';