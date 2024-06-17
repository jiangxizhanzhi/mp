package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneStage;

public class CollectionSceneStage extends SceneStage {

    public static CollectionSceneStage CREATE_FLOW_COLLECT = new CollectionSceneStage("create_flow_collect", "tanzhenguanli");
    public static CollectionSceneStage CREATE_DATA_COLLECT = new CollectionSceneStage("create_data_collect", "zhongduanshebei");
    public static CollectionSceneStage CREATE_COLLECT_NODE_INSTANCE = new CollectionSceneStage("create_collect_node_instance", "tanzhenguanli");

    private CollectionSceneStage(String stage, String icon) {
        super(stage, icon);
    }

    @Override
    public String getStageName() {
        return MessageCommon.get("sceneplugincollection.stage." + this.getStage());
    }
}