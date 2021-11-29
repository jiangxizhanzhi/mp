package cn.cyberpeace.mp.cscene.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CsceneErrorCode implements ErrorCode {
    SCENE_NOT_EXIST("MP-110200", "scene_not_exist"),
    SCENE_EDIT_NODE_LIMIT("MP-110201", "scene_edit_node_limit"),
    SCENE_USING_NODE_LIMIT("MP-110202", "scene_using_node_limit");

    CsceneErrorCode(String code, String message) {
        setCode(code, "cscene.error_code." + message);
    }
}
