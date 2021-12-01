package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 场景监控Service
 * @Author zhouxinghuai
 * @Date：2021/11/23 14:45
 */
@Slf4j
@Service
public class SceneMonitorService {

    private static final String CPCLOUD_PLUGIN = "cpcloud";

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneElementMonitorService elementMonitorService;

    /**
     * CsceneInstanceBO转为SceneMonitorInfo
     *
     * @return
     */
    public SceneMonitorInfo getSceneMonitorInfo(String sceneId) {
        log.info("sceneId:{}", sceneId);
        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        SceneMonitorInfo sceneMonitorInfo = new SceneMonitorInfo();
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return sceneMonitorInfo;
        }

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


    /**
     * 查询场景中所有的节点列表
     *
     * @param sceneId
     * @return
     */
    public List<ElementMonitorBasicInfo> getAllSceneElements(String sceneId) {
        log.info("sceneId:{}", sceneId);
        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();

        List<String> eids = topologyElements.stream().map(SceneElementBO::getId).collect(Collectors.toList());

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        for (String eid : eids) {
            ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
            elementMonitorService.processTopologyElement(basicInfo, csceneInstanceBO, eid);
            elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, eid);
            basicInfos.add(basicInfo);
        }

        return basicInfos;
    }

}
