package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementPort;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.job.SceneHostStateReportScheduleService;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.AccessUserBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
import java.util.stream.Stream;

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

    @Resource
    private CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    /**
     * CsceneInstanceBO转为SceneMonitorInfo
     *
     * @return
     */
    public SceneMonitorInfo getSceneMonitorInfo(String sceneId) {
        log.info("query scene monitor info, sceneId:{}", sceneId);
        ServiceResult<CsceneInstanceBO> result = csceneInstanceService.getById(sceneId, null);
        CsceneInstanceBO csceneInstanceBO = result.getSource();

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
            CsceneInstanceModel instanceModel = csceneInstanceService.get(sceneId).getSource().getObj();
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
        SceneDataBO scene = csceneInstanceBO.getScene();


        ScenePluginDataBO scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);

        Map<String, Map<String, Object>> pluginElements = scenePluginDataBO.getElements();
        if (CollectionUtil.isNotEmpty(pluginElements)) {
            for (Map.Entry<String, Map<String, Object>> entry : pluginElements.entrySet()) {
                String k = entry.getKey();
                Map<String, Object> v = entry.getValue();
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

            }

            // 网络数量
            Set<String> networkIds = new HashSet<>();
            if (CollectionUtil.isNotEmpty(scenePluginDataBO.getElementPorts())) {
                for (Map<String, Object> p : scenePluginDataBO.getElementPorts().values()) {
                    CloudPluginSceneElementPortBO port = JsonCommon.snakeMapToBean(p, CloudPluginSceneElementPortBO.class);
                    if (StringUtils.isNotEmpty(port.getAuxiliaryNetworkId())) {
                        networkIds.add(port.getAuxiliaryNetworkId());
                    }
                }
                sceneMonitorInfo.setSwitchCount(networkIds.size());
            }

        }

        return sceneMonitorInfo;
    }

    public List<ElementMonitorBasicInfo> getSceneElements(String sceneId, Boolean supportProbe, Boolean supportFlow) {
        ServiceResult<CsceneInstanceBO> result = csceneInstanceService.getById(sceneId, null);
        CsceneInstanceBO csceneInstanceBO = result.getSource();

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        for (SceneElementBO element : csceneInstanceBO.getScene().getTopology().getElements()) {
            ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
            elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, element.getId());
            basicInfos.add(basicInfo);
        }
        if (Boolean.TRUE.equals(supportProbe)) {
            List<ElementMonitorBasicInfo> list = new ArrayList<>();
            for (ElementMonitorBasicInfo element : basicInfos) {
                if (extracted(element)) {
                    list.add(element);
                }
            }
            basicInfos = list;
        }
        if (Boolean.TRUE.equals(supportFlow)) {
            List<ElementMonitorBasicInfo> list = new ArrayList<>();
            for (ElementMonitorBasicInfo element : basicInfos) {
                if (extracted1(element)) {
                    list.add(element);
                }
            }
            basicInfos = list;
        }
        return basicInfos;
    }

    private static boolean extracted1(ElementMonitorBasicInfo element) {
        boolean isVm = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE;
        boolean isContainer = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
        boolean isEntity = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE;
        boolean isDynamips = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS;
        return isVm || isContainer || isEntity || isDynamips;
    }

    private static boolean extracted(ElementMonitorBasicInfo element) {
        if (element.getVirtualType() == null) {
            return false;
        }
        // 容器  非核心路由器的虚拟机支持采集
        boolean isVm = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE;
        boolean isContainer = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
        boolean isCoreRouter = ElementTypeUtils.isCoreRouter(element.getImageName());
        if ((isVm || isContainer) && !isCoreRouter) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询场景中所有的节点列表
     *
     * @param sceneId
     * @return
     */
    public List<ElementMonitorBasicInfo> getAllSceneElements(String sceneId, Boolean fetchRealTimeHostState, SceneAllElementsQueryRequest request) {
        log.info("sceneId:{}, fetchRealTimeHostState:{}", sceneId, fetchRealTimeHostState);
        if (Objects.isNull(fetchRealTimeHostState)) {
            fetchRealTimeHostState = true;
        }
        boolean isAll = Objects.nonNull(request) && Objects.nonNull(request.getIsAll()) ? request.getIsAll() : Boolean.FALSE;
        ServiceResult<CsceneInstanceBO> result = csceneInstanceService.getById(sceneId, null);
        CsceneInstanceBO csceneInstanceBO = result.getSource();
        String sceneInstanceId = csceneCommonService.translateSceneId(sceneId);

        if (StringUtils.isEmpty(sceneInstanceId)) {
            return Collections.emptyList();
        }

        SceneHandler sceneHandler = new SceneHandler(sceneInstanceId, null, null);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);

        // 根据名称模糊筛选
        Stream<SceneElementBO> stream = sceneHandler.getElementMap().values().stream();
        if (Objects.nonNull(request) && StringUtils.isNotEmpty(request.getName())) {
            stream = stream.filter(e -> e.getName().toLowerCase().contains(request.getName().toLowerCase()));
        }
        List<String> nodeIds = stream
                .filter(n -> !Boolean.TRUE.equals(n.getIsAuxiliary()))
                .map(SceneElementBO::getId)
                .collect(Collectors.toList());
        // 根据节点id筛选
        if (Objects.nonNull(request) && CollectionUtils.isNotEmpty(request.getElementIds())) {
            nodeIds.retainAll(request.getElementIds());
        }

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        List<CompletableFuture<ElementMonitorBasicInfo>> cfList = new ArrayList<>();
        for (String nodeId : nodeIds) {
            // 此处fetchRealTimeHostState设定为false，因为后面会统一查询一次
            CompletableFuture<ElementMonitorBasicInfo> cf = CompletableFuture.supplyAsync(() -> processElementBasicInfo(csceneInstanceBO, sceneId, nodeId, false, isAll));
            cfList.add(cf);
        }

        if (CollectionUtil.isNotEmpty(cfList)) {
            CompletableFuture[] cfArray = cfList.toArray(new CompletableFuture[0]);
            CompletableFuture<List<ElementMonitorBasicInfo>> finalCF = CompletableFuture.allOf(cfArray).thenApply(v -> {
                List<ElementMonitorBasicInfo> list = new ArrayList<>();
                for (CompletableFuture<ElementMonitorBasicInfo> elementMonitorBasicInfoCompletableFuture : cfList) {
                    ElementMonitorBasicInfo join = elementMonitorBasicInfoCompletableFuture.join();
                    list.add(join);
                }
                return list;
            });
            basicInfos = finalCF.join();

            if (CollectionUtil.isNotEmpty(basicInfos)) {
                if (Boolean.TRUE.equals(request.getIsCollect())) {
                    List<ElementMonitorBasicInfo> list = new ArrayList<>();
                    for (ElementMonitorBasicInfo basicInfo : basicInfos) {
                        if (Objects.nonNull(basicInfo)
                                && !ElementTypeUtils.isCoreRouter(basicInfo.getImageName())
                                && !BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(basicInfo.getImageType())) {
                            list.add(basicInfo);
                        }
                    }
                    basicInfos = list;
                } else {
                    List<ElementMonitorBasicInfo> list = new ArrayList<>();
                    for (ElementMonitorBasicInfo basicInfo : basicInfos) {
                        if (basicInfo != null) {
                            list.add(basicInfo);
                        }
                    }
                    basicInfos = list;
                }
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

        // 根据设备类型筛选
        if (Objects.nonNull(request) && Objects.nonNull(request.getCategory())) {
            List<ElementMonitorBasicInfo> list = new ArrayList<>();
            for (ElementMonitorBasicInfo b : basicInfos) {
                if (b.getCategory().equals(request.getCategory())) {
                    list.add(b);
                }
            }
            basicInfos = list;
        }
        // 根据操作系统名称筛选
        if (Objects.nonNull(request) && StringUtils.isNotEmpty(request.getSystemTypeName())) {
            List<ElementMonitorBasicInfo> list = new ArrayList<>();
            for (ElementMonitorBasicInfo b : basicInfos) {
                if (b.getSystemTypeName().equals(request.getSystemTypeName())) {
                    list.add(b);
                }
            }
            basicInfos = list;
        }

        // 添加云插件端口接入方式
        if (CollectionUtils.isNotEmpty(basicInfos)) {
            for (ElementMonitorBasicInfo basicInfo : basicInfos) {
                if (CollectionUtils.isEmpty(basicInfo.getPorts())) {
                    continue;
                }
                for (ElementPort p : basicInfo.getPorts()) {
                    CloudPluginSceneElementPortBO cloudPort = cloudPlugin.getPort(p.getId());
                    p.setCloudAccessModes(cloudPort.getAccessModes());
                }
            }
        }

        if (fetchRealTimeHostState && CollectionUtil.isNotEmpty(basicInfos)) {
            // 查询实时主机状态
            List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(sceneId, null);
            if (CollectionUtil.isNotEmpty(agentNames)) {
                AgentNameBasedRequest agentNameBasedRequest = new AgentNameBasedRequest();
                agentNameBasedRequest.setAgentNames(agentNames);
                agentNameBasedRequest.setPageSize(1);

                Map<String, List<HostState>> map = hostStateService.mapHostStatesByAgentName(agentNameBasedRequest);
                for (ElementMonitorBasicInfo basicInfo : basicInfos) {
                    List<HostState> hostStates = map.get(basicInfo.getEid());
                    //非容器的，如果从ES取到了系统运行资源信息，就设置上，否则就使用靶标创建时配置上的。容器如果靶标创建时有配置就使用配置，否则使用默认1C512MB10GB
                    if (CollectionUtils.isNotEmpty(hostStates)) {
                        if (!BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(basicInfo.getImageType())) {
                            basicInfo.setHostState(hostStates.get(0));
                        } else {
                            HostState realTimeHostState = getRealTimeHostState(basicInfo, hostStates);
                            basicInfo.setHostState(realTimeHostState);
                        }
                    }
                }
            }

            // 查询用户连接信息
            List<CloudPluginSceneConnectionModel> connections = cloudPluginSceneConnectionMapper.list(new HumanCondition()
                    .select(CloudPluginSceneConnectionModel::getSceneElement)
                    .select(CloudPluginSceneConnectionModel::getAccessUser)
                    .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                    .filterEqual(CloudPluginSceneConnectionModel::getScene, csceneInstanceBO.getScene().getTopology().getScene().getEid())
                    .reverseOrderBy(CloudPluginSceneConnectionModel::getPk));
            Map<String, List<AccessUserBO>> nodeId2Users = new HashMap<>();
            if (CollectionUtils.isNotEmpty(connections)) {
                Set<String> userIds = new HashSet<>();
                for (CloudPluginSceneConnectionModel connection : connections) {
                    String user = connection.getAccessUser();
                    userIds.add(user);
                }
                List<UserModel> userModels = UserCommon.getUsers(new ArrayList<>(userIds));
                Map<String, UserModel> userId2User = new HashMap<>();
                for (UserModel userModel1 : userModels) {
                    if (userId2User.put(userModel1.getId(), userModel1) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
                for (CloudPluginSceneConnectionModel model : connections) {
                    List<AccessUserBO> users = nodeId2Users.getOrDefault(model.getSceneElement(), new ArrayList<>());
                    nodeId2Users.put(model.getSceneElement(), users);
                    UserModel userModel = userId2User.get(model.getAccessUser());
                    if (userModel != null) {
                        AccessUserBO accessUser = new AccessUserBO();
                        accessUser.setId(userModel.getId());
                        accessUser.setName(userModel.getName());
                        users.add(accessUser);
                    }
                }
            }
            for (ElementMonitorBasicInfo basicInfo : basicInfos) {
                List<AccessUserBO> list = new ArrayList<>();
                Set<AccessUserBO> uniqueValues = new HashSet<>();
                for (AccessUserBO accessUserBO : nodeId2Users.getOrDefault(basicInfo.getId(), new ArrayList<>())) {
                    if (uniqueValues.add(accessUserBO)) {
                        list.add(accessUserBO);
                    }
                }
                basicInfo.setAccessUsers(list);
            }

            // 添加定时上报任务
            SceneHostStateReportScheduleService.addCsceneInstance(sceneId);
        }
        return basicInfos;
    }

    private HostState getRealTimeHostState(ElementMonitorBasicInfo basicInfo, List<HostState> hostStates) {
        HostState oldHostState = basicInfo.getHostState();
        HostState realTimeHostState = hostStates.get(0);
        if (Objects.nonNull(oldHostState)) {
            realTimeHostState.setCpuCount(oldHostState.getCpuCount());
            realTimeHostState.setDiskSpace(oldHostState.getDiskSpace());
            realTimeHostState.setMemoryTotal(oldHostState.getMemoryTotal());
        }
        return realTimeHostState;
    }

    public List<ElementMonitorBasicInfo> getAllSceneConfigElements(String id, SceneAllElementsQueryRequest request) {
        String sceneConfigId = csceneCommonService.translateSceneConfigId(id);
        if (sceneConfigId == null) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        List<ElementMonitorBasicInfo> res = new ArrayList<>();
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneConfigId, null, null);
        CloudSceneConfigPluginHandler cloudConfigPlugin = sceneConfigHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Stream<SceneConfigElementBO> stream = sceneConfigHandler.getElementMap().values().stream();
        // 根据名称模糊筛选
        if (StringUtils.isNotEmpty(request.getName())) {
            stream = stream.filter(e -> e.getName().toLowerCase().contains(request.getName().toLowerCase()));
        }
        stream.forEach(e -> {
            CloudPluginSceneConfigElementBO cloudElement = cloudConfigPlugin.getElement(e.getId());
            if (!isSupportedVirtualType(cloudElement.getVirtualType()) && !Boolean.TRUE.equals(request.getIsAll())) {
                return;
            }
            ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
            List<ElementPort> ports = new ArrayList<>();
            basicInfo.setSceneId(id);
            basicInfo.setEid(e.getEid());
            basicInfo.setId(e.getId());
            basicInfo.setName(e.getName());
            basicInfo.setIcon2(FsCommon.genHttpUrl(e.getIcon2()));
            basicInfo.setSystemType(e.getSystemType());
            basicInfo.setType(e.getType());
            basicInfo.setSystemTypeName(SystemTypeUtils.getStandardSystemType(e.getSystemType()));
            if (CollectionUtils.isNotEmpty(e.getPorts())) {
                for (SceneConfigElementPortBO port : e.getPorts()) {
                    ElementPort elementPort = new ElementPort();
                    elementPort.setIp(port.getIp());
                    elementPort.setName(port.getName());
                    elementPort.setAccessModes(port.getAccessModes());
                    ports.add(elementPort);
                }
                basicInfo.setPorts(ports);
            }

            basicInfo.setHost(cloudElement.getHost());
            String imageName;
            if (StringUtils.contains(cloudElement.getImage(), cloudElement.getImageUuid())) {
                imageName = cloudElement.getImage().replaceFirst(cloudElement.getImageUuid(), "");
            } else {
                imageName = cloudElement.getImage();
            }
            basicInfo.setImageName(imageName);
            basicInfo.setImageType(cloudElement.getImageType());
            basicInfo.setRole(cloudElement.getRole());
            basicInfo.setIsBuildIn(cloudElement.getIsBuildIn());
            basicInfo.setCategory(cloudElement.getCategory());
            basicInfo.setVirtualType(cloudElement.getVirtualType());
            basicInfo.setExternal(cloudElement.getExternal());
            res.add(basicInfo);
        });
        List<ElementMonitorBasicInfo> result;
        // 根据设备类型筛选
        if (Objects.nonNull(request.getCategory())) {
            List<ElementMonitorBasicInfo> list = new ArrayList<>();
            for (ElementMonitorBasicInfo b : res) {
                if (b.getCategory().equals(request.getCategory())) {
                    list.add(b);
                }
            }
            result = list;
        } else {
            result = res;
        }
        return result;
    }

    private ElementMonitorBasicInfo processElementBasicInfo(CsceneInstanceBO csceneInstanceBO, String sceneId, String nodeId,
                                                            boolean fetchRealTimeHostState, boolean isAll) {
        ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
        basicInfo.setSceneId(sceneId);
        elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, nodeId);
        if (isSupportedVirtualType(basicInfo.getVirtualType()) || isAll) {
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
        List<UserModel> users = new ArrayList<>();
        for (String id : userIds) {
            UserModel user = UserUtils.getUser(id);
            users.add(user);
        }
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

        List<ElementMonitorBasicInfo> list = getAllSceneElements(sceneId, true, new SceneAllElementsQueryRequest());

        // cpu
        Comparator<ElementMonitorBasicInfo> sortByCpu = Comparator.comparing(e -> {
            HostState hostState = e.getHostState();
            return hostState != null ? hostState.getCpuPercent() : null;
        }, Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<ElementMonitorBasicInfo> sortByMemory = Comparator.comparing(e -> {
            HostState hostState = e.getHostState();
            return hostState != null ? hostState.getMemoryPercent() : null;
        }, Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<ElementMonitorBasicInfo> sortByDisk = Comparator.comparing(e -> {
            HostState hostState = e.getHostState();
            return hostState != null ? hostState.getDiskPercent() : null;
        }, Comparator.nullsFirst(Comparator.naturalOrder()));

        List<ElementMonitorBasicInfo> toSort1 = new ArrayList<>();
        for (ElementMonitorBasicInfo monitorBasicInfo : list) {
            toSort1.add(monitorBasicInfo);
        }
        toSort1.sort(sortByCpu.reversed());
        List<ElementMonitorBasicInfo> sortByCpuList = new ArrayList<>();
        long limit2 = topCount;
        for (ElementMonitorBasicInfo monitorBasicInfo : toSort1) {
            if (limit2-- == 0) break;
            sortByCpuList.add(monitorBasicInfo);
        }
        List<ElementMonitorBasicInfo> listToSort = new ArrayList<>();
        for (ElementMonitorBasicInfo elementMonitorBasicInfo : list) {
            listToSort.add(elementMonitorBasicInfo);
        }
        listToSort.sort(sortByMemory.reversed());
        List<ElementMonitorBasicInfo> sortByMemoryList = new ArrayList<>();
        long limit1 = topCount;
        for (ElementMonitorBasicInfo elementMonitorBasicInfo : listToSort) {
            if (limit1-- == 0) break;
            sortByMemoryList.add(elementMonitorBasicInfo);
        }
        List<ElementMonitorBasicInfo> toSort = new ArrayList<>();
        for (ElementMonitorBasicInfo basicInfo : list) {
            toSort.add(basicInfo);
        }
        toSort.sort(sortByDisk.reversed());
        List<ElementMonitorBasicInfo> sortByDiskList = new ArrayList<>();
        long limit = topCount;
        for (ElementMonitorBasicInfo basicInfo : toSort) {
            if (limit-- == 0) break;
            sortByDiskList.add(basicInfo);
        }

        Map<String, List<ElementMonitorBasicInfo>> result = new HashMap<>();
        result.put("cpu", sortByCpuList);
        result.put("memory", sortByMemoryList);
        result.put("disk", sortByDiskList);
        return result;
    }


    /**
     * 是否是需要的节点
     * @param virtualType
     * @return
     */
    private boolean isSupportedVirtualType(Integer virtualType) {
        if (Objects.isNull(virtualType)) {
            return false;
        }
        boolean supported = false;
        if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == virtualType
                || BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == virtualType
                || BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE == virtualType
                || BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS == virtualType) {
            supported = true;
        }
        return supported;
    }


}
