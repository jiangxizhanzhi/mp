-- 载荷同步表添加字段
ALTER TABLE `mp_devops_load` ADD COLUMN `vulne_code` varchar(64) NULL COMMENT '漏洞编号';
ALTER TABLE `mp_devops_load` ADD COLUMN `script_file_path` varchar(255) NULL COMMENT '脚本文件地址';
ALTER TABLE `mp_devops_load` ADD COLUMN `script_file` varchar(255) NULL COMMENT '脚本文件名称';
ALTER TABLE `mp_devops_load` ADD COLUMN `script_type` tinyint(2) NULL COMMENT '脚本类型 0.python   1.shell';
ALTER TABLE `mp_devops_load` ADD COLUMN `script_code` varchar(255) NULL COMMENT '脚本代码';
ALTER TABLE `mp_devops_load` ADD COLUMN `technology` tinyint(2) NULL COMMENT '技战法  0.信息收集  1.漏洞利用  3.远程控制  4.权限提升';