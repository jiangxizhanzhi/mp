-- 节点信息增加漏洞信息记录
ALTER TABLE mp_sceneplugincloud_scene_element ADD leaks LONGVARCHAR not null default '' ;
ALTER TABLE mp_sceneplugincloud_scene_config_element ADD leaks LONGVARCHAR not null default '';
ALTER TABLE mp_devops_device add leaks LONGVARCHAR not null default '';
ALTER TABLE mp_devops_target add leaks LONGVARCHAR not null default '';