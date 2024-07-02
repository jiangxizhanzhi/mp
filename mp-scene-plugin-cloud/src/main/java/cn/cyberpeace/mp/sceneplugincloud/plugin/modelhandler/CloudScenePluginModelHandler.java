package cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler;

import cn.cyberpeace.mp.base.bo.DiffTargets;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * CloudScenePluginModelHandler
     *
     * @param pluginHandler
     */
    public CloudScenePluginModelHandler(CloudScenePluginHandler pluginHandler) {
        init(pluginHandler);
        this.pluginHandler = pluginHandler;
        this.cloudPluginSceneElementNetworkMapper = BeanCommon.getBean(CloudPluginSceneElementNetworkMapper.class);
    }

    /**
     * readNetworks
     *
     * @return
     */
    public List<CloudPluginSceneElementNetworkModel> readNetworks() {
        String scene = pluginHandler.getSourceSceneId();
        return this.cloudPluginSceneElementNetworkMapper.list(
                new HumanCondition()
                        .filterEqual(CloudPluginSceneElementNetworkModel::getScene, scene)
        );
    }

    /**
     * readElementNetworks
     *
     * @param sceneElement
     * @return
     */
    public List<CloudPluginSceneElementNetworkModel> readElementNetworks(String sceneElement) {
        String scene = pluginHandler.getSourceSceneId();
        return this.cloudPluginSceneElementNetworkMapper.list(
                new HumanCondition()
                        .filterEqual(CloudPluginSceneElementNetworkModel::getScene, scene)
                        .filterEqual(CloudPluginSceneElementNetworkModel::getSceneElement, sceneElement)
        );
    }

    /**
     * createElementNetworks
     *
     * @param deriveNetworks
     * @return int
     */
    public int createElementNetworks(List<ConnectedNetworkBO> deriveNetworks) {
        // 删除原来的
        List<String> list = new ArrayList<>();
        for (ConnectedNetworkBO deriveNetwork : deriveNetworks) {
            String id = deriveNetwork.getNetwork().getId();
            list.add(id);
        }
        this.cloudPluginSceneElementNetworkMapper.bulkDelete(list
                .toArray(new String[]{})
        );
        // 新建
        List<CloudPluginSceneElementNetworkModel> result = new ArrayList<>();
        for (ConnectedNetworkBO d : deriveNetworks) {
            CloudPluginSceneElementNetworkModel cloudPluginSceneElementNetworkModel = d.getNetwork().toModel();
            result.add(cloudPluginSceneElementNetworkModel);
        }
        return this.cloudPluginSceneElementNetworkMapper.bulkCreate(result).getSuccessCount();
    }

    /**
     * deleteElementNetworks
     *
     * @param networkIds
     */
    public void deleteElementNetworks(String... networkIds) {
        this.cloudPluginSceneElementNetworkMapper.bulkDelete(networkIds);
    }

    /**
     * updateElementNetworks
     *
     * @param sceneElement
     * @return int
     */
    public int updateElementNetworks(String sceneElement) {
        List<ConnectedNetworkBO> deriveNetworks = this.pluginHandler.getElement(sceneElement).getDeriveNetworks();
        List<CloudPluginSceneElementNetworkModel> existNetworks = this.readElementNetworks(sceneElement);
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
            List<String> list = new ArrayList<>();
            for (CloudPluginSceneElementNetworkModel e : diffTargets.getRemovingTargets()) {
                String id = e.getId();
                list.add(id);
            }
            int r = this.cloudPluginSceneElementNetworkMapper.bulkDelete(list
                    .toArray(new String[]{})
            ).getSuccessCount();
            result += r;
        }
        return result;
    }

    /**
     * diffTargets
     *
     * @param targetMap
     * @param existTargetMap
     * @return
     */
    private DiffTargets<CloudPluginSceneElementNetworkModel> diffTargets(Map<String, CloudPluginSceneElementNetworkModel> targetMap,
                                                                         Map<String, CloudPluginSceneElementNetworkModel> existTargetMap) {
        DiffTargets<CloudPluginSceneElementNetworkModel> diff = new DiffTargets<CloudPluginSceneElementNetworkModel>();
        targetMap.forEach((key, target) -> {
            CloudPluginSceneElementNetworkModel existTarget = existTargetMap.getOrDefault(key, null);
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

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.pluginSceneBOClass = CloudPluginSceneBO.class;
        super.pluginSceneElementBOClass = CloudPluginSceneElementBO.class;
        super.pluginSceneElementPortBOClass = CloudPluginSceneElementPortBO.class;
        super.pluginSceneModelClass = CloudPluginSceneModel.class;
        super.pluginSceneElementModelClass = CloudPluginSceneElementModel.class;
        super.pluginSceneElementPortModelClass = CloudPluginSceneElementPortModel.class;
    }

    /**
     * initMapper
     */
    @Override
    protected void initMapper() {
        super.pluginSceneMapper = BeanCommon.getBean(CloudPluginSceneMapper.class);
        super.pluginSceneElementMapper = BeanCommon.getBean(CloudPluginSceneElementMapper.class);
        super.pluginSceneElementPortMapper = BeanCommon.getBean(CloudPluginSceneElementPortMapper.class);
    }
}
