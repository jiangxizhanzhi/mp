# 租户表
CREATE TABLE mp_auth_tenant
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `identifier`  varchar(64) NOT NULL COMMENT '编号',
    `name`        varchar(64) NOT NULL COMMENT '名称',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`)
) comment '租户表';

# 组织表
CREATE TABLE mp_auth_org
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '所属租户' default '',
    `parent`      varchar(64)   NOT NULL COMMENT '父组织'  default '',
    `identifier`  varchar(64)   NOT NULL COMMENT '编号',
    `name`        varchar(64)   NOT NULL COMMENT '名称',
    `path`        varchar(180)  NOT NULL COMMENT '组织结构路径',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `identifier`),
    UNIQUE (`path`)
) comment '组织表';

# 角色表
CREATE TABLE mp_auth_role
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '所属租户' default '',
    `org`         varchar(64)   NOT NULL COMMENT '所属组织' default '',
    `identifier`  varchar(64)   NOT NULL COMMENT '编号',
    `name`        varchar(64)   NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    `create_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `identifier`),
    UNIQUE (`tenant`, `name`)
) comment '角色表';

# 用户表
CREATE TABLE mp_auth_user
(
    `pk`           int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(64)   NOT NULL COMMENT 'id',
    `tenant`       varchar(64)   NOT NULL COMMENT '原生租户' default '',
    `identifier`   varchar(64)   NULL COMMENT '编号',
    `username`     varchar(64)   NOT NULL COMMENT '用户名',
    `password`     varchar(128)  NOT NULL COMMENT '密码',
    `token_secret` varchar(128)  NOT NULL COMMENT 'token密钥',
    `name`         varchar(128)  NOT NULL COMMENT '姓名',
    `nickname`     varchar(128)  NOT NULL COMMENT '昵称',
    `avatar`       varchar(2048) NOT NULL COMMENT '头像',
    `birthday`     date          NOT NULL COMMENT '生日',
    `email`        varchar(255)  NOT NULL COMMENT '邮箱',
    `mobile`       varchar(64)   NOT NULL COMMENT '手机号',
    `id_card`      varchar(64)   NOT NULL COMMENT '身份证号',
    `address`      varchar(255)  NOT NULL COMMENT '地址',
    `is_enabled`   int           NOT NULL COMMENT '1:启用 0：禁用',
    `is_audited`   int           NOT NULL COMMENT '1:已审核 0：待审核',
    `init_check`   int           NOT NULL                DEFAULT 0 COMMENT '是否首次登陆需要强制修改密码 0:否，1:是',
    `description`  varchar(1024) NULL COMMENT '描述',
    `date_joined`  datetime      NULL COMMENT '加入时间',
    `expired_time` datetime      NOT NULL COMMENT '过期时间',
    `last_login`   datetime      NULL COMMENT '最后登录时间',
    `res`          varchar(255)  NULL COMMENT '保留字段',
    `create_time`  datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime      NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`identifier`),
    UNIQUE (`username`),
    KEY (`update_time`)
) comment '用户表';

# 租户用户
CREATE TABLE mp_auth_tenant_user
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `org`         varchar(64) NOT NULL COMMENT '所属组织',
    `user`        varchar(64) NOT NULL COMMENT '用户',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `user`)
) comment '租户用户表';



# 用户在线报告统计表
CREATE TABLE mp_auth_user_online_report
(
    `pk`                 int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`                 varchar(64)  NOT NULL COMMENT 'id',
    `user`               varchar(64)  NOT NULL COMMENT '用户id',
    `tenant`             varchar(64)  NOT NULL COMMENT '所属租户',
    `username`           varchar(64)  NOT NULL COMMENT '用户名',
    `name`               varchar(64)  NOT NULL COMMENT '用户姓名',
    `org`                varchar(200) NOT NULL COMMENT '组织',
    `action`             varchar(64)  NOT NULL COMMENT '动作',
    `subject`            varchar(64)  NOT NULL COMMENT '对象',
    `total_online_hours` float        NOT NULL DEFAULT 0 COMMENT '累计在线时长',
    `monitor`            varchar(64)  NOT NULL COMMENT '监控',
    `is_online`          int                   DEFAULT 0 COMMENT '是否在线 0:不在线 1:在线',
    `create_time`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`user`)
) comment '用户在线报告统计表';
create index idx_user_olrpt_uname on mp_auth_user_online_report (username);


# 用户在线记录表
CREATE TABLE mp_auth_user_online_record
(
    `pk`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`               varchar(64) NOT NULL COMMENT 'id',
    `user`             varchar(64) NOT NULL COMMENT '用户id',
    `tenant`           varchar(64) NOT NULL COMMENT '所属租户',
    `username`         varchar(64) NOT NULL COMMENT '用户名',
    `name`             varchar(64) NOT NULL COMMENT '用户姓名',
    `start_time`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time`         datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
    `duration_minutes` int         NOT NULL DEFAULT 0 COMMENT '本轮累计在线分钟数',
    `status`           int         NOT NULL DEFAULT 0 COMMENT '记录完成与否 0:未完成 1:已完成',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '用户在线记录表';
create index idx_olrec_user on mp_auth_user_online_record (user);
create index idx_olrec_uname on mp_auth_user_online_record (username);
create index idx_olrec_endtime_stat on mp_auth_user_online_record (end_time, status);
create index idx_olrec_startend_stat on mp_auth_user_online_record (start_time, end_time, status);


# 用户角色表
CREATE TABLE mp_auth_user_role
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `user`        varchar(64) NOT NULL COMMENT '用户',
    `role`        varchar(64) NOT NULL COMMENT '角色',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `user`, `role`)
) comment '用户角色表';

# 资源目标表(有哪些资源)
CREATE TABLE mp_auth_target
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'id',
    `parent`      varchar(64)  NULL COMMENT '父权限ID',
    `identifier`  varchar(64)  NOT NULL COMMENT '编号',
    `tenant`      varchar(64)  NOT NULL COMMENT '所属租户',
    `name`        varchar(64)  NOT NULL COMMENT '名称',
    `path`        varchar(255) NULL COMMENT '路径',
    `type`        int          NOT NULL COMMENT '资源类型 1:菜单, 2:API, 3:数据',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`identifier`)
) comment '权限（资源）表';

# 组织资源权限表
CREATE TABLE mp_auth_org_permission
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(组织)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
) comment '组织权限表';

# 角色资源权限表
CREATE TABLE mp_auth_role_permission
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(角色)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
) comment '角色权限表';

# 用户资源权限表
CREATE TABLE mp_auth_user_permission
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户',
    `owner`       varchar(64) NOT NULL COMMENT '权限所有者(用户)',
    `target`      varchar(64) NOT NULL COMMENT '资源目标',
    `can_read`    tinyint(1)  NOT NULL COMMENT '读权限',
    `can_create`  tinyint(1)  NOT NULL COMMENT '创建权限',
    `can_update`  tinyint(1)  NOT NULL COMMENT '更新权限',
    `can_delete`  tinyint(1)  NOT NULL COMMENT '删除权限',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `owner`, `target`)
) comment '用户权限表';


# 组织-用户关系表
CREATE TABLE mp_auth_org_user
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '所属租户' default '',
    `org`         varchar(64) NOT NULL COMMENT '组织id',
    `user`        varchar(64) NOT NULL COMMENT '账号id',
    `create_time` datetime    NOT NULL                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) comment '组织用户表';


create table mp_auth_client_details
(
    `pk`                    int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    client_id               VARCHAR(128) comment '客户端ID',
    resource_ids            VARCHAR(256) comment '资源id',
    client_secret           VARCHAR(256) comment '客户端秘钥',
    type                    int     not null default 1 comment '类型 1:Oauth2渠道 2:免认证渠道',
    scope                   VARCHAR(256) comment '权限范围',
    authorized_grant_types  VARCHAR(256) comment '授权类型',
    web_server_redirect_uri VARCHAR(256) comment '回跳地址',
    authorities             VARCHAR(256) comment '权限',
    access_token_validity   INTEGER comment 'access_token的有效期（秒）',
    refresh_token_validity  INTEGER comment 'refresh_token的有效期（秒）',
    additional_information  VARCHAR(4096) comment '预留字段',
    autoapprove             VARCHAR(256) comment '设置用户是否自动approve操作',
    PRIMARY KEY (`pk`),
    UNIQUE (`client_id`)
) comment '客户端认证配置表';


# 用户默认头像
CREATE TABLE mp_auth_avatar
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `url`         varchar(1024) NULL COMMENT '头像url',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`)
) COMMENT '头像表';


# 系统配置信息表
CREATE TABLE mp_sys_config
(
    `pk`          int(11)       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)   NOT NULL COMMENT 'id',
    `tenant`      varchar(64)   NOT NULL COMMENT '租户',
    `key_name`    varchar(100)  NULL COMMENT '键',
    `value`       varchar(8000) NULL COMMENT '值',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    UNIQUE (`tenant`, `key_name`)
) COMMENT '系统配置信息表';


# 系统公告
CREATE TABLE mp_sys_notice
(
    `pk`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64)  NOT NULL COMMENT 'id',
    `tenant`      varchar(64)  NOT NULL COMMENT '租户',
    `content`     text         NULL COMMENT '公告内容',
    `title`       varchar(150) NOT NULL COMMENT '标题',
    `publisher`   varchar(64)  NOT NULL COMMENT '发布人',
    `is_public`   int          NOT NULL default 0 COMMENT '是否公开，对所有人可见  0:否 1:是 默认是0',
    `type`        int          NOT NULL default 1 COMMENT '公告类型 1:系统、2:课程、3:练习、4:考试',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`title`)
) COMMENT '系统公告';


# 系统公告-组织表
CREATE TABLE mp_sys_notice_org
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '租户',
    `notice_id`   varchar(64) NOT NULL COMMENT '公告ID',
    `org`         varchar(64) NULL COMMENT '组织id',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`org`, `notice_id`)
) COMMENT '系统公告-组织表';


# 系统公告-角色表
CREATE TABLE mp_sys_notice_role
(
    `pk`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`          varchar(64) NOT NULL COMMENT 'id',
    `tenant`      varchar(64) NOT NULL COMMENT '租户',
    `notice_id`   varchar(64) NOT NULL COMMENT '公告ID',
    `role`        varchar(64) NULL COMMENT '角色id',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`role`, `notice_id`)
) COMMENT '系统公告-角色表';


# 系统公告消息(已读/推送)统计表
CREATE TABLE mp_sys_notice_read
(
    `pk`           int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `id`           varchar(64) NOT NULL COMMENT 'id',
    `tenant`       varchar(64) NOT NULL COMMENT '租户',
    `notice_id`    varchar(64) NOT NULL COMMENT '公告ID',
    `user`         varchar(64) NULL COMMENT '读者',
    `is_readed`    int         NOT NULL DEFAULT 1 COMMENT '是否已读 1:已读 0:未读  首次创建一条数据即为已读',
    `is_need_push` int         NOT NULL DEFAULT 1 COMMENT '是否推送本条公告 1:是（需要推送）； 0:否（不再推送）默认为1',
    `read_count`   int         NOT NULL DEFAULT 1 COMMENT '已读次数 首次创建一条数据即为已读次数为1',
    `create_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk`),
    UNIQUE (`id`),
    KEY (`notice_id`),
    KEY (`user`, `notice_id`)
) COMMENT '系统公告消息(已读/推送)统计表';
