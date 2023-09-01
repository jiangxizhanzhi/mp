package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

public class CloudSceneStage extends SceneStage {

    public static CloudSceneStage CREATE_NETWORK = new CloudSceneStage("create_network");
    public static CloudSceneStage CREATE_GATEWAY = new CloudSceneStage("create_gateway");
    public static CloudSceneStage CREATE_INSTANCE = new CloudSceneStage("create_instance");
    public static CloudSceneStage CONFIG = new CloudSceneStage("config");

    private CloudSceneStage(String stage) {
        super(stage);
    }

    @Override
    public String getStageName() {
        return MessageCommon.get("sceneplugincloud.stage." + this.getStage());
    }
}