-- 去掉create_time和update_time中的DEFAULT CURRENT_TIMESTAMP
ALTER TABLE mp_sceneplugincloud_scene_element_network modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sceneplugincloud_scene_element_network modify update_time datetime NOT NULL COMMENT '更新时间';