-- v5.3.6
-- 表 mp_auth_url_ac 新增一列 group
ALTER TABLE `mp_auth_url_ac`
    ADD COLUMN `group` varchar(64) NOT NULL COMMENT '权限组' AFTER `is_pattern`;

-- 新增列原有数据的值设置为对应行的id
UPDATE mp_auth_url_ac ua SET ua.group=ua.id where pk > 0;

-- 表 mp_auth_url_ac_target 列 urlac_id 改为 group
ALTER TABLE mp_auth_url_ac_target change urlac_id `group` varchar (64) NOT NULL COMMENT '权限组';