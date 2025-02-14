alter table mp_devops_load_scene_result_log
    add command varchar(255) null comment '命令';

alter table mp_auth_cloud_user
    add expire_time datetime null comment '过期时间';