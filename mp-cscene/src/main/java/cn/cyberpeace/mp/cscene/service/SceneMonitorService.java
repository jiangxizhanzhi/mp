package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 场景监控Service
 * @Author zhouxinghuai
 * @Date：2021/11/23 14:45
 */
@Slf4j
@Service
public class SceneMonitorService {

    private static final String CPCLOUD_PLUGIN = "cpcloud";

    /**
     * SceneInstanceBO转为SceneMonitorInfo
     * @param csceneInstanceBO
     * @return
     */
    public SceneMonitorInfo sceneInstanceBO2SceneMonitorInfo(CsceneInstanceBO csceneInstanceBO) {
        SceneMonitorInfo sceneMonitorInfo = new SceneMonitorInfo();
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return sceneMonitorInfo;
        }

        ScenePluginDataBO scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);

        var emap = scenePluginDataBO.getElements();
        if (CollectionUtil.isNotEmpty(emap)) {
            emap.forEach((k1, m1) -> {
                CloudPluginSceneElementBO e = JsonCommon.snakeMapToBean(m1, CloudPluginSceneElementBO.class);
                // 虚拟机
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, e.getVirtualType())) {
                    sceneMonitorInfo.setVmCount(sceneMonitorInfo.getVmCount() + 1);
                }
                // 容器
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, e.getVirtualType())) {
                    sceneMonitorInfo.setContainerCount(sceneMonitorInfo.getContainerCount() + 1);
                }
                // 实体设备
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, e.getVirtualType())) {
                    sceneMonitorInfo.setEntityCount(sceneMonitorInfo.getEntityCount() + 1);
                }
                // 网络
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH, e.getVirtualType())) {
                    sceneMonitorInfo.setNetworkCount(sceneMonitorInfo.getNetworkCount() + 1);
                }

                // todo 弹性IP 暂时没有

                // 路由
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER, e.getVirtualType())) {
                    sceneMonitorInfo.setRouterCount(sceneMonitorInfo.getRouterCount() + 1);
                }

                // 防火墙规则
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL, e.getVirtualType())) {
                    sceneMonitorInfo.setFirewallCount(sceneMonitorInfo.getFirewallCount() + 1);
                }

                // todo 流表规则 暂时没有

            });
        }

        return sceneMonitorInfo;
    }

}
