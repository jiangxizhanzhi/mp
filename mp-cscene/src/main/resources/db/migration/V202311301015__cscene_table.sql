# yz4
alter table mp_devops_target add column business_tag int(11) not null default 1 comment '业务标签,1:终端防护靶标,2:路由交换靶标,3:边界安全防护设备靶标,4:流量分析靶标,5:应用服务靶标' ;