alter table mp_common_option add interactive_type tinyint(2) null comment '交互类型：0 背景流量，1 攻击流量，2 全流量';

update mp_common_option set interactive_type = 0 where type = 'flow_category' and code in (select category from mp_flow_flow_info where interactive_type = 0);

update mp_common_option set interactive_type = 2 where type = 'flow_category' and interactive_type = 0
                                                   and code in (select category from mp_flow_flow_info where interactive_type = 1);

update mp_common_option set interactive_type = 1 where type = 'flow_category' and interactive_type is null
                                                   and code in (select category from mp_flow_flow_info where interactive_type = 1);