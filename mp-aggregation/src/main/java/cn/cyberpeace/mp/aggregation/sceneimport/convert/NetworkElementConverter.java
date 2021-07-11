package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.constant.CommonConstant;
import cn.cyberpeace.mp.aggregation.constant.TargetConstant;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetworkElementParseResult;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class NetworkElementConverter implements IConverter {

    private List<NetworkElementParseResult> results;

    public NetworkElementConverter(List<NetworkElementParseResult> results) {
        this.results = results == null ? new ArrayList<>() : results;
    }

    public void convert2BO(ConvertDataContainer container) {
        
        this.prepareExternalNetwork(container);
        if (CollectionUtils.isNotEmpty(results)) {
            results.stream().forEach(network -> this.createElementAndPort(network, container));
        }
    }

    private void prepareExternalNetwork(ConvertDataContainer container) {
        if (CollectionUtils.isEmpty(container.getNoNetworkNodeElements())) {
            
            return;
        } else {
            
        }
    }

    private void createElementAndPort(NetworkElementParseResult network, ConvertDataContainer container) {
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        container.addNetworkElement(sceneConfigElementBO);
        sceneConfigElementBO.setName(network.getName());
        sceneConfigElementBO.setId(network.getId());
        sceneConfigElementBO.setType(BaseSceneElementModel.Type.SWITCH);
        sceneConfigElementBO.setIcon2(DefaultImgUtil.pathToFdfsUrl(CommonConstant.DefaultImageUrl.SWITCHER));
        List<SceneConfigElementPortBO> ports = new ArrayList<>();
        sceneConfigElementBO.setPorts(ports);
        sceneConfigElementBO.setDisplayContext(network.getDisplayContext());

        if (StringUtils.isNotEmpty(network.getPosition())) {
            String[] xy = network.getPosition().split(",");
            Origin origin = new Origin(formatDouble(xy[0]), formatDouble(xy[1]));
            sceneConfigElementBO.setPosition2(JsonCommon.stringify(origin));
        }

        CloudPluginSceneConfigElementBO pluginElement = new CloudPluginSceneConfigElementBO();
        pluginElement.setCidr(network.getCidr());
        pluginElement.setGateway(network.getGateway());
        if (network.getDhcp() == null) {
            
            pluginElement.setDhcp(true);
        } else {
            pluginElement.setDhcp("on".equals(network.getDhcp()) || "true".equals(network.getDhcp()));
        }
        if (StringUtils.isNotEmpty(network.getDhcpPool())) {
            List<AllocationPool> allocationList = new ArrayList<>();
            String[] allocationStrs = StringUtils.split(network.getDhcpPool(), ",");
            if (allocationStrs != null && allocationStrs.length > 0) {
                for (String dhcpStr : allocationStrs) {
                    String[] dhcpStartAndEnd = StringUtils.split(dhcpStr, "-");
                    if (dhcpStartAndEnd != null && dhcpStartAndEnd.length == 2) {
                        AllocationPool allocationPool = new AllocationPool();
                        allocationPool.setStart(dhcpStartAndEnd[0]);
                        allocationPool.setEnd(dhcpStartAndEnd[1]);
                        allocationList.add(allocationPool);
                    }
                }
                if (CollectionUtils.isNotEmpty(allocationList)) {
                    pluginElement.setAllocationPools(allocationList);
                }
            }
        }
        if (!StringUtils.isEmpty(network.getDns())) {
            if (network.getDns().contains(",")) {
                String[] dnsArray = StringUtils.split(network.getDns(), ",");
                pluginElement.setDnsNameservers(List.of(dnsArray));
            } else {
                pluginElement.setDnsNameservers(List.of(network.getDns()));
            }
        }
        pluginElement.setGateway(network.getGateway());
        if (network.getId().toLowerCase().startsWith("internet")) {
            
            pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK);
            pluginElement.setImage(TargetConstant.DefaultTargetName.NETWORK_EXTERNAL);
        } else {
            if (container.getSimpleLayer3Switch().contains(network.getId())) {
                pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH);
            } else {
                pluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY);
            }
            pluginElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.SIMPLE);
            pluginElement.setImage(TargetConstant.DefaultTargetName.DEFAULT_NETWORK);
        }
        container.putPluginElement(sceneConfigElementBO.getId(), pluginElement);

        
        for (Map<String, SceneConfigElementPortBO> portsMap : container.getNode2NetworkPorts().values()) {
            SceneConfigElementPortBO connectPort = portsMap.get(network.getId());
            
            if (connectPort == null) {
                continue;
            }

            
            SceneConfigElementPortBO newPort = new SceneConfigElementPortBO();
            newPort.setId(IConverter.getPortId(sceneConfigElementBO.getId(), sceneConfigElementBO.getType(), ports.size()));
            newPort.setGateway(network.getGateway());
            newPort.setDhcp("on".equals(network.getDhcp()) || "true".equals(network.getDhcp()));
            newPort.setName(network.getName());
            newPort.setDns(network.getDns());
            ports.add(newPort);

            CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
            container.putPluginElementPort(newPort.getId(), pluginPort);

            
            SceneConfigElementPortConnectionBO connection = new SceneConfigElementPortConnectionBO();
            connection.setId(TextUtils.uuid());
            connection.setSource(newPort.getId());
            connection.setTarget(connectPort.getId());
            
            connection.setName(newPort.getName() + "_" + connectPort.getName());
            container.addConnection(connection);
        }

    }

    private double formatDouble(String number) {
        return new BigDecimal(number).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
    }

    @Getter
    @Setter
    private static class Origin {
        private double x;
        private double y;

        public Origin(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
