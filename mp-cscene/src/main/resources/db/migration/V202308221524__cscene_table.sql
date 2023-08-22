alter table mp_cscene_probeoperate_log
    add column collect_ips varchar(2048) not null comment '需要采集的节点ip',
    add column collect_period int not null comment '采集周期';