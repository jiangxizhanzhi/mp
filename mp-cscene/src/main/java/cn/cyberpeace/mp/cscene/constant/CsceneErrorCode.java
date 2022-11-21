package cn.cyberpeace.mp.cscene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CsceneErrorCode implements ErrorCode {
    SCENE_NOT_EXIST("MP-110200", "scene_not_exist"),
    SCENE_EDIT_NODE_LIMIT("MP-110201", "scene_edit_node_limit"),
    SCENE_USING_NODE_LIMIT("MP-110202", "scene_using_node_limit"),
    ENTITY_HAS_BEEN_USED("MP-110203", "entity_has_been_used"),

    SNAPSHOT_IN_USE("MP-110204", "snapshot_in_use"),
    SNAPSHOT_NAME_TOO_LONG("MP-110205", "snapshot_name_too_long"),
    SNAPSHOT_DESC_TOO_LONG("MP-110206", "snapshot_desc_too_long"),
    SNAPSHOT_NAME_DUPLICATED("MP-110207", "snapshot_name_duplicated"),
    SNAPSHOT_NOT_CURRENT_SCENE("MP-110208", "snapshot_not_current_scene"),

    VLANID_HAS_BEEN_USED("MP-110209", "vlan_id_has_been_used"),



    // validation-api params
    PARAMS_SHOULD_NOT_NULL("MP-110291", "params_should_not_null"),
    PARAMS_SHOULD_NOT_EMPTY("MP-110292", "params_should_not_empty"),
    PARAMS_FLOWS_DATA_TOO_LONG("MP-110293", "params_flows_data_too_long"),
    PARAMS_FIRE_RULES_OUT_OF_LIMIT("MP-110294", "params_fire_rules_out_of_limit"),
    PARAMS_NAME_TOO_LONG("MP-110295", "params_name_too_long"),
    PARAMS_DESCRIPTION_TOO_LONG("MP-110296", "params_description_too_long");

    CsceneErrorCode(String code, String message) {
        setCode(code, "cscene.error_code." + message);
    }
}
