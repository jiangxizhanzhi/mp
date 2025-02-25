# v5.3.8
alter table mp_devops_target add column `compute_node` varchar(64) NULL COMMENT '计算节点' ;
alter table mp_devops_target add column `vlan_id` int(4) NULL COMMENT 'VLAN ID' ;
alter table mp_devops_target add column `cidr` varchar(16) NULL COMMENT 'cidr地址' ;