package cn.cyberpeace.mp.devops.cluster.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


/**
 * 错误码
 * @author wanggang
 */
public enum DevopsClusterErrorCode implements ErrorCode {
    EXPORT_IMAGE_ERROR("MP-110601", "export_image_error"),
    IMAGE_NAME_EXCEED_MAX_LENGTH("MP-110602", "image_name_exceed_max_length"),
    IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH("MP-110603", "image_description_exceed_max_length"),
    IMAGE_REFERENCED_SCENE("MP-110604", "image_referenced_scene"),
    IMAGE_USED_SCENE("MP-110605", "image_used_scene"),
    REQUEST_CLOUD_INTERFACE_ERROR("MP-110606", "request_cloud_interface_error"),
    CUSTOM_UPLOAD_TYPE_ERROR("MP-110607", "custom_upload_type_error"),
    SCREEN_RECORD_ERROR("MP-110606", "screen_record_error"),
    REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR("MP-110607", "request_cloud_interface_business_error"),
    IMAGE_SNAPSHOT_REFERENCED_SCENE("MP-110608", "image_snapshot_referenced_scene"),
    IMAGE_SNAPSHOT_USED_SCENE("MP-110609", "image_snapshot_used_scene"),
    IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME("MP-110610", "image_snapshot_referenced_scene_with_name");

    DevopsClusterErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
