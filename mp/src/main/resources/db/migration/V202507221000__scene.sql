alter table mp_sceneplugincollection_scene modify column `probe_router` longtext NOT NULL COMMENT '采集路由id' ;
alter table mp_sceneplugincollection_scene_config modify column `probe_router` longtext NOT NULL COMMENT '采集路由id' ;
alter table mp_scenepluginflow_scene modify executor  longtext NOT NULL COMMENT '采集路由id' ;