ALTER TABLE mp_devops_target
    ADD COLUMN application_num INT(8) NOT NULL DEFAULT 0 COMMENT '申请数量';

ALTER TABLE mp_devops_target
    ADD COLUMN is_occupy tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否被占用: 0 未被占用, 1 被占用';