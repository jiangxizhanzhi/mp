package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
    private CsceneCommonService csceneCommonService;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private HostStateService hostStateService;

    /**
     * CsceneInstanceBO转为SceneMonitorInfo
     *
     * @return
     */
    public SceneMonitorInfo getSceneMonitorInfo(String sceneId) {
        log.info("query scene monitor info, sceneId:{}", sceneId);
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
            var instanceModel = csceneInstanceService.get(sceneId).getSource().getObj();
            if (Boolean.TRUE.equals(instanceModel.getDeleted())) {
                Duration duration = Duration.between(createTime, instanceModel.getUpdateTime());
                Long minutes = duration.toMinutes();
                sceneMonitorInfo.setRunMinutes(minutes);
            } else {
                Duration duration = Duration.between(createTime, now);
                Long minutes = duration.toMinutes();
                sceneMonitorInfo.setRunMinutes(minutes);
            }
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

            // 网络数量
            Set<String> networkIds = new HashSet<>();
            if (CollectionUtil.isNotEmpty(scenePluginDataBO.getElementPorts())) {
                scenePluginDataBO.getElementPorts().values().forEach(p -> {
                    var port = JsonCommon.snakeMapToBean(p, CloudPluginSceneElementPortBO.class);
                    if (StringUtils.isNotEmpty(port.getAuxiliaryNetworkId())) {
                        networkIds.add(port.getAuxiliaryNetworkId());
                    }
                });
                sceneMonitorInfo.setSwitchCount(networkIds.size());
            }

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
        log.info("sceneId:{}, fetchRealTimeHostState:{}", sceneId, fetchRealTimeHostState);
        if (Objects.isNull(fetchRealTimeHostState)) {
            fetchRealTimeHostState = true;
        }
        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
            return Collections.emptyList();
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();

        List<String> nodeIds = topologyElements.stream().map(SceneElementBO::getId).collect(Collectors.toList());

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        List<CompletableFuture<ElementMonitorBasicInfo>> cfList = new ArrayList<>();
        for (String nodeId : nodeIds) {
            // 此处fetchRealTimeHostState设定为false，因为后面会统一查询一次
            var cf = CompletableFuture.supplyAsync(() -> processElementBasicInfo(csceneInstanceBO, sceneId, nodeId, false));
            cfList.add(cf);
        }

        if (CollectionUtil.isNotEmpty(cfList)) {
            var cfArray = cfList.toArray(new CompletableFuture[0]);
            CompletableFuture<List<ElementMonitorBasicInfo>> finalCF = CompletableFuture.allOf(cfArray).thenApply(v -> cfList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            basicInfos = finalCF.join();

            if (CollectionUtil.isNotEmpty(basicInfos)) {
                basicInfos = basicInfos.stream().filter(Objects::nonNull).collect(Collectors.toList());
                Collections.sort(basicInfos, (o1, o2) -> {
                    int o1i = 0;
                    int o2i = 0;
                    for (int i = 0; i < nodeIds.size(); i++) {
                        if (Objects.equals(nodeIds.get(i), o1.getId())) {
                            o1i = i;
                        }
                        if (Objects.equals(nodeIds.get(i), o2.getId())) {
                            o2i = i;
                        }
                    }
                    return o1i - o2i;
                });
            }

        }

        if (fetchRealTimeHostState && CollectionUtil.isNotEmpty(basicInfos)) {
            // 查询实时主机状态
            var agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(sceneId, null);
            if (CollectionUtil.isNotEmpty(agentNames)) {
                AgentNameBasedRequest request = new AgentNameBasedRequest();
                request.setAgentNames(agentNames);
                request.setPageSize(1);

                Map<String, List<HostState>> map = hostStateService.mapHostStatesByAgentName(request);
                basicInfos.forEach(basicInfo -> {
                    var hostStates = map.get(basicInfo.getEid());
                    if (CollectionUtil.isNotEmpty(hostStates)) {
                        basicInfo.setHostState(hostStates.get(0));
                    }
                });
            }
        }
        return basicInfos;
    }

    private ElementMonitorBasicInfo processElementBasicInfo(CsceneInstanceBO csceneInstanceBO, String sceneId, String nodeId, boolean fetchRealTimeHostState) {
        ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
        basicInfo.setSceneId(sceneId);
        elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, nodeId);
        if (isSupportedVirtualType(basicInfo.getVirtualType())) {
            elementMonitorService.processTopologyElement(basicInfo, csceneInstanceBO, nodeId, fetchRealTimeHostState);
            return basicInfo;
        }
        return null;
    }


    /**
     * 查询场景IP
     *
     * @param request
     * @return
     */
    public List<String> listIps(BaseSceneElementRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);

        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(CollectionUtil.toList(request.getSceneId()));
        if (CollectionUtil.isEmpty(translatedSceneIds)) {
            return new ArrayList<>();
        }
        return scenePluginCollectionSceneService.listIPs(translatedSceneIds.get(0), request.getElementIds()).getSource();
    }

    /**
     * 列出场景所有录屏的用户
     * @param request
     * @return
     */
    public List<UserModel> listSceneRecordingUsers(BaseSceneElementRequest request) {
        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(CollectionUtil.toList(request.getSceneId()));
        if (CollectionUtil.isEmpty(translatedSceneIds)) {
            return new ArrayList<>();
        }
        List<String> userIds = scenePluginCloudSceneService.getRecordingUsers(translatedSceneIds.get(0), request.getElementIds()).getSource();
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
