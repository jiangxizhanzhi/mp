ALTER TABLE mp_auth_user ADD `rank` varchar(200) DEFAULT '' NOT NULL COMMENT '职级' AFTER `last_login`;
