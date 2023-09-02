package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

public class CollectionSceneStage extends SceneStage {

    public static CollectionSceneStage CREATE_FLOW_COLLECT = new CollectionSceneStage("create_flow_collect");
    public static CollectionSceneStage CREATE_DATA_COLLECT = new CollectionSceneStage("create_data_collect");

    private CollectionSceneStage(String stage) {
        super(stage);
    }

    @Override
    public String getStageName() {
        return MessageCommon.get("sceneplugincollection.stage." + this.getStage());
    }
}