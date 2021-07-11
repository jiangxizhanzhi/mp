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
    VIRTUAL_USB_OUT_OF_RANGE("MP-110210", "virtual_usb_out_of_range"),
    SAME_USB_HAS_USED("MP-110211", "same_usb_has_used"),
    ENTITY_USB_HAS_USD("MP-110212", "entity_usb_has_used"),
    ENTITY_USB_HOST_NOT_SAME("MP-110213", "entity_usb_host_not_same"),
    SCENE_NOT_STARTED("MP-110214", "scene_not_started"),
    USB_USED_SCENE("MP-110215", "usb_used_scene"),

    NODEIP_NOT_EXIST("MP-110210", "nodeip_not_exist"),
    NETWORKID_NOT_EXIST("MP-110211", "networkid_not_exist"),
    FLOW_NOT_EXIST("MP-110212", "flow_not_exist"),

    
    PARAMS_SHOULD_NOT_NULL("MP-110281", "params_should_not_null"),
    PARAMS_SHOULD_NOT_EMPTY("MP-110282", "params_should_not_empty"),
    PARAMS_FLOWS_DATA_TOO_LONG("MP-110283", "params_flows_data_too_long"),
    PARAMS_FIRE_RULES_OUT_OF_LIMIT("MP-110284", "params_fire_rules_out_of_limit"),
    PARAMS_NAME_TOO_LONG("MP-110285", "params_name_too_long"),
    PARAMS_DESCRIPTION_TOO_LONG("MP-110286", "params_description_too_long"),
    PARAMS_SAME_CIDR_FOR_COLLECTION("MP-110287", "params_same_cidr_for_collection"),

    SCENE_INSTANCE_IS_CLEARING("MP-110291", "scene_instance_is_clearing"),
    SCENE_INSTANCE_NEED_CLEAR("MP-110292", "scene_instance_need_clear"),
    SCENE_INSTANCE_STILL_NEED_CLEAR("MP-110293", "scene_instance_still_need_clear"),
    PARAMS_FIELD_ERROR("MP-110300", "params_field_error");

    CsceneErrorCode(String code, String message) {
        setCode(code, "cscene.error_code." + message);
    }
}
