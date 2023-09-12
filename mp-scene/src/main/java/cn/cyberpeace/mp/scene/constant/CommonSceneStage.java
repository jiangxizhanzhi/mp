package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;

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
