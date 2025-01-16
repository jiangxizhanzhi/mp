alter table mp_devops_load_scene_result_log
    add judgment_status bigint(2) null comment '研判研判结果(0:未研判 1:成功 2:失败) ' after result_type;
alter table mp_local_load_execution_record
    add demo_status bigint(2) null comment '演示动效状态' after status;
alter table mp_devops_load_scene_result_log
    add remark varchar(255) null comment '研判结果备注' after judgment_status;
alter table mp_devops_load_scene_result_log
    add execution_record_id varchar(64) null comment '载荷执行记录id' after remark;