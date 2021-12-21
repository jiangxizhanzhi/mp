package cn.cyberpeace.mp.devops.cluster.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum DevopsClusterErrorCode implements ErrorCode {
    EXPORT_IMAGE_ERROR("MP-110601", "export_image_error"),
    IMAGE_NAME_EXCEED_MAX_LENGTH("MP-110602", "image_name_exceed_max_length"),
    IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH("MP-110603", "image_description_exceed_max_length"),
    IMAGE_REFERENCED_SCENE("MP-110604", "image_referenced_scene"),
    IMAGE_USED_SCENE("MP-110605", "image_used_scene");

    DevopsClusterErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
