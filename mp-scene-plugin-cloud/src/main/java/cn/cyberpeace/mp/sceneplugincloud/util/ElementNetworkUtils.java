package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于节点网络操作
 */
public class ElementNetworkUtils {

    /**
     * 筛选出所有cidr是否连接容器
     *
     * @param allElements
     * @return
     */
    @NotNull
    public static ConcurrentHashMap<String, Boolean> getCidrContainerMap(List<CloudPluginSceneElementBO> allElements) {
        ConcurrentHashMap<String, Boolean> cidrContainerMap = new ConcurrentHashMap<>();
        allElements.forEach(cloudElement -> {
            ConcurrentHashMap<String, Boolean> extracted = getCidrToContainerMap(allElements, cloudElement);
            cidrContainerMap.putAll(extracted);
        });
        return cidrContainerMap;
    }

    public static ConcurrentHashMap<String, Boolean> getCidrToContainerMap(List<CloudPluginSceneElementBO> allElements, CloudPluginSceneElementBO cloudElement) {
        AtomicReference<Boolean> isContainer = new AtomicReference<>(false);
        ConcurrentHashMap<String, Boolean> cidrContainerMap = new ConcurrentHashMap<>();
        cloudElement.getDeriveNetworks().forEach(connectedNetworkBO -> {
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
