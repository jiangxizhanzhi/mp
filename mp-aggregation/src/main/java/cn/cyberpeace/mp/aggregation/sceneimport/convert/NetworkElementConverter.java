package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetworkElementParseResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
        this.results = results == null ? new ArrayList<>() : results;
    }

    public void convert2BO(ConvertDataContainer container) {
        // 处理下外部网络
        this.prepareExternalNetwork(container);
        if (CollectionUtils.isNotEmpty(results)) {
            results.stream().forEach(network -> this.createElementAndPort(network, container));
        }
    }

    private void prepareExternalNetwork(ConvertDataContainer container) {
        if (CollectionUtils.isEmpty(container.getNoNetworkNodeElements())) {
            // 不存在未连接的节点,不需要单独处理外部网络
            return;
        } else {
            // 存在未连接的节点,需要默认连上一个外部网络
            // 查找已配置的外部网络
            NetworkElementParseResult externalNetworkResult = null;
            if (CollectionUtils.isNotEmpty(results)) {
                // 无外部网络则新建一个
                externalNetworkResult = results.stream()
                        .filter(network -> network.getId().toLowerCase().startsWith("internet"))
                        .findFirst().get();
            }
            if (externalNetworkResult == null) {
                externalNetworkResult = new NetworkElementParseResult();
                externalNetworkResult.setId("internet_" + System.currentTimeMillis());
                externalNetworkResult.setName("external_network");
                this.results.add(externalNetworkResult);
            }
            // 给未连接的节点新建端口并与外部网络相连
            NetworkElementParseResult finalExternalNetworkResult = externalNetworkResult;
            container.getNoNetworkNodeElements().stream().forEach(elementBO -> {
                SceneConfigElementPortBO portBO = new SceneConfigElementPortBO();
                portBO.setId(TextUtils.uuid());
                elementBO.getPorts().add(portBO);
                container.putNode2NetworkPorts(elementBO.getId(), finalExternalNetworkResult.getId(), portBO);

                CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
                container.putPluginElementPort(portBO.getId(), pluginPort);
            });
        }
    }

    private void createElementAndPort(NetworkElementParseResult network, ConvertDataContainer container) {
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        container.addNetworkElement(sceneConfigElementBO);
        sceneConfigElementBO.setName(network.getName());
        sceneConfigElementBO.setId(network.getId());
        sceneConfigElementBO.setType(BaseSceneElementModel.Type.SWITCH);
        sceneConfigElementBO.setIcon2("/network-equipment/switcher.png");
        List<SceneConfigElementPortBO> ports = new ArrayList<>();
        sceneConfigElementBO.setPorts(ports);

        CloudPluginSceneConfigElementBO pluginElement = new CloudPluginSceneConfigElementBO();
        pluginElement.setCidr(network.getCidr());
        pluginElement.setGateway(network.getGateway());
        pluginElement.setDhcp("on".equals(network.getDhcp()) || "true".equals(network.getDhcp()));
        // TODO: 设置地址池
//        pluginElement.setAllocationPools();
        if (!StringUtils.isEmpty(network.getDns())) {
            pluginElement.setDnsNameservers(List.of(network.getDns()));
        }
        pluginElement.setGateway(network.getGateway());
        if (network.getId().toLowerCase().startsWith("internet")) {
            // id以internet开头,则是外部网络
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK);
        } else {
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY);
        }
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
            newPort.setDhcp("on".equals(network.getDhcp()) || "true".equals(network.getDhcp()));
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
