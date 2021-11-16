# 5.3 添加系统备份，场景自动清除数据间隔配置
INSERT INTO `mp_sys_config` (`id`, `tenant`, `key_name`, `value`, `create_time`, `update_time`) VALUES ('5c54e1099f4f4e579e86304cbeda0fe0', 'default', 'sys_backup', '{\"protocol\": \"SFTP\", \"remote_server_ip\": \"\", \"remote_server_user\": \"\", \"remote_server_password\": \"\", \"backup_interval_minutes\": 60}\n', '2021-11-16 03:27:39.178391', '2021-11-16 03:35:51.743097');

INSERT INTO `mp_sys_config` (`id`, `tenant`, `key_name`, `value`, `create_time`, `update_time`) VALUES ('7efa492b90da4414b0df264876fc6886', 'default', 'scene_auto_clean_interval_day', '30', '2021-11-16 03:36:41.646178', '2021-11-16 03:36:41.646178');
