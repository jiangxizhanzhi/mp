package cn.cyberpeace.mp.scriptcollectionevaluation.constant;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ScriptCollectionEvaluationConstant {
    private ScriptCollectionEvaluationConstant() {
    }

    public static final Map<Integer, Integer> ROLE_TO_TYPE = new HashMap<>() {{
        put(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK, BaseSceneElementModel.Type.OTHER);
        put(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH, BaseSceneElementModel.Type.SWITCH);
        put(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER, BaseSceneElementModel.Type.ROUTER);
        put(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL, BaseSceneElementModel.Type.FIREWALL);
        put(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL, BaseSceneElementModel.Type.TERMINAL);
        put(BaseCloudPluginSceneElementModel.Role.NPC, BaseSceneElementModel.Type.TERMINAL);
        put(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY, BaseSceneElementModel.Type.SWITCH);
    }};
}
