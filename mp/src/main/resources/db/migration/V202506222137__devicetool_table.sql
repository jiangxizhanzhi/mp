create table mp_devops_tool
(
    pk                   int auto_increment comment '主键'
        primary key,
    id            VARCHAR(64)   NOT NULL COMMENT '靶标id',
    device_uuid            VARCHAR(64)   NOT NULL COMMENT '靶标id',
    tool_name     VARCHAR(128)  NOT NULL COMMENT '工具名称',
    tool_params   VARCHAR(4096) NOT NULL COMMENT '工具参数',
    attack_method VARCHAR(128) NOT NULL COMMENT '攻击方式',
    return_type   VARCHAR(128)  NOT NULL COMMENT '攻击返回类型',
    create_time          datetime(6)          not null comment '创建时间',
    update_time          datetime(6)          not null comment '更新时间',
    constraint id
        unique (id)
);