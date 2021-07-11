package cn.cyberpeace.mp.devopscluster.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;



public enum DevopsClusterErrorCode implements ErrorCode {
    EXPORT_IMAGE_ERROR("MP-110601", "export_image_error"),
    IMAGE_NAME_EXCEED_MAX_LENGTH("MP-110602", "image_name_exceed_max_length"),
    IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH("MP-110603", "image_description_exceed_max_length"),
    IMAGE_REFERENCED_SCENE("MP-110604", "image_referenced_scene"),
    IMAGE_USED_SCENE("MP-110605", "image_used_scene"),
    REQUEST_CLOUD_INTERFACE_ERROR("MP-110606", "request_cloud_interface_error"),
    CUSTOM_UPLOAD_TYPE_ERROR("MP-110607", "custom_upload_type_error"),
    IMAGE_SNAPSHOT_REFERENCED_SCENE("MP-110608", "image_snapshot_referenced_scene"),
    IMAGE_SNAPSHOT_USED_SCENE("MP-110609", "image_snapshot_used_scene"),
    IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME("MP-110610", "image_snapshot_referenced_scene_with_name"),
    CP_SERVICE_ERROR("MP-110611", "cp_service_error"),
    INIT_TARGET_CAN_NOT_DELETE("MP-110612", "init_target_can_not_delete"),
    SCREEN_RECORD_ERROR("MP-110613", "screen_record_error"),
    REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR("MP-110614", "request_cloud_interface_business_error"),
    BUILD_IN_TARGET_NOT_ALLOWED_EXPORT("mp-110615", "build_in_target_not_allowed_export"),
    ISO_REFERENCE_SCENE_WITH_NAME("mp-110616", "iso_reference_scene_with_name"),
    USB_NUM_EXCEEDS_LIMIT("MP-110617", "usb_num_exceeds_limit"),
    USB_REFERENCED_SCENE("MP-110618", "usb_referenced_scene");

    DevopsClusterErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
