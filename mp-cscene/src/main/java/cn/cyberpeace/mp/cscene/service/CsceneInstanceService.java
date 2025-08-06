package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.base.util.mptool.CacheUtil;
import cn.cyberpeace.mp.base.util.mptool.CollUtil;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCountByReferenceTypeBO;
import cn.cyberpeace.mp.cscene.bo.CsceneUpdateBO;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.bo.ElementModifyBO;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.core.BcSceneInstanceHandler;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.dto.scene.ElementCommandExecResult;
import cn.cyberpeace.mp.cscene.dto.scene.SceneInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneLinkStatusInfo;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountByReferenceTypeModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneSnapshotModel;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementCommonBO;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneReporter;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.service.action.CsceneCallAction;
import cn.cyberpeace.mp.cscene.service.impl.ScreenshotServiceImpl;
import cn.cyberpeace.mp.cscene.service.taskpool.CsceneReportTaskPool;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.base.util.ReentrantLockUtils;
import cn.cyberpeace.mp.flow.util.SshUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneTopologyBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.scene.service.SceneSnapshotService;
import cn.cyberpeace.mp.sceneplugincloud.bo.AccessUserBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.ElementCommandExecBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeLinkStatusBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneConfigService;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.SnapshotAddressUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.QueryCollectionDataCountParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.service.ScenePluginFlowSceneService;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhProbeCommonModel;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.SystemUser;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
@Service
@Data
public class CsceneInstanceService extends BaseModelServiceImpl<CsceneInstanceModel, CsceneInstanceMapper,
        BaseModelFields, QueryParams> {

    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;
    private CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    public static final String PROBE_COLLECTION_DATA_REQUEST_ID = "mp:probe:collection:request_id:";

    @Autowired
    private RabbitmqCsceneReporter csceneReporter;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService;

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

    @Resource
    private CsceneService csceneService;

    @Resource
    private CsceneSpecialService csceneSpecialService;

    @Resource
    private CsceneMapper csceneMapper;

    @Resource
    private SceneSnapshotService sceneSnapshotService;

    @Resource
    private CsceneSnapshotService csceneSnapshotService;

    @Resource
    private FlowMonitorService flowMonitorService;

    @Resource
    private SceneElementMapper sceneElementMapper;

    @Resource
    private SceneService sceneService ;

    // expires in 10 minutes
    private Cache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000);


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
        ObjResult<CsceneInstanceModel> csceneInstanceResult = get(csceneId).getSource();
        if (csceneInstanceResult == null) {
            return null;
        }
        CsceneInstanceModel csceneInstance = csceneInstanceResult.getObj();
        String sceneId = csceneInstance.getScene();
        return sceneId;
    }

    public String getSceneConfigId(String csceneId) {
        ObjResult<CsceneInstanceModel> csceneInstanceResult = get(csceneId).getSource();
        if (csceneInstanceResult == null) {
            return null;
        }
        CsceneInstanceModel csceneInstance = csceneInstanceResult.getObj();
        String sceneId = csceneInstance.getCscene();
        ObjResult<CsceneModel> cscene = csceneService.get(csceneInstance.getCscene()).getSource();
        return cscene.getObj().getSceneConfig();
    }

    private List<CsceneInstanceModel> getCsceneInstanceObjs(List<String> csceneIds) {
        if (csceneIds.isEmpty()) {
            return new ArrayList<>();
        }
        QueryParams queryParams = new QueryParams();
        queryParams.setIds(csceneIds.toArray(new String[0]));
        ListObjResult<CsceneInstanceModel> csceneInstancesResult = list(queryParams).getSource();
        if (csceneInstancesResult == null) {
            return new ArrayList<>();
        }
        return csceneInstancesResult.getObjs();
    }

    private ListObjServiceResult<CollectionHostEventBO> getProbeCollectData(String sceneInstanceId, String sceneId, String requestId,
                                                                            List<String> nodeIds, SceneHandler sceneHandler,
                                                                            Map<String, Map<String, String>> nodeId2ProbeNameMap,
                                                                            String probeId, HostEventQueryParams queryParams,
                                                                            CloudScenePluginHandler cloudPluginHandler,
                                                                            String type) {
        String scrollId = null;
        String cacheKey = null;
        boolean isScrollRequest = false;
        if (StringUtils.isNotBlank(requestId)) {
            isScrollRequest = true;
            cacheKey = PROBE_COLLECTION_DATA_REQUEST_ID + requestId;
            String cacheValue = null;
            try {
                cacheValue = TIMED_CACHE.get(cacheKey, () -> {
                    // 如果缓存中没有对应的value，可以在这里进行计算或者从其他地方获取
                    return "";
                });
            } catch (ExecutionException e) {
            }
            if (StringUtils.isNotEmpty(cacheValue)) {
                scrollId = cacheValue;
            }
        }

        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
        int total = 0;
        List<CollectionHostEventBO> result = new ArrayList<>();
        boolean multiNode = nodeIds != null && nodeIds.size() > 1;
        if (multiNode) {
            if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probeId) || ProbeConstant.ProbeId.REGISTRY_MODIFY.equals(probeId) ||
                    ProbeConstant.ProbeId.OS_LOG.equals(probeId) || ProbeConstant.ProbeId.APPLICATION_LOG.equals(probeId) ||
                    ProbeConstant.ProbeId.SECURITY_LOG.equals(probeId)) {
                HostEventQueryParams params = new HostEventQueryParams();
                params.setNodeIds(nodeIds);
                params.setPage(queryParams.getPage());
                params.setPageSize(queryParams.getPageSize());
                params.setStartTime(queryParams.getStartTime());
                params.setEndTime(queryParams.getEndTime());
                params.setLevels(queryParams.getLevels());
                ListObjServiceResult<CollectionHostEventBO> r;
                if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probeId)) {
                    // 文件变更信息
                    params.setType(4);
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
                List<CollectionHostEventBO> res = r.getSource().getObjs();
                total = r.getSource().getTotal() + total;
                return new ListObjServiceResult<>(new ListObjResult<>(res, total));
            }
        }
        for (String nodeId : nodeIds) {
            SceneElementBO element = sceneHandler.getElement(nodeId);
            CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(nodeId);
            Map<String, String> probeId2Name = nodeId2ProbeNameMap.get(nodeId);
            List<?> res = null;
            if (ProbeConstant.ProbeId.HARDWARE_INFO.equals(probeId) || ProbeConstant.ProbeId.LOG_FILE.equals(probeId) ||
                    ProbeConstant.ProbeId.INTERNET_CONNECTION.equals(probeId) || ProbeConstant.ProbeId.PORT_INFO.equals(probeId)) {
                BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
                request.setSceneId(sceneInstanceId);
                request.setElementIds(CollUtil.toList(element.getId()));
                request.setPage(queryParams.getPage());
                request.setPageSize(queryParams.getPageSize());
                request.setLevels(queryParams.getLevels());
                if (ProbeConstant.ProbeId.HARDWARE_INFO.equals(probeId)) {
                    // 硬件信息
                    res = hostStateService.listHostStates(request);
                    if (!CollUtil.isEmpty(res)) {
                        total = res.size() + total;
                    }
                } else if (ProbeConstant.ProbeId.LOG_FILE.equals(probeId)) {
                    // 原始日志信息
                    request.setFetchIfSceneInstanceShutdown(true);
                    ListObjServiceResult<LogFileVO> r = logFileService.pageByElementId(request);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                } else {
                    // 网络连接
                    request.setFetchIfSceneInstanceShutdown(true);
                    if (multiNode) {
                        request.setPage(1);
                        request.setPageSize(1000);
                    }
                    ListObjServiceResult<ConnectionState> r = sceneElementMonitorService.pageNetworkConnections(request);
                    if (r.getData() != null) {
                        res = r.getData().getList();
                        total = r.getData().getTotal() + total;
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
            } else if (ProbeConstant.ProbeId.NETWORK_INTERFACE_INFO.equals(probeId) ||
                    ProbeConstant.ProbeId.PROCESS_INFO.equals(probeId) || ProbeConstant.ProbeId.USERS_MODIFY.equals(probeId)) {
                TimeListPageInfoQueryParams params = new TimeListPageInfoQueryParams();
                params.setSceneIds(CollUtil.toList(sceneInstanceId));
                params.setNodeIds(CollUtil.toList(nodeId));
                params.setPage(queryParams.getPage());
                params.setPageSize(queryParams.getPageSize());
                params.setFetchIfSceneInstanceShutdown(true);
                params.setLevels(queryParams.getLevels());
                if (ProbeConstant.ProbeId.NETWORK_INTERFACE_INFO.equals(probeId)) {
                    // 网卡信息
                    ListObjServiceResult<Netiface> r = hostStateService.pageNetifaces(params);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                } else if (ProbeConstant.ProbeId.PROCESS_INFO.equals(probeId)) {
                    // 进程信息
                    if (multiNode) {
                        params.setPage(1);
                        params.setPageSize(1000);
                    }
                    ListObjServiceResult<Process> r = hostStateService.pageProcesses(params);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                } else {
                    // 用户变更信息
                    ListObjServiceResult<SystemUser> r = hostStateService.pageSystemUserEvents(params);
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
                params.setLevels(queryParams.getLevels());
                ListObjServiceResult<CollectionHostEventBO> r;
                if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probeId)) {
                    // 文件变更信息
                    params.setType(4);
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
                request.setLevels(queryParams.getLevels());
                ListObjResult<WazuhProbeCommonModel> r = dataCollectionService.queryCustomProbeData(request, CollUtil.toList(probeId));
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

            if (StringUtils.isNotBlank(scrollId) && StringUtils.isNotBlank(cacheKey)) {
                TIMED_CACHE.put(cacheKey, scrollId);
            }

            for (Object r : res) {
                CollectionHostEventBO hostEventBO = new CollectionHostEventBO();
                hostEventBO.setSceneId(sceneInstanceId);
                hostEventBO.setNodeId(nodeId);
                hostEventBO.setNodeName(element.getName());
                hostEventBO.setProbeId(probeId);
                hostEventBO.setVirtualType(cloudElement.getVirtualType());
                hostEventBO.setSystemType(element.getSystemType());
                hostEventBO.setSystemTypeName(SystemTypeUtils.getStandardSystemType(element.getSystemType()));
                hostEventBO.setType(type);
                hostEventBO.setProbeName(probeId2Name.get(probeId));
                Map<String, Object> map = JsonCommon.beanToMap(r);
                if (Objects.nonNull(map.get("level"))) {
                    hostEventBO.setLevel((Integer) map.get("level"));
                } else {
                    // 默认等级
                    hostEventBO.setLevel(3);
                }
                Object time = map.get("time");
                if (Objects.nonNull(time)) {
                    hostEventBO.setTime(TimeUtils.stringToLocalDateTime(time.toString()));
                } else {
                    hostEventBO.setTime(LocalDateTime.now());
                }
                Object description = map.get("description");
                if (Objects.nonNull(description)) {
                    hostEventBO.setDescription(description.toString());
                } else {
                    hostEventBO.setDescription("CMD LAUNCH LOG");
                }
                map.put("scrollId", null);
                hostEventBO.setContent(map);
                result.add(hostEventBO);
            }
        }
        // 多节点分页大小处理
        if (multiNode) {
            int start = queryParams.getPage() <= 0 ? 0 : (queryParams.getPage() - 1) * queryParams.getPageSize();
            if (start >= result.size()) {
                return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), total));
            } else {
                int end = Math.min(result.size(), start + queryParams.getPageSize());
                List<CollectionHostEventBO> newRes = result.subList(start, end);
                return new ListObjServiceResult<>(new ListObjResult<>(newRes, total));
            }
        } else {
            List<CollectionHostEventBO> newRes = new ArrayList<>();
            long limit = queryParams.getPageSize();
            for (CollectionHostEventBO collectionHostEventBO : result) {
                if (limit-- == 0) break;
                newRes.add(collectionHostEventBO);
            }
            return new ListObjServiceResult<>(new ListObjResult<>(newRes, total));
        }
    }

    private ListObjServiceResult<CollectionHostEventBO> getCustomProbeCollectData(String sceneInstanceId, String sceneId,
                                                                                  List<String> nodeIds, CollectionScenePluginHandler pluginHandler,
                                                                                  Map<String, Map<String, String>> nodeId2ProbeNameMap,
                                                                                  HostEventQueryParams queryParams) {
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
        List<CollectionHostEventBO> result = new ArrayList<>();
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollUtil.toList(sceneId), null);
        Map<String, SceneElementBO> eid2Element = batchSceneHandler.getAllEid2ElementMap();
        // 获取节点所有自定义探针id
        List<String> agentNames = new ArrayList<>();
        List<String> probeIds = new ArrayList<>();
        Map<String, String> probeId2ProbeName = new HashMap<>();
        for (CollectionPluginSceneElementBO e : pluginHandler.getElementMap().values()) {
            SceneElementBO element = e.getSrcElement();
            if (nodeIds.contains(element.getId())) {
                agentNames.add(element.getEid());
            }
            for (Probe probe : e.getProbes()) {
                if (Boolean.FALSE.equals(probe.getCommon()) && !probeIds.contains(probe.getId())) {
                    probeIds.add(probe.getId());
                    probeId2ProbeName.put(probe.getId(), probe.getName());
                }
            }
        }
        List<String> queryProbeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryParams.getKeyword())) {
            String keyword = queryParams.getKeyword().get(0);
            for (Map.Entry<String, String> entry : probeId2ProbeName.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (v.contains(keyword)) {
                    queryProbeIds.add(k);
                }
            }
        } else {
            queryProbeIds.addAll(probeIds);
        }
        if (CollUtil.isEmpty(queryProbeIds)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        // 构造查询请求
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPageNum(queryParams.getPage());
        request.setPageSize(queryParams.getPageSize());
        request.setAgentNames(agentNames);
        // 分页查询自定义探针数据
        ListObjResult<WazuhProbeCommonModel> listObjResult = dataCollectionService.queryCustomProbeData(request, queryProbeIds);
        List<WazuhProbeCommonModel> res = listObjResult.getObjs();
        if (CollUtil.isEmpty(res)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        Integer total = listObjResult.getTotal();
        for (WazuhProbeCommonModel r : res) {
            SceneElementBO element = eid2Element.get(r.getAgentName());
            Map<String, String> probeId2Name = nodeId2ProbeNameMap.get(element.getId());
            CollectionHostEventBO hostEventBO = new CollectionHostEventBO();
            hostEventBO.setSceneId(sceneInstanceId);
            hostEventBO.setNodeId(element.getId());
            hostEventBO.setNodeName(element.getName());
            hostEventBO.setProbeId(r.getProbeId());
            hostEventBO.setProbeName(probeId2Name.get(r.getProbeId()));
            hostEventBO.setTime(r.getTime());
            Object rawOutput = JsonCommon.beanToMap(r).get("rawOutPut");
            if (rawOutput != null) {
                hostEventBO.setContent(JsonCommon.parseMap(rawOutput.toString()));
            }
            result.add(hostEventBO);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    @Autowired
    public CsceneInstanceService(CsceneInstanceMapper modelMapper, CsceneInstanceRoleMapper csceneInstanceRoleMapper,
                                 CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper) {
        setModelMapper(modelMapper);
        setCsceneInstanceRoleMapper(csceneInstanceRoleMapper);
        setCloudPluginSceneConnectionMapper(cloudPluginSceneConnectionMapper);
    }

    public ListObjServiceResult<CsceneInstanceBO> listCsceneInstanceBO(QueryParams queryParams,
                                                                       CsceneContextBO context) {
        ListObjServiceResult<CsceneInstanceModel> listResult = super.list(queryParams);
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        HashMap<String, CsceneInstanceModel> instanceObjMap = new HashMap<>();
        for (CsceneInstanceModel x : listResult.getSource().getObjs()) {
            if (instanceObjMap.put(x.getId(), x) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        HashMap<String, List<CsceneInstanceRoleModel>> instanceRoleObjMap = new HashMap<>();
        for (CsceneInstanceRoleModel csceneInstanceRoleModel : readCsceneInstanceRoles(instanceObjMap.keySet()
                .toArray(new String[0]))) {
            instanceRoleObjMap.computeIfAbsent(csceneInstanceRoleModel.getCscene(), k -> new ArrayList<>()).add(csceneInstanceRoleModel);
        }
        List<CsceneInstanceBO> instanceBOs = new ArrayList<>();
        for (Map.Entry<String, CsceneInstanceModel> entry : instanceObjMap.entrySet()) {
            String instanceId = entry.getKey();
            CsceneInstanceModel instanceObj = entry.getValue();
            List<CsceneInstanceRoleModel> roleObjs = instanceRoleObjMap.getOrDefault(instanceId, new ArrayList<>());
            BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(instanceObj, roleObjs, context);
            instanceBOs.add(instanceHandler.toData());
        }
        return new ListObjServiceResult<>(new ListObjResult<>(instanceBOs, listResult.getSource().getTotal(),
                listResult.getSource().getFields()));
    }

    public ServiceResult<CsceneInstanceBO> getById(String id, CsceneContextBO context) {
        BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(id, null, context);
        CsceneInstanceBO data = instanceHandler.toData();
        SceneBO scene = data.getScene().getTopology().getScene();
        SceneStageRecorder recorder = SceneStageRecorder.getRecorder(scene.getEid());
        if (recorder != null) {
            recorder.refreshSceneBO(scene);
        }
        return new ServiceResult<>(data);
    }

    public ServiceResult<CsceneInstanceBO> getById(String id, String areaId, CsceneContextBO context) {
        BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(id,areaId, context);
        CsceneInstanceBO data = instanceHandler.toData();
        SceneBO scene = data.getScene().getTopology().getScene();
        SceneStageRecorder recorder = SceneStageRecorder.getRecorder(scene.getEid());
        if (recorder != null) {
            recorder.refreshSceneBO(scene);
        }
        return new ServiceResult<>(data);
    }

    public ServiceResult<Boolean> deleteById(String id, boolean logic, CsceneContextBO context) {
        if (ReentrantLockUtils.tryLock(id)) {
            try {
                // 移除定时任务
                SceneHostStateReportScheduleService.removeCsceneInstance(id);
                BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(id, context);
                NodeLimitUtils.substractLimitedNodeCount(instanceHandler.getCscene());
                String sceneId = this.getSceneId(id);
                boolean flag = instanceHandler.delete(logic, null);
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
        int result = 0;
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
        List<CsceneInstanceModel> csceneInstanceObjs = new ArrayList<>();
        for (CsceneInstanceModel csceneInstanceModel : getCsceneInstanceObjs(List.of(id))) {
            if (csceneInstanceModel.getDeleted()) {
                csceneInstanceObjs.add(csceneInstanceModel);
            }
        }
        if (csceneInstanceObjs.isEmpty()) {
            return new ServiceResult<>(false);
        }
        return deleteById(csceneInstanceObjs.get(0).getId(), false, context);
    }

    public ServiceResult<Integer> bulkClearByIds(String[] ids, CsceneContextBO context) {
        List<CsceneInstanceModel> csceneInstanceObjs = new ArrayList<>();
        for (CsceneInstanceModel csceneInstanceModel : getCsceneInstanceObjs(Arrays.asList(ids))) {
            if (csceneInstanceModel.getDeleted()) {
                csceneInstanceObjs.add(csceneInstanceModel);
            }
        }
        List<String> list = new ArrayList<>();
        for (CsceneInstanceModel csceneInstanceObj : csceneInstanceObjs) {
            String id = csceneInstanceObj.getId();
            list.add(id);
        }
        return bulkDeleteByIds(list.toArray(new String[0]), false, context);
    }

    public ServiceResult<List<UserNodeSessionInfo>> getNodeSessions(List<String> ids, List<String> nodeIds, List<String> userIds, String type) {
        if (type == null) {
            type = MONITOR;
        }
        List<CsceneInstanceModel> csceneInstances = getCsceneInstanceObjs(ids);
        if (csceneInstances.isEmpty()) {
            return new ServiceResult<>(new ArrayList<>());
        }
        HashMap<String, CsceneInstanceModel> sceneIdCsceneInstanceMap = new HashMap<>();
        for (CsceneInstanceModel csceneInstanceModel : csceneInstances) {
            if (sceneIdCsceneInstanceMap.put(csceneInstanceModel.getScene(), csceneInstanceModel) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
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
        for (NodeSessionBO sessionBO : nodeSessionResult.getSource()) {
            String sceneId = sessionBO.getSceneId();
            CsceneInstanceModel csceneInstance = sceneIdCsceneInstanceMap.getOrDefault(sceneId, null);
            if (csceneInstance == null) {
                continue;
            }
            sessionBO.setSceneId(csceneInstance.getId());
        }

        List<UserNodeSessionInfo> result = new ArrayList<>();
        for (NodeSessionBO nodeSessionBO : nodeSessionResult.getSource()) {
            UserNodeSessionInfo userNodeSessionInfo = new UserNodeSessionInfo();
            BeanUtils.copyProperties(nodeSessionBO, userNodeSessionInfo);
            result.add(userNodeSessionInfo);
        }

        Set<String> existUserIds = new HashSet<>();
        for (UserNodeSessionInfo userNodeSessionInfo : result) {
            if (StringUtils.isNotEmpty(userNodeSessionInfo.getUser())) {
                existUserIds.add(userNodeSessionInfo.getUser());
            }
        }
        if (!existUserIds.isEmpty()) {
            List<UserBo> users = userService.listByUserIds(existUserIds.toArray(new String[]{}));
            if (CollectionUtils.isNotEmpty(users)) {
                Map<String, String> userId2Name = new HashMap<>();
                for (UserBo user : users) {
                    userId2Name.put(user.getId(), user.getName());
                }
                for (UserNodeSessionInfo nodeSession : result) {
                    nodeSession.setUserName(userId2Name.containsKey(nodeSession.getUser()) ? userId2Name.get(nodeSession.getUser()) : StringUtils.EMPTY);
                }
            }
        }
        return new ServiceResult<>(result);
    }

    public ListObjServiceResult<NodeRecordingBO> listRecordings(List<String> ids, List<String> nodeIds, List<String> userIds,
                                                                RecordingQueryParams queryParams) {
        List<CsceneInstanceModel> csceneInstances = getCsceneInstanceObjs(ids);
        if (csceneInstances.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        HashMap<String, CsceneInstanceModel> sceneIdCsceneInstanceMap = new HashMap<>();
        for (CsceneInstanceModel csceneInstanceModel : csceneInstances) {
            if (sceneIdCsceneInstanceMap.put(csceneInstanceModel.getScene(), csceneInstanceModel) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        List<String> sceneIds = new ArrayList<>(sceneIdCsceneInstanceMap.keySet());
        ListObjServiceResult<NodeRecordingBO> nodeRecordingResult = scenePluginCloudSceneService.getSceneRemoteRecordings(
                sceneIds, nodeIds, userIds, queryParams);
        for (NodeRecordingBO nodeRecording : nodeRecordingResult.getSource().getObjs()) {
            String sceneId = nodeRecording.getSceneId();
            CsceneInstanceModel csceneInstance = sceneIdCsceneInstanceMap.getOrDefault(sceneId, null);
            if (csceneInstance == null) {
                continue;
            }
            nodeRecording.setSceneId(csceneInstance.getId());

            if (StringUtils.isNotEmpty(nodeRecording.getUser())) {
                nodeRecording.setUsername(UserUtils.getUser(nodeRecording.getUser()).getUsername());
            }
        }
        return nodeRecordingResult;
    }

    public ListObjServiceResult<CsceneCollectionFlowBO> listFlows(String id, FlowQueryParams queryParams) {
        String[] csceneIds = StringUtils.split(id, ",");
        Map<String, String> sceneId2CsceneId = new HashMap<>();
        for (String csceneId : csceneIds) {
            String sceneId = getSceneId(csceneId);
            if (StringUtils.isNotEmpty(sceneId)) {
                sceneId2CsceneId.put(sceneId, csceneId);
            }
        }
        if (sceneId2CsceneId.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        ListObjServiceResult<CollectionFlowBO> serviceResult = scenePluginCollectionSceneService.listFlows(new ArrayList<>(sceneId2CsceneId.keySet()), queryParams);
        List<CollectionFlowBO> flowBOS = serviceResult.getSource().getObjs();
        List<CsceneCollectionFlowBO> result = new ArrayList<>();
        for (CollectionFlowBO bo : flowBOS) {
            CsceneCollectionFlowBO csceneCollectionFlowBO = getCsceneCollectionFlowBO(sceneId2CsceneId, bo);
            result.add(csceneCollectionFlowBO);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, serviceResult.getSource().getTotal()));
    }

    private static CsceneCollectionFlowBO getCsceneCollectionFlowBO(Map<String, String> sceneId2CsceneId, CollectionFlowBO bo) {
        CsceneCollectionFlowBO csceneCollectionFlowBO = new CsceneCollectionFlowBO();
        BeanUtils.copyProperties(bo, csceneCollectionFlowBO);
        if (bo.getSceneId() != null) {
            csceneCollectionFlowBO.setSceneId(sceneId2CsceneId.get(bo.getSceneId()));
        }
        return csceneCollectionFlowBO;
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStates(String id, HostStateQueryParams queryParams) {
        String sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        return scenePluginCollectionSceneService.listHostStates(sceneId, queryParams);
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStatesByAgentNames(List<String> agentNames, HostStateQueryParams queryParams) {
        queryParams.setNodeIds(null);
        return scenePluginCollectionSceneService.listHostStatesByAgentNames(agentNames, queryParams);
    }

    public Map<String, List<CollectionHostStateBO>> mapHostStatesByAgentNames(List<String> agentNames, HostStateQueryParams queryParams) {
        queryParams.setNodeIds(null);
        return scenePluginCollectionSceneService.mapHostStatesByAgentNames(agentNames, queryParams);
    }

    public ServiceResult<Map<String, Integer>> queryCollectionDataCount(QueryCollectionDataCountParams params) {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        List<Runnable> tasks = new ArrayList<>();
        String sceneId = params.getSceneId();
        // 事件类
        tasks.add(() -> {
            ElementMonitorRequest request = new ElementMonitorRequest();
            request.setSceneId(params.getSceneId());
            request.setElementIds(params.getNodeIds());
            request.setEndTime(params.getEndTime());
            request.setPage(1);
            request.setPageSize(10);
            request.setPerPage(10);
            request.setFetchIfSceneInstanceShutdown(true);
            Integer fileCount = sceneElementMonitorService.getModifyData(request, CsceneBizConstant.FILE_MODIFY_GROUP,
                    null).getSource().getTotal();
            Integer registryCount = sceneElementMonitorService.getModifyData(request, CsceneBizConstant.REGISTRY_MODIFY_GROUP,
                    null).getSource().getTotal();
            count.put("event", fileCount + registryCount);
        });
        // 信息类
        tasks.add(() -> {
            // 网络端口
            TimeListPageInfoQueryParams infoParams = new TimeListPageInfoQueryParams();
            infoParams.setSceneIds(Arrays.asList(params.getSceneId()));
            infoParams.setNodeIds(params.getNodeIds());
            infoParams.setEndTime(params.getEndTime());
            infoParams.setFetchIfSceneInstanceShutdown(true);
            Integer netCount = hostStateService.pageNetifaces(infoParams).getSource().getTotal();
            // 端口列表
            infoParams.setSceneIds(Arrays.asList(params.getSceneId()));
            Integer portCount = hostStateService.pagePorts(infoParams).getSource().getTotal();
            // 进程列表
            infoParams.setSceneIds(Arrays.asList(params.getSceneId()));
            Integer processCount = hostStateService.pageProcesses(infoParams).getSource().getTotal();
            count.put("info", netCount + portCount + processCount);
        });
        // 日志类
        tasks.add(() -> {
            HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
            hostEventQueryParams.setNodeIds(params.getNodeIds());
            hostEventQueryParams.setEndTime(params.getEndTime());
            ListObjServiceResult<CollectionHostEventBO> result = listHostEvents(params.getSceneId(), hostEventQueryParams);
            count.put("log", result.getSource().getTotal());
        });
        // 流量类
        tasks.add(() -> {
            FlowQueryParams flowQueryParams = new FlowQueryParams();
            flowQueryParams.setTargetNodeIds(params.getNodeIds());
            flowQueryParams.setEndTime(params.getEndTime());
            Integer flowCount = flowMonitorService.pageFlow(params.getSceneId(), flowQueryParams).getSource().getTotal();
            count.put("flow", flowCount);
        });
        TaskCommon.runTasks(tasks);
        return new ServiceResult<>(count);
    }

    /**
     * 事件分页列表
     *
     * @param id
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(String id, HostEventQueryParams queryParams) {
        String sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        // 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户 6:注册表
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() != 0) {
            // 传入的sceneId不需要提前转
            if (queryParams.getType().equals(1)) {
                return getProbeCollectionData(null, id, ProbeConstant.ProbeId.PROCESS_INFO,
                        queryParams, FileEventType.PROCESS.name());
            }

            if (queryParams.getType().equals(2)) {
                return getProbeCollectionData(null, id, ProbeConstant.ProbeId.PORT_INFO, queryParams,
                        FileEventType.PORT.name());
            }

            if (queryParams.getType().equals(3)) {
                // 网络连接同端口
                return getProbeCollectionData(null, id, ProbeConstant.ProbeId.INTERNET_CONNECTION, queryParams,
                        FileEventType.INTERNET_CONNECTION.name());
            }

            if (queryParams.getType().equals(4)) {
                // 文件变动
                return getProbeCollectionData(null, id, ProbeConstant.ProbeId.FILE_MODIFY, queryParams,
                        FileEventType.FILE.name());
            }

            if (queryParams.getType().equals(5)) {
                // 用户变动
                return getProbeCollectionData(null, id, ProbeConstant.ProbeId.USERS_MODIFY, queryParams,
                        FileEventType.USERS.name());
            }

            if (queryParams.getType().equals(6)) {
                return getProbeCollectionData(null, id, ProbeConstant.ProbeId.REGISTRY_MODIFY, queryParams,
                        FileEventType.REGISTRY.name());
            }
        }

        return scenePluginCollectionSceneService.listHostEvents(sceneId, queryParams);
    }

    /**
     * 滚动分页列表
     *
     * @param scrollId
     * @return
     */
    public ListObjServiceResult<CollectionHostEventBO> scrollHostEvents(String id, String scrollId, HostEventQueryParams queryParams) {
        if (StringUtils.isEmpty(scrollId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        String sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        return scenePluginCollectionSceneService.scrollHostEvents(sceneId, scrollId, queryParams);
    }

    public ListObjServiceResult<CollectionHostEventBO> getProbeCollectionData(String requestId, String sceneInstanceId,
                                                                              String probeId, HostEventQueryParams queryParams,
                                                                              String type) {
        String sceneId = getSceneId(sceneInstanceId);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<String> nodeIds = queryParams.getNodeIds();
        if (nodeIds == null || nodeIds.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneElementBO e : sceneHandler.getElementMap().values()) {
                String id = e.getId();
                list.add(id);
            }
            nodeIds = list;
        }
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        // 非虚拟机和容器节点不支持探针采集
        List<String> newNodeIds = new ArrayList<>();
        for (String nodeId : nodeIds) {
            CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(nodeId);
            if (cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                    cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                newNodeIds.add(nodeId);
            }
        }
        if (newNodeIds.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        Map<String, Map<String, String>> nodeId2ProbeNameMap = new HashMap<>();
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (Objects.isNull(pluginHandler)) {
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        for (CollectionPluginSceneElementBO e : pluginHandler.getElementMap().values()) {
            Map<String, String> probeId2Name = new HashMap<>();
            for (Probe probe : e.getProbes()) {
                probeId2Name.put(probe.getId(), probe.getName());
            }
            nodeId2ProbeNameMap.put(e.getSrcElement().getId(), probeId2Name);
        }
        if (queryParams.getStartTime() == null) {
            queryParams.setStartTime(TimeUtils.localDateTimeToString(sceneHandler.getScene().getCreateTime()));
        }
        if (queryParams.getEndTime() == null) {
            queryParams.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        }
        if (probeId != null) {
            // 有探针id，查询指定探针采集数据
            return getProbeCollectData(sceneInstanceId, sceneId, requestId, newNodeIds, sceneHandler, nodeId2ProbeNameMap,
                    probeId, queryParams, cloudPluginHandler, type);
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

        String sceneId = getSceneId(id);
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

        String sceneId = getSceneId(id);
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
        String sceneId = getSceneId(id);
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

        String sceneId = getSceneId(id);
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

        String sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(false);
        }

        List<String> flowIds = new ArrayList<>();
        for (FlowIdWithSourceIp f : flowIdWithSourceIps) {
            String flowId = f.getFlowId();
            flowIds.add(flowId);
        }
        return scenePluginFlowSceneService.stopFlows(sceneId, toNodeId, flowIds, context == null ? null : context.toSceneContext());
    }

    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(String id, String nodeId, String flowId,
                                                                    CsceneContextBO context, Boolean isBatch) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context != null) {
            context.loadTransparentContext();
        }
        String sceneId = getSceneId(id);
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
        try {
            CsceneContextHolder.setCsceneInstanceId(id);
            CsceneContextHolder.setCallAction(CsceneCallAction.NODE_ACCESS_USER);
            if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
                return new ServiceResult<>(null);
            }
            if (context.getPassThrough() == null) {
                CsceneInstanceBO csceneInstance = new CsceneInstanceBO();
                CsceneInstanceModel csceneInstanceModel = getModelMapper().get(id);
                csceneInstance.fromModel(csceneInstanceModel);
            }
            context.loadTransparentContext();

            String sceneId = getSceneId(id);
            if (StringUtils.isEmpty(sceneId)) {
                return new ServiceResult<>(null);
            }
            CloudScenePluginContext cloudScenePluginContext = context.toCloudScenePluginContext();
            ServiceResult<NodeConnectionBO> result = scenePluginCloudSceneService.getSceneRemoteConnection(sceneId, nodeId, accessModeId,
                    context.getAccessUserId(), cloudScenePluginContext);

            this.reportElementAccessUser(sceneId, nodeId);
            return result;
        } finally {
            CsceneContextHolder.resetContext();
        }
    }

    public ServiceResult<NodeConnectionBO> getRemoteTtyConnection(String id, String nodeId) {
        String sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        NodeConnectionBO result = scenePluginCloudSceneService.getRemoteTtyConnection(sceneId, nodeId).getSource();
        result.setSceneId(id);
        return new ServiceResult<>(result);
    }

    public ServiceResult<NodeConnectionBO> enableRecording(String id, String nodeId, String connectionId, CsceneContextBO context) {
        CsceneContextHolder.setCsceneInstanceId(id);
        if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
            return new ServiceResult<>(null);
        }
        context.loadTransparentContext();

        String sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ServiceResult<>(null);
        }
        CloudScenePluginContext cloudScenePluginContext = context.toCloudScenePluginContext();
        return scenePluginCloudSceneService.enableRemoteRecording(sceneId, nodeId, context.getAccessUserId(), connectionId, cloudScenePluginContext);
    }

    public ServiceResult<NodeConnectionBO> disableRecording(String id, String nodeId, String connectionId, CsceneContextBO context) {
        try {
            CsceneContextHolder.setCsceneInstanceId(id);
            CsceneContextHolder.setCallAction(CsceneCallAction.NODE_ACCESS_USER);
            if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
                return new ServiceResult<>(null);
            }
            if (context.getPassThrough() == null) {
                CsceneInstanceBO csceneInstance = new CsceneInstanceBO();
                CsceneInstanceModel csceneInstanceModel = getModelMapper().get(id);
                csceneInstance.fromModel(csceneInstanceModel);
            }
            context.loadTransparentContext();

            String sceneId = getSceneId(id);
            if (StringUtils.isEmpty(sceneId)) {
                return new ServiceResult<>(null);
            }
            ServiceResult<NodeConnectionBO> result = scenePluginCloudSceneService.disableRemoteRecording(sceneId, nodeId, context.getAccessUserId(), connectionId);
            this.reportElementAccessUser(sceneId, nodeId);
            return result;
        } finally {
            CsceneContextHolder.resetContext();
        }
    }

    /**
     * 根据引用类型分组统计
     *
     * @param events 需要排除的event
     * @return
     */
    public List<CsceneInstanceCountByReferenceTypeBO> countGroupByReferenceType(List<Integer> events) {
        List<CsceneInstanceCountByReferenceTypeModel> csceneInstanceCountByReferenceTypeModels = getModelMapper().countGroupByReferenceType(events);
        if (CollectionUtils.isEmpty(csceneInstanceCountByReferenceTypeModels)) {
            return Lists.newArrayList();
        }
        List<CsceneInstanceCountByReferenceTypeBO> list = new ArrayList<>();
        for (CsceneInstanceCountByReferenceTypeModel model : csceneInstanceCountByReferenceTypeModels) {
            CsceneInstanceCountByReferenceTypeBO csceneInstanceCountByReferenceTypeBO = new CsceneInstanceCountByReferenceTypeBO(model.getReferenceType(), model.getTotal());
            list.add(csceneInstanceCountByReferenceTypeBO);
        }
        return list;
    }

    public ServiceResult<Boolean> pause(String id, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.pause(sceneId, context.toSceneContext());
    }

    public ServiceResult<Boolean> unpause(String id, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.unpause(sceneId, context.toSceneContext());
    }

    public ServiceResult<Boolean> suspend(String id, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.suspend(sceneId, context.toSceneContext());
    }

    public ServiceResult<Boolean> resume(String id, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.resume(sceneId, context.toSceneContext());
    }

    /**
     * @param id
     * @param nodeId
     * @param context
     * @return ServiceResult<Boolean>
     * @description: 启动节点
     */
    public ServiceResult<Boolean> startElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.startElement(sceneId, nodeId, context.toSceneContext());
    }

    /**
     * @param csceneId 场景id
     * @param nodeId   节点id
     * @description: 如果是gns3靶标，禁止开机/重启/关机/重建
     */
    public void unSupportedOperation(String csceneId, String nodeId) {
        String sceneId = this.getSceneId(csceneId);
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CloudPluginSceneElementBO cloudElement = sceneHandler.getPluginElement("cpcloud", nodeId);
        if (cloudElement != null && cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            throw new MpRuntimeException(CloudErrorCode.UN_SUPPORT_OPERATE);
        }
    }

    /**
     * @param id
     * @param nodeId
     * @param context
     * @return ServiceResult<Boolean>
     * @description: 关闭节点
     */
    public ServiceResult<Boolean> stopElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.stopElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> pauseElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.pauseElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> unpauseElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.unpauseElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> restartElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.restartElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> createElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.createElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> recreateElement(String id, String nodeId, String image, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.recreateElement(sceneId, nodeId, image, context.toSceneContext());
    }

    public ServiceResult<Boolean> modifyElement(ElementModifyBO bo, CsceneContextBO context) {
        String sceneId = this.getSceneId(bo.getId());
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        String nodeId = bo
                .getUpdate()
                .getTopology()
                .getElements()
                .get(0)
                .getId();
        if (context != null) {
            context.loadTransparentContext();
        }
        return this.sceneService.modifyElement(sceneId, nodeId, bo.getUpdate(), context.toSceneContext());
    }

    public ServiceResult<Boolean> deleteElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
        }
        return this.sceneService.deleteElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> suspendElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.suspendElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> resumeElement(String id, String nodeId, CsceneContextBO context) {
        String sceneId = this.getSceneId(id);
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(id);
        }
        return this.sceneService.resumeElement(sceneId, nodeId, context.toSceneContext());
    }

    public ServiceResult<Boolean> snapshotElement(String id, String nodeId, String snapshotName, String description, CsceneContextBO context) {
        // 校验快照名长度、是否重复等
        if (StringUtils.isEmpty(snapshotName)) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if (snapshotName.length() > 219) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NAME_TOO_LONG);
        }
        if (description != null && description.length() > 2048) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_DESC_TOO_LONG);
        }
        ObjResult<CsceneInstanceModel> queryInstance = this.get(id).getSource();
        if (queryInstance == null || queryInstance.getObj() == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        CsceneInstanceModel csceneInstance = queryInstance.getObj();
        String csceneId = csceneInstance.getCscene();
        String csceneInstanceId = csceneInstance.getId();
        Boolean checkNameDuplicated = csceneSnapshotService.checkSnapshotName(csceneId, csceneInstanceId, nodeId, snapshotName).getSource();
        if (!Boolean.TRUE.equals(checkNameDuplicated)) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NAME_DUPLICATED);
        }

        // 先保存cscene中的关联信息
        String snapshotId = csceneSnapshotService.saveCsceneSnapshot(csceneId, csceneInstanceId, nodeId, snapshotName).getSource();

        if (context != null) {
            context.loadTransparentContext();
            CsceneContextHolder.setCsceneInstanceId(csceneInstanceId);
        }
        // 打快照
        String sceneId = csceneInstance.getScene();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        boolean result = this.sceneService.snapshotElementSyncV2(sceneId, nodeId, snapshotId, snapshotName, description, context != null ? context.toSceneContext() : null)
                .getSource();

        // 更新场景模板靶标的快照、cscene中的关联信息
        SceneInfo sceneInfo = this.getSceneInfo(id);
        if (StringUtils.isNotEmpty(sceneInfo.getSceneConfigId())) {
            if (result) {
                SceneElementSnapshotModel model = sceneSnapshotService.getElementSnapshot(snapshotId).getSource();
                if (model != null) {
                    scenePluginCloudSceneConfigService.updateElementImage(sceneInfo.getSceneConfigId(), nodeId, SnapshotAddressUtils.parseAddress(model.getAddress())
                            .getImageName());
                    SnapshotAddressUtils.SnapshotAddressInfo parseResult = SnapshotAddressUtils.parseAddress(model.getAddress());
                    csceneSnapshotService.updateCsceneSnapshotRelativeInfo(snapshotId, parseResult.getSnapshotUuid(), model.getSourceName());
                } else {
                    log.error("do not find snapshot from scene snapshot , id is {}", snapshotId);
                }
            }
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<ElementCommandExecResult> execElement(String id, String nodeId, String command,
                                                               String user, CsceneContextBO context) {
        String sceneId = getSceneId(id);
        ElementCommandExecBO execBO = scenePluginCloudSceneService.execElementCommand(sceneId, nodeId, command, user).getSource();
        ElementCommandExecResult result = new ElementCommandExecResult();
        result.setOutput(execBO.getOutput());
        result.setStatus(execBO.getStatus());
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> uploadElementFile(String id, String nodeId, String filePath, String filename, String destPath, CsceneContextBO context) {
        String sceneId = getSceneId(id);
        scenePluginCloudSceneService.uploadElementFile(sceneId, nodeId, filePath, filename, destPath);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> uploadElementFile(String id, String nodeId, InputStream inputStream, String filename, String destPath, CsceneContextBO context) {
        String sceneId = getSceneId(id);
        scenePluginCloudSceneService.uploadElementFile(sceneId, nodeId, inputStream, filename, destPath);
        return new ServiceResult<>(true);
    }

    public ServiceResult<SceneLinkStatusInfo> linkStatus(String csceneId) {
        String sceneId = getSceneId(csceneId);
        List<NodeLinkStatusBO> result = scenePluginCloudSceneService.linkStatus(sceneId).getSource();
        SceneLinkStatusInfo sceneLinkStatusInfo = new SceneLinkStatusInfo();
        sceneLinkStatusInfo.setNodeLinkStatusList(result);
        sceneLinkStatusInfo.setCsceneId(csceneId);
        return new ServiceResult<>(sceneLinkStatusInfo);
    }

    public ServiceResult<Boolean> deleteElementSnapshot(String csceneId, String nodeId, String snapshotId) {
        // 校验场景是否存在、快照是否在当前场景模板下
        ObjResult<CsceneInstanceModel> queryInstance = this.get(csceneId).getSource();
        if (queryInstance == null || queryInstance.getObj() == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        CsceneInstanceModel csceneInstance = queryInstance.getObj();
        CsceneSnapshotModel csceneSnapshot = csceneSnapshotService.getCsceneSnapshot(csceneInstance.getCscene(), nodeId, snapshotId).getSource();
        if (csceneSnapshot == null) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NOT_CURRENT_SCENE);
        }
        SceneElementSnapshotModel snapshot = sceneSnapshotService.getElementSnapshot(csceneSnapshot.getSnapshotId()).getSource();
        if (snapshot == null) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NOT_CURRENT_SCENE);
        }
        SceneInfo sceneInfo = this.getSceneInfo(csceneId);
        if (sceneInfo == null || StringUtils.isEmpty(sceneInfo.getSceneId()) || StringUtils.isEmpty(sceneInfo.getSceneConfigId())) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }

        // 校验快照是否被场景和场景模板引用
        String image = SnapshotAddressUtils.parseAddress(snapshot.getAddress()).getImageName();
        Map<String, List<DeviceReferCsceneStat>> configUsed = csceneSpecialService.statDeviceReferCscenes(new String[]{image}).getSource();
        List<String> configNames = this.getReferCsceneNames(configUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(configNames)) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_IN_USE.format(StringUtils.join(configNames, ",")));
        }

        Map<String, List<DeviceReferCsceneInstanceStat>> instanceUsed = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{image}).getSource();
        List<String> instanceNames = this.getReferCsceneNames(instanceUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(instanceNames)) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_IN_USE.format(StringUtils.join(instanceNames, ",")));
        }

        String sceneId = sceneInfo.getSceneId();
        csceneSnapshotService.logicDeleteCsceneSnapshot(csceneInstance.getCscene(), nodeId, snapshotId);
        Boolean result = scenePluginCloudSceneService.deleteElementSnapshot(sceneId, nodeId, snapshot.getId(), snapshotId, snapshot.getName()).getSource();
        if (result) {
            csceneSnapshotService.deleteCsceneSnapshotRelativeInfo(csceneInstance.getCscene(), nodeId, snapshotId);
        }
        return new ServiceResult<>(result);
    }

    private <T> List<String> getReferCsceneNames(List<T> usingList, Function<T, String> getName, int nameSize) {
        if (CollectionUtils.isEmpty(usingList)) {
            return null;
        }
        Set<String> names = new HashSet<>();
        for (T used : usingList) {
            if (names.size() < nameSize) {
                names.add(getName.apply(used));
            }
        }
        return new ArrayList<>(names);
    }

    private SceneInfo getSceneInfo(String csceneInstanceId) {
        HumanCondition condition = new HumanCondition().link(CsceneInstanceModel.class, CsceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getCscene, CsceneModel.class, CsceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getId, csceneInstanceId)
                .selectAs(CsceneInstanceModel::getScene, SceneInfo::getSceneId)
                .currentLink(CsceneModel.class)
                .selectAs(CsceneModel::getSceneConfig, SceneInfo::getSceneConfigId);
        List<SceneInfo> list = getModelMapper().list(condition, SceneInfo.class);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ServiceResult<CsceneInstanceBO> instanceModify(CsceneUpdateBO csceneBO) {
        BcSceneInstanceHandler originHandler = new BcSceneInstanceHandler(csceneBO.getId(), null);

        // 更新区域
        csceneBO.getUpdateScene().getTopology().getScene().setEid(originHandler.getSceneId());
        SceneHandler sceneHandler = new SceneHandler(csceneBO.getUpdateScene(), null);
        sceneHandler.save();

        CsceneInstanceBO instance = new CsceneInstanceBO();
        instance.setId(csceneBO.getId());
        instance.setPassThrough(csceneBO.getContext() != null ? csceneBO.getContext()
                .getPassThrough() : new HashMap<>());
        BcSceneInstanceHandler newData = new BcSceneInstanceHandler(csceneBO.getId(), null);
        instance.setScene(newData.toData().getScene());

        // 保留隐藏节点和隐藏端口
        SceneDataBO originData = originHandler.toDataWithHidden().getScene();
        Set<String> hiddenElementIds = new HashSet<>();
        Set<String> hiddenPortIds = new HashSet<>();
        Map<String, List<SceneElementPortBO>> hiddenPorts = new HashMap<>();
        for (SceneElementBO e : originData.getTopology().getElements()) {
            if (Boolean.TRUE.equals(e.getIsAuxiliary())) {
                csceneBO.getUpdateScene().getTopology().getElements().add(e);
                hiddenElementIds.add(e.getId());
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (SceneElementPortBO p : e.getPorts()) {
                        hiddenPortIds.add(p.getId());
                    }
                }
            } else if (CollectionUtils.isNotEmpty(e.getPorts())) {
                for (SceneElementPortBO p : e.getPorts()) {
                    if (Boolean.TRUE.equals(p.getIsAuxiliary())) {
                        hiddenPortIds.add(p.getId());
                        if (!hiddenPorts.containsKey(e.getId())) {
                            hiddenPorts.put(e.getId(), new ArrayList<>());
                        }
                        hiddenPorts.get(e.getId()).add(p);
                    }
                }
            }
        }
        if (!hiddenPorts.isEmpty()) {
            for (SceneElementBO e : instance.getScene().getTopology().getElements()) {
                if (hiddenPorts.containsKey(e.getId())) {
                    List<SceneElementPortBO> hiddenPostList = hiddenPorts.get(e.getId());
                    for (int i = hiddenPostList.size() - 1; i >= 0; i--) {
                        if (e.getPorts() == null) {
                            e.setPorts(new ArrayList<>());
                        }
                        e.getPorts().add(0, hiddenPostList.get(i));
                    }
                }
            }
        }
        for (SceneElementPortConnectionBO c : originData.getTopology().getConnections()) {
            if (hiddenPortIds.contains(c.getSource()) || hiddenPortIds.contains(c.getTarget())) {
                instance.getScene().getTopology().getConnections().add(c);
            }
        }

        Map<String, ScenePluginDataBO> plugins = originData.getPlugins();
        for (Map.Entry<String, ScenePluginDataBO> plugin : plugins.entrySet()) {
            if (!cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY.equals(plugin.getKey())) {
                // 暂不支持采集和发生插件的修改 保留原有配置
                instance.getScene().getPlugins().put(plugin.getKey(), plugin.getValue());
            } else {
                // 保留插件的隐藏节点和隐藏端口
                ScenePluginDataBO newPlugin = instance.getScene().getPlugins().get(plugin.getKey());
                for (Map.Entry<String, Map<String, Object>> e : plugin.getValue().getElements().entrySet()) {
                    if (hiddenElementIds.contains(e.getKey())) {
                        newPlugin.getElements().put(e.getKey(), e.getValue());
                    }
                }
                for (Map.Entry<String, Map<String, Object>> p : plugin.getValue().getElementPorts().entrySet()) {
                    if (hiddenPortIds.contains(p.getKey())) {
                        newPlugin.getElementPorts().put(p.getKey(), p.getValue());
                    }
                }
            }
        }

        Map<String, SceneElementPortBO> newPorts = new HashMap<>();
        for (SceneElementBO newElement : instance.getScene().getTopology().getElements()) {
            if (CollectionUtils.isNotEmpty(newElement.getPorts())) {
                for (SceneElementPortBO p : newElement.getPorts()) {
                    newPorts.put(p.getId(), p);
                }
            }
        }

        SceneHandler originSceneHandler = new SceneHandler(originData, null);
        SceneHandler newSceneHandler = new SceneHandler(instance.getScene(), null);
        for (String portId : newPorts.keySet()) {
            SceneElementPortBO srcPort = originSceneHandler.getPort(portId);
            if (srcPort != null && srcPort.getElement() != null && srcPort.getElement().getType() != null
                    && srcPort.getElement().getType() == BaseSceneElementModel.Type.TERMINAL) {
                SceneElementPortBO originConnectPort = originSceneHandler.getPortConnectedPort(portId);
                SceneElementPortBO newConnectPort = newSceneHandler.getPortConnectedPort(portId);
                if (originConnectPort == null && newConnectPort != null) {
                    // 赋值一个新的id
                    SceneElementPortBO p = newPorts.get(portId);
                    p.setChange(true);
//                    p.setId(portId + "00");
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        SceneElementBO connectElement = newConnectPort.getElement();
                        if (connectElement != null) {
                            CloudScenePluginHandler handler = newSceneHandler.getPluginHandler("cpcloud");
                            if (handler != null) {
                                CloudPluginSceneElementBO connectCloudElement = handler.getElement(connectElement.getId());
                                if (connectCloudElement != null && StringUtils.isNotEmpty(connectCloudElement.getCidr())) {
                                    if (!IpUtils.newIsInCidr2(p.getIp(), connectCloudElement.getCidr())) {
                                        p.setIp(StringUtils.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                    for (SceneElementPortConnectionBO connect : instance.getScene().getTopology().getConnections()) {
                        if (portId.equals(connect.getSource())) {
                            connect.setSource(p.getId());
                        }
                        if (portId.equals(connect.getTarget())) {
                            connect.setTarget(p.getId());
                        }
                    }
                    for (ScenePluginDataBO plugin : instance.getScene().getPlugins().values()) {
                        Map<String, Object> pluginPort = plugin.getElementPorts().get(portId);
                        if (pluginPort != null) {
                            pluginPort.remove("logic_network_id");
                            pluginPort.remove("resource_id");
                            plugin.getElementPorts().put(p.getId(), pluginPort);
                        }
                    }
                }
            }
        }

        BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(instance, csceneBO.getContext());
        instanceHandler.update(originSceneHandler);
        return new ServiceResult<>(instanceHandler.toData());
    }

    private CsceneInstanceBO mergeInstance(String instanceId, CsceneInstanceBO newData, CsceneInstanceBO originData) {
        CsceneInstanceBO instance = new CsceneInstanceBO();
        instance.setId(instanceId);
        instance.setCscene(originData.getCscene());
        instance.setPassThrough(newData.getPassThrough());

        SceneDataBO sceneDataBO = new SceneDataBO();
        instance.setScene(sceneDataBO);

        List<String> updateElementIds = new ArrayList<>();
        SceneTopologyBO sceneTopologyBO = new SceneTopologyBO();
        sceneTopologyBO.setScene(originData.getScene().getTopology().getScene());
        sceneTopologyBO.setAreas(originData.getScene().getTopology().getAreas());
        sceneTopologyBO.setPieces(originData.getScene().getTopology().getPieces());
        sceneTopologyBO.setAreaId(originData.getScene().getTopology().getAreaId());
        sceneTopologyBO.setConnections(newData.getScene().getTopology().getConnections());
        sceneTopologyBO.setElements(mergeElements(newData.getScene().getTopology().getElements(), originData.getScene().getTopology().getElements(), updateElementIds));
        sceneDataBO.setTopology(sceneTopologyBO);

        Map<String, ScenePluginDataBO> plugins = new HashMap<>();
        Set<String> pluginKeys = originData.getScene().getPlugins().keySet();
        for (String key : pluginKeys) {
            ScenePluginDataBO newPlugin = newData.getScene().getPlugins().get(key);
            ScenePluginDataBO originPlugin = originData.getScene().getPlugins().get(key);
            if (cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY.equals(key)) {
                plugins.put(key, this.mergePluginDatas(newPlugin, originPlugin, updateElementIds));
            } else {
                plugins.put(key, originPlugin);
            }
        }
        sceneDataBO.setPlugins(plugins);

        return instance;
    }

    private List<SceneElementBO> mergeElements(List<SceneElementBO> newElements, List<SceneElementBO> originElements, List<String> updateElementIds) {
        Map<String, SceneElementBO> originElementsMap = new HashMap<>();
        Map<String, SceneElementBO> newElementsMap = new HashMap<>();
        for (SceneElementBO e : originElements) {
            originElementsMap.put(e.getId(), e);
        }
        for (SceneElementBO e : newElements) {
            newElementsMap.put(e.getId(), e);
        }

        Set<String> originElementIds = new HashSet<>();
        for (SceneElementBO originElement : originElements) {
            if (originElement.getEvent() == SceneModel.Event.MODIFY && originElement.getEventStatus() == EventStatus.ABNORMAL) {
                updateElementIds.add(originElement.getId());
                for (SceneElementPortBO p : originElement.getPorts()) {
                    if (StringUtils.isNotEmpty(p.getId())) {
                        updateElementIds.add(p.getId());
                    }
                }
                continue;
            }
            originElementIds.add(originElement.getId());
        }
        Set<String> newElementIds = new HashSet<>();
        for (SceneElementBO newElement : newElements) {
            newElementIds.add(newElement.getId());
        }
        List<SceneElementBO> result = new ArrayList<>();
        for (SceneElementBO e : originElements) {
            if (e.getEvent() == SceneModel.Event.MODIFY && e.getEventStatus() == EventStatus.ABNORMAL) {
                continue;
            }
            if (newElementIds.contains(e.getId())) {
                List<SceneElementPortBO> newPorts = newElementsMap.get(e.getId()).getPorts();
                List<SceneElementPortBO> originPorts = e.getPorts();
                List<SceneElementPortBO> mergedPorts = this.mergePorts(
                        CollectionUtils.isEmpty(newPorts) ? new ArrayList<>() : newPorts,
                        CollectionUtils.isEmpty(originPorts) ? new ArrayList<>() : originPorts
                );
                e.setPorts(mergedPorts);
                result.add(e);
            }
        }
        newElementIds.removeAll(originElementIds);
        for (SceneElementBO e : newElements) {
            if (newElementIds.contains(e.getId())) {
                result.add(e);
            }
        }
        return result;
    }

    private List<SceneElementPortBO> mergePorts(List<SceneElementPortBO> newPorts, List<SceneElementPortBO> originPorts) {
        Set<String> originPortIds = new HashSet<>();
        for (SceneElementPortBO originPort : originPorts) {
            originPortIds.add(originPort.getId());
        }
        Set<String> newPortIds = new HashSet<>();
        for (SceneElementPortBO newPort : newPorts) {
            newPortIds.add(newPort.getId());
        }
        List<SceneElementPortBO> result = new ArrayList<>();
        for (SceneElementPortBO e : originPorts) {
            if (newPortIds.contains(e.getId())) {
                result.add(e);
            }
        }
        newPortIds.removeAll(originPortIds);
        for (SceneElementPortBO e : newPorts) {
            if (newPortIds.contains(e.getId())) {
                result.add(e);
            }
        }
        return result;
    }

    private ScenePluginDataBO mergePluginDatas(ScenePluginDataBO newData, ScenePluginDataBO originData, List<String> updateElementIds) {
        ScenePluginDataBO scenePluginDataBO = new ScenePluginDataBO();
        scenePluginDataBO.setScene(originData.getScene());
        scenePluginDataBO.setElements(this.mergeMap(newData.getElements(), originData.getElements(), updateElementIds));
        scenePluginDataBO.setElementPorts(this.mergeMap(newData.getElementPorts(), originData.getElementPorts(), updateElementIds));
        return scenePluginDataBO;
    }

    private Map<String, Map<String, Object>> mergeMap(Map<String, Map<String, Object>> newData, Map<String, Map<String, Object>> originData, List<String> updateElementIds) {
        Set<String> originElementIds = new HashSet<>(originData.keySet());
        Set<String> newElementIds = newData.keySet();
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> e : originData.entrySet()) {
            String k = e.getKey();
            Map<String, Object> v = e.getValue();
            if (updateElementIds.contains(k)) {
                originElementIds.remove(k);
                continue;
            }
            if (newElementIds.contains(k)) {
                result.put(k, v);
            }
        }
        newElementIds.removeAll(originElementIds);
        for (Map.Entry<String, Map<String, Object>> entry : newData.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> value = entry.getValue();
            result.put(key, value);
        }
        return result;
    }

    public void reportElementAccessUser(String sceneId, String nodeId) {
        List<CloudPluginSceneConnectionModel> connections = cloudPluginSceneConnectionMapper.list(new HumanCondition()
                .select(CloudPluginSceneConnectionModel::getAccessUser)
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, nodeId)
                .reverseOrderBy(CloudPluginSceneConnectionModel::getPk));
        List<AccessUserBO> accessUsers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(connections)) {
            Set<String> userIds = new HashSet<>();
            for (CloudPluginSceneConnectionModel cloudPluginSceneConnectionModel : connections) {
                String user = cloudPluginSceneConnectionModel.getAccessUser();
                userIds.add(user);
            }
            Map<String, UserModel> userId2User = new HashMap<>();
            for (UserModel model : UserCommon.getUsers(new ArrayList<>(userIds))) {
                if (userId2User.put(model.getId(), model) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            Set<CloudPluginSceneConnectionModel> uniqueValues = new HashSet<>();
            for (CloudPluginSceneConnectionModel connection : connections) {
                if (uniqueValues.add(connection)) {
                    UserModel userModel = userId2User.get(connection.getAccessUser());
                    if (userModel != null) {
                        AccessUserBO accessUser = new AccessUserBO();
                        accessUser.setId(userModel.getId());
                        accessUser.setName(userModel.getName());
                        accessUsers.add(accessUser);
                    }
                }
            }
        }
        CollectedCsceneElementCommonBO<List<AccessUserBO>> csceneElementCommon = new CollectedCsceneElementCommonBO<>();
        csceneElementCommon.setNodeId(nodeId);
        csceneElementCommon.setData(accessUsers);
        CsceneReportTaskPool.getInstance().executeTask(() -> csceneReporter.reportElementCommon(csceneElementCommon));
    }

    public List<SceneElementModel> listCsceneInstanceNodes(String sceneId, String[] nodeIds, SerializableFunction<SceneElementModel>... selectFields) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(SceneElementModel::getScene, sceneId);
        if (Objects.nonNull(nodeIds) && nodeIds.length > 0) {
            humanCondition.filterIn(SceneElementModel::getNodeId, nodeIds);
        }
        if (Objects.nonNull(selectFields) && selectFields.length > 0) {
            humanCondition.select(selectFields);
        }
        return sceneElementMapper.list(humanCondition);
    }

    public Boolean resetPassword(String nodeId,String oldPassword, String newPassword, String sceneId) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler("cpcloud");
        var elementBO = sceneHandler.getElement(nodeId);
        // 取源节点上的ssh信息
        SshUtil.ServerInfo serverInfo = this.getSshInfo(elementBO.getPorts(), handler);
        serverInfo.setUser("root");
        serverInfo.setPassword(oldPassword);
        String command = "echo \"root:" + newPassword + "\" | chpasswd";
        SshUtil.execCommand(serverInfo, command);
        log.info("虚拟机密码修改成功");
        updatePassword(elementBO.getPorts(), handler, newPassword);
        updatePassword2(sceneHandler.getElement(nodeId).getPorts().get(0).getAccessModes(), newPassword);
        sceneHandler.save();
        log.info("数据库中密码修改成功");
        return true;
    }

    public Boolean setPortForwarding(String nodeId, String sceneId, Integer sourcePort, Integer destPort) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, false);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler("cpcloud");
        var elementBO = sceneHandler.getElement(nodeId);
        // 取源节点上的ssh信息
        SshUtil.ServerInfo serverInfo = this.getSshInfo(elementBO.getPorts(), handler);
        String command = "sudo iptables -t nat -A PREROUTING -p tcp --dport " + sourcePort + " -j REDIRECT --to-port " + destPort +
                "; sudo iptables -t nat -A PREROUTING -p udp --dport " + sourcePort + " -j REDIRECT --to-port " + destPort;
        SshUtil.execCommand(serverInfo, command);
        return true;
    }


    private SshUtil.ServerInfo getSshInfo(List<SceneElementPortBO> portBOS, CloudScenePluginHandler handler) {
        String sourceIp = "";
        Integer sourcePort = null;
        String username = "";
        String password = "";
        for (var port : portBOS) {
            var pluginPort = handler.getPort(port.getId());
            if (CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                for (var a : pluginPort.getAccessModes()) {
                    if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                        sourceIp = a.getAccessIp();
                        sourcePort = a.getAccessPort();
                        username = a.getUsername();
                        password = a.getPassword();
                    }
                }
            }
        }
        if (sourcePort == null) {
            throw new MpRuntimeException(CsceneErrorCode.CANNOT_FIND_SSH);
        }
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(sourceIp);
        serverInfo.setPort(sourcePort);
        serverInfo.setUser(username);
        serverInfo.setPassword(password);
        return serverInfo;
    }

    private void updatePassword(List<SceneElementPortBO> portBOS, CloudScenePluginHandler handler, String newPassword) {
        for (var port : portBOS) {
            var pluginPort = handler.getPort(port.getId());
            if (CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                for (var a : pluginPort.getAccessModes()) {
                    if (AccessMode.Protocol.SSH.equals(a.getProtocol()) && "root".equals(a.getUsername())) {
                        a.setPassword(newPassword);
                    }
                }
            }
        }
    }

    private void updatePassword2(List<AccessMode> accessModes, String newPassword) {
        for (AccessMode a : accessModes) {
            if (AccessMode.Protocol.SSH.equals(a.getProtocol()) && "root".equals(a.getUsername())) {
                a.setPassword(newPassword);
            }
        }
    }
}
