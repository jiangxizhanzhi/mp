package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountByReferenceTypeBO;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountByReferenceTypeModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneReporter;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.service.action.CsceneCallAction;
import cn.cyberpeace.mp.cscene.service.impl.ScreenshotServiceImpl;
import cn.cyberpeace.mp.cscene.service.taskpool.CsceneReportTaskPool;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.cscene.util.ReentrantLockUtils;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.service.ScenePluginFlowSceneService;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
public class CsceneInstanceService extends BaseModelServiceImpl<CsceneInstanceModel, CsceneInstanceMapper,
        BaseModelFields, QueryParams> {

    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;

    public static final String PROBE_COLLECTION_DATA_REQUEST_ID = "mp:probe:collection:request_id:";

    @Autowired
    private RabbitmqCsceneReporter csceneReporter;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private ScenePluginFlowSceneService scenePluginFlowSceneService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScreenshotServiceImpl screenshotService;

    @Autowired
    private ProbeService probeService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private LogFileService logFileService;

    @Autowired
    private StringRedisTemplate redisTemplate;



    private final CsceneReportTaskPool csceneReportTaskPool = CsceneReportTaskPool.getInstance();

    private static final String ASSISTANT = "assistance";

    private static final String MONITOR = "monitor";

    private static final String ALL = "all";

    private List<CsceneInstanceRoleModel> readCsceneInstanceRoles(String[] csceneInstanceIds) {
        if (csceneInstanceIds == null || csceneInstanceIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneInstanceRoleMapper.list(new HumanCondition()
                .filterIn(CsceneInstanceRoleModel::getCscene, csceneInstanceIds));
    }

    public String getSceneId(String csceneId) {
        var csceneInstanceResult = get(csceneId).getSource();
        if (csceneInstanceResult == null) {
            return null;
        }
        var csceneInstance = csceneInstanceResult.getObj();
        var sceneId = csceneInstance.getScene();
        return sceneId;
    }

    private List<CsceneInstanceModel> getCsceneInstanceObjs(List<String> csceneIds) {
        if (csceneIds.isEmpty()) {
            return new ArrayList<>();
        }
        var queryParams = new QueryParams();
        queryParams.setIds(csceneIds.toArray(new String[0]));
        var csceneInstancesResult = list(queryParams).getSource();
        if (csceneInstancesResult == null) {
            return new ArrayList<>();
        }
        return csceneInstancesResult.getObjs();
    }

    private ListObjServiceResult<CollectionHostEventBO> getProbeCollectData(String sceneInstanceId, String sceneId, String requestId,
                                                                            List<String> nodeIds, SceneHandler sceneHandler,
                                                                            Map<String, Map<String, String>> nodeId2ProbeNameMap,
                                                                            String probeId, HostEventQueryParams queryParams) {
        String scrollId = null;
        String redisKey = null;
        boolean isScrollRequest = false;
        if (StringUtils.isNotBlank(requestId)) {
            isScrollRequest = true;
            redisKey = PROBE_COLLECTION_DATA_REQUEST_ID + requestId;
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNotEmpty(redisValue)) {
                scrollId = redisValue;
            }
        }

        var startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
        var endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
        int total = 0;
        List<CollectionHostEventBO> result = new ArrayList<>();
        for (String nodeId : nodeIds) {
            var element = sceneHandler.getElement(nodeId);
            Map<String, String> probeId2Name = nodeId2ProbeNameMap.get(nodeId);
            List<?> res = null;
            if (ProbeConstant.ProbeId.HARDWARE_INFO.equals(probeId) || ProbeConstant.ProbeId.LOG_FILE.equals(probeId)) {
                BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
                request.setSceneId(sceneInstanceId);
                request.setElementIds(CollUtil.toList(element.getId()));
                request.setPage(queryParams.getPage());
                request.setPageSize(queryParams.getPageSize());
                if (ProbeConstant.ProbeId.HARDWARE_INFO.equals(probeId)) {
                    // 硬件信息
                    res = hostStateService.getHostStates(request);
                    if (!CollUtil.isEmpty(res)) {
                        total = res.size() + total;
                    }
                } else {
                    // 原始日志信息
                    request.setFetchIfSceneInstanceShutdown(true);
                    var r = logFileService.pageByElementId(request);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                }
            } else if (ProbeConstant.ProbeId.OS_INFO.equals(probeId)) {
                // 操作系统信息
                BaseSceneElementRequest request = new BaseSceneElementRequest();
                request.setSceneId(sceneInstanceId);
                request.setElementIds(CollUtil.toList(element.getId()));
                res = CollUtil.toList(sceneElementMonitorService.getOSInfo(request));
                if (!CollUtil.isEmpty(res)) {
                    total = res.size() + total;
                }
            } else if (ProbeConstant.ProbeId.NETWORK_INTERFACE_INFO.equals(probeId) || ProbeConstant.ProbeId.PORT_INFO.equals(probeId) ||
                    ProbeConstant.ProbeId.PROCESS_INFO.equals(probeId)) {
                ListPageInfoQueryParams params = new ListPageInfoQueryParams();
                params.setSceneIds(CollUtil.toList(sceneInstanceId));
                params.setNodeIds(CollUtil.toList(nodeId));
                params.setPage(queryParams.getPage());
                params.setPageSize(queryParams.getPageSize());
                params.setFetchIfSceneInstanceShutdown(true);
                if (ProbeConstant.ProbeId.NETWORK_INTERFACE_INFO.equals(probeId)) {
                    // 网卡信息
                    var r = hostStateService.pageNetifaces(params);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                } else if (ProbeConstant.ProbeId.PORT_INFO.equals(probeId)) {
                    // 端口信息
                    var r = hostStateService.pagePorts(params);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                } else {
                    // 进程信息
                    var r = hostStateService.pageProcesses(params);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                }
            } else if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probeId) || ProbeConstant.ProbeId.REGISTRY_MODIFY.equals(probeId) ||
                    ProbeConstant.ProbeId.OS_LOG.equals(probeId) || ProbeConstant.ProbeId.APPLICATION_LOG.equals(probeId) ||
                    ProbeConstant.ProbeId.SECURITY_LOG.equals(probeId)) {
                HostEventQueryParams params = new HostEventQueryParams();
                params.setNodeIds(CollUtil.toList(nodeId));
                params.setPage(queryParams.getPage());
                params.setPageSize(queryParams.getPageSize());
                params.setStartTime(queryParams.getStartTime());
                params.setEndTime(queryParams.getEndTime());
                ListObjServiceResult<CollectionHostEventBO> r;
                if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probeId)) {
                    // 文件变更信息
                    params.setRuleGroups(CsceneBizConstant.FILE_MODIFY_GROUP);
                } else if (ProbeConstant.ProbeId.REGISTRY_MODIFY.equals(probeId)) {
                    // 注册表变更信息
                    params.setRuleGroups(CsceneBizConstant.REGISTRY_MODIFY_GROUP);
                } else if (ProbeConstant.ProbeId.OS_LOG.equals(probeId)) {
                    // 操作系统日志信息
                    params.setCatagories(CsceneBizConstant.OS_CATEGORY);
                } else if (ProbeConstant.ProbeId.APPLICATION_LOG.equals(probeId)) {
                    // 应用系统日志信息
                    params.setCatagories(CsceneBizConstant.APPLICATION_CATEGORY);
                } else {
                    // 安全系统日志信息
                    params.setCatagories(CsceneBizConstant.SECURITY_CATEGORY);
                }
                if (StringUtils.isEmpty(scrollId)) {
                    params.setScroll(isScrollRequest);
                    r = scenePluginCollectionSceneService.listHostEvents(sceneId, params);
                } else {
                    r = scenePluginCollectionSceneService.scrollHostEvents(sceneId, scrollId, params);
                }

                if (r.getSource() != null) {
                    res = r.getSource().getObjs();
                    total = r.getSource().getTotal() + total;
                }
            } else {
                // 自定义探针信息
                WazuhEventRequest request = new WazuhEventRequest();
                request.setAgentNames(CollUtil.toList(element.getEid()));
                request.setStartTime(startTime);
                request.setEndTime(endTime);
                request.setPageNum(queryParams.getPage());
                request.setPageSize(queryParams.getPageSize());
                var r = dataCollectionService.queryCustomProbeData(request, CollUtil.toList(probeId));
                res = r.getObjs();
                total = r.getTotal() + total;
            }
            if (CollUtil.isEmpty(res)) {
                continue;
            }

            if (StringUtils.isBlank(scrollId) && isScrollRequest) {
                if (CollUtil.isNotEmpty(res)) {
                    if (Objects.nonNull(res.get(0)) && res.get(0) instanceof CollectionHostEventBO) {
                        CollectionHostEventBO event = (CollectionHostEventBO) res.get(0);
                        if (Objects.nonNull(event)) {
                            scrollId = event.getScrollId();
                        }
                    }
                }
            }

            if (StringUtils.isNotBlank(scrollId) && StringUtils.isNotBlank(redisKey)) {
                redisTemplate.opsForValue().set(redisKey, scrollId, Duration.ofMinutes(30));
            }

            res.forEach(r -> {
                CollectionHostEventBO hostEventBO = new CollectionHostEventBO();
                hostEventBO.setSceneId(sceneInstanceId);
                hostEventBO.setNodeId(nodeId);
                hostEventBO.setNodeName(element.getName());
                hostEventBO.setProbeId(probeId);
                hostEventBO.setProbeName(probeId2Name.get(probeId));
                Map<String, Object> map = JsonCommon.beanToMap(r);
                map.put("scrollId", null);
                hostEventBO.setContent(map);
                result.add(hostEventBO);
            });
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    private ListObjServiceResult<CollectionHostEventBO> getCustomProbeCollectData(String sceneInstanceId, String sceneId,
                                                                                  List<String> nodeIds, CollectionScenePluginHandler pluginHandler,
                                                                                  Map<String, Map<String, String>> nodeId2ProbeNameMap,
                                                                                  HostEventQueryParams queryParams) {
        var startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
        var endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
        List<CollectionHostEventBO> result = new ArrayList<>();
        var batchSceneHandler = new BatchSceneHandler(CollUtil.toList(sceneId), null);
        var eid2Element = batchSceneHandler.getAllEid2ElementMap();
        // 获取节点所有自定义探针id
        List<String> agentNames = new ArrayList<>();
        List<String> probeIds = new ArrayList<>();
        pluginHandler.getElementMap().values().forEach(e -> {
            var element = e.getSrcElement();
            if (nodeIds.contains(element.getId())) {
                agentNames.add(element.getEid());
            }
            e.getProbes().forEach(probe -> {
                if (Boolean.FALSE.equals(probe.getCommon())) {
                    probeIds.add(probe.getId());
                }
            });
        });
        if (CollUtil.isEmpty(probeIds)) {
            return new ListObjServiceResult<>();
        }
        // 构造查询请求
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPageNum(queryParams.getPage());
        request.setPageSize(queryParams.getPageSize());
        request.setAgentNames(agentNames);
        // 分页查询自定义探针数据
        var listObjResult = dataCollectionService.queryCustomProbeData(request, probeIds);
        var res = listObjResult.getObjs();
        if (CollUtil.isEmpty(res)) {
            return new ListObjServiceResult<>();
        }
        var total = listObjResult.getTotal();
        res.forEach(r -> {
            var element = eid2Element.get(r.getAgentName());
            Map<String, String> probeId2Name = nodeId2ProbeNameMap.get(element.getId());
            CollectionHostEventBO hostEventBO = new CollectionHostEventBO();
            hostEventBO.setSceneId(sceneInstanceId);
            hostEventBO.setNodeId(element.getId());
            hostEventBO.setNodeName(element.getName());
            hostEventBO.setProbeId(r.getProbeId());
            hostEventBO.setProbeName(probeId2Name.get(r.getProbeId()));
            hostEventBO.setTime(r.getTime());
            var rawOutput = JsonCommon.beanToMap(r).get("rawOutPut");
            if (rawOutput != null) {
                hostEventBO.setContent(JsonCommon.parseMap(rawOutput.toString()));
            }
            result.add(hostEventBO);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Autowired
    public CsceneInstanceService(CsceneInstanceMapper modelMapper, CsceneInstanceRoleMapper csceneInstanceRoleMapper) {
        setModelMapper(modelMapper);
        setCsceneInstanceRoleMapper(csceneInstanceRoleMapper);
    }

    public ListObjServiceResult<CsceneInstanceBO> listBO(QueryParams queryParams, CsceneContextBO context) {
        var listResult = super.list(queryParams);
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        var instanceObjMap = listResult.getSource().getObjs().stream()
                .collect(Collectors.toMap(BaseModel::getId, x -> x));
        var instanceRoleObjMap = readCsceneInstanceRoles(instanceObjMap.keySet()
                .toArray(new String[0])).stream().collect(Collectors.groupingBy(CsceneInstanceRoleModel::getCscene));
        List<CsceneInstanceBO> instanceBOs = new ArrayList<>();
        instanceObjMap.forEach((instanceId, instanceObj) -> {
            var roleObjs = instanceRoleObjMap.getOrDefault(instanceId, new ArrayList<>());
            var instanceHandler = new CsceneInstanceHandler(instanceObj, roleObjs, context);
            instanceBOs.add(instanceHandler.toData());
        });
        return new ListObjServiceResult<>(new ListObjResult<>(instanceBOs, listResult.getSource().getTotal(),
                listResult.getSource().getFields()));
    }

    public ServiceResult<CsceneInstanceBO> getById(String id, CsceneContextBO context) {
        var instanceHandler = new CsceneInstanceHandler(id, context);
        return new ServiceResult<>(instanceHandler.toData());
    }

    public ServiceResult<Boolean> deleteById(String id, boolean logic, CsceneContextBO context) {
        if (ReentrantLockUtils.tryLock(id)) {
            try {
                var instanceHandler = new CsceneInstanceHandler(id, context);
                NodeLimitUtils.substractLimitedNodeCount(instanceHandler);
                String sceneId = this.getSceneId(id);
                var flag = instanceHandler.delete(logic);
                if (sceneId != null) {
                    scenePluginCloudSceneService.deleteSceneRecords(sceneId, logic);
                }
                if (!logic) {
                    screenshotService.batchDeleteBySceneId(id);
                    scenePluginCollectionSceneService.asyncCleanProbeData(sceneId);
                }
                if (flag) {
                    CsceneReportTaskPool.getInstance().executeTask(() -> csceneReporter.reportDeleteCsceneInstance(id));
                }
            } finally {
                ReentrantLockUtils.unLock(id);
            }
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean logic, CsceneContextBO context) {
        var result = 0;
        Map<String, Object> originPassThrough = SysContextHolder.getPassThroughMap();
        for (String id : ids) {
            if (Boolean.TRUE.equals(deleteById(id, logic, context).getSource())) {
                result += 1;
            }
            SysContextHolder.setPassThroughMap(originPassThrough);
        }

        if (result < ids.length) {
            log.error("Service bulk delete cscene instance {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    public ServiceResult<Boolean> clearById(String id, CsceneContextBO context) {
        var csceneInstanceObjs = getCsceneInstanceObjs(List.of(id)).stream()
                .filter(BaseCsceneModel::getDeleted).collect(Collectors.toList());
        if (csceneInstanceObjs.isEmpty()) {
            return new ServiceResult<>(false);
        }
        return deleteById(csceneInstanceObjs.get(0).getId(), false, context);
    }

    public ServiceResult<Integer> bulkClearByIds(String[] ids, CsceneContextBO context) {
        var csceneInstanceObjs = getCsceneInstanceObjs(Arrays.asList(ids)).stream()
                .filter(BaseCsceneModel::getDeleted).collect(Collectors.toList());
        return bulkDeleteByIds(csceneInstanceObjs.stream().map(BaseModel::getId).toArray(String[]::new), false, context);
    }

    public ServiceResult<List<UserNodeSessionInfo>> getNodeSessions(List<String> ids, List<String> nodeIds, List<String> userIds, String type) {
        if (type == null) {
            type = MONITOR;
        }
        var csceneInstances = getCsceneInstanceObjs(ids);
        if (csceneInstances.isEmpty()) {
            return new ServiceResult<>(new ArrayList<>());
        }
        var sceneIdCsceneInstanceMap = csceneInstances.stream()
                .collect(Collectors.toMap(csceneInstance -> csceneInstance.getScene(), csceneInstance -> csceneInstance));
        List<String> sceneIds = new ArrayList<>(sceneIdCsceneInstanceMap.keySet());
        ServiceResult<List<NodeSessionBO>> nodeSessionResult = new ServiceResult<>();
        switch (type) {
            case ASSISTANT:
                nodeSessionResult = scenePluginCloudSceneService.getSceneRemoteSessions(
                        sceneIds, nodeIds, userIds, false, true);
                break;
            case ALL:
                nodeSessionResult = scenePluginCloudSceneService.getSceneRemoteSessions(
                        sceneIds, nodeIds, userIds, true, true);
                break;
            case MONITOR:
                nodeSessionResult = scenePluginCloudSceneService.getSceneRemoteSessions(
                        sceneIds, nodeIds, userIds, true, false);
                break;
            default:
                throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        nodeSessionResult.getSource().forEach(nodeSession -> {
            var sceneId = nodeSession.getSceneId();
            var csceneInstance = sceneIdCsceneInstanceMap.getOrDefault(sceneId, null);
            if (csceneInstance == null) {
                return;
            }
            nodeSession.setSceneId(csceneInstance.getId());
        });

        List<UserNodeSessionInfo> result = new ArrayList<>();
        nodeSessionResult.getSource().forEach(nodeSession -> {
            UserNodeSessionInfo userNodeSessionInfo = new UserNodeSessionInfo();
            BeanUtils.copyProperties(nodeSession, userNodeSessionInfo);
            result.add(userNodeSessionInfo);
        });

        Set<String> existUserIds = new HashSet<>();
        result.forEach(nodeSession -> {
            if (StringUtils.isNotEmpty(nodeSession.getUser())) {
                existUserIds.add(nodeSession.getUser());
            }
        });
        if (!existUserIds.isEmpty()) {
            List<UserDto> users = userService.listByUserIds(existUserIds.toArray(new String[]{}));
            if (CollectionUtils.isNotEmpty(users)) {
                Map<String, String> userId2Name = new HashMap<>();
                users.forEach(user -> userId2Name.put(user.getId(), user.getName()));
                result.forEach(nodeSession -> {
                    nodeSession.setUserName(userId2Name.containsKey(nodeSession.getUser()) ? userId2Name.get(nodeSession.getUser()) : StringUtils.EMPTY);
                });
            }
        }
        return new ServiceResult<>(result);
    }

    public ListObjServiceResult<NodeRecordingBO> listRecordings(List<String> ids, List<String> nodeIds, List<String> userIds,
                                                                RecordingQueryParams queryParams) {
        var csceneInstances = getCsceneInstanceObjs(ids);
        if (csceneInstances.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        var sceneIdCsceneInstanceMap = csceneInstances.stream()
                .collect(Collectors.toMap(csceneInstance -> csceneInstance.getScene(), csceneInstance -> csceneInstance));
        List<String> sceneIds = new ArrayList<>(sceneIdCsceneInstanceMap.keySet());
        var nodeRecordingResult = scenePluginCloudSceneService.getSceneRemoteRecordings(
                sceneIds, nodeIds, userIds, queryParams);
        nodeRecordingResult.getSource().getObjs().forEach(nodeRecording -> {
            var sceneId = nodeRecording.getSceneId();
            var csceneInstance = sceneIdCsceneInstanceMap.getOrDefault(sceneId, null);
            if (csceneInstance == null) {
                return;
            }
            nodeRecording.setSceneId(csceneInstance.getId());

            if (StringUtils.isNotEmpty(nodeRecording.getUser())) {
                nodeRecording.setUsername(UserUtils.getUser(nodeRecording.getUser()).getUsername());
            }
        });
        return nodeRecordingResult;
    }

    public ListObjServiceResult<CsceneCollectionFlowBO> listFlows(String id, FlowQueryParams queryParams) {
        String[] csceneIds = StringUtils.split(id, ",");
        Map<String, String> sceneId2CsceneId = new HashMap<>();
        for (String csceneId : csceneIds) {
            var sceneId = getSceneId(csceneId);
            if (StringUtils.isNotEmpty(sceneId)) {
                sceneId2CsceneId.put(sceneId, csceneId);
            }
        }
        if (sceneId2CsceneId.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var serviceResult = scenePluginCollectionSceneService.listFlows(new ArrayList<>(sceneId2CsceneId.keySet()), queryParams);
        var flowBOS = serviceResult.getSource().getObjs();
        List<CsceneCollectionFlowBO> result = flowBOS.stream().map(bo -> {
            CsceneCollectionFlowBO csceneCollectionFlowBO = new CsceneCollectionFlowBO();
            BeanUtils.copyProperties(bo, csceneCollectionFlowBO);
            if (bo.getSceneId() != null) {
                csceneCollectionFlowBO.setSceneId(sceneId2CsceneId.get(bo.getSceneId()));
            }
            return csceneCollectionFlowBO;
        }).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(result, serviceResult.getSource().getTotal()));
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStates(String id, HostStateQueryParams queryParams) {
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        return scenePluginCollectionSceneService.listHostStates(sceneId, queryParams);
    }

    /**
     * 事件分页列表
     * @param id
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(String id, HostEventQueryParams queryParams) {
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        return scenePluginCollectionSceneService.listHostEvents(sceneId, queryParams);
    }

    /**
     * 滚动分页列表
     * @param scrollId
     * @return
     */
    public ListObjServiceResult<CollectionHostEventBO> scrollHostEvents(String id, String scrollId, HostEventQueryParams queryParams) {
        if (StringUtils.isEmpty(scrollId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        return scenePluginCollectionSceneService.scrollHostEvents(sceneId, scrollId, queryParams);
    }

    public ListObjServiceResult<CollectionHostEventBO> getProbeCollectionData(String requestId, String sceneInstanceId, String probeId, HostEventQueryParams queryParams) {
        var sceneId = getSceneId(sceneInstanceId);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<String> nodeIds = queryParams.getNodeIds();
        if (nodeIds == null || nodeIds.isEmpty()) {
            nodeIds = sceneHandler.getElementMap().values().stream().map(e -> e.getId()).collect(Collectors.toList());
        }
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        // 非虚拟机和容器节点不支持探针采集
        List<String> newNodeIds = new ArrayList<>();
        for (String nodeId : nodeIds) {
            var cloudElement = cloudPluginHandler.getElement(nodeId);
            if (cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                    cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                newNodeIds.add(nodeId);
            }
        }
        if (newNodeIds.isEmpty()) {
            return new ListObjServiceResult<>();
        }
        Map<String, Map<String, String>> nodeId2ProbeNameMap = new HashMap<>();
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (Objects.isNull(pluginHandler)) {
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        pluginHandler.getElementMap().values().forEach(e -> {
            Map<String, String> probeId2Name = new HashMap<>();
            e.getProbes().forEach(probe -> {
                probeId2Name.put(probe.getId(), probe.getName());
            });
            nodeId2ProbeNameMap.put(e.getSrcElement().getId(), probeId2Name);
        });
        if (queryParams.getStartTime() == null) {
            queryParams.setStartTime(TimeUtils.localDateTimeToString(sceneHandler.getScene().getCreateTime()));
        }
        if (queryParams.getEndTime() == null) {
            queryParams.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        }
        if (probeId != null) {
            // 有探针id，查询指定探针采集数据
            return getProbeCollectData(sceneInstanceId, sceneId, requestId, newNodeIds, sceneHandler, nodeId2ProbeNameMap,
                    probeId, queryParams);
        } else {
            // 无探针id，查询所有自定义探针采集数据
            return getCustomProbeCollectData(sceneInstanceId, sceneId, newNodeIds, pluginHandler, nodeId2ProbeNameMap, queryParams);
        }
    }

    public ServiceResult<Boolean> startFlow(String id, String toNodeId, String flowId, String sourceIp, CsceneContextBO context) {
        CsceneContextHolder.setCallAction(CsceneCallAction.NODE_START_FLOW);
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(false);
        }

        return scenePluginFlowSceneService.startFlow(sceneId, toNodeId, flowId, sourceIp,
                context == null ? null : context.toSceneContext());
    }

    public ServiceResult<Boolean> stopFlow(String id, String toNodeId, String flowId, CsceneContextBO context) {
        CsceneContextHolder.setCallAction(CsceneCallAction.NODE_STOP_FLOW);
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(false);
        }

        return scenePluginFlowSceneService.stopFlow(sceneId, toNodeId, flowId,
                context == null ? null : context.toSceneContext());
    }

    public ServiceResult<Boolean> updateFlowParams(String id, String nodeId, String flowId, String sourceIp, CsceneContextBO context) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(false);
        }

        return scenePluginFlowSceneService.updateFlowParams(sceneId, nodeId, flowId, sourceIp, context == null ? null : context.toSceneContext());
    }

    public ServiceResult<Boolean> startFlows(String id, String toNodeId, List<FlowIdWithSourceIp> flowIdWithSourceIps,
                                             CsceneContextBO context, int runType) {
        CsceneContextHolder.setCallAction(CsceneCallAction.NODE_START_FLOW);
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(false);
        }

        return scenePluginFlowSceneService.startFlows(sceneId, toNodeId, flowIdWithSourceIps,
                context == null ? null : context.toSceneContext(), runType);
    }

    public ServiceResult<Boolean> stopFlows(String id, String toNodeId, List<FlowIdWithSourceIp> flowIdWithSourceIps,
                                            CsceneContextBO context) {
        CsceneContextHolder.setCallAction(CsceneCallAction.NODE_STOP_FLOW);
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(false);
        }

        List<String> flowIds = flowIdWithSourceIps.stream().map(f -> f.getFlowId()).collect(Collectors.toList());
        return scenePluginFlowSceneService.stopFlows(sceneId, toNodeId, flowIds, context == null ? null : context.toSceneContext());
    }

    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(String id, String nodeId, String flowId,
                                                                    CsceneContextBO context, Boolean isBatch) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>();
        }
        return scenePluginFlowSceneService.queryFlowRecords(sceneId, nodeId, flowId,
                context == null ? null : context.toSceneContext(), isBatch);
    }

    public ServiceResult<Probe> startProbe(String id, String toNodeId, String probeId, CsceneContextBO context) {
        CsceneContextHolder.setCallAction(CsceneCallAction.NODE_START_PROBE);
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }

        return new ServiceResult<>(null);
    }

    public ServiceResult<Probe> stopProbe(String id, String toNodeId, String probeId, CsceneContextBO context) {
        CsceneContextHolder.setCallAction(CsceneCallAction.NODE_STOP_PROBE);
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }

        return new ServiceResult<>(null);
    }

    public ServiceResult<NodeConnectionBO> getRemoteConnection(String id, String nodeId, String accessModeId, CsceneContextBO context) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
            return new ServiceResult<>(null);
        }
        context.loadTransparentContext();

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(null);
        }
        var cloudScenePluginContext = context.toCloudScenePluginContext();
        return scenePluginCloudSceneService.getSceneRemoteConnection(sceneId, nodeId, accessModeId,
                context.getAccessUserId(), cloudScenePluginContext);
    }

    public ServiceResult<NodeConnectionBO> enableRecording(String id, String nodeId, String connectionId, CsceneContextBO context) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
            return new ServiceResult<>(null);
        }
        context.loadTransparentContext();

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(null);
        }
        var cloudScenePluginContext = context.toCloudScenePluginContext();
        return scenePluginCloudSceneService.enableRemoteRecording(sceneId, nodeId, context.getAccessUserId(), connectionId, cloudScenePluginContext);
    }

    public ServiceResult<NodeConnectionBO> disableRecording(String id, String nodeId, String connectionId, CsceneContextBO context) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
            return new ServiceResult<>(null);
        }
        context.loadTransparentContext();

        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(null);
        }
        return scenePluginCloudSceneService.disableRemoteRecording(sceneId, nodeId, context.getAccessUserId(), connectionId);
    }

    /**
     * 根据引用类型分组统计
     * @param events 需要排除的event
     * @return
     */
    public List<CsceneInstanceCountByReferenceTypeBO> countGroupByReferenceType(List<Integer> events) {
        List<CsceneInstanceCountByReferenceTypeModel> csceneInstanceCountByReferenceTypeModels = getModelMapper().countGroupByReferenceType(events);
        if (CollectionUtils.isEmpty(csceneInstanceCountByReferenceTypeModels)) {
            return Lists.newArrayList();
        }
        return csceneInstanceCountByReferenceTypeModels.stream()
                .map(model -> new CsceneInstanceCountByReferenceTypeBO(model.getReferenceType(), model.getTotal())).collect(Collectors.toList());
    }

    public ServiceResult<Boolean> pause(String id, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.pause();
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> unpause(String id, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.unpause();
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> suspend(String id, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.suspend();
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> resume(String id, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.resume();
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> pauseElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.pauseElement(nodeId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> unpauseElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.unpauseElement(nodeId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> restartElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.restartElement(nodeId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> recreateElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.recreateElement(nodeId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> suspendElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.suspendElement(nodeId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> resumeElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.resumeElement(nodeId);
        return new ServiceResult<>(result);
    }
}
