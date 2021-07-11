package cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler;

import cn.cyberpeace.mp.base.bo.DiffTargets;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementNetworkMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneMapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ClassFieldClasses({
        CloudPluginSceneBO.class,
        CloudPluginSceneElementBO.class,
        CloudPluginSceneElementPortBO.class,
        CloudPluginSceneModel.class,
        CloudPluginSceneElementModel.class,
        CloudPluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        CloudPluginSceneMapper.class,
        CloudPluginSceneElementMapper.class,
        CloudPluginSceneElementPortMapper.class,
})
public class CloudScenePluginModelHandler extends BasePluginModelHandler<
        SceneBO,
        SceneElementBO,
        SceneElementPortBO,
        CloudPluginSceneModel,
        CloudPluginSceneElementModel,
        CloudPluginSceneElementPortModel,
        CloudPluginSceneBO,
        CloudPluginSceneElementBO,
        CloudPluginSceneElementPortBO,
        CloudPluginSceneMapper,
        CloudPluginSceneElementMapper,
        CloudPluginSceneElementPortMapper,
        CloudScenePluginHandler> {

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementNetworkMapper cloudPluginSceneElementNetworkMapper;

    public CloudScenePluginModelHandler(CloudScenePluginHandler pluginHandler) {
        init(pluginHandler);
        this.pluginHandler = pluginHandler;
        this.cloudPluginSceneElementNetworkMapper = BeanCommon.getBean(CloudPluginSceneElementNetworkMapper.class);
    }

    public List<CloudPluginSceneElementNetworkModel> readNetworks() {
        String scene = pluginHandler.getSourceSceneId();
        return this.cloudPluginSceneElementNetworkMapper.list(
                new HumanCondition()
                        .filterEqual(CloudPluginSceneElementNetworkModel::getScene, scene)
        );
    }

    public List<CloudPluginSceneElementNetworkModel> readElementNetworks(String sceneElement) {
        String scene = pluginHandler.getSourceSceneId();
        return this.cloudPluginSceneElementNetworkMapper.list(
                new HumanCondition()
                        .filterEqual(CloudPluginSceneElementNetworkModel::getScene, scene)
                        .filterEqual(CloudPluginSceneElementNetworkModel::getSceneElement, sceneElement)
        );
    }

    public int createElementNetworks(List<ConnectedNetworkBO> deriveNetworks) {
        
        this.cloudPluginSceneElementNetworkMapper.bulkDelete(deriveNetworks.stream()
                .map(d -> d.getNetwork().getId())
                .collect(Collectors.toList())
                .toArray(new String[]{})
        );
        
        return this.cloudPluginSceneElementNetworkMapper.bulkCreate(deriveNetworks.stream().map(d -> d.getNetwork().toModel()).collect(Collectors.toList())).getSuccessCount();
    }

    public void deleteElementNetworks(String... networkIds) {
        this.cloudPluginSceneElementNetworkMapper.bulkDelete(networkIds);
    }

    public int updateElementNetworks(String sceneElement) {
        var deriveNetworks = this.pluginHandler.getElement(sceneElement).getDeriveNetworks();
        var existNetworks = this.readElementNetworks(sceneElement);
        Map<String, CloudPluginSceneElementNetworkModel> toUpdateMap = new HashMap<>();
        deriveNetworks.forEach(deriveNetwork -> toUpdateMap.put(deriveNetwork.getNetwork().getId(), deriveNetwork.getNetwork().toModel()));
        Map<String, CloudPluginSceneElementNetworkModel> existMap = new HashMap<>();
        existNetworks.forEach(exist -> existMap.put(exist.getId(), exist));
        DiffTargets<CloudPluginSceneElementNetworkModel> diffTargets = this.diffTargets(toUpdateMap, existMap);
        int result = 0;
        if (CollectionUtils.isNotEmpty(diffTargets.getAddingTargets())) {
            int r = this.cloudPluginSceneElementNetworkMapper.bulkCreate(diffTargets.getAddingTargets()).getSuccessCount();
            result += r;
        }
        if (CollectionUtils.isNotEmpty(diffTargets.getUpdatingTargets())) {
            int r = this.cloudPluginSceneElementNetworkMapper.bulkUpdate(diffTargets.getUpdatingTargets()).getSuccessCount();
            result += r;
        }
        if (CollectionUtils.isNotEmpty(diffTargets.getRemovingTargets())) {
            int r = this.cloudPluginSceneElementNetworkMapper.bulkDelete(diffTargets.getRemovingTargets().stream()
                    .map(e -> e.getId())
                    .collect(Collectors.toList())
                    .toArray(new String[]{})
            ).getSuccessCount();
            result += r;
        }
        return result;
    }

    private DiffTargets<CloudPluginSceneElementNetworkModel> diffTargets(Map<String, CloudPluginSceneElementNetworkModel> targetMap,
                                                                         Map<String, CloudPluginSceneElementNetworkModel> existTargetMap) {
        var diff = new DiffTargets<CloudPluginSceneElementNetworkModel>();
        targetMap.forEach((key, target) -> {
            var existTarget = existTargetMap.getOrDefault(key, null);
            if (existTarget == null) {
                diff.getAddingTargets().add(target);
            } else {
                existTargetMap.remove(key);
                target.setId(existTarget.getId());
                diff.getUpdatingTargets().add(target);
            }
        });
        existTargetMap.forEach((key, node) -> diff.getRemovingTargets().add(node));
        return diff;
    }
}
