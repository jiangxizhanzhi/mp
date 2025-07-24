alter table mp_sceneplugincollection_scene_config
    add column flow_collect_nodes text comment '用户选择的流量采集节点';

alter table mp_sceneplugincollection_scene
    add column flow_collect_nodes text comment '用户选择的流量采集节点';