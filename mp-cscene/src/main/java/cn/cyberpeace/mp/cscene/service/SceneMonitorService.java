package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private SceneTranslationService sceneTranslationService;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

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

        List<String> nodeIds = topologyElements.stream().map(SceneElementBO::getId).collect(Collectors.toList());

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        for (String nodeId : nodeIds) {
            ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
            basicInfo.setSceneId(sceneId);
            elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, nodeId);
            if (isSupportedVirtualType(basicInfo.getVirtualType())) {
                elementMonitorService.processTopologyElement(basicInfo, csceneInstanceBO, nodeId, fetchRealTimeHostState);
                basicInfos.add(basicInfo);
            }
        }

        return basicInfos;
    }


    /**
     * 查询场景IP
     *
     * @param request
     * @return
     */
    public List<String> listIps(BaseSceneElementRequest request) {
        log.info("{}", request);
        BaseRequestUtil.checkBaseRequestWithSceneId(request);

        List<String> translatedSceneIds = sceneTranslationService.translateSceneIds(CollectionUtil.toList(request.getSceneId()));
        if (CollectionUtil.isEmpty(translatedSceneIds)) {
            return new ArrayList<>();
        }
        return scenePluginCollectionSceneService.listIPs(translatedSceneIds.get(0), request.getElementIds());
    }

    /**
     * 列出场景所有录屏的用户
     * @param request
     * @return
     */
    public List<UserModel> listSceneRecordingUsers(BaseSceneElementRequest request) {
        log.info("{}", request);
        List<String> translatedSceneIds = sceneTranslationService.translateSceneIds(CollectionUtil.toList(request.getSceneId()));
        if (CollectionUtil.isEmpty(translatedSceneIds)) {
            return new ArrayList<>();
        }
        List<String> userIds = scenePluginCloudSceneService.getRecordingUsers(translatedSceneIds.get(0), request.getElementIds());
        List<UserModel> users = userIds.stream().map(id -> UserUtils.getUser(id)).collect(Collectors.toList());
        return users;
    }


    /**
     * 查看top N的节点状态
     */
    public Map<String, List<ElementMonitorBasicInfo>> topAggrByHoststates(String sceneId, Integer topCount) {
        if (Objects.isNull(topCount)) {
            topCount = 5;
        }
        if (topCount < 1) {
            log.error("topCount:{} should not be less than 1", topCount);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        List<ElementMonitorBasicInfo> list = getAllSceneElements(sceneId, true);

        // cpu
        Comparator<ElementMonitorBasicInfo> sortByCpu = Comparator.comparing(e -> e.getHostState().getCpuPercent(), Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<ElementMonitorBasicInfo> sortByMemory = Comparator.comparing(e -> e.getHostState().getMemoryPercent(), Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<ElementMonitorBasicInfo> sortByDisk = Comparator.comparing(e -> e.getHostState().getDiskPercent(), Comparator.nullsFirst(Comparator.naturalOrder()));

        List<ElementMonitorBasicInfo> sortByCpuList = list.stream().sorted(sortByCpu.reversed()).limit(topCount).collect(Collectors.toList());
        List<ElementMonitorBasicInfo> sortByMemoryList = list.stream().sorted(sortByMemory.reversed()).limit(topCount).collect(Collectors.toList());
        List<ElementMonitorBasicInfo> sortByDiskList = list.stream().sorted(sortByDisk.reversed()).limit(topCount).collect(Collectors.toList());

        Map<String, List<ElementMonitorBasicInfo>> result = new HashMap<>();
        result.put("cpu", sortByCpuList);
        result.put("memory", sortByMemoryList);
        result.put("disk", sortByDiskList);
        return result;
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
