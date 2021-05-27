package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum SceneErrorCode implements ErrorCode {
    SCENE_NOT_EXIST("SCENE-0001", "scene_not_exist");

    SceneErrorCode(String code, String message) {
        setCode(code, "scene.error_code." + message);
    }
}
