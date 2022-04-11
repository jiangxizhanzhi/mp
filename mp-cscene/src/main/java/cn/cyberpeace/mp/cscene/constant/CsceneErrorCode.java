package cn.cyberpeace.mp.cscene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CsceneErrorCode implements ErrorCode {
    SCENE_NOT_EXIST("MP-110200", "scene_not_exist"),
    SCENE_EDIT_NODE_LIMIT("MP-110201", "scene_edit_node_limit"),
    SCENE_USING_NODE_LIMIT("MP-110202", "scene_using_node_limit"),
    ENTITY_HAS_BEEN_USED("MP-110203", "entity_has_been_used"),




    // validation-api params
    PARAMS_SHOULD_NOT_NULL("MP-110291", "params_should_not_null"),
    PARAMS_SHOULD_NOT_EMPTY("MP-110292", "params_should_not_empty"),
    PARAMS_FLOWS_DATA_TOO_LONG("MP-110293", "params_flows_data_too_long");


    CsceneErrorCode(String code, String message) {
        setCode(code, "cscene.error_code." + message);
    }
}
