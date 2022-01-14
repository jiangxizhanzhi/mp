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

import java.time.Duration;
import java.time.LocalDateTime;
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
        sceneMonitorInfo.setSceneAttr(csceneInstanceBO.getSceneAttr());
        sceneMonitorInfo.setCreateUsername(csceneInstanceBO.getCreateUsername());
        sceneMonitorInfo.setType(csceneInstanceBO.getType());
        sceneMonitorInfo.setReferenceType(csceneInstanceBO.getReferenceType());
        sceneMonitorInfo.setReferenceMetaData(csceneInstanceBO.getReferenceMetaData());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createTime = csceneInstanceBO.getCreateTime();
        if (Objects.nonNull(createTime)) {
            sceneMonitorInfo.setCreateTime(createTime);
            Duration duration = Duration.between(createTime, now);
            Long minutes = duration.toMinutes();
            sceneMonitorInfo.setRunMinutes(minutes);
        }

        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return sceneMonitorInfo;
        }
        var scene = csceneInstanceBO.getScene();


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
    public List<ElementMonitorBasicInfo> getAllSceneElements(String sceneId, Boolean fetchRealTimeHostState) {
        if (Objects.isNull(fetchRealTimeHostState)) {
            fetchRealTimeHostState = true;
        }
        log.info("sceneId:{}, fetchRealTimeHostState:{}", sceneId, fetchRealTimeHostState);
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
            basicInfo.setSceneId(sceneId);
            elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, eid);
            if (isSupportedVirtualType(basicInfo.getVirtualType())) {
                elementMonitorService.processTopologyElement(basicInfo, csceneInstanceBO, eid, fetchRealTimeHostState);
                basicInfos.add(basicInfo);
            }
        }

        return basicInfos;
    }


    /**
     * 是否是需要的节点
     * @param type
     * @return
     */
    private boolean isSupportedVirtualType(Integer type) {
        if (Objects.isNull(type)) {
            return false;
        }
        boolean supported = false;
        if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == type
                || BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == type
                || BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE == type) {
            supported = true;
        }
        return supported;
    }



}
