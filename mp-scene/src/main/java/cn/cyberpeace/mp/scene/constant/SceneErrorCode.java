package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum SceneErrorCode implements ErrorCode {
    INVALID_PARAM("SCENE-0001", "invalid_param");

    SceneErrorCode(String code, String message) {
        setCode(code, "scene.error_code." + message);
    }
}
