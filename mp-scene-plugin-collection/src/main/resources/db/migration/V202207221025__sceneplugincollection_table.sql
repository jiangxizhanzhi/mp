ALTER table mp_sceneplugincollection_wazuh_rule_type_relation ADD UNIQUE (`rule_id`,`type_id`);
ALTER table mp_sceneplugincollection_scene_log_probe_location ADD UNIQUE (`location`);