package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetworkElementParseResult;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/12
 */
class NetworkElementConverter implements IConverter {

    private List<NetworkElementParseResult> results;

    public NetworkElementConverter(List<NetworkElementParseResult> results) {
        this.results = results;
    }

    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(results)) {
            results.stream().forEach(network -> this.createElementAndPort(network, container));
        }
    }

    private void createElementAndPort(NetworkElementParseResult network, ConvertDataContainer container) {
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        container.addNetworkElement(sceneConfigElementBO);
        sceneConfigElementBO.setName(network.getName());
        sceneConfigElementBO.setId(network.getId());
        sceneConfigElementBO.setType(BaseSceneElementModel.Type.SWITCH);
        List<SceneConfigElementPortBO> ports = new ArrayList<>();
        sceneConfigElementBO.setPorts(ports);

        CloudPluginSceneConfigElementBO pluginElement = new CloudPluginSceneConfigElementBO();
        container.putPluginElement(sceneConfigElementBO.getId(), pluginElement);

        // 判断和其他节点是否有连线
        for (Map<String, SceneConfigElementPortBO> portsMap : container.getNode2NetworkPorts().values()) {
            SceneConfigElementPortBO connectPort = portsMap.get(network.getId());
            // 不存在连线
            if (connectPort == null) {
                continue;
            }

            // 存在连线 先创建端口
            SceneConfigElementPortBO newPort = new SceneConfigElementPortBO();
            newPort.setId(TextUtils.uuid());
            newPort.setGateway(network.getGateway());
            newPort.setDhcp("on".equals(network.getDhcp()));
            newPort.setName(network.getName());
            newPort.setDns(network.getDns());
            ports.add(newPort);

            CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
            container.putPluginElementPort(newPort.getId(), pluginPort);

            // 创建连线
            SceneConfigElementPortConnectionBO connection = new SceneConfigElementPortConnectionBO();
            connection.setId(TextUtils.uuid());
            connection.setSource(newPort.getId());
            connection.setTarget(connectPort.getId());
            // todo
            connection.setName(newPort.getName() + "_" + connectPort.getName());
            container.addConnection(connection);
        }

    }
}
