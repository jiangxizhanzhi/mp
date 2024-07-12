 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneNotExistException.java.
 // File size 473.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.exception;

import cn.cyberpeace.mp.scene.constant.SceneErrorCode;

/**
 * @ClassName SceneNotExistException
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SceneException
 */
public class SceneNotExistException extends SceneException {

    public SceneNotExistException(String message) {
        super(message);
        super.setServiceCode(SceneErrorCode.SCENE_NOT_EXIST);
    }

}
