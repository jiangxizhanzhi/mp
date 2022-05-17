package cn.cyberpeace.mp.flow.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum FlowErrorCode implements ErrorCode {

    FLOW_NOT_EXIST("MP-110300", "flow_not_exist"),
    TASK_DELETE_FAILED("MP-110301", "task_delete_failed"),
    TITLE_UNIQUE("MP-110302", "title_unique"),
    NOT_ATTACK_FLOW("MP-110303", "not_attack_flow"),
    IDS_CAN_NOT_EMPTY("MP-110304", "ids_can_not_empty"),
    NOT_BACKGROUND_FLOW("MP-110305", "not_background_flow"),
    COLLECTION_NOT_EXIST("MP-110306", "collection_not_exist"),
    NOT_PCAP_FILE("MP-110307", "not_pcap_file"),
    UPDATE_TASK_ERROR("MP-110308", "update_task_error"),
    ACTUATOR_TASK_ERROR("MP-110309", "actuator_task_error"),
    TASK_NOT_EXIST("MP-110310", "task_not_exist"),
    ADD_TASK_ERROR("MP-110311", "add_task_error"),
    ADD_ATTACK_FLOW_ERROR("MP-110312", "add_attack_flow_error"),
    NO_TASK_TO_DELETE("MP-110313", "no_task_to_delete"),
    NO_ACTUATOR_TO_USE("MP-110314", "no_actuator_to_use"),
    MODIFY_ATTACK_FLOW_ERROR("MP-110315", "modify_attack_flow_error"),
    FLOW_IN_TASK_NOT_EXIST("MP-110316", "fow_in_task_not_exist"),
    TASK_IS_RUNNING("MP-110317", "task_is_running"),
    NO_RUNNING_TASK_TO_STOP("MP-110318", "no_running_task_to_stop"),
    GET_TASK_STATUS_ERROR("MP-110319", "get_task_status_error"),
    ADD_FLOW_EXECUTOR_ERROR("MP-110320", "add_flow_executor_error"),
    GET_OUTER_NETWORK_ERROR("MP-110321", "get_outer_network_error"),
    FLOW_FILE_UPLOAD_ERROR("MP-110322", "flow_file_upload_error"),
    ADD_PORT_ERROR("MP-110323", "add_port_error"),
    RECREATE_CUSTOM_FLOW_ERROR("MP-110324", "recreate_custom_flow");


    FlowErrorCode(String code, String message) {
        setCode(code, "flow.error_code." + message);
    }
}