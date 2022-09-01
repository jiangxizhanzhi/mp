-- 去掉create_time和update_time中的DEFAULT CURRENT_TIMESTAMP
ALTER TABLE mp_situation_flow_port_protocol modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_flow_port_protocol modify update_time datetime NULL COMMENT '更新时间';

ALTER TABLE mp_situation_flow_port_protocol modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_flow_port_protocol modify update_time datetime NULL COMMENT '更新时间';

ALTER TABLE mp_situation_probe modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_probe modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_situation_probe_ability modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_probe_ability modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_situation_probe_script modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_probe_script modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_situation_probe_param modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_probe_param modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_situation_collection_template modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_collection_template modify update_time datetime NOT NULL COMMENT '更新时间';

ALTER TABLE mp_situation_collection_template_probe modify create_time datetime NOT NULL COMMENT '创建时间';
ALTER TABLE mp_situation_collection_template_probe modify update_time datetime NOT NULL COMMENT '更新时间';


