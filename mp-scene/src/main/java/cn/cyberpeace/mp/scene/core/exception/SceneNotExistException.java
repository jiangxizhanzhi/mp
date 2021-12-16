package cn.cyberpeace.mp.scene.core.exception;

import cn.cyberpeace.mp.scene.constant.SceneErrorCode;

public class SceneNotExistException extends SceneException {

    public SceneNotExistException(String message) {
        super(message);
        super.setServiceCode(SceneErrorCode.SCENE_NOT_EXIST);
    }

}
