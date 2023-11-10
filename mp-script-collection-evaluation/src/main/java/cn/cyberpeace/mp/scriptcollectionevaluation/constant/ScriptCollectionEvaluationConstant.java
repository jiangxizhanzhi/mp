package cn.cyberpeace.mp.scriptcollectionevaluation.constant;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Data;

import java.util.Map;

@Data
public class ScriptCollectionEvaluationConstant {
    private ScriptCollectionEvaluationConstant() {
    }

    public static final Map<Integer, Integer> ROLE_TO_TYPE = Map.of(
            BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK, BaseSceneElementModel.Type.OTHER,
            BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH, BaseSceneElementModel.Type.SWITCH,
            BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER, BaseSceneElementModel.Type.ROUTER,
            BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL, BaseSceneElementModel.Type.FIREWALL,
            BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL, BaseSceneElementModel.Type.TERMINAL,
            BaseCloudPluginSceneElementModel.Role.NPC, BaseSceneElementModel.Type.TERMINAL,
            BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY, BaseSceneElementModel.Type.SWITCH
    );
}
