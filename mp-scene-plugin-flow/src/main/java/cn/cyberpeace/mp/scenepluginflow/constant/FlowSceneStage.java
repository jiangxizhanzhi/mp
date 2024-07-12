 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowSceneStage.java.
 // File size 720.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

public class FlowSceneStage extends SceneStage {

    public static FlowSceneStage CREATE_FLOW_SIMULATE = new FlowSceneStage("create_flow_simulate", "liuliangfasheng");

    /**
     * FlowSceneStage
     *
     * @param stage
     * @param icon
     */
    private FlowSceneStage(String stage, String icon) {
        super(stage, icon);
    }

    /**
     * getStageName
     *
     * @return
     */
    @Override
    public String getStageName() {
        return MessageCommon.get("scenepluginflow.stage." + this.getStage());
    }
}