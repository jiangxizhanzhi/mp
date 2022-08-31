-- 去掉create_time和update_time中的DEFAULT LOCALTIME
ALTER TABLE mp_sceneplugincollection_wazuh_rule modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sceneplugincollection_wazuh_rule modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sceneplugincollection_wazuh_rule_type modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sceneplugincollection_wazuh_rule_type modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_sceneplugincollection_wazuh_rule_type_relation modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_sceneplugincollection_wazuh_rule_type_relation modify update_time datetime NOT NULL COMMENT '更新时间';