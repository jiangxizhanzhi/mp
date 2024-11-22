alter table mp_devops_target add column `ssh_config` varchar(255) COMMENT 'ssh配置' ;
alter table mp_devops_target add column `telnet_config` varchar(255) COMMENT 'telnet配置' ;
alter table mp_devops_target add column `syslog_config` varchar(255) COMMENT 'syslog配置' ;