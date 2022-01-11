package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.model.base.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseSceneDataBO<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        P1 extends BaseScenePieceModel,
        S extends BaseSceneBO<S0, S>,
        A extends BaseSceneAreaBO<A0, A>,
        P extends BaseSceneElementPortBO<P0, P, E>,
        E extends BaseSceneElementBO<E0, P, E>,
        C extends BaseSceneElementPortConnectionBO<C0, C>,
        P2 extends BaseScenePieceBO<P1, P2>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, P1, S, A, P, E, C, P2, T>, T1> {

    @ApiModelProperty(value = "拓扑数据")
    @Valid
    private T topology;

    /**
     * pluginId: pluginData
     */
    @ApiModelProperty(value = "插件数据(字典: key-插件代号, value-插件数据[不同插件代号对应的数据模型不一样]), "
            + "cpcloud(云平台): CloudPluginSceneConfigDataBO/CloudPluginSceneDataBO, "
            + "cpcollection(采集): CollectionPluginSceneConfigDataBO/CollectionPluginSceneDataBO, "
            + "cpflow(流量): FlowPluginSceneConfigDataBO/FlowPluginSceneDataBO")
    private Map<String, ScenePluginDataBO> plugins;

    public T1 export() {
        if (topology != null) {
            setTopology(topology.export());
        }
        if (plugins != null) {
            plugins.keySet().forEach(pluginKey -> plugins.put(pluginKey, plugins.get(pluginKey).export()));
        }

        return (T1) this;
    }

    public <S9 extends BaseSceneBO<?, S9>,
            A9 extends BaseSceneAreaBO<?, A9>,
            P9 extends BaseSceneElementPortBO<?, P9, E9>,
            E9 extends BaseSceneElementBO<?, P9, E9>,
            C9 extends BaseSceneElementPortConnectionBO<?, C9>,
            P7 extends BaseScenePieceBO<?, P7>,
            T9 extends BaseSceneTopologyBO<?, ?, ?, ?, ?, ?, S9, A9, P9, E9, C9, P7, T9>,
            P8 extends BaseSceneDataBO<?, ?, ?, ?, ?, ?, S9, A9, P9, E9, C9, P7, T9, P8>> P8 export(
                    Class<P8> pClass, Class<T9> toplogyClass, Class<S9> sceneClass, Class<A9> areaClass,
                    Class<E9> elementClass, Class<P9> portClass, Class<C9> connectionClass, Class<P7> pieceClass) {
        var pObj = ClassUtils.createInstance(pClass);
        if (topology != null) {
            pObj.setTopology(topology.export(toplogyClass, sceneClass, areaClass, elementClass, portClass,
                    connectionClass, pieceClass));
        }

        if (plugins != null) {
            Map<String, ScenePluginDataBO> exportedPlugins = new HashMap<>();
            plugins.forEach((pluginKey, pluginData) -> {
                exportedPlugins.put(pluginKey, pluginData.export());
            });
            pObj.setPlugins(exportedPlugins);
        }

        return pObj;
    }
}
