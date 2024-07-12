 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonSceneStage.java.
 // File size 606.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;

/**
 * @ClassName CommonSceneStage
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SceneStage
 */
public class CommonSceneStage extends SceneStage {

    public static CommonSceneStage INIT = new CommonSceneStage("init", "xinxitongji");

    private CommonSceneStage(String stage, String icon) {
        super(stage, icon);
    }

    @Override
    public String getStageName() {
        return MessageCommon.get("scene.base." + this.getStage());
    }
}
