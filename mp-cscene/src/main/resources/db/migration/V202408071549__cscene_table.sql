ALTER TABLE mp_devops_target MODIFY COLUMN business_tag int(11) DEFAULT NULL NULL COMMENT '业务标签,1:终端防护靶标,2:路由交换靶标,3:边界安全防护设备靶标,4:流量分析靶标,5:应用服务靶标';
ALTER TABLE mp_devops_target MODIFY COLUMN place varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL NULL COMMENT '位置信息';

