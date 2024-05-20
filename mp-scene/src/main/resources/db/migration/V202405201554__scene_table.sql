alter table mp_sceneplugincloud_scene_config_element
    add publisher varchar(64) null comment '发布者信息 Azuer镜像信息（1/3）';

alter table mp_sceneplugincloud_scene_config_element
    add offer varchar(64) null comment '产品报价 Azuer镜像信息（2/3）';

alter table mp_sceneplugincloud_scene_config_element
    add sku varchar(64) null comment '产品SKU（Stock Keeping Unit）编码 Azuer镜像信息（3/3）';

alter table mp_sceneplugincloud_scene_element
    add publisher varchar(64) null comment '发布者信息 Azuer镜像信息（1/3）';

alter table mp_sceneplugincloud_scene_element
    add offer varchar(64) null comment '产品报价 Azuer镜像信息（2/3）';

alter table mp_sceneplugincloud_scene_element
    add sku varchar(64) null comment '产品SKU（Stock Keeping Unit）编码 Azuer镜像信息（3/3）';

alter table mp_sceneplugincloud_scene_config_element
    add load_resource_instance_id varchar(64) null comment '云平台虚拟化资源实例id（载荷实例ID）';

alter table mp_sceneplugincloud_scene_element
    add load_resource_instance_id varchar(64) null comment '云平台虚拟化资源实例id（载荷实例ID）';

alter table mp_sceneplugincloud_scene_config_element
    add load_task_name varchar(500) null comment '载荷任务名称';

alter table mp_sceneplugincloud_scene_config_element
    add load_task_describe varchar(500) null comment '载荷任务描述';

alter table mp_sceneplugincloud_scene_config_element
    add load_exec_command varchar(64) null comment '载荷访问工具控制页面执行的命令，vmware平台不需要';

alter table mp_sceneplugincloud_scene_config_element
    add load_tool_id varchar(64) null comment '载荷工具id';

alter table mp_sceneplugincloud_scene_element
    add load_task_name varchar(500) null comment '载荷任务名称';

alter table mp_sceneplugincloud_scene_element
    add load_task_describe varchar(500) null comment '载荷任务描述';

alter table mp_sceneplugincloud_scene_element
    add load_exec_command varchar(64) null comment '载荷访问工具控制页面执行的命令，vmware平台不需要';

alter table mp_sceneplugincloud_scene_element
    add load_tool_id varchar(64) null comment '载荷工具id';

alter table mp_sceneplugincloud_scene_config_element
    add vm_type varchar(32) null comment '虚拟机类型（linux/windows）';

alter table mp_sceneplugincloud_scene_element
    add vm_type varchar(32) null comment '虚拟机类型（linux/windows）';

alter table mp_sceneplugincloud_scene_config_element
    add vm_size_type varchar(32) null comment '虚拟机大小';

alter table mp_sceneplugincloud_scene_element
    add vm_size_type varchar(32) null comment '虚拟机大小';

alter table mp_scene_scene
    add cloud_type int default 0 not null comment '0:赛宁云;1:公有云;2:私有云;3:混合云';
alter table mp_scene_scene
    add scene_create_range_tag varchar(50) null comment '当前拓扑创建的靶场标识';
alter table mp_scene_scene
    add multi_scene_type int null comment '当前拓扑的多靶场类型0-普通靶场1-多靶场';
alter table mp.mp_sceneplugincloud_scene_element_network
    modify resource_id varchar (500) not null comment '虚拟网络资源id';
alter table mp.mp_sceneplugincollection_scene_probe_router_port
    modify network_id varchar (255) not null comment '虚拟网络资源id';
alter table mp.mp_sceneplugincloud_scene_element
    modify resource_id varchar (500) not null comment '云平台虚拟化资源id';

create table mp_azure_vm_size
(
    pk              int auto_increment comment '主键'
        primary key,
    id              varchar(64)                not null comment 'ID',
    name            varchar(32)     default '' not null comment '名称',
    vcpu            int(2) unsigned default 0  not null comment 'cpu',
    ram             int(3) unsigned default 0  not null comment '内存',
    max_data_disks  int(2) unsigned default 0  not null comment '最多可用数据盘数',
    max_nics        int(2) unsigned default 0  not null comment '最多可用网卡数',
    cost_month      varchar(32)     default '' not null comment '成本/月',
    create_user     varchar(64)                not null comment '创建用户id',
    create_username varchar(64)                not null comment '创建用户名称',
    deleted         tinyint(1)                 not null comment '是否删除',
    create_time     datetime(6)                not null comment '创建时间',
    update_time     datetime(6)                not null comment '更新时间',
    constraint id
        unique (id)
)
    comment '微软云虚拟机大小列表';

alter table mp_sceneplugincloud_scene_config_element
    add vuln_code varchar(255) null comment '漏洞code';

alter table mp_sceneplugincloud_scene_element
    add vuln_code varchar(255) null comment '漏洞code';
