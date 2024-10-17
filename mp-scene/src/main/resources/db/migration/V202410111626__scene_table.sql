alter table mp_scene_scene_config
    add column interval_sec varchar(32) default '180' not null comment '探针执行间隔，为大于1的数值，单位秒';
alter table mp_scene_scene
    add column interval_sec varchar(32) default '180' not null comment '探针执行间隔，为大于1的数值，单位秒';

alter table mp_scene_scene_config_element
    add column node_state VARCHAR(255) DEFAULT NULL NULL comment '节点状态' ;
alter table mp_scene_scene_element
    add column node_state VARCHAR(255) DEFAULT NULL NULL comment '节点状态' ;

CREATE INDEX idx_scene_type ON mp_scene_scene_config_element(scene,type);
CREATE INDEX idx_scene_virtual_type ON mp_sceneplugincloud_scene_config_element (scene, virtual_type);
CREATE INDEX idx_id_scene_config_deleted ON mp_cscene_cscene(id, scene_config, deleted);
CREATE INDEX idx_cscene_scene_type ON mp_cscene_cscene_instance (cscene, scene, type);
CREATE INDEX idx_element_scene ON mp_scene_scene_config_element_port (scene_element, scene);
CREATE INDEX idx_scene_area ON mp_scene_scene_config_element (scene, area);