alter table mp_devops_load_scene_result_log
    add judgment_status bigint(2) null comment '研判研判结果(0:未研判 1:成功 2:失败) ' after result_type;