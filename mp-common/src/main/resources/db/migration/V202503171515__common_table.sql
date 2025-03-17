# v5.4.0
INSERT INTO `mp`.`mp_common_option` (`id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES
('f01f907881f14452804b4e1b0b9df451', '1', '', '操作系统', 'Operate System', '1', 'image_resource_type', '{\"activKeys\":\"no_auth\",\"visible\":1}', '2024-03-26 10:49:04', '2024-03-26 11:14:46');

INSERT INTO `mp`.`mp_common_option` (`id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES
    ('f01f907881f14452804b4e1b0b9df452', '2', '', '数据库', 'Database', '2', 'image_resource_type', '{\"activKeys\":\"no_auth\",\"visible\":1}', '2024-03-26 10:49:04', '2024-03-26 11:14:46');

INSERT INTO `mp`.`mp_common_option` (`id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES
    ('f01f907881f14452804b4e1b0b9df453', '3', '', '中间件', 'Middleware', '3', 'image_resource_type', '{\"activKeys\":\"no_auth\",\"visible\":1}', '2024-03-26 10:49:04', '2024-03-26 11:14:46');

INSERT INTO `mp`.`mp_common_option` (`id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES
    ('f01f907881f14452804b4e1b0b9df454', '4', '', '办公软件', 'Office Software', '4', 'image_resource_type', '{\"activKeys\":\"no_auth\",\"visible\":1}', '2024-03-26 10:49:04', '2024-03-26 11:14:46');

INSERT INTO `mp`.`mp_common_option` (`id`, `code`, `parent_code`, `zh_name`, `en_name`, `sort`, `type`, `meta`, `create_time`, `update_time`) VALUES
    ('f01f907881f14452804b4e1b0b9df455', '5', '', '专用业务软件', 'Dedicated Business Software', '5', 'image_resource_type', '{\"activKeys\":\"no_auth\",\"visible\":1}', '2024-03-26 10:49:04', '2024-03-26 11:14:46');

# 增加镜像资源类型字段
alter table mp_devops_target add column image_resource_type varchar(4) DEFAULT NULL COMMENT '镜像资源类型';
