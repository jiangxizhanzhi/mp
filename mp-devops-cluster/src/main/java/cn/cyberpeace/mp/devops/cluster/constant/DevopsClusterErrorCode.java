package cn.cyberpeace.mp.devops.cluster.constant;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum DevopsClusterErrorCode implements ErrorCode {
    EXPORT_IMAGE_ERROR("MP-110601", "export_image_error");

    DevopsClusterErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
