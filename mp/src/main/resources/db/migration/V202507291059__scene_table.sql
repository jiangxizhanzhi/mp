-- 将现有的 NULL 值更新为空字符串
UPDATE mp_sceneplugincollection_scene
SET flow_collect_nodes = ''
WHERE flow_collect_nodes IS NULL;
ALTER TABLE mp_sceneplugincollection_scene
    MODIFY COLUMN flow_collect_nodes TEXT NOT NULL DEFAULT '' COMMENT '用户选择的流量采集节点';