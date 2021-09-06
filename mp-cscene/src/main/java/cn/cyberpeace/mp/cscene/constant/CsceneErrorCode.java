package cn.cyberpeace.mp.cscene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CsceneErrorCode implements ErrorCode {
    SCENE_NOT_EXIST("MP-110200", "scene_not_exist");

    CsceneErrorCode(String code, String message) {
        setCode(code, "cscene.error_code." + message);
    }
}
