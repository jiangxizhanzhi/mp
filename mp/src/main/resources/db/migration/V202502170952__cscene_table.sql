alter table mp_sceneplugincloud_scene add column `flow_mirrors` text  DEFAULT '' COMMENT '节点流量镜像的配置' ;
alter table mp_sceneplugincloud_scene_config add column `flow_mirrors` text DEFAULT '' COMMENT '节点流量镜像的配置' ;
