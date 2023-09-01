package cn.cyberpeace.mp.scenepluginflow.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

public class FlowSceneStage extends SceneStage {

    public static FlowSceneStage CREATE_FLOW_SIMULATE = new FlowSceneStage("create_flow_simulate");

    private FlowSceneStage(String stage) {
        super(stage);
    }

    @Override
    public String getStageName() {
        return MessageCommon.get("scenepluginflow.stage." + this.getStage());
    }
}