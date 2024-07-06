package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 用于节点网络操作
 */
public class ElementNetworkUtils {

    /**
     * 筛选出所有cidr是否连接容器
     *
     * @param allElements
     * @param cloudElement
     * @param virtualNicsMap
     * @return
     */
    public static ConcurrentHashMap<String, Boolean> getCidrToContainerMap(
            List<CloudPluginSceneElementBO> allElements,
            CloudPluginSceneElementBO cloudElement,
            HashMap<String, List<VirtualNic>> virtualNicsMap) {
//        VNet创建所有子网
        if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(cloudElement.getImageType())) {

            List<String> cidrPools = cloudElement.getCidrPools();
            List<String> nodeIds = cloudElement.getDeriveNetworks().stream()
                    .flatMap(connectedNetworkBO -> connectedNetworkBO.getToPorts().stream())
                    .map(SceneElementPortBO::getElement)
                    .map(SceneElementBO::getId)
                    .distinct()
                    .collect(Collectors.toList());

            Set<String> allFixedIps = nodeIds.stream()
                    .map(virtualNicsMap::get)
                    .flatMap(List::stream)
                    .map(VirtualNic::getFixedIp)
                    .collect(Collectors.toSet());
//                                更新cidr
            List<String> cidrList = allFixedIps.stream().map(ip -> IpUtils.ip2Cidr24(ip)).collect(Collectors.toList());
            cidrPools.addAll(cidrList);
            cidrPools = cidrPools.stream().filter(cidr -> StringUtils.isNotEmpty(cidr)).distinct().collect(Collectors.toList());

//            TODO 处理子网容器委托
            Map<String, Boolean> cidrContainerMap = cidrPools.stream().collect(Collectors.toMap(cidr -> cidr, cidr -> false));
            return new ConcurrentHashMap<>(cidrContainerMap);
        }

        AtomicReference<Boolean> isContainer = new AtomicReference<>(false);
        ConcurrentHashMap<String, Boolean> cidrContainerMap = new ConcurrentHashMap<>();
        cloudElement.getDeriveNetworks().

                forEach(connectedNetworkBO ->

                {
                    isContainer.set(false);
                    String cidr = connectedNetworkBO.getNetwork().getCidr();
//              拓扑未连线增加子网委托  查 getFromPorts 中有ip 且 getToPorts==null 的 想办法找出是否是container
                    for (ConnectedNetworkBO.ConnectPort connectPort : connectedNetworkBO.getConnectPorts()) {
                        SceneElementPortBO from = connectPort.getFrom();
                        if (null != from.getIp() && null == connectPort.getTo()) {
                            checkIfElementIsContainerById(allElements, isContainer, from.getElement().getId());
                        }
                    }

                    connectedNetworkBO
                            .getToPorts()
                            .stream()
                            .filter(sceneElementPortBO -> null != sceneElementPortBO)
                            .map(sceneElementPortBO -> sceneElementPortBO.getElement().getId())
                            .forEach(id -> {
                                checkIfElementIsContainerById(allElements, isContainer, id);
                            });
                    cidrContainerMap.put(cidr, isContainer.get());
                });
        return cidrContainerMap;
    }

    private static void checkIfElementIsContainerById(List<CloudPluginSceneElementBO> allElements, AtomicReference<Boolean> isContainer, String id) {
        // 根据id 从allElements取出对应的element
        CloudPluginSceneElementBO cloudElement1 = allElements
                .stream()
                .filter(cloudElement2 -> cloudElement2.getSrcElement().getId().equals(id))
                .findFirst()
                .get();
        if (null != cloudElement1) {
            if ((BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement1.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == cloudElement1.getVirtualType())
                    || (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(cloudElement1.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == cloudElement1.getVirtualType())) {
                isContainer.set(true);
            }
        }
    }
}
