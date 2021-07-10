ALTER TABLE mp_sys_config ADD init_value VARCHAR(8000) DEFAULT '' NOT NULL COMMENT '初始值' AFTER value;
