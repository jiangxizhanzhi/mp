-- 选项表添加 parent_code 字段
ALTER TABLE mp_common_option ADD `parent_code` varchar(255) COMMENT '父级选项编码';