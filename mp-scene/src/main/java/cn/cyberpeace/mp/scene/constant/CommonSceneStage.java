package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;

public class CommonSceneStage extends SceneStage {

    public static CommonSceneStage INIT = new CommonSceneStage("init");

    private CommonSceneStage(String stage) {
        super(stage);
    }

    @Override
    public String getStageName() {
        return MessageCommon.get("scene.base." + this.getStage());
    }
}
