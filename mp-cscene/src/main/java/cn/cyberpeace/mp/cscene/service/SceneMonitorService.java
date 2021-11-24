package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
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
     * CsceneInstanceBO转为SceneMonitorInfo
     *
     * @param csceneInstanceBO
     * @return
     */
    public SceneMonitorInfo sceneInstanceBO2SceneMonitorInfo(CsceneInstanceBO csceneInstanceBO) {
        log.info("sceneId:{}", csceneInstanceBO.getId());

        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return null;
        }

        SceneMonitorInfo sceneMonitorInfo = new SceneMonitorInfo();
        var scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);

        var pluginElements = scenePluginDataBO.getElements();
        if (CollectionUtil.isNotEmpty(pluginElements)) {
            pluginElements.forEach((k, v) -> {
                CloudPluginSceneElementBO elementBO = JsonCommon.snakeMapToBean(v, CloudPluginSceneElementBO.class);
                // 虚拟机
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setVirtualMachineCount(sceneMonitorInfo.getVirtualMachineCount() + 1);
                }
                // 容器
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setContainerCount(sceneMonitorInfo.getContainerCount() + 1);
                }
                // 实体设备
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setEntityCount(sceneMonitorInfo.getEntityCount() + 1);
                }
                // 网络
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setSwitchCount(sceneMonitorInfo.getSwitchCount() + 1);
                }

                // todo 弹性IP 暂时没有

                // 路由
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setRouterCount(sceneMonitorInfo.getRouterCount() + 1);
                }

                // 防火墙规则
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setFirewallCount(sceneMonitorInfo.getFirewallCount() + 1);
                }

                // todo 流表规则 暂时没有

            });
        }

        return sceneMonitorInfo;
    }


}
