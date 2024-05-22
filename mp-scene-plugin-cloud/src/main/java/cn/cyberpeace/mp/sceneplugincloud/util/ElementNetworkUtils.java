package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于节点网络操作
 */
public class ElementNetworkUtils {

    /**
     * 筛选出所有cidr是否连接容器
     * @param allElements
     * @return
     */
    @NotNull
    public static ConcurrentHashMap<String, Boolean> getCidrContainerMap(List<CloudPluginSceneElementBO> allElements) {
        ConcurrentHashMap<String, Boolean> cidrContainerMap = new ConcurrentHashMap<>();
        allElements.forEach(cloudElement -> {
            cloudElement.getDeriveNetworks().forEach(connectedNetworkBO -> {
                String cidr = connectedNetworkBO.getNetwork().getCidr();
                AtomicReference<Boolean> isContainer = new AtomicReference<>(false);
                connectedNetworkBO
                        .getToPorts()
                        .stream()
                        .filter(sceneElementPortBO -> null != sceneElementPortBO)
                        .map(sceneElementPortBO -> sceneElementPortBO.getElement().getId())
                        .forEach(id -> {
                            // 根据id 从allElements取出对应的element
                            CloudPluginSceneElementBO cloudElement1 = allElements
                                    .stream()
                                    .filter(cloudElement2 -> cloudElement2.getSrcElement().getId().equals(id))
                                    .findFirst()
                                    .get();
                            if (null != cloudElement1) {
                                if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement1.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == cloudElement1.getVirtualType()) {
                                    isContainer.set(true);
                                }
                            }
                        });
                cidrContainerMap.put(cidr, isContainer.get());
            });
        });
        return cidrContainerMap;
    }
}
