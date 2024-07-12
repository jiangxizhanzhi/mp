 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudSceneStage.java.
 // File size 1155.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

/**
 * @ClassName CloudSceneStage
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SceneStage
 */
public class CloudSceneStage extends SceneStage {

    public static CloudSceneStage CREATE_NETWORK = new CloudSceneStage("create_network", "wangluoyu");
    public static CloudSceneStage CREATE_GATEWAY = new CloudSceneStage("create_gateway", "wangluoshebei");
    public static CloudSceneStage CREATE_INSTANCE = new CloudSceneStage("create_instance", "changjingmoban");
    public static CloudSceneStage CONFIG = new CloudSceneStage("config", "renwufangan");

    /**
     * CloudSceneStage
     *
     * @param stage
     * @param icon
     */
    private CloudSceneStage(String stage, String icon) {
        super(stage, icon);
    }

    /**
     * getStageName
     *
     * @return
     */
    @Override
    public String getStageName() {
        return MessageCommon.get("sceneplugincloud.stage." + this.getStage());
    }
}