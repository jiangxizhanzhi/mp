alter table mp_sceneplugincloud_scene_config_element
    add delivery_node_id             varchar(64)           null comment '投递节点',
    add delivery_file_path           varchar(64)           null comment '投递文件路径';

alter table mp_sceneplugincloud_scene_element
    add delivery_node_id             varchar(64)           null comment '投递节点',
    add delivery_file_path           varchar(64)           null comment '投递文件路径';
