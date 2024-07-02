package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

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