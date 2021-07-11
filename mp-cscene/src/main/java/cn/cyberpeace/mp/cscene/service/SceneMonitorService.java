package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementPort;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.AccessUserBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


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
                
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setVirtualMachineCount(sceneMonitorInfo.getVirtualMachineCount() + 1);
                }
                
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setContainerCount(sceneMonitorInfo.getContainerCount() + 1);
                }
                
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setEntityCount(sceneMonitorInfo.getEntityCount() + 1);
                }

                

                
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setRouterCount(sceneMonitorInfo.getRouterCount() + 1);
                }


                
                if (Objects.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL, elementBO.getVirtualType())) {
                    sceneMonitorInfo.setFirewallCount(sceneMonitorInfo.getFirewallCount() + 1);
                }

                

            });

            
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

    public List<ElementMonitorBasicInfo> getSceneElements(String sceneId, Boolean supportProbe, Boolean supportFlow) {
        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        for (var element : csceneInstanceBO.getScene().getTopology().getElements()) {
            ElementMonitorBasicInfo basicInfo = new ElementMonitorBasicInfo();
            elementMonitorService.processPluginElement(basicInfo, csceneInstanceBO, element.getId());
            basicInfos.add(basicInfo);
        }
        if (Boolean.TRUE.equals(supportProbe)) {
            basicInfos = basicInfos.stream().filter(element -> {
                if (element.getVirtualType() == null) {
                    return false;
                }
                
                boolean isVm = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE;
                boolean isContainer = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
                boolean isCoreRouter = ElementTypeUtils.isCoreRouter(element.getImageName());
                if ((isVm || isContainer) && !isCoreRouter) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
        }
        if (Boolean.TRUE.equals(supportFlow)) {
            basicInfos = basicInfos.stream().filter(element -> {
                boolean isVm = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE;
                boolean isContainer = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
                boolean isEntity = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE;
                boolean isDynamips = element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS;
                return isVm || isContainer || isEntity || isDynamips;
            }).collect(Collectors.toList());
        }
        return basicInfos;
    }

    
    public List<ElementMonitorBasicInfo> getAllSceneElements(String sceneId, Boolean fetchRealTimeHostState, SceneAllElementsQueryRequest request) {
        log.info("sceneId:{}, fetchRealTimeHostState:{}", sceneId, fetchRealTimeHostState);
        if (Objects.isNull(fetchRealTimeHostState)) {
            fetchRealTimeHostState = true;
        }
        boolean isAll = Objects.nonNull(request) && Objects.nonNull(request.getIsAll()) ? request.getIsAll() : Boolean.FALSE;
        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();
        var sceneInstanceId = csceneCommonService.translateSceneId(sceneId);

        if (StringUtils.isEmpty(sceneInstanceId)) {
            return Collections.emptyList();
        }

        SceneHandler sceneHandler = new SceneHandler(sceneInstanceId, null, null);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);

        
        var stream = sceneHandler.getElementMap().values().stream();
        if (Objects.nonNull(request) && StringUtils.isNotEmpty(request.getName())) {
            stream = stream.filter(e -> e.getName().toLowerCase().contains(request.getName().toLowerCase()));
        }
        List<String> nodeIds = stream
                .filter(n -> !Boolean.TRUE.equals(n.getIsAuxiliary()))
                .map(SceneElementBO::getId)
                .collect(Collectors.toList());
        
        if (Objects.nonNull(request) && CollectionUtils.isNotEmpty(request.getElementIds())) {
            nodeIds.retainAll(request.getElementIds());
        }

        List<ElementMonitorBasicInfo> basicInfos = new ArrayList<>();
        List<CompletableFuture<ElementMonitorBasicInfo>> cfList = new ArrayList<>();
        for (String nodeId : nodeIds) {
            
            var cf = CompletableFuture.supplyAsync(() -> processElementBasicInfo(csceneInstanceBO, sceneId, nodeId, false, isAll));
            cfList.add(cf);
        }

        if (CollectionUtil.isNotEmpty(cfList)) {
            var cfArray = cfList.toArray(new CompletableFuture[0]);
            CompletableFuture<List<ElementMonitorBasicInfo>> finalCF = CompletableFuture.allOf(cfArray).thenApply(v -> cfList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            basicInfos = finalCF.join();

            if (CollectionUtil.isNotEmpty(basicInfos)) {
                if (Boolean.TRUE.equals(request.getIsCollect())) {
                    basicInfos = basicInfos.stream().filter(basicInfo -> Objects.nonNull(basicInfo)
                            && !ElementTypeUtils.isCoreRouter(basicInfo.getImageName())
                            && !BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(basicInfo.getImageType())).collect(Collectors.toList());
                } else {
                    basicInfos = basicInfos.stream().filter(Objects::nonNull).collect(Collectors.toList());
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

        
        if (Objects.nonNull(request) && Objects.nonNull(request.getCategory())) {
            basicInfos = basicInfos.stream().filter(b -> b.getCategory().equals(request.getCategory())).collect(Collectors.toList());
        }
        
        if (Objects.nonNull(request) && StringUtils.isNotEmpty(request.getSystemTypeName())) {
            basicInfos = basicInfos.stream().filter(b -> b.getSystemTypeName().equals(request.getSystemTypeName())).collect(Collectors.toList());
        }

        
        if (CollectionUtils.isNotEmpty(basicInfos)) {
            for (var basicInfo : basicInfos) {
                if (CollectionUtils.isEmpty(basicInfo.getPorts())) {
                    continue;
                }
                for (var p : basicInfo.getPorts()) {
                    var cloudPort = cloudPlugin.getPort(p.getId());
                    p.setCloudAccessModes(cloudPort.getAccessModes());
                }
            }
        }

        if (fetchRealTimeHostState && CollectionUtil.isNotEmpty(basicInfos)) {
            
            var agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(sceneId, null);
            if (CollectionUtil.isNotEmpty(agentNames)) {
                AgentNameBasedRequest agentNameBasedRequest = new AgentNameBasedRequest();
                agentNameBasedRequest.setAgentNames(agentNames);
                agentNameBasedRequest.setPageSize(1);

                Map<String, List<HostState>> map = hostStateService.mapHostStatesByAgentName(agentNameBasedRequest);
                basicInfos.forEach(basicInfo -> {
                    var hostStates = map.get(basicInfo.getEid());
                    if (CollectionUtil.isNotEmpty(hostStates)) {
                        basicInfo.setHostState(hostStates.get(0));
                    }
                });
            }

            
            var connections = cloudPluginSceneConnectionMapper.list(new HumanCondition()
                    .select(CloudPluginSceneConnectionModel::getSceneElement)
                    .select(CloudPluginSceneConnectionModel::getAccessUser)
                    .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                    .filterEqual(CloudPluginSceneConnectionModel::getScene, csceneInstanceBO.getScene().getTopology().getScene().getEid())
                    .reverseOrderBy(CloudPluginSceneConnectionModel::getPk));
            Map<String, List<AccessUserBO>> nodeId2Users = new HashMap<>();
            if (CollectionUtils.isNotEmpty(connections)) {
                Set<String> userIds = connections.stream().map(CloudPluginSceneConnectionModel::getAccessUser).collect(Collectors.toSet());
                var userModels = UserCommon.getUsers(new ArrayList<>(userIds));
                Map<String, UserModel> userId2User = userModels.stream().collect(Collectors.toMap(UserModel::getId, userModel -> userModel));
                connections.forEach(model -> {
                    var users = nodeId2Users.getOrDefault(model.getSceneElement(), new ArrayList<>());
                    nodeId2Users.put(model.getSceneElement(), users);
                    var userModel = userId2User.get(model.getAccessUser());
                    if (userModel != null) {
                        AccessUserBO accessUser = new AccessUserBO();
                        accessUser.setId(userModel.getId());
                        accessUser.setName(userModel.getName());
                        users.add(accessUser);
                    }
                });
            }
            basicInfos.forEach(basicInfo -> basicInfo.setAccessUsers(nodeId2Users.getOrDefault(basicInfo.getId(), new ArrayList<>())
                    .stream().distinct().collect(Collectors.toList())));

            
            SceneHostStateReportScheduleService.addCsceneInstance(sceneId);
        }
        return basicInfos;
    }

    public List<ElementMonitorBasicInfo> getAllSceneConfigElements(String id, SceneAllElementsQueryRequest request) {
        String sceneConfigId = csceneCommonService.translateSceneConfigId(id);
        if (sceneConfigId == null) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        List<ElementMonitorBasicInfo> res = new ArrayList<>();
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneConfigId, null, null);
        CloudSceneConfigPluginHandler cloudConfigPlugin = sceneConfigHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var stream = sceneConfigHandler.getElementMap().values().stream();
        
        if (StringUtils.isNotEmpty(request.getName())) {
            stream = stream.filter(e -> e.getName().toLowerCase().contains(request.getName().toLowerCase()));
        }
        stream.forEach(e -> {
            var cloudElement = cloudConfigPlugin.getElement(e.getId());
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
                e.getPorts().forEach(port -> {
                    ElementPort elementPort = new ElementPort();
                    elementPort.setIp(port.getIp());
                    elementPort.setName(port.getName());
                    elementPort.setAccessModes(port.getAccessModes());
                    ports.add(elementPort);
                });
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
        
        if (Objects.nonNull(request.getCategory())) {
            result = res.stream().filter(b -> b.getCategory().equals(request.getCategory())).collect(Collectors.toList());
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


    
    public List<String> listIps(BaseSceneElementRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);

        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(CollectionUtil.toList(request.getSceneId()));
        if (CollectionUtil.isEmpty(translatedSceneIds)) {
            return new ArrayList<>();
        }
        return scenePluginCollectionSceneService.listIPs(translatedSceneIds.get(0), request.getElementIds()).getSource();
    }

    
    public List<UserModel> listSceneRecordingUsers(BaseSceneElementRequest request) {
        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(CollectionUtil.toList(request.getSceneId()));
        if (CollectionUtil.isEmpty(translatedSceneIds)) {
            return new ArrayList<>();
        }
        List<String> userIds = scenePluginCloudSceneService.getRecordingUsers(translatedSceneIds.get(0), request.getElementIds()).getSource();
        List<UserModel> users = userIds.stream().map(id -> UserUtils.getUser(id)).collect(Collectors.toList());
        return users;
    }


    
    public Map<String, List<ElementMonitorBasicInfo>> topAggrByHoststates(String sceneId, Integer topCount) {
        if (Objects.isNull(topCount)) {
            topCount = 5;
        }
        if (topCount < 1) {
            log.error("topCount:{} should not be less than 1", topCount);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        List<ElementMonitorBasicInfo> list = getAllSceneElements(sceneId, true, new SceneAllElementsQueryRequest());

        
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

        List<ElementMonitorBasicInfo> sortByCpuList = list.stream().sorted(sortByCpu.reversed()).limit(topCount).collect(Collectors.toList());
        List<ElementMonitorBasicInfo> sortByMemoryList = list.stream().sorted(sortByMemory.reversed()).limit(topCount).collect(Collectors.toList());
        List<ElementMonitorBasicInfo> sortByDiskList = list.stream().sorted(sortByDisk.reversed()).limit(topCount).collect(Collectors.toList());

        Map<String, List<ElementMonitorBasicInfo>> result = new HashMap<>();
        result.put("cpu", sortByCpuList);
        result.put("memory", sortByMemoryList);
        result.put("disk", sortByDiskList);
        return result;
    }


    
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
