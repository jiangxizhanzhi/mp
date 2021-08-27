package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum SceneErrorCode implements ErrorCode {
    SCENE_NOT_EXIST("SCENE-0001", "scene_not_exist"),

    ENTER_EVENT_FAILED("SCENE-0002", "enter_event_failed");

    SceneErrorCode(String code, String message) {
        setCode(code, "scene.error_code." + message);
    }
}
