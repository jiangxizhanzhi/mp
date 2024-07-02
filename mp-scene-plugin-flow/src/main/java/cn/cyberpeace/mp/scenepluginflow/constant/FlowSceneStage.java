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