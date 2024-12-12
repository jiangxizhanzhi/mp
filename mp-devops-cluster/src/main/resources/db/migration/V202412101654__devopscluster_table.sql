CREATE TABLE mp_version_library
(
    pk                int auto_increment comment '主键' primary key,
    id                varchar(64)  not null comment 'ID',
    name              varchar(255) NOT NULL COMMENT '名称',
    firmware_version  varchar(50)  NOT NULL COMMENT '固件版本',
    applicable_object varchar(255) COMMENT '适用对象',
    software_name     varchar(255) COMMENT '软件名',
    firmware_name     varchar(255) COMMENT '固件名',
    firmware_address  varchar(255) COMMENT '固件地址',
    type              int(1)   NOT NULL COMMENT '类型(0:软件 1:固件)',
    create_time       datetime     not null comment '创建时间',
    update_time       datetime     not null comment '更新时间'
) COMMENT ='软件/固件版本库';

SET FOREIGN_KEY_CHECKS = 1;