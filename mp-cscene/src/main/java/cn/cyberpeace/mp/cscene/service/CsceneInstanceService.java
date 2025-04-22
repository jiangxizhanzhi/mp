package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
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
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.base.util.mptool.CacheUtil;
import cn.cyberpeace.mp.base.util.mptool.CollUtil;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonStatusCodeEnum;
import cn.cyberpeace.mp.common.service.impl.CommonCodeServiceImpl;
import cn.cyberpeace.mp.cscene.CscenePluginDataRecordService;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceNodeExpansionDiskActionRequest;
import cn.cyberpeace.mp.cscene.controller.request.DeliveryLoadFileRequest;
import cn.cyberpeace.mp.cscene.core.CsceneInstanceHandler;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.dto.scene.*;
import cn.cyberpeace.mp.cscene.model.*;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementCommonBO;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneReporter;
import cn.cyberpeace.mp.cscene.repository.*;
import cn.cyberpeace.mp.cscene.repository.mapper.LoadMapper;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.service.action.CsceneCallAction;
import cn.cyberpeace.mp.cscene.service.impl.ScreenshotServiceImpl;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import cn.cyberpeace.mp.cscene.service.taskpool.CsceneReportTaskPool;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.cscene.util.ReentrantLockUtils;
import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.scene.repository.*;
import cn.cyberpeace.mp.scene.service.SceneSnapshotService;
import cn.cyberpeace.mp.scene.service.taskpool.ElementTaskPool;
import cn.cyberpeace.mp.scene.service.taskpool.SceneTaskPool;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.scene.util.ResourceClearHandler;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneConfigService;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.SnapshotAddressUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.model.ElementLogListModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertScriptParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.PublicWazuhControlCommon;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.ElementLogListMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.*;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.service.ScenePluginFlowSceneService;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhProbeDataModel;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeScriptMapper;
import cn.cyberpeace.mp.situationaware.repository.WazuhProbeDataMapper;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.azure.resourcemanager.compute.models.ComputeResourceType;
import com.azure.resourcemanager.resourcegraph.ResourceGraphManager;
import com.azure.resourcemanager.resourcegraph.models.QueryRequest;
import com.azure.resourcemanager.resourcegraph.models.QueryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
public class CsceneInstanceService extends BaseModelServiceImpl<CsceneInstanceModel, CsceneInstanceMapper,
        BaseModelFields, QueryParams> {

    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;
    private CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    public static final String PROBE_COLLECTION_DATA_REQUEST_ID = "mp:probe:collection:request_id:";

    private static final String WINDOWS_DELIVERY_LOAD_FILE_COMMAND = "curl -o %s \"http://%s/download/tmp/%s\"";
    private static final String LINUX_DELIVERY_LOAD_FILE_COMMAND = "curl -o %s \"http://%s/download/tmp/%s\"";
    private static final String IP_PATTERN = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";

    @Autowired
    private SituationAwareConfig situationAwareConfig;

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

    @Autowired
    private ResourceClearHandler resourceClearHandler;

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
    private SceneConfigElementMapper sceneConfigElementMapper;
    @Resource
    private CloudPluginSceneElementMapper cloudPluginSceneElementMapper;

    @Resource
    private CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper;

    @Resource
    private ProbeMapper probeMapper;
    @Resource
    private ProbeScriptMapper probeScriptMapper;
    @Resource
    private ProbeParamMapper probeParamMapper;
    @Resource
    private SceneElementSituationProbeMapper sceneElementSituationProbeMapper;
    @Resource
    private AzureVmSizeMapper azureVmSizeMapper;
    @Resource
    private AzureVmOsDiskMapper azureVmOsDiskMapper;

    @Autowired
    private ElementLogListMapper elementLogListMapper;

    @Autowired
    private WazuhProbeDataMapper wazuhProbeDataMapper;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private CollectionPluginSceneElementMapper collectionPluginSceneElementMapper;

    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Autowired
    private InstanceMapper instanceMapper;

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private LoadMapper loadMapper;

    @Resource
    private SceneElementPluginMapper sceneElementPluginMapper;

    @Resource
    private SceneAreaMapper sceneAreaMapper;

    @Resource
    private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;

    @Resource
    private LoadServiceImpl loadService;

    @Resource
    private CscenePluginDataRecordService dataRecordService;

    @Resource
    private LocalLoadExecutionRecordMapper localLoadExecutionRecordMapper;

    @Resource
    private DevopsLoadSourceMapper devopsLoadSourceMapper;

    @Resource
    private CommonCodeServiceImpl commonCodeService;

    @Resource
    private MicrosoftAzureLoginMapper microsoftAzureLoginMapper;

    // expires in 10 minutes
    private Cache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000);


    private final CsceneReportTaskPool csceneReportTaskPool = CsceneReportTaskPool.getInstance();

    private static final String ASSISTANT = "assistance";

    private static final String MONITOR = "monitor";

    private static final String ALL = "all";

    private static final String KQL = "alertsmanagementresources\n" +
            "// 只查报警\n" +
            "| where type == 'microsoft.alertsmanagement/alerts'\n" +
            "// 资源组\n" +
            "| where properties[\"essentials\"][\"targetResource\"] startswith '%s'\n" +
            "// 报警状态 Fired:已触发 Resolved:已解决\n" +
            "| where properties[\"essentials\"][\"monitorCondition\"] in~ ('Fired')\n" +
            "// 查询字段\n" +
            "| project name,severity = properties[\"essentials\"][\"severity\"],targetResource = properties[\"essentials\"][\"targetResourceName\"],\n" +
            "                            monitorCondition = properties[\"essentials\"][\"monitorCondition\"],targetResourceName = properties[\"essentials\"][\"targetResourceName\"],\n" +
            "                            description = properties[\"essentials\"][\"description\"],\n" +
            "                            startDateTime = datetime_add('hour',8,todatetime(properties[\"essentials\"][\"startDateTime\"])),essentials = properties[\"essentials\"]\n" +
            "| order by todatetime(essentials[\"startDateTime\"]) desc";

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

    public String getSceneConfigId(String csceneId) {
        var csceneInstanceResult = get(csceneId).getSource();
        if (csceneInstanceResult == null) {
            return null;
        }
        var csceneInstance = csceneInstanceResult.getObj();
        var sceneId = csceneInstance.getCscene();
        var cscene = csceneService.get(csceneInstance.getCscene()).getSource();
        return cscene.getObj().getSceneConfig();
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
                                                                            String probeId, HostEventQueryParams queryParams,
                                                                            CloudScenePluginHandler cloudPluginHandler,
                                                                            String type, CollectionScenePluginHandler pluginHandler) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition
                .filterEqual(ProbeModel::getType, ProbeModel.TYPE_CUSTOM)
                .filterEqual(ProbeModel::getIsDeleted, 0);
        List<ProbeModel> list = probeMapper.list(humanCondition);
        List<String> customProbeIds = list.stream().map(ProbeModel::getId).collect(Collectors.toList());

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

        var startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
        var endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
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
                var res = r.getSource().getObjs();
                total = r.getSource().getTotal() + total;
                return new ListObjServiceResult<>(new ListObjResult<>(res, total));
            }
        }
        for (String nodeId : nodeIds) {
            var element = sceneHandler.getElement(nodeId);
            var cloudElement = cloudPluginHandler.getElement(nodeId);
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
                    var r = logFileService.pageByElementId(request);
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
                    var r = sceneElementMonitorService.pageNetworkConnections(request);
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
                    var r = hostStateService.pageNetifaces(params);
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
                    var r = hostStateService.pageProcesses(params);
                    if (r.getSource() != null) {
                        res = r.getSource().getObjs();
                        total = r.getSource().getTotal() + total;
                    }
                } else {
                    // 用户变更信息
                    var r = hostStateService.pageSystemUserEvents(params);
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
                var r = dataCollectionService.queryCustomProbeData(request, customProbeIds, sceneHandler.getScene().getCloudType());
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

            res.forEach(r -> {
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
                var time = map.get("time");
                if (Objects.nonNull(time)) {
                    hostEventBO.setTime(TimeUtils.stringToLocalDateTime(time.toString()));
                } else {
                    hostEventBO.setTime(LocalDateTime.now());
                }
                var description = map.get("description");
                if (Objects.nonNull(description)) {
                    hostEventBO.setDescription(description.toString());
                } else {
                    hostEventBO.setDescription("CMD LAUNCH LOG");
                }
                map.put("scrollId", null);
                hostEventBO.setContent(map);
                result.add(hostEventBO);
            });
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
            var newRes = result.stream().limit(queryParams.getPageSize()).collect(Collectors.toList());
            return new ListObjServiceResult<>(new ListObjResult<>(newRes, total));
        }
    }

    private ListObjServiceResult<CollectionHostEventBO> getCustomProbeCollectData(String sceneInstanceId, String sceneId,
                                                                                  List<String> nodeIds, CollectionScenePluginHandler pluginHandler,
                                                                                  Map<String, Map<String, String>> nodeId2ProbeNameMap,
                                                                                  HostEventQueryParams queryParams) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition
                .filterEqual(ProbeModel::getType, ProbeModel.TYPE_CUSTOM)
                .filterEqual(ProbeModel::getIsDeleted, 0);
        List<ProbeModel> list = probeMapper.list(humanCondition);
        List<String> customProbeIds = list.stream().map(ProbeModel::getId).collect(Collectors.toList());
        Map<String, String> customProbeMap = list.stream().collect(Collectors.toMap(ProbeModel::getId, ProbeModel::getName));

        var startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
        var endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
        List<CollectionHostEventBO> result = new ArrayList<>();
        var batchSceneHandler = new BatchSceneHandler(CollUtil.toList(sceneId), null);
        var eid2Element = batchSceneHandler.getAllEid2ElementMap();
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        // 获取节点所有自定义探针id
        List<String> agentNames = new ArrayList<>();
        List<String> probeIds = new ArrayList<>();
        Map<String, String> probeId2ProbeName = new HashMap<>();
        pluginHandler.getElementMap().values().forEach(e -> {
            var element = e.getSrcElement();
            if (nodeIds.contains(element.getId())) {
                agentNames.add(element.getEid());
            }
//            自定义探针采集需要查询历史数据 这里查所有探针id
            probeIds.addAll(customProbeIds);
            probeId2ProbeName.putAll(customProbeMap);
            /*e.getProbes().forEach(probe -> {
                if (!Boolean.TRUE.equals(probe.getCommon()) && !probeIds.contains(probe.getId())) {
                    probeIds.add(probe.getId());
                    probeId2ProbeName.put(probe.getId(), probe.getName());
                }
            });*/
        });
        List<String> queryProbeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryParams.getKeyword())) {
            String keyword = queryParams.getKeyword().get(0);
            probeId2ProbeName.forEach((k, v) -> {
                if (v.contains(keyword)) {
                    queryProbeIds.add(k);
                }
            });
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
        var listObjResult = dataCollectionService.queryCustomProbeData(request, queryProbeIds, sceneHandler.getScene().getCloudType());
        var res = listObjResult.getObjs();
        if (CollUtil.isEmpty(res)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
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
    public CsceneInstanceService(CsceneInstanceMapper modelMapper, CsceneInstanceRoleMapper csceneInstanceRoleMapper,
                                 CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper) {
        setModelMapper(modelMapper);
        setCsceneInstanceRoleMapper(csceneInstanceRoleMapper);
        setCloudPluginSceneConnectionMapper(cloudPluginSceneConnectionMapper);
    }

    public ListObjServiceResult<CsceneInstanceBO> listCsceneInstanceBO(QueryParams queryParams,
                                                                       CsceneContextBO context) {
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
        var instanceHandler = new CsceneInstanceHandler(id, null, context);
        CsceneInstanceBO data = instanceHandler.toData();
        SceneBO scene = data.getScene().getTopology().getScene();
        SceneStageRecorder recorder = SceneStageRecorder.getRecorder(scene.getEid());
        if (recorder != null) {
            recorder.refreshSceneBO(scene);
        }
        return new ServiceResult<>(data);
    }

    public ServiceResult<CsceneInstanceBO> getById(String id, String areaId, CsceneContextBO context) {
        var instanceHandler = new CsceneInstanceHandler(id, areaId, context);
        CsceneInstanceBO data = instanceHandler.toData();
        SceneBO scene = data.getScene().getTopology().getScene();
        SceneStageRecorder recorder = SceneStageRecorder.getRecorder(scene.getEid());
        if (recorder != null) {
            recorder.refreshSceneBO(scene);
        }
        return new ServiceResult<>(data);
    }

    public ServiceResult<Boolean> deleteById(String id, boolean logic, CsceneContextBO context) {
        log.info("deleteById function logic request : {}", logic);
        if (ReentrantLockUtils.tryLock(id)) {
            try {
                // 移除定时任务
                SceneHostStateReportScheduleService.removeCsceneInstance(id);
                var instanceHandler = new CsceneInstanceHandler(id, context);
                NodeLimitUtils.substractLimitedNodeCount(instanceHandler);
                String sceneId = this.getSceneId(id);
                var flag = instanceHandler.delete(logic);
                if (sceneId != null) {
//                    关闭场景接口中删除节点探针关联表数据
                    HumanCondition probeListHumanCondition = new HumanCondition()
                            .filterEqual(SceneElementSituationProbeModel::getScene, sceneId);
                    sceneElementSituationProbeMapper.filterDelete(probeListHumanCondition);

                    scenePluginCloudSceneService.deleteSceneRecords(sceneId, logic);
                    resourceClearHandler.doCallback(id);
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
            List<UserBo> users = userService.listByUserIds(existUserIds.toArray(new String[]{}));
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

    public List<CloudPluginSceneElementModel> listLoads(String id) {
//        2.根据sceneId查询场景
//            plugins.topology
        SceneHandler sceneHandler = new SceneHandler(csceneCommonService.translateSceneId(id), null, false, null);
//            plugins.cpcloud
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
//        3.获取节点内工具idList
        Map<String, CloudPluginSceneElementBO> elementMap = pluginHandler.getElementMap();
        List<CloudPluginSceneElementModel> list = elementMap.values()
                .stream()
                .filter(element -> element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.LOAD)
                .map(element -> element.toModel())
                .collect(Collectors.toList());

        return list;
    }

    public List<CloudPluginSceneElementModel> getLoadResultByNodeIds(String sceneId, String[] nodeIds) {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CloudPluginSceneElementModel::getScene, sceneId);
        if (nodeIds != null && nodeIds.length > 0) {
            humanCondition.filterIn(CloudPluginSceneElementModel::getSceneElement, nodeIds);
        }
        List<CloudPluginSceneElementModel> list = cloudPluginSceneElementMapper.list(humanCondition);
        return list;
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStatesByAgentNames(List<String> agentNames, HostStateQueryParams queryParams) {
        queryParams.setNodeIds(null);
        return scenePluginCollectionSceneService.listHostStatesByAgentNames(agentNames, queryParams);
    }

    public Map<String, List<CollectionHostStateBO>> mapHostStatesByAgentNames(List<String> agentNames, HostStateQueryParams queryParams, Integer cloudType) {
        queryParams.setNodeIds(null);
        return scenePluginCollectionSceneService.mapHostStatesByAgentNames(agentNames, queryParams, cloudType);
    }

    public ServiceResult<Map<String, Integer>> queryCollectionDataCount(QueryCollectionDataCountParams params) {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        List<Runnable> tasks = new ArrayList<>();
        String sceneId = params.getSceneId();

        String translateSceneId = csceneCommonService.translateSceneId(sceneId);
        SceneHandler sceneHandler = new SceneHandler(translateSceneId, null, false, null);
        if (params.getNodeIds() == null || params.getNodeIds().isEmpty()) {
            params.setNodeIds(new ArrayList<>(sceneHandler.getElementMap().keySet()));
        }
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
            var fileCount = sceneElementMonitorService.getModifyData(request, CsceneBizConstant.FILE_MODIFY_GROUP,
                    null).getSource().getTotal();
            var registryCount = sceneElementMonitorService.getModifyData(request, CsceneBizConstant.REGISTRY_MODIFY_GROUP,
                    null).getSource().getTotal();
//            载荷执行结果
            List<MpDevopsLoadSceneLog> loadlog = loadService.getLoadResultByNodeIds(sceneId, params.getNodeIds().toArray(new String[0]), null, params.getEndTime());
            count.put("event", fileCount + registryCount + loadlog.size());
        });
        // 信息类
        tasks.add(() -> {
            // 网络端口
            TimeListPageInfoQueryParams infoParams = new TimeListPageInfoQueryParams();
            infoParams.setSceneIds(Arrays.asList(params.getSceneId()));
            infoParams.setNodeIds(params.getNodeIds());
            infoParams.setEndTime(params.getEndTime());
            infoParams.setFetchIfSceneInstanceShutdown(true);
            var netCount = hostStateService.pageNetifaces(infoParams).getSource().getTotal();
            // 端口列表
            infoParams.setSceneIds(Arrays.asList(params.getSceneId()));
            var portCount = hostStateService.pagePorts(infoParams).getSource().getTotal();
            // 进程列表
            infoParams.setSceneIds(Arrays.asList(params.getSceneId()));
            var processCount = hostStateService.pageProcesses(infoParams).getSource().getTotal();
            count.put("info", netCount + portCount + processCount);
        });
        // 日志类
        tasks.add(() -> {
            List<String> categories = Arrays.asList(
                    "020101", "020102", "020103", "020104", "020105", "020106", "020107",
                    "020201", "020202", "020203", "020204", "020205", "020206", "020207", "020208", "020210",
                    "020301", "020302", "020303", "020304", "020313", "020305"
            );
            HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
            hostEventQueryParams.setNodeIds(params.getNodeIds());
            hostEventQueryParams.setEndTime(params.getEndTime());
            hostEventQueryParams.setCatagories(categories);
            var result = listHostEvents(params.getSceneId(), hostEventQueryParams);

            TimeListPageInfoQueryParams timeListPageInfoQueryParams = new TimeListPageInfoQueryParams();
            timeListPageInfoQueryParams.setSceneIds(List.of(translateSceneId));
            timeListPageInfoQueryParams.setNodeIds(params.getNodeIds());
            var dataModels = dataRecordService.getData(timeListPageInfoQueryParams, CollectionPluginSceneDataModel.Type.LOGFile, sceneHandler.getScene().getCreateTime(),
                    LocalDateTime.now());
            var logs = dataModels.stream().map(d -> {
                return JsonCommon.parse(d.getData(), LogFileVO.class);
            }).collect(Collectors.toList());
            Set<String> exists = new HashSet<>();
            List<LogFileVO> finalResult = new ArrayList<>();
            for (var log : logs) {
                if (!exists.contains(log.getPath())) {
                    exists.add(log.getPath());
                    finalResult.add(log);
                }
            }
            count.put("log", result.getSource().getTotal() + finalResult.size());
        });
        // 流量类
        tasks.add(() -> {
            FlowQueryParams flowQueryParams = new FlowQueryParams();
            flowQueryParams.setTargetNodeIds(params.getNodeIds());
            flowQueryParams.setEndTime(params.getEndTime());
            var flowCount = flowMonitorService.pageFlow(params.getSceneId(), flowQueryParams).getSource().getTotal();
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
        var sceneId = getSceneId(id);
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

            if (queryParams.getType().equals(7) || queryParams.getType().equals(8)) {
                //        处理云场景警告
                ListObjServiceResult<CollectionHostEventBO> listObjServiceResult = new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
                List<ElementLogListModel> logList = new ArrayList<>();
                processAndFillHostEvents(listObjServiceResult, queryParams, id, logList);
                return listObjServiceResult;
            }
        }
        ListObjServiceResult<CollectionHostEventBO> hostEvents = scenePluginCollectionSceneService.listHostEvents(sceneId, queryParams);
        mergeFileData(hostEvents, queryParams, id);
        return hostEvents;
    }

    private void mergeFileData(ListObjServiceResult<CollectionHostEventBO> hostEventsBos, HostEventQueryParams queryParams,
                               String sceneId) {
        HumanCondition humanCondition = new HumanCondition().currentLink(ElementLogListModel.class)
                .filterEqual(ElementLogListModel::getSceneId, sceneId);
//        区分日志类型(日志/告警)
        if (null != queryParams.getRuleGroups() && queryParams.getRuleGroups().size() > 0) {
            humanCondition.filterEqual(ElementLogListModel::getLogType, "");
        } else if (null != queryParams.getRuleIds() && CollectionUtils.isNotEmpty(queryParams.getRuleIds())) {
            humanCondition.filterIn(ElementLogListModel::getRuleId, queryParams.getRuleIds().toArray());
            humanCondition.filterEqual(ElementLogListModel::getLogType, SceneElementMonitorService.LOG);
        } else {
            humanCondition.filterEqual(ElementLogListModel::getLogType, SceneElementMonitorService.ALARM);
        }
        if (StringUtils.isNotEmpty(queryParams.getStartTime())) {
            humanCondition.filterGreaterThan(ElementLogListModel::getTime, queryParams.getStartTime());
        }
        if (StringUtils.isNotEmpty(queryParams.getEndTime())) {
            humanCondition.filterLessThan(ElementLogListModel::getTime, queryParams.getEndTime());
        }
        if (CollectionUtils.isNotEmpty(queryParams.getLevels())) {
            humanCondition.filterIn(ElementLogListModel::getLevel, queryParams.getLevels().toArray());
        }
        if (CollectionUtils.isNotEmpty(queryParams.getKeyword())) {

            humanCondition.filterContains(ElementLogListModel::getDescription, queryParams.getKeyword().get(0));
        }
        var logList = elementLogListMapper.list(humanCondition);


        processAndFillHostEvents(hostEventsBos, queryParams, sceneId, logList);
    }

    /**
     * 处理和填充主机事件列表
     *
     * @param hostEventsBos
     * @param queryParams
     * @param sceneId
     * @param logList
     */
    private void processAndFillHostEvents(ListObjServiceResult<CollectionHostEventBO> hostEventsBos, HostEventQueryParams queryParams, String sceneId, List<ElementLogListModel> logList) {
        //        处理云场景警告
        getCloudAlerts(queryParams, sceneId, logList);
        List<CollectionHostEventBO> bos = hostEventsBos.getSource().getObjs();
        // 设置total
        int alreadyHasNum = hostEventsBos.getSource().getTotal();
        hostEventsBos.getSource().setTotal(alreadyHasNum + logList.size());

        // 当前页元素数量足够
        int pageSize = queryParams.getPageSize() == null ? 10 : queryParams.getPageSize();
        if (bos.size() == pageSize) {
            return;
        }

        // 计算分页起始位置
        Integer page = queryParams.getPage() == null ? 1 : queryParams.getPage();
        int totalNeedNum = pageSize * page;
        int stillNeedNum = totalNeedNum - alreadyHasNum;
        int startIndex = stillNeedNum <= pageSize ? 0 : stillNeedNum - pageSize;
        if (startIndex >= logList.size()) {
            return;
        }
        logList = logList.subList(startIndex, Math.min(logList.size(), stillNeedNum));

        for (ElementLogListModel model : logList) {
            CollectionHostEventBO bo = new CollectionHostEventBO();
            bo.setNodeName(model.getNodeName());
            if (Objects.nonNull(model.getLevel())) {
                bo.setLevel(model.getLevel());
            } else {
                bo.setLevel(-1);
            }
            bo.setDescription(model.getDescription());
            bo.setTime(model.getTime());
            bo.setAppName(model.getAppName());
            bo.setEventDesc(model.getEventDesc());
            bo.setLevel(model.getLevel());
            bo.setCatagory(model.getCatagory());
            bo.setType(model.getLogType());
            bos.add(bo);
            if (bos.size() == queryParams.getPageSize()) {
                return;
            }
        }
    }

    /**
     * 处理云场景告警
     *
     * @param queryParams
     * @param sceneId
     * @param logList
     */
    private void getCloudAlerts(HostEventQueryParams queryParams, String sceneId, List<ElementLogListModel> logList) {
        if (queryParams.getIsAlert() != null && queryParams.getIsAlert()) {
            var scene = getSceneId(sceneId);
            var sceneHandler = new SceneHandler(scene, null, false, null);
            Integer cloudType = sceneHandler.getScene().getCloudType();
            if (cloudType != null && BaseSceneModel.CloudType.DEFAULT != cloudType) {
//            查询报警规则
                HumanCondition instanceHumanCondition = new HumanCondition()
                        .filterEqual(InstanceModel::getScene, scene);
                InstanceModel instanceModel = instanceMapper.first(instanceHumanCondition);

                HumanCondition alertRuleHumanCondition = new HumanCondition()
                        .filterEqual(AlertRuleModel::getSceneId, instanceModel.getCscene());
                List<AlertRuleModel> list = alertRuleMapper.list(alertRuleHumanCondition);
                List<AlertRuleBO> alertRuleBOList = list.stream().map(alertRuleModel -> {
                            AlertRuleBO alertRuleBO = new AlertRuleBO();
                            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(alertRuleModel, alertRuleBO);
                            alertRuleBO.parseHosts();
                            alertRuleBO.parseAlertConditions();
                            return alertRuleBO;
                        })
                        .collect(Collectors.toList());

//            查询报警

                CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                List<CloudPluginSceneElementBO> allElements = new ArrayList<>(pluginHandler.getElementMap().values())
                        .stream()
                        .filter(element -> queryParams.getNodeIds() != null ? queryParams.getNodeIds().contains(element.getSrcElement().getId()) : true)
                        .collect(Collectors.toList());

                for (AlertRuleBO alertRuleBO : alertRuleBOList) {
                    List<String> hostList = alertRuleBO.getHostList();
                    List<String> collect = allElements
                            .stream()
                            .filter(element -> hostList.contains(element.getSrcElement().getId()))
                            .filter(element -> StringUtils.isNotBlank(element.getResourceId()))
                            .map(CloudPluginSceneElementBO::getResourceId)
                            .collect(Collectors.toList());
                    alertRuleBO.setVmResourceIdList(collect);
                }

                IAzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType);
                ResourceGraphManager resourceGraphManager = azureResourceManager.getResourceGraphManager();

                String resourceGroupId = sceneHandler.getScene().getResourceGroupId();

//                String kql = String.format(KQL, resourceGroupId);
                StringBuilder kql = new StringBuilder("alertsmanagementresources\n" +
                        "| where type == 'microsoft.alertsmanagement/alerts'\n");
                kql.append("| where properties[\"essentials\"][\"targetResource\"] startswith '").append(resourceGroupId).append("'\n");
                kql.append("| where properties[\"essentials\"][\"monitorCondition\"] in~ ('Fired')\n");
                if (null != queryParams.getNodeIds() && queryParams.getNodeIds().size() > 0) {
                    List<String> vmIds = allElements.stream()
                            .filter(element -> queryParams.getNodeIds().contains(element.getSrcElement().getId()))
                            .map(CloudPluginSceneElementBO::getResourceId)
                            .collect(Collectors.toList());
                    for (int i = 0; i < vmIds.size(); i++) {
                        if (i == 0) {
                            kql.append("| where properties[\"essentials\"][\"targetResource\"] == '").append(vmIds.get(i).toLowerCase()).append("'");
                        } else {
                            kql.append(" or properties[\"essentials\"][\"targetResource\"] == '").append(vmIds.get(i).toLowerCase()).append("'");
                        }
                    }
                    kql.append("\n");
                }
                if (queryParams.getType() != null) {
                    if (queryParams.getType().equals(7)) {
                        kql.append("| where properties[\"essentials\"][\"signalType\"] == 'Metric'\n");
                    }
                    if (queryParams.getType().equals(8)) {
                        kql.append("| where properties[\"essentials\"][\"signalType\"] == 'Log'\n");
                    }
                }
                if (queryParams.getLevels() != null) {
                    Integer azureCodeByCode = AlertRuleBO.Grade.getAzureCodeByCodes(queryParams.getLevels());
                    kql.append("| where properties[\"essentials\"][\"severity\"] matches regex '.*").append(azureCodeByCode).append("'\n");
                }

                if (StringUtils.isNotBlank(queryParams.getStartTime())) {
                    String startTime = TimeUtils.stringToLocalDateTime(queryParams.getStartTime()).atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter);
                    kql.append("| where properties['essentials']['startDateTime'] >= datetime('").append(startTime).append("') \n");
                }
                if (StringUtils.isNotBlank(queryParams.getEndTime())) {
                    String endTime = TimeUtils.stringToLocalDateTime(queryParams.getEndTime()).atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter);
                    kql.append("| where properties['essentials']['startDateTime'] <= datetime('").append(endTime).append("') \n");
                }

                if (queryParams.getKeyword() != null && queryParams.getKeyword().size() > 0) {
                    kql.append("| where properties[\"essentials\"][\"description\"] contains '").append(queryParams.getKeyword().get(0)).append("'\n");
                }
                kql.append("| project name,severity = properties[\"essentials\"][\"severity\"],targetResource = properties[\"essentials\"][\"targetResource\"],\n" +
                        "                            monitorCondition = properties[\"essentials\"][\"monitorCondition\"],targetResourceName = properties[\"essentials\"][\"targetResourceName\"],\n" +
                        "                            description = properties[\"essentials\"][\"description\"],startDateTime = todatetime(properties[\"essentials\"][\"startDateTime\"]),\n" +
                        "                            essentials = properties[\"essentials\"],signalType = properties[\"essentials\"][\"signalType\"]\n" +
                        "| order by todatetime(essentials[\"startDateTime\"]) desc");

                QueryRequest queryRequest = new QueryRequest();
                queryRequest.withQuery(kql.toString());
                QueryResponse resources = resourceGraphManager.resourceProviders().resources(queryRequest);
//            组装数据
                if (resources.data() != null) {
                    List<Map> dataList = (List<Map>) resources.data();
                    for (Map map : dataList) {
                        String targetResourceName = map.get("targetResourceName").toString();
                        String severity = map.get("severity").toString();
                        String startDateTime = map.get("startDateTime").toString();
                        String description = map.get("description") != null ? map.get("description").toString() : "";
                        String signalType = map.get("signalType") != null ? map.get("signalType").toString() : "";

                        String finalTargetResourceName = targetResourceName;
                        Optional<CloudPluginSceneElementBO> first = allElements.stream()
                                .filter(element -> element.getResourceId().toLowerCase().contains(finalTargetResourceName))
                                .findFirst();


                        if (first.isPresent()) {
                            CloudPluginSceneElementBO cloudPluginSceneElementBO = first.get();

                            // 将UTC时间字符串转换为ZonedDateTime
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
                            ZonedDateTime zonedDateTime = ZonedDateTime.parse(startDateTime, formatter);

                            // 将ZonedDateTime转换为LocalDateTime
                            ZoneId localZoneId = ZoneId.systemDefault(); // 获取本地时区
                            LocalDateTime localDateTime1 = zonedDateTime.withZoneSameInstant(localZoneId).toLocalDateTime();

                            ElementLogListModel elementLogListModel = new ElementLogListModel();
                            elementLogListModel.setNodeName(cloudPluginSceneElementBO.getSrcElement().getName());
                            elementLogListModel.setLevel(AlertRuleBO.Grade.getCodeByAzureCode(Integer.valueOf(severity.substring(severity.length() - 1))));
                            elementLogListModel.setTime(localDateTime1);
                            elementLogListModel.setDescription(description);
                            elementLogListModel.setLogType(signalType.equals("Metric") ? "SYSTEM" : "OTHER");
                            logList.add(elementLogListModel);
                        }
                    }
                }
            }
        }
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
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        return scenePluginCollectionSceneService.scrollHostEvents(sceneId, scrollId, queryParams);
    }

    public ListObjServiceResult<CollectionHostEventBO> getProbeCollectionData(String requestId, String sceneInstanceId,
                                                                              String probeId, HostEventQueryParams queryParams,
                                                                              String type) {
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
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
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
                    probeId, queryParams, cloudPluginHandler, type, pluginHandler);
        } else {
            // 无探针id，查询所有自定义探针采集数据
            ListObjServiceResult<CollectionHostEventBO> hostEventsBos = getCustomProbeCollectData(sceneInstanceId, sceneId, newNodeIds, pluginHandler, nodeId2ProbeNameMap, queryParams);
            mergeCustomProbeCollectData(hostEventsBos, queryParams, sceneInstanceId);
            return hostEventsBos;
        }
    }


    private void mergeCustomProbeCollectData(ListObjServiceResult<CollectionHostEventBO> hostEventsBos, HostEventQueryParams queryParams, String sceneId) {

        List<CollectionHostEventBO> bos = hostEventsBos.getSource().getObjs();

        HumanCondition humanCondition = new HumanCondition().currentLink(WazuhProbeDataModel.class)
                .filterEqual(WazuhProbeDataModel::getSceneId, sceneId);

        if (StringUtils.isNotEmpty(queryParams.getStartTime())) {
            humanCondition.filterGreaterThan(WazuhProbeDataModel::getTime, queryParams.getStartTime());
        }
        if (StringUtils.isNotEmpty(queryParams.getEndTime())) {
            humanCondition.filterLessThan(WazuhProbeDataModel::getTime, queryParams.getEndTime());
        }
        if (CollectionUtils.isNotEmpty(queryParams.getKeyword())) {
            humanCondition.filterContains(WazuhProbeDataModel::getProbeName, queryParams.getKeyword().get(0));
        }

        var dataList = wazuhProbeDataMapper.list(humanCondition);

        // 设置total
        int alreadyHasNum = hostEventsBos.getSource().getTotal();
        hostEventsBos.getSource().setTotal(alreadyHasNum + dataList.size());

        // 当前页元素数量足够
        int pageSize = queryParams.getPageSize();
        if (bos.size() == pageSize) {
            return;
        }

        // 计算分页起始位置
        int totalNeedNum = pageSize * queryParams.getPage();
        int stillNeedNum = totalNeedNum - alreadyHasNum;
        int startIndex = stillNeedNum <= pageSize ? 0 : stillNeedNum - pageSize;
        if (startIndex >= dataList.size()) {
            return;
        }
        dataList = dataList.subList(startIndex, Math.min(dataList.size(), stillNeedNum));

        for (WazuhProbeDataModel model : dataList) {
            CollectionHostEventBO bo = new CollectionHostEventBO();
            bo.setNodeName(model.getNodeName());
            bo.setProbeName(model.getProbeName());
            bo.setEventDesc(model.getEventDesc());
            bo.setContent(stringToMap(model.getContent()));
            bo.setTime(model.getTime());
            bos.add(bo);
            if (bos.size() == queryParams.getPageSize()) {
                return;
            }
        }
    }

    public Map<String, Object> stringToMap(String str) {
        if (str == null) return new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        String[] pairs = str.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split("\":\"");
            if (keyValue.length == 2) {
                if (keyValue[0].startsWith("\"")) {
                    keyValue[0] = keyValue[0].substring(1);
                }
                if (keyValue[1].endsWith("\"")) {
                    keyValue[1] = keyValue[1].substring(0, keyValue[1].length() - 1);
                }
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
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

    public ServiceResult<NodeConnectionBO> getRemoteConnection(String id, String nodeId, String accessModeId, String interpreter, CsceneContextBO context) {
        try {
            CsceneContextHolder.setCsceneInstanceId(id);
            CsceneContextHolder.setCallAction(CsceneCallAction.NODE_ACCESS_USER);
            if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
                return new ServiceResult<>(null);
            }
            if (context.getPassThrough() == null) {
                CsceneInstanceBO csceneInstance = new CsceneInstanceBO();
                var csceneInstanceModel = getModelMapper().get(id);
                csceneInstance.fromModel(csceneInstanceModel);
            }
            context.loadTransparentContext();

            var sceneId = getSceneId(id);
            if (StringUtils.isEmpty(sceneId)) {
                return new ServiceResult<>(null);
            }
            var cloudScenePluginContext = context.toCloudScenePluginContext();
            var result = scenePluginCloudSceneService.getSceneRemoteConnection(sceneId, nodeId, accessModeId, interpreter,
                    context.getAccessUserId(), cloudScenePluginContext);

            this.reportElementAccessUser(sceneId, nodeId);
            return result;
        } finally {
            CsceneContextHolder.resetContext();
        }
    }

    public ServiceResult<NodeConnectionBO> getRemoteTtyConnection(String id, String nodeId) {
        var sceneId = getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        var result = scenePluginCloudSceneService.getRemoteTtyConnection(sceneId, nodeId).getSource();
        result.setSceneId(id);
        return new ServiceResult<>(result);
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
        try {
            CsceneContextHolder.setCsceneInstanceId(id);
            CsceneContextHolder.setCallAction(CsceneCallAction.NODE_ACCESS_USER);
            if (context == null || StringUtils.isEmpty(context.getAccessUserId())) {
                return new ServiceResult<>(null);
            }
            if (context.getPassThrough() == null) {
                CsceneInstanceBO csceneInstance = new CsceneInstanceBO();
                var csceneInstanceModel = getModelMapper().get(id);
                csceneInstance.fromModel(csceneInstanceModel);
            }
            context.loadTransparentContext();

            var sceneId = getSceneId(id);
            if (StringUtils.isEmpty(sceneId)) {
                return new ServiceResult<>(null);
            }
            var result = scenePluginCloudSceneService.disableRemoteRecording(sceneId, nodeId, context.getAccessUserId(), connectionId);
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

    /**
     * @param id
     * @param nodeId
     * @param context
     * @return ServiceResult<Boolean>
     * @description: 启动节点
     */
    public ServiceResult<Boolean> startElement(String id, String nodeId, CsceneContextBO context) {
        //获取场景实例操作对象
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        //执行节点启动
        boolean result = instanceHandler.startElement(nodeId);
        return new ServiceResult<>(result);
    }

    /**
     * @param csceneId 场景id
     * @param nodeId   节点id
     * @description: 如果是gns3靶标，禁止开机/重启/关机/重建
     */
    public void unSupportedOperation(String csceneId, String nodeId) {
        CsceneInstanceHandler csceneInstanceHandler = new CsceneInstanceHandler(csceneId, null, true);
        String sceneId = csceneInstanceHandler.getSceneId();
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
        //获取场景实例操作对象
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        //执行节点启动
        boolean result = instanceHandler.stopElement(nodeId);
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

    public ServiceResult<Boolean> createElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.createElement(nodeId);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> recreateElement(String id, String nodeId, String image, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.recreateElement(nodeId, image);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> modifyElement(ElementModifyBO bo, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(bo.getId(), context, true);
        boolean result = instanceHandler.modifyElement(bo);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> deleteElement(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.deleteElement(nodeId);
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
        var queryInstance = this.get(id).getSource();
        if (queryInstance == null || queryInstance.getObj() == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        var csceneInstance = queryInstance.getObj();
        String csceneId = csceneInstance.getCscene();
        String csceneInstanceId = csceneInstance.getId();
        var checkNameDuplicated = csceneSnapshotService.checkSnapshotName(csceneId, csceneInstanceId, nodeId, snapshotName).getSource();
        if (!Boolean.TRUE.equals(checkNameDuplicated)) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NAME_DUPLICATED);
        }

        // 先保存cscene中的关联信息
        String snapshotId = csceneSnapshotService.saveCsceneSnapshot(csceneId, csceneInstanceId, nodeId, snapshotName).getSource();

        // 异步创建快照
        ElementTaskPool.getInstance().executeTask(() -> {
            // 打快照
            CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
            boolean result = instanceHandler.snapshotElement(nodeId, snapshotId, snapshotName, description);

            // 更新场景模板靶标的快照、cscene中的关联信息
            var sceneInfo = this.getSceneInfo(id);
            if (StringUtils.isNotEmpty(sceneInfo.getSceneConfigId())) {
                if (result) {
                    var model = sceneSnapshotService.getElementSnapshot(snapshotId).getSource();
                    if (model != null) {
                        scenePluginCloudSceneConfigService.updateElementImage(sceneInfo.getSceneConfigId(), nodeId, SnapshotAddressUtils.parseAddress(model.getAddress()).getImageName());
                        var parseResult = SnapshotAddressUtils.parseAddress(model.getAddress());
                        csceneSnapshotService.updateCsceneSnapshotRelativeInfo(snapshotId, parseResult.getSnapshotUuid(), model.getSourceName());
                    } else {
                        log.error("do not find snapshot from scene snapshot , id is {}", snapshotId);
                    }
                }
            }
        });
        return new ServiceResult<>(true);
    }

    public ServiceResult<ElementCommandExecResult> execElement(String id, String nodeId, String command,
                                                               String user, CsceneContextBO context) {
        String sceneId = getSceneId(id);
        var execBO = scenePluginCloudSceneService.execElementCommand(sceneId, nodeId, command, user).getSource();
        var result = new ElementCommandExecResult();
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
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        Integer cloudType = sceneHandler.getScene().getCloudType();
        var result = scenePluginCloudSceneService.linkStatus(sceneId).getSource();
        SceneLinkStatusInfo sceneLinkStatusInfo = new SceneLinkStatusInfo();
        sceneLinkStatusInfo.setNodeLinkStatusList(result);
        sceneLinkStatusInfo.setCsceneId(csceneId);
        sceneLinkStatusInfo.getNodeLinkStatusList().stream().forEach(nodeLinkStatusBO -> {
            nodeLinkStatusBO.setStatus(1);
            if (cloudType != null && BaseSceneModel.CloudType.DEFAULT != cloudType) {
                nodeLinkStatusBO.getConnectionStatusList().stream().forEach(connectionStatusBO -> {
                    connectionStatusBO.setStatus(1);
                });
            }

        });
        return new ServiceResult<>(sceneLinkStatusInfo);
    }

    public ServiceResult<Boolean> deleteElementSnapshot(String csceneId, String nodeId, String snapshotId) {
        // 校验场景是否存在、快照是否在当前场景模板下
        var queryInstance = this.get(csceneId).getSource();
        if (queryInstance == null || queryInstance.getObj() == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        var csceneInstance = queryInstance.getObj();
        var csceneSnapshot = csceneSnapshotService.getCsceneSnapshot(csceneInstance.getCscene(), nodeId, snapshotId).getSource();
        if (csceneSnapshot == null) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NOT_CURRENT_SCENE);
        }
        var snapshot = sceneSnapshotService.getElementSnapshot(csceneSnapshot.getSnapshotId()).getSource();
        if (snapshot == null) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_NOT_CURRENT_SCENE);
        }
        SceneInfo sceneInfo = this.getSceneInfo(csceneId);
        if (sceneInfo == null || StringUtils.isEmpty(sceneInfo.getSceneId()) || StringUtils.isEmpty(sceneInfo.getSceneConfigId())) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }

        // 校验快照是否被场景和场景模板引用
        String image = SnapshotAddressUtils.parseAddress(snapshot.getAddress()).getImageName();
        var configUsed = csceneSpecialService.statDeviceReferCscenes(new String[]{image}).getSource();
        var configNames = this.getReferCsceneNames(configUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(configNames)) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_IN_USE.format(StringUtils.join(configNames, ",")));
        }

        var instanceUsed = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{image}).getSource();
        var instanceNames = this.getReferCsceneNames(instanceUsed.get(image), (c) -> c.getName(), 3);
        if (CollectionUtils.isNotEmpty(instanceNames)) {
            throw new MpRuntimeException(CsceneErrorCode.SNAPSHOT_IN_USE.format(StringUtils.join(instanceNames, ",")));
        }

        String sceneId = sceneInfo.getSceneId();
        csceneSnapshotService.logicDeleteCsceneSnapshot(csceneInstance.getCscene(), nodeId, snapshotId);
        var result = scenePluginCloudSceneService.deleteElementSnapshot(sceneId, nodeId, snapshot.getId(), snapshotId, snapshot.getName()).getSource();
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
        for (var used : usingList) {
            if (names.size() < nameSize) {
                names.add(getName.apply(used));
            }
        }
        return new ArrayList<>(names);
    }

    private SceneInfo getSceneInfo(String csceneInstanceId) {
        var condition = new HumanCondition().link(CsceneInstanceModel.class, CsceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getCscene, CsceneModel.class, CsceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getId, csceneInstanceId)
                .selectAs(CsceneInstanceModel::getScene, SceneInfo::getSceneId)
                .currentLink(CsceneModel.class)
                .selectAs(CsceneModel::getSceneConfig, SceneInfo::getSceneConfigId);
        var list = getModelMapper().list(condition, SceneInfo.class);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ServiceResult<CsceneInstanceBO> instanceModify(CsceneUpdateBO csceneBO) {
        var originHandler = new CsceneInstanceHandler(csceneBO.getId(), null);
        // 更新区域
        csceneBO.getUpdateScene().getTopology().getScene().setEid(originHandler.getSceneId());
        SceneHandler sceneHandler = new SceneHandler(csceneBO.getUpdateScene(), null);
        sceneHandler.save();

        CsceneInstanceBO instance = new CsceneInstanceBO();
        instance.setId(csceneBO.getId());
        instance.setPassThrough(csceneBO.getContext() != null ? csceneBO.getContext().getPassThrough() : new HashMap<>());
        instance.setScene(csceneBO.getUpdateScene());

        // 保留隐藏节点和隐藏端口
        var originData = originHandler.toDataWithHidden().getScene();
        Set<String> hiddenElementIds = new HashSet<>();
        Set<String> hiddenPortIds = new HashSet<>();
        Map<String, List<SceneElementPortBO>> hiddenPorts = new HashMap<>();
        for (var e : originData.getTopology().getElements()) {
            if (Boolean.TRUE.equals(e.getIsAuxiliary())) {
                csceneBO.getUpdateScene().getTopology().getElements().add(e);
                hiddenElementIds.add(e.getId());
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (var p : e.getPorts()) {
                        hiddenPortIds.add(p.getId());
                    }
                }
            } else if (CollectionUtils.isNotEmpty(e.getPorts())) {
                for (var p : e.getPorts()) {
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
            for (var e : instance.getScene().getTopology().getElements()) {
                if (hiddenPorts.containsKey(e.getId())) {
                    var hiddenPostList = hiddenPorts.get(e.getId());
                    for (int i = hiddenPostList.size() - 1; i >= 0; i--) {
                        if (e.getPorts() == null) {
                            e.setPorts(new ArrayList<>());
                        }
                        e.getPorts().add(0, hiddenPostList.get(i));
                    }
                }
            }
        }
        for (var c : originData.getTopology().getConnections()) {
            if (hiddenPortIds.contains(c.getSource()) || hiddenPortIds.contains(c.getTarget())) {
                instance.getScene().getTopology().getConnections().add(c);
            }
        }

        var plugins = originData.getPlugins();
        for (var plugin : plugins.entrySet()) {
            if (!cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY.equals(plugin.getKey())) {
                // 暂不支持采集和发生插件的修改 保留原有配置
                instance.getScene().getPlugins().put(plugin.getKey(), plugin.getValue());
            } else {
                // 保留插件的隐藏节点和隐藏端口
                var newPlugin = instance.getScene().getPlugins().get(plugin.getKey());
                for (var e : plugin.getValue().getElements().entrySet()) {
                    if (hiddenElementIds.contains(e.getKey())) {
                        newPlugin.getElements().put(e.getKey(), e.getValue());
                    }
                }
                for (var p : plugin.getValue().getElementPorts().entrySet()) {
                    if (hiddenPortIds.contains(p.getKey())) {
                        newPlugin.getElementPorts().put(p.getKey(), p.getValue());
                    }
                }
            }
        }

        Map<String, SceneElementPortBO> newPorts = new HashMap<>();
        for (var newElement : instance.getScene().getTopology().getElements()) {
            if (CollectionUtils.isNotEmpty(newElement.getPorts())) {
                for (var p : newElement.getPorts()) {
                    newPorts.put(p.getId(), p);
                }
            }
        }

        SceneHandler originSceneHandler = new SceneHandler(originData, null);
        SceneHandler newSceneHandler = new SceneHandler(instance.getScene(), null);
        for (String portId : newPorts.keySet()) {
            var srcPort = originSceneHandler.getPort(portId);
            if (srcPort != null && srcPort.getElement() != null && srcPort.getElement().getType() != null
                    && srcPort.getElement().getType() == BaseSceneElementModel.Type.TERMINAL) {
                var originConnectPort = originSceneHandler.getPortConnectedPort(portId);
                var newConnectPort = newSceneHandler.getPortConnectedPort(portId);
                if (originConnectPort == null && newConnectPort != null) {
                    // 赋值一个新的id
                    var p = newPorts.get(portId);
                    p.setChange(true);
//                    p.setId(portId + "00");
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        var connectElement = newConnectPort.getElement();
                        if (connectElement != null) {
                            CloudScenePluginHandler handler = newSceneHandler.getPluginHandler("cpcloud");
                            if (handler != null) {
                                var connectCloudElement = handler.getElement(connectElement.getId());
                                if (connectCloudElement != null && StringUtils.isNotEmpty(connectCloudElement.getCidr())) {
                                    if (!IpUtils.newIsInCidr2(p.getIp(), connectCloudElement.getCidr())) {
                                        p.setIp(StringUtils.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                    for (var connect : instance.getScene().getTopology().getConnections()) {
                        if (portId.equals(connect.getSource())) {
                            connect.setSource(p.getId());
                        }
                        if (portId.equals(connect.getTarget())) {
                            connect.setTarget(p.getId());
                        }
                    }
                    for (var plugin : instance.getScene().getPlugins().values()) {
                        var pluginPort = plugin.getElementPorts().get(portId);
                        if (pluginPort != null) {
                            pluginPort.remove("logic_network_id");
                            pluginPort.remove("resource_id");
                            plugin.getElementPorts().put(p.getId(), pluginPort);
                        }
                    }
                }
            }
        }

        var instanceHandler = new CsceneInstanceHandler(instance, csceneBO.getContext());
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
            var newPlugin = newData.getScene().getPlugins().get(key);
            var originPlugin = originData.getScene().getPlugins().get(key);
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
        for (var e : originElements) {
            originElementsMap.put(e.getId(), e);
        }
        for (var e : newElements) {
            newElementsMap.put(e.getId(), e);
        }

        Set<String> originElementIds = new HashSet<>();
        originElements.forEach(e -> {
            if (e.getEvent() == SceneModel.Event.MODIFY && e.getEventStatus() == EventStatus.ABNORMAL) {
                updateElementIds.add(e.getId());
                e.getPorts().forEach(p -> {
                    if (StringUtils.isNotEmpty(p.getId())) {
                        updateElementIds.add(p.getId());
                    }
                });
                return;
            }
            originElementIds.add(e.getId());
        });
        Set<String> newElementIds = new HashSet<>();
        newElements.forEach(e -> newElementIds.add(e.getId()));
        List<SceneElementBO> result = new ArrayList<>();
        for (var e : originElements) {
            if (e.getEvent() == SceneModel.Event.MODIFY && e.getEventStatus() == EventStatus.ABNORMAL) {
                continue;
            }
            if (newElementIds.contains(e.getId())) {
                var newPorts = newElementsMap.get(e.getId()).getPorts();
                var originPorts = e.getPorts();
                var mergedPorts = this.mergePorts(
                        CollectionUtils.isEmpty(newPorts) ? new ArrayList<>() : newPorts,
                        CollectionUtils.isEmpty(originPorts) ? new ArrayList<>() : originPorts
                );
                e.setPorts(mergedPorts);
                result.add(e);
            }
        }
        newElementIds.removeAll(originElementIds);
        for (var e : newElements) {
            if (newElementIds.contains(e.getId())) {
                result.add(e);
            }
        }
        return result;
    }

    private List<SceneElementPortBO> mergePorts(List<SceneElementPortBO> newPorts, List<SceneElementPortBO> originPorts) {
        Set<String> originPortIds = new HashSet<>();
        originPorts.forEach(e -> {
            originPortIds.add(e.getId());
        });
        Set<String> newPortIds = new HashSet<>();
        newPorts.forEach(e -> newPortIds.add(e.getId()));
        List<SceneElementPortBO> result = new ArrayList<>();
        for (var e : originPorts) {
            if (newPortIds.contains(e.getId())) {
                result.add(e);
            }
        }
        newPortIds.removeAll(originPortIds);
        for (var e : newPorts) {
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
        originData.forEach((key, value) -> {
            if (updateElementIds.contains(key)) {
                originElementIds.remove(key);
                return;
            }
            if (newElementIds.contains(key)) {
                result.put(key, value);
            }
        });
        newElementIds.removeAll(originElementIds);
        newData.forEach((key, value) -> result.put(key, value));
        return result;
    }

    public void reportElementAccessUser(String sceneId, String nodeId) {
        var connections = cloudPluginSceneConnectionMapper.list(new HumanCondition()
                .select(CloudPluginSceneConnectionModel::getAccessUser)
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, nodeId)
                .reverseOrderBy(CloudPluginSceneConnectionModel::getPk));
        List<AccessUserBO> accessUsers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(connections)) {
            Set<String> userIds = connections.stream().map(CloudPluginSceneConnectionModel::getAccessUser).collect(Collectors.toSet());
            Map<String, UserModel> userId2User = UserCommon.getUsers(new ArrayList<>(userIds)).stream()
                    .collect(Collectors.toMap(userModel -> userModel.getId(), userModel -> userModel));
            connections.stream().distinct().forEach(connection -> {
                var userModel = userId2User.get(connection.getAccessUser());
                if (userModel != null) {
                    AccessUserBO accessUser = new AccessUserBO();
                    accessUser.setId(userModel.getId());
                    accessUser.setName(userModel.getName());
                    accessUsers.add(accessUser);
                }
            });
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


    public List<String> vulnList(String id) {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CloudPluginSceneElementModel::getScene, id);
        List<CloudPluginSceneElementModel> list = cloudPluginSceneElementMapper.list(humanCondition);
        List<String> vulnList = list.stream()
                .filter(e -> Objects.nonNull(e.getLoopholeInfo()))
                .flatMap(e -> Arrays.stream(e.getLoopholeInfo().split(";")))
                .distinct()
                .filter(e -> !e.isEmpty())
                .collect(Collectors.toList());
        return vulnList;
    }

    /**
     * 修改节点探针关联信息
     *
     * @param id
     * @param nodeId
     * @param probeList
     * @param probeType
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyProbes(String id, String nodeId, List<Probe> probeList, int status, Boolean isDelete, SceneElementSituationProbeModel.ProbeType probeType) {
        SceneHandler sceneHandler = new SceneHandler(id, null, null);
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, CollectionPluginSceneElementBO> elementMap = pluginHandler.getElementMap();
        CollectionPluginSceneElementBO collectionPluginSceneElementBO = elementMap.get(nodeId);
//        先删除
        HumanCondition probeListHumanCondition = new HumanCondition()
                .filterEqual(SceneElementSituationProbeModel::getScene, id)
                .filterEqual(SceneElementSituationProbeModel::getSceneElement, nodeId)
                .filterIn(SceneElementSituationProbeModel::getProbeId, probeList.stream().filter(probe -> null != probe.getId()).map(Probe::getId).toArray());
        sceneElementSituationProbeMapper.filterDelete(probeListHumanCondition);

//        更新探针
        probeList.forEach(probe -> {
            modifyProbe(sceneHandler, nodeId, probe, probeType, collectionPluginSceneElementBO);
        });
//            默认执行完成
        List<SceneElementSituationProbeModel> sceneElementSituationProbeModels =
                probeList.stream()
                        .map(probe -> {
                            SceneElementSituationProbeModel sceneElementSituationProbeModel = new SceneElementSituationProbeModel();
                            sceneElementSituationProbeModel.setScene(id);
                            sceneElementSituationProbeModel.setSceneElement(nodeId);
                            sceneElementSituationProbeModel.setProbeId(probe.getId());
                            sceneElementSituationProbeModel.setStatus(status);
                            sceneElementSituationProbeModel.setDeleted(false);
                            sceneElementSituationProbeModel.setDeleted(isDelete);
                            return sceneElementSituationProbeModel;
                        }).collect(Collectors.toList());
        sceneElementSituationProbeMapper.bulkCreate(sceneElementSituationProbeModels);
        HumanCondition humanCondition = new HumanCondition()
                .currentLink(CollectionPluginSceneElementModel.class)
                .filterEqual(CollectionPluginSceneElementModel::getScene, id)
                .filterEqual(CollectionPluginSceneElementModel::getSceneElement, nodeId);
        CollectionPluginSceneElementModel collectionPluginSceneElementModel = collectionPluginSceneElementMapper.first(humanCondition);
        if (null != collectionPluginSceneElementModel) {
            collectionPluginSceneElementModel.setProbes(JsonCommon.stringify(collectionPluginSceneElementBO.getProbes()));
            collectionPluginSceneElementMapper.update(collectionPluginSceneElementModel);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> syncVmSize() throws IOException {
//        AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(BaseSceneModel.CloudType.PUBLIC));
//        List<Map<String, String>> dataMaps = azureVirtualMachine.listsAllSkusWithCapabilities(ComputeResourceType.VIRTUALMACHINES);
        String jsonStr = "[{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"24\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"1000000000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"1000000000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"274877906944\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"384000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC24ads_A100_v4\",\"CombinedTempDiskAndCachedIOPS\":\"75000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_NC24ads_A100_v4\",\"MaxResourceVolumeMB\":\"65536\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"220\",\"ACUs\":\"0\",\"vCPUs\":\"24\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"48\",\"GPUs\":\"2\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"3000000000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"3000000000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"549755813888\",\"UncachedDiskIOPS\":\"76800\",\"UncachedDiskBytesPerSecond\":\"1152000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC48ads_A100_v4\",\"CombinedTempDiskAndCachedIOPS\":\"225000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_NC48ads_A100_v4\",\"MaxResourceVolumeMB\":\"131072\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"440\",\"ACUs\":\"0\",\"vCPUs\":\"48\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A0\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"1\",\"name\":\"Basic_A0\",\"MaxResourceVolumeMB\":\"20480\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"0.75\",\"ACUs\":\"50\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A1\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"2\",\"name\":\"Basic_A1\",\"MaxResourceVolumeMB\":\"40960\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"1.75\",\"ACUs\":\"100\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A2\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"4\",\"name\":\"Basic_A2\",\"MaxResourceVolumeMB\":\"61440\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"3.5\",\"ACUs\":\"100\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A3\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"8\",\"name\":\"Basic_A3\",\"MaxResourceVolumeMB\":\"122880\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"7\",\"ACUs\":\"100\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A4\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"16\",\"name\":\"Basic_A4\",\"MaxResourceVolumeMB\":\"245760\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"100\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A0\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"1\",\"name\":\"Standard_A0\",\"MaxResourceVolumeMB\":\"20480\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"0.75\",\"ACUs\":\"50\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A1\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"2\",\"name\":\"Standard_A1\",\"MaxResourceVolumeMB\":\"71680\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"1.75\",\"ACUs\":\"100\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"41943040\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"20971520\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"48000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A2\",\"CombinedTempDiskAndCachedIOPS\":\"2000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_A2\",\"MaxResourceVolumeMB\":\"138240\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"3.5\",\"ACUs\":\"100\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"83886080\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"41943040\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A3\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_A3\",\"MaxResourceVolumeMB\":\"291840\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"7\",\"ACUs\":\"100\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A4\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_A4\",\"MaxResourceVolumeMB\":\"619520\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"100\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"41943040\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"20971520\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"48000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A5\",\"CombinedTempDiskAndCachedIOPS\":\"2000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_A5\",\"MaxResourceVolumeMB\":\"138240\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"100\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"83886080\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"41943040\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A6\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_A6\",\"MaxResourceVolumeMB\":\"291840\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"100\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"167772160\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"83886080\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"192000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A7\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_A7\",\"MaxResourceVolumeMB\":\"619520\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"100\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"48234496\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"24117248\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D1\",\"CombinedTempDiskAndCachedIOPS\":\"3000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_D1\",\"MaxResourceVolumeMB\":\"51200\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"3.5\",\"ACUs\":\"160\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"97517568\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"48234496\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D2\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_D2\",\"MaxResourceVolumeMB\":\"102400\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"7\",\"ACUs\":\"160\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196083712\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"97517568\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D3\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_D3\",\"MaxResourceVolumeMB\":\"204800\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"160\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196083712\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D4\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_D4\",\"MaxResourceVolumeMB\":\"409600\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"160\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"97517568\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"48234496\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D11\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_D11\",\"MaxResourceVolumeMB\":\"102400\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"160\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196083712\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"97517568\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D12\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_D12\",\"MaxResourceVolumeMB\":\"204800\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"160\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196083712\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D13\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_D13\",\"MaxResourceVolumeMB\":\"409600\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"160\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"786432000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"393216000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D14\",\"CombinedTempDiskAndCachedIOPS\":\"48000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_D14\",\"MaxResourceVolumeMB\":\"819200\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"160\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"48234496\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"24117248\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D1_v2\",\"CombinedTempDiskAndCachedIOPS\":\"3000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_D1_v2\",\"MaxResourceVolumeMB\":\"51200\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"3.5\",\"ACUs\":\"210\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"97517568\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"48234496\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D2_v2\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_D2_v2\",\"MaxResourceVolumeMB\":\"102400\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"7\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196083712\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"97517568\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D3_v2\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_D3_v2\",\"MaxResourceVolumeMB\":\"204800\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196083712\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D4_v2\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_D4_v2\",\"MaxResourceVolumeMB\":\"409600\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"786432000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"393216000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D5_v2\",\"CombinedTempDiskAndCachedIOPS\":\"48000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_D5_v2\",\"MaxResourceVolumeMB\":\"819200\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"97517568\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"48234496\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D11_v2\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_D11_v2\",\"MaxResourceVolumeMB\":\"102400\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196083712\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"97517568\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D12_v2\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_D12_v2\",\"MaxResourceVolumeMB\":\"204800\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196083712\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D13_v2\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_D13_v2\",\"MaxResourceVolumeMB\":\"409600\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"786432000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"393216000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D14_v2\",\"CombinedTempDiskAndCachedIOPS\":\"48000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_D14_v2\",\"MaxResourceVolumeMB\":\"819200\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"33554432\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"33554432\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"46170898432\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"33554432\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS1\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_DS1\",\"MaxResourceVolumeMB\":\"7168\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"3.5\",\"ACUs\":\"160\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"67108864\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"67108864\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"92341796864\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"67108864\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_DS2\",\"MaxResourceVolumeMB\":\"14336\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"7\",\"ACUs\":\"160\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"184683593728\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"134217728\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS3\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_DS3\",\"MaxResourceVolumeMB\":\"28672\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"160\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"369367187456\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"268435456\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS4\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_DS4\",\"MaxResourceVolumeMB\":\"57344\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"160\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"67108864\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"67108864\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"77309411328\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"67108864\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS11\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_DS11\",\"MaxResourceVolumeMB\":\"28672\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"160\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154618822656\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"134217728\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS12\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_DS12\",\"MaxResourceVolumeMB\":\"57344\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"160\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"309237645312\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"268435456\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS13\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_DS13\",\"MaxResourceVolumeMB\":\"114688\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"160\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"618475290624\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"536870912\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS14\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_DS14\",\"MaxResourceVolumeMB\":\"229376\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"160\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"33554432\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"33554432\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"46170898432\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"50331648\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS1_v2\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_DS1_v2\",\"MaxResourceVolumeMB\":\"7168\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"3.5\",\"ACUs\":\"210\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"67108864\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"67108864\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"92341796864\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"100663296\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS2_v2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_DS2_v2\",\"MaxResourceVolumeMB\":\"14336\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"7\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"184683593728\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"201326592\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS3_v2\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_DS3_v2\",\"MaxResourceVolumeMB\":\"28672\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"369367187456\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"402653184\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS4_v2\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_DS4_v2\",\"MaxResourceVolumeMB\":\"57344\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"738734374912\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"805306368\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS5_v2\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_DS5_v2\",\"MaxResourceVolumeMB\":\"114688\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"67108864\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"67108864\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"77309411328\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"100663296\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS11_v2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_DS11_v2\",\"MaxResourceVolumeMB\":\"28672\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"67108864\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"67108864\",\"ParentSize\":\"Standard_DS11_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"77309411328\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"100663296\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS11-1_v2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_DS11-1_v2\",\"MaxResourceVolumeMB\":\"28672\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"14\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"ParentSize\":\"Standard_DS12_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154618822656\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"201326592\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS12_v2\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_DS12_v2\",\"MaxResourceVolumeMB\":\"57344\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"ParentSize\":\"Standard_DS12_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154618822656\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"201326592\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS12-1_v2\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_DS12-1_v2\",\"MaxResourceVolumeMB\":\"57344\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154618822656\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"201326592\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS12-2_v2\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_DS12-2_v2\",\"MaxResourceVolumeMB\":\"57344\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"309237645312\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"402653184\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS13_v2\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_DS13_v2\",\"MaxResourceVolumeMB\":\"114688\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"ParentSize\":\"Standard_DS13_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"309237645312\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"402653184\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS13-4_v2\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_DS13-4_v2\",\"MaxResourceVolumeMB\":\"114688\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"ParentSize\":\"Standard_DS13_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"309237645312\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"402653184\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS13-2_v2\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_DS13-2_v2\",\"MaxResourceVolumeMB\":\"114688\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"618475290624\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"805306368\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS14_v2\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_DS14_v2\",\"MaxResourceVolumeMB\":\"229376\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"ParentSize\":\"Standard_DS14_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"618475290624\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"805306368\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS14-8_v2\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_DS14-8_v2\",\"MaxResourceVolumeMB\":\"229376\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"ParentSize\":\"Standard_DS14_v2\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"618475290624\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"805306368\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"DS14-4_v2\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_DS14-4_v2\",\"MaxResourceVolumeMB\":\"229376\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"49152000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"24576000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"48000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F1\",\"CombinedTempDiskAndCachedIOPS\":\"3000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_F1\",\"MaxResourceVolumeMB\":\"16384\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"2\",\"ACUs\":\"210\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"98304000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"49152000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F2\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_F2\",\"MaxResourceVolumeMB\":\"32768\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"4\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196608000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"98304000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"192000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F4\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_F4\",\"MaxResourceVolumeMB\":\"65536\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"8\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196608000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"384000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F8\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_F8\",\"MaxResourceVolumeMB\":\"131072\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"16\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"786432000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"393216000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"768000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F16\",\"CombinedTempDiskAndCachedIOPS\":\"48000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_F16\",\"MaxResourceVolumeMB\":\"262144\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"32\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"33554432\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"33554432\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"12884901888\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"50331648\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F1s\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_F1s\",\"MaxResourceVolumeMB\":\"4096\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"2\",\"ACUs\":\"210\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"67108864\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"67108864\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"25769803776\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"100663296\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F2s\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_F2s\",\"MaxResourceVolumeMB\":\"8192\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"4\",\"ACUs\":\"210\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"134217728\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"134217728\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"51539607552\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"201326592\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F4s\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_F4s\",\"MaxResourceVolumeMB\":\"16384\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"8\",\"ACUs\":\"210\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"268435456\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"268435456\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"103079215104\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"402653184\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F8s\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_F8s\",\"MaxResourceVolumeMB\":\"32768\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"16\",\"ACUs\":\"210\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"206158430208\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"805306368\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F16s\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"64\",\"name\":\"Standard_F16s\",\"MaxResourceVolumeMB\":\"65536\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"32\",\"ACUs\":\"210\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"1\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"20971520\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"10485760\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"1600\",\"UncachedDiskBytesPerSecond\":\"24000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A1_v2\",\"CombinedTempDiskAndCachedIOPS\":\"1000\",\"MaxDataDiskCount\":\"2\",\"name\":\"Standard_A1_v2\",\"MaxResourceVolumeMB\":\"10240\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"2\",\"ACUs\":\"100\",\"vCPUs\":\"1\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"41943040\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"20971520\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"48000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A2_v2\",\"CombinedTempDiskAndCachedIOPS\":\"2000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_A2_v2\",\"MaxResourceVolumeMB\":\"20480\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"4\",\"ACUs\":\"100\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"83886080\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"41943040\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A4_v2\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_A4_v2\",\"MaxResourceVolumeMB\":\"40960\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"8\",\"ACUs\":\"100\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"167772160\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"83886080\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"192000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A8_v2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_A8_v2\",\"MaxResourceVolumeMB\":\"81920\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"16\",\"ACUs\":\"100\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"41943040\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"20971520\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"48000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A2m_v2\",\"CombinedTempDiskAndCachedIOPS\":\"2000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_A2m_v2\",\"MaxResourceVolumeMB\":\"20480\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"16\",\"ACUs\":\"100\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"83886080\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"41943040\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A4m_v2\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_A4m_v2\",\"MaxResourceVolumeMB\":\"40960\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"32\",\"ACUs\":\"100\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"167772160\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"83886080\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"192000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"A8m_v2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_A8m_v2\",\"MaxResourceVolumeMB\":\"81920\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"64\",\"ACUs\":\"100\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"48234496\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"24117248\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D2_v3\",\"CombinedTempDiskAndCachedIOPS\":\"3000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_D2_v3\",\"MaxResourceVolumeMB\":\"51200\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"8\",\"ACUs\":\"160\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"97517568\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"48234496\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D4_v3\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_D4_v3\",\"MaxResourceVolumeMB\":\"102400\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"16\",\"ACUs\":\"160\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196083712\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"97517568\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D8_v3\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_D8_v3\",\"MaxResourceVolumeMB\":\"204800\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"32\",\"ACUs\":\"160\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196083712\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D16_v3\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_D16_v3\",\"MaxResourceVolumeMB\":\"409600\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"64\",\"ACUs\":\"160\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"32\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"786432000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"393216000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"D32_v3\",\"CombinedTempDiskAndCachedIOPS\":\"48000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_D32_v3\",\"MaxResourceVolumeMB\":\"819200\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"128\",\"ACUs\":\"160\",\"vCPUs\":\"32\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"49152000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"24576000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"48000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"E2_v3\",\"CombinedTempDiskAndCachedIOPS\":\"3000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_E2_v3\",\"MaxResourceVolumeMB\":\"51200\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"16\",\"ACUs\":\"160\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"98304000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"49152000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"E4_v3\",\"CombinedTempDiskAndCachedIOPS\":\"6000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_E4_v3\",\"MaxResourceVolumeMB\":\"102400\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"32\",\"ACUs\":\"160\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"196608000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"98304000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"192000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"E8_v3\",\"CombinedTempDiskAndCachedIOPS\":\"12000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_E8_v3\",\"MaxResourceVolumeMB\":\"204800\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"64\",\"ACUs\":\"160\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"393216000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"196608000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"384000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"E16_v3\",\"CombinedTempDiskAndCachedIOPS\":\"24000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_E16_v3\",\"MaxResourceVolumeMB\":\"409600\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"128\",\"ACUs\":\"160\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"2\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"32505856\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"32505856\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"34359738368\",\"UncachedDiskIOPS\":\"3200\",\"UncachedDiskBytesPerSecond\":\"49283072\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F2s_v2\",\"CombinedTempDiskAndCachedIOPS\":\"4000\",\"MaxDataDiskCount\":\"4\",\"name\":\"Standard_F2s_v2\",\"MaxResourceVolumeMB\":\"16384\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"4\",\"ACUs\":\"195\",\"vCPUs\":\"2\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"65536000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"65536000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"68719476736\",\"UncachedDiskIOPS\":\"6400\",\"UncachedDiskBytesPerSecond\":\"96000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F4s_v2\",\"CombinedTempDiskAndCachedIOPS\":\"8000\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_F4s_v2\",\"MaxResourceVolumeMB\":\"32768\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"8\",\"ACUs\":\"195\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"131072000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"131072000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"137438953472\",\"UncachedDiskIOPS\":\"12800\",\"UncachedDiskBytesPerSecond\":\"192000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F8s_v2\",\"CombinedTempDiskAndCachedIOPS\":\"16000\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_F8s_v2\",\"MaxResourceVolumeMB\":\"65536\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"16\",\"ACUs\":\"195\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"262144000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"262144000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"274877906944\",\"UncachedDiskIOPS\":\"25600\",\"UncachedDiskBytesPerSecond\":\"384000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F16s_v2\",\"CombinedTempDiskAndCachedIOPS\":\"32000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_F16s_v2\",\"MaxResourceVolumeMB\":\"131072\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"32\",\"ACUs\":\"195\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"32\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"536870912\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"536870912\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"549755813888\",\"UncachedDiskIOPS\":\"51200\",\"UncachedDiskBytesPerSecond\":\"768000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F32s_v2\",\"CombinedTempDiskAndCachedIOPS\":\"64000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_F32s_v2\",\"MaxResourceVolumeMB\":\"262144\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"64\",\"ACUs\":\"195\",\"vCPUs\":\"32\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"64\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"1073741824\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"1073741824\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"1099511627776\",\"UncachedDiskIOPS\":\"80000\",\"UncachedDiskBytesPerSecond\":\"1200000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"F64s_v2\",\"CombinedTempDiskAndCachedIOPS\":\"128000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_F64s_v2\",\"MaxResourceVolumeMB\":\"524288\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"128\",\"ACUs\":\"195\",\"vCPUs\":\"64\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"6\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC6s_v3\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"12\",\"name\":\"Standard_NC6s_v3\",\"MaxResourceVolumeMB\":\"344064\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"112\",\"ACUs\":\"0\",\"vCPUs\":\"6\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"12\",\"GPUs\":\"2\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC12s_v3\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"24\",\"name\":\"Standard_NC12s_v3\",\"MaxResourceVolumeMB\":\"688128\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"224\",\"ACUs\":\"0\",\"vCPUs\":\"12\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"24\",\"GPUs\":\"4\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC24s_v3\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_NC24s_v3\",\"MaxResourceVolumeMB\":\"1376256\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"448\",\"ACUs\":\"0\",\"vCPUs\":\"24\"},{\"PremiumIO\":\"False\",\"vCPUsAvailable\":\"20\",\"GPUs\":\"0\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"491520000\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"245760000\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"32000\",\"UncachedDiskBytesPerSecond\":\"480000000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"E20_v3\",\"CombinedTempDiskAndCachedIOPS\":\"30000\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_E20_v3\",\"MaxResourceVolumeMB\":\"512000\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"160\",\"ACUs\":\"160\",\"vCPUs\":\"20\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NV4as_v4\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_NV4as_v4\",\"MaxResourceVolumeMB\":\"90112\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"14\",\"ACUs\":\"0\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NV8as_v4\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_NV8as_v4\",\"MaxResourceVolumeMB\":\"180224\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"28\",\"ACUs\":\"0\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NV16as_v4\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_NV16as_v4\",\"MaxResourceVolumeMB\":\"360448\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"56\",\"ACUs\":\"0\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"32\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"0\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"0\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"0\",\"UncachedDiskIOPS\":\"0\",\"UncachedDiskBytesPerSecond\":\"0\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NV32as_v4\",\"CombinedTempDiskAndCachedIOPS\":\"0\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_NV32as_v4\",\"MaxResourceVolumeMB\":\"720896\",\"vCPUsPerCore\":\"2\",\"MemoryGB\":\"112\",\"ACUs\":\"0\",\"vCPUs\":\"32\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"4\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"251658240\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"251658240\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154619000000\",\"UncachedDiskIOPS\":\"24480\",\"UncachedDiskBytesPerSecond\":\"368640000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC4as_T4_v3\",\"CombinedTempDiskAndCachedIOPS\":\"16320\",\"MaxDataDiskCount\":\"8\",\"name\":\"Standard_NC4as_T4_v3\",\"MaxResourceVolumeMB\":\"180224\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"28\",\"ACUs\":\"0\",\"vCPUs\":\"4\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"8\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"251658240\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"251658240\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154619000000\",\"UncachedDiskIOPS\":\"24480\",\"UncachedDiskBytesPerSecond\":\"368640000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC8as_T4_v3\",\"CombinedTempDiskAndCachedIOPS\":\"16320\",\"MaxDataDiskCount\":\"16\",\"name\":\"Standard_NC8as_T4_v3\",\"MaxResourceVolumeMB\":\"360448\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"56\",\"ACUs\":\"0\",\"vCPUs\":\"8\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"16\",\"GPUs\":\"1\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"251658240\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"251658240\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154619000000\",\"UncachedDiskIOPS\":\"24480\",\"UncachedDiskBytesPerSecond\":\"368640000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC16as_T4_v3\",\"CombinedTempDiskAndCachedIOPS\":\"16320\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_NC16as_T4_v3\",\"MaxResourceVolumeMB\":\"360448\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"110\",\"ACUs\":\"0\",\"vCPUs\":\"16\"},{\"PremiumIO\":\"True\",\"vCPUsAvailable\":\"64\",\"GPUs\":\"4\",\"OSVhdSizeMB\":\"1047552\",\"CombinedTempDiskAndCachedReadBytesPerSecond\":\"251658240\",\"CombinedTempDiskAndCachedWriteBytesPerSecond\":\"251658240\",\"EncryptionAtHostSupported\":\"False\",\"HyperVGenerations\":\"V1,V2\",\"VMDeploymentTypes\":\"IaaS\",\"CachedDiskBytes\":\"154618822656\",\"UncachedDiskIOPS\":\"48000\",\"UncachedDiskBytesPerSecond\":\"737280000\",\"EphemeralOSDiskSupported\":\"False\",\"size\":\"NC64as_T4_v3\",\"CombinedTempDiskAndCachedIOPS\":\"16320\",\"MaxDataDiskCount\":\"32\",\"name\":\"Standard_NC64as_T4_v3\",\"MaxResourceVolumeMB\":\"2883584\",\"vCPUsPerCore\":\"1\",\"MemoryGB\":\"440\",\"ACUs\":\"0\",\"vCPUs\":\"64\"}]";
        List<Map> dataMaps = JSONArray.parseArray(jsonStr, Map.class);

        if (dataMaps != null && !dataMaps.isEmpty()) {
            List<Map<String, String>> mapList = dataMaps.stream().map(map -> (Map<String, String>) map).collect(Collectors.toList());
//            清空表
//            azureVmSizeMapper.truncate();
            //list按照一千数量间隔批量插入数据
            for (int i = 0; i < dataMaps.size(); i += 1000) {
                List<Map<String, String>> subList = mapList.subList(i, i + 1000 > mapList.size() ? mapList.size() : i + 1000);
                Boolean aBoolean = azureVmSizeMapper.batchInsert(subList);
                if (!aBoolean) {
                    new MpRuntimeException("批量插入数据失败");
                }
            }

        }
        return new ServiceResult<>(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> syncVmOsDisk() throws IOException {
        AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(BaseSceneModel.CloudType.PUBLIC));
        List<Map<String, String>> dataMaps = azureVirtualMachine.listsAllSkusWithCapabilities(ComputeResourceType.DISKS);
        if (dataMaps != null && !dataMaps.isEmpty()) {
//            清空表
            azureVmOsDiskMapper.truncate();
            //list按照一千数量间隔批量插入数据
            for (int i = 0; i < dataMaps.size(); i += 1000) {
                List<Map<String, String>> subList = dataMaps.subList(i, i + 1000 > dataMaps.size() ? dataMaps.size() : i + 1000);
                Boolean aBoolean = azureVmOsDiskMapper.batchInsert(subList);
                if (!aBoolean) {
                    new MpRuntimeException("批量插入数据失败");
                }
            }

        }
        return new ServiceResult<>(true);
    }

    /**
     * 节点扩容
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> createElementExpansionDisk(CsceneInstanceNodeExpansionDiskActionRequest request) {
        //获取场景实例操作对象
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(request.getId(), request.getContext(), true);
        String sceneId = instanceHandler.getSceneId();


        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        Integer cloudType = sceneHandler.getScene().getCloudType();
        JSONObject jsonObject = new JSONObject();
        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
        jsonObject.put("cloudType", cloudType);
        MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);
        if (cloudType == BaseSceneModel.CloudType.DEFAULT) {
            throw new MpRuntimeException("传统场景，不支持扩容");
        }

        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO element = cloudPlugin.getElement(request.getNodeId());
        String imageType = element.getImageType();
        if (!BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(imageType)) {
            throw new MpRuntimeException("非虚拟机节点，不支持扩容");
        }

        if (null != request.getVmSizeType()) {
            element.setVmSizeType(request.getVmSizeType());
        }
        element.setVmExpansionDiskType(request.getExpansionDiskType());
        element.getSrcElement().setExpansionDisk(request.getExpansionDisk());
        if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(element.getImageType()) &&
                BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == element.getVirtualType()) {
            Boolean expansion = element.getExpansion();
            SceneTaskPool.getInstance().executeTask(() -> {
                AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(cloudType));
                azureVirtualMachine.expansion(localAzurer, element, expansion);
            });
            Integer expansionDisk = element.getSrcElement().getExpansionDisk();
            if (!element.getExpansion() &&
                    expansionDisk != null &&
                    expansionDisk > 0 &&
                    StringUtils.isNotBlank(element.getVmExpansionDiskType())) {
                element.setExpansion(true);
            }
            CloudPluginSceneElementModel cloudPluginSceneElementModel = element.toModel();
            HumanCondition humanCondition = new HumanCondition()
                    .filterEqual(CloudPluginSceneElementModel::getScene, sceneHandler.getSceneId())
                    .filterEqual(CloudPluginSceneElementModel::getSceneElement, request.getNodeId());
            cloudPluginSceneElementMapper.filterUpdate(cloudPluginSceneElementModel, humanCondition);
            SceneElementModel sceneElementModel = element.getSrcElement().toModel();
            sceneElementMapper.update(sceneElementModel);
            return new ServiceResult<>(true);
        }
        return new ServiceResult<>(false);
    }

    /**
     * 节点探针安装
     *
     * @param id
     * @param nodeId
     * @param probeList
     * @return
     */
    public ServiceResult<Boolean> elementInstallProbes(String id, String nodeId, List<Probe> probeList) {

        modifyProbes(id, nodeId, probeList, SceneElementSituationProbeModel.ProbeStatus.RUNNING.getValue(), false, SceneElementSituationProbeModel.ProbeType.ADD);

        return new ServiceResult<>(true);
    }

    /**
     * 节点探针删除
     *
     * @param id
     * @param nodeId
     * @param probeList
     * @return
     */
    public ServiceResult<Boolean> elementDeleteProbes(String id, String nodeId, List<Probe> probeList) {

//          2. 删除
        modifyProbes(id, nodeId, probeList, SceneElementSituationProbeModel.ProbeStatus.NOT_INSTALL.getValue(), true, SceneElementSituationProbeModel.ProbeType.REMOVE);

        return new ServiceResult<>(true);
    }

    /**
     * 节点探针卸载
     *
     * @param id
     * @param nodeId
     * @param probeList
     * @return
     */
    public ServiceResult<Boolean> elementUninstallProbes(String id, String nodeId, List<Probe> probeList) {

        modifyProbes(id, nodeId, probeList, SceneElementSituationProbeModel.ProbeStatus.NOT_INSTALL.getValue(), false, SceneElementSituationProbeModel.ProbeType.REMOVE);
        return new ServiceResult<>(true);
    }

    /**
     * 节点探针重启
     *
     * @param id
     * @param nodeId
     * @param probeList
     * @return
     */
    public ServiceResult<Boolean> elementRestartProbes(String id, String nodeId, List<Probe> probeList) {
        modifyProbes(id, nodeId, probeList, SceneElementSituationProbeModel.ProbeStatus.RUNNING.getValue(), false, SceneElementSituationProbeModel.ProbeType.UPDATE);
        return new ServiceResult<>(true);
    }


    /**
     * 更新探针
     *
     * @param sceneHandler
     * @param elementId
     * @param probe
     * @param probeType
     * @param collectionPluginSceneElementBO
     */
    public void modifyProbe(SceneHandler sceneHandler, String elementId, Probe probe, SceneElementSituationProbeModel.ProbeType probeType, CollectionPluginSceneElementBO collectionPluginSceneElementBO) {
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);

        var collectionElement = pluginHandler.getElement(elementId);
        var srcElement = collectionElement.getSrcElement();
        String agentName = srcElement.getEid();
        String systemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());


        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer(sceneHandler.getScene().getCloudType());

        var controlConfigParam = new WazuhControl.ControlConfigParam();
        controlConfigParam.setProbeParams(new ArrayList<>());
        ConvertScriptParams convertScript = new ConvertScriptParams(probe, agentName, pluginHandler.getScene().getSrcScene().getCloudType());

        // 删除操作
        if (SceneElementSituationProbeModel.ProbeType.REMOVE == probeType || SceneElementSituationProbeModel.ProbeType.UPDATE == probeType) {
            var removeParams = convertScript.convertParams().get(0);
//            convertScript.convertParams()内拼接url错误 重新拼接
            removeParams.setScriptUrl(StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer, probe.getScriptUrl().replaceFirst("/api/mp/file/", "")));
            removeParams.setAction(WazuhControl.CONTROL_ACTION_REMOVE);
            controlConfigParam.getProbeParams().add(removeParams);
            List<Probe> probes = collectionPluginSceneElementBO.getProbes();
            probes = probes.stream()
                    .filter(p -> !p.getId().equals(probe.getId()))
                    .collect(Collectors.toList());
            collectionPluginSceneElementBO.setProbes(probes);
        }
        // 新增操作
        if (SceneElementSituationProbeModel.ProbeType.ADD == probeType || SceneElementSituationProbeModel.ProbeType.UPDATE == probeType) {
            var addParams = convertScript.convertParams().get(0);
            addParams.setScriptUrl(StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer, probe.getScriptUrl().replaceFirst("/api/mp/file/", "")));
            String groupId = TextUtils.uuid();
            addParams.setGroup(groupId);
            addParams.setAction(WazuhControl.CONTROL_ACTION_ADD);
            controlConfigParam.getProbeParams().add(addParams);
            List<Probe> probes = collectionPluginSceneElementBO.getProbes();
            probes.add(probe);
            collectionPluginSceneElementBO.setProbes(probes);
        }

        var osType = SystemTypeCommandUtils.getOsType(systemType);

        switch (sceneHandler.getScene().getCloudType()) {
            case BaseSceneModel.CloudType.DEFAULT:
                WazuhControlCommon.wazuhControl.controlConfigOld(agentName, osType, controlConfigParam);
                break;
            case BaseSceneModel.CloudType.PUBLIC:
                PublicWazuhControlCommon.wazuhControl.controlConfigOld(agentName, osType, controlConfigParam);
                break;
            default:
                WazuhControlCommon.wazuhControl.controlConfigOld(agentName, osType, controlConfigParam);
                break;
        }


    }

    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> addLoad(CsceneLoadDto csceneLoadDto) {
//        过滤出所有load节点 数据库过滤出要新增节点
        String sceneId = csceneLoadDto.getSceneId();
        String loadToolId = csceneLoadDto.getPluginSceneConfigElement().getLoadToolId();
        log.info("addLoad begin. sceneId:{} loadToolId:{}", sceneId, loadToolId);
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(sceneId, null, true);
        SceneHandler sceneHandler = new SceneHandler(instanceHandler.getSceneId(), null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        List<CloudPluginSceneElementBO> terminalExcludeNpcCloudElements = pluginHandler.getTerminalExcludeNpcCloudElements();
        if (terminalExcludeNpcCloudElements.size() == 0) {
            return new ServiceResult<String>("场景中未找到本地kali节点", BaseErrorCode.NO_KALI);
        }
        CloudPluginSceneElementBO loadToolElementBO = pluginHandler.getTerminalExcludeNpcCloudElements().get(0);
        CsceneInstanceModel csceneInstanceModel = getModelMapper().get(sceneId);
//        根据场景id获取对应模板
        HumanCondition selectHumanCondition = new HumanCondition()
                .filterEqual(CloudPluginSceneElementModel::getScene, csceneInstanceModel.getScene())
                .filterEqual(CloudPluginSceneElementModel::getLoadToolId, loadToolId);
        List<CloudPluginSceneElementModel> list = cloudPluginSceneElementMapper.list(selectHumanCondition);
//        上传载荷文件
        uploadLoadFile(loadToolId, instanceHandler);

        if (list.isEmpty()) {
//            增加载荷
//            确定位置
            Map<String, SceneAreaBO> areaMap = sceneHandler.getAreaMap();
            SceneAreaBO sceneAreaBO = areaMap.get("loadUUID");
            Position arePosition = JsonCommon.parse(sceneAreaBO.getPosition2(), Position.class);

            String position2 = getPosition2(csceneInstanceModel, arePosition);
            String outerPosition2 = getOuterPosition2(csceneInstanceModel, arePosition);
            String arePositionStr = JsonCommon.stringify(arePosition);
            sceneAreaBO.setPosition2(arePositionStr);
            //            实例场景保存
            SceneConfigElementModel sceneConfigElementModel = csceneLoadDto.getSceneConfigElement().toModel();
            CloudPluginSceneConfigElementModel cloudPluginSceneConfigElementModel = csceneLoadDto.getPluginSceneConfigElement().toModel();
            MpDevopsLoad mpDevopsLoad = loadMapper.getLoadById(cloudPluginSceneConfigElementModel.getLoadToolId());
            Map<String, String> nextLevelMapsByCodes = commonCodeService.getNextLevelMapsByCodes(CommonStatusCodeEnum.CODE.getCode());
            log.info("addLoad mpDevopsLoad:{}", JsonCommon.stringify(mpDevopsLoad));
            SceneElementModel sceneElementModel = new SceneElementModel();
            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(sceneConfigElementModel, sceneElementModel);
            sceneElementModel.setScene(csceneInstanceModel.getScene());
            sceneElementModel.setArea("loadUUID");
            sceneElementModel.setPosition2(position2);
            sceneElementModel.setOuterPosition2(outerPosition2);
            sceneElementModel.setName(mpDevopsLoad.getToolName());
            sceneElementModel.setActionType(mpDevopsLoad.getActionType());
            sceneElementModel.setActionTypeName(mpDevopsLoad.getActionTypeName());
            sceneElementModel.setControlType(mpDevopsLoad.getControlType());
            sceneElementModel.setControlTypeName(nextLevelMapsByCodes.get(mpDevopsLoad.getControlType()));
            sceneElementModel.setIcon2(mpDevopsLoad.getCover());
            sceneElementMapper.create(sceneElementModel);
            SceneElementPluginModel sceneElementPluginModel = new SceneElementPluginModel();
            sceneElementPluginModel.setScene(csceneInstanceModel.getScene());
            sceneElementPluginModel.setSceneElement(sceneElementModel.getId());
            sceneElementPluginModel.setPluginKey("cpcloud");
            sceneElementPluginMapper.create(sceneElementPluginModel);
            CloudPluginSceneElementModel cloudPluginSceneElementModel = new CloudPluginSceneElementModel();
            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(cloudPluginSceneConfigElementModel, cloudPluginSceneElementModel);
            cloudPluginSceneElementModel.setScene(csceneInstanceModel.getScene());
            cloudPluginSceneElementModel.setSceneElement(sceneElementModel.getNodeId());
            cloudPluginSceneElementModel.setRole(BaseSceneElementModel.Type.LOAD);
            cloudPluginSceneElementModel.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.LOAD);
            cloudPluginSceneElementModel.setLoadResourceInstanceId(loadToolElementBO.getSrcElement().getId());
            List<CloudPluginSceneElementPortBO> ports = pluginHandler.getPorts(loadToolElementBO.getSrcElement());
            List<CloudAccessMode> collect = ports.get(0).getAccessModes().stream().filter(cloudAccessMode -> AccessMode.Protocol.RDP.equals(cloudAccessMode.getProtocol())).collect(Collectors.toList());
            if ("shell".equals(sceneElementModel.getControlTypeName())) {
                collect = ports.get(0).getAccessModes().stream().filter(cloudAccessMode -> AccessMode.Protocol.SSH.equals(cloudAccessMode.getProtocol())).collect(Collectors.toList());
            }
            if (collect.size() > 0) {
                cloudPluginSceneElementModel.setLoadAccessModel(collect.get(0).getId());
            }
            cloudPluginSceneElementMapper.create(cloudPluginSceneElementModel);
            SceneAreaModel model = sceneAreaBO.toModel();
            sceneAreaMapper.update(model);
//            启动载荷节点
            log.info("addLoad.createElement");
            this.addLoad(sceneId, cloudPluginSceneElementModel.getSceneElement(), csceneLoadDto.getContext());

//            保存实例信息
            cn.cyberpeace.mp.base.util.basic.BeanUtils.copyProperties(sceneElementModel, sceneConfigElementModel);
            csceneService.saveConfigData(csceneLoadDto.getCsceneId(), sceneConfigElementModel, cloudPluginSceneElementModel, cloudPluginSceneConfigElementModel, arePositionStr);
        } else {
            MpDevopsLoad mpDevopsLoad = loadMapper.getLoadById(loadToolId);
            SceneElementModel sceneElementModel = new SceneElementModel();
            sceneElementModel.setDefaultCmd(csceneLoadDto.getSceneConfigElement().getDefaultCmd());
            HumanCondition humanCondition = new HumanCondition();
            humanCondition.filterEqual(SceneElementModel::getScene, csceneInstanceModel.getScene());
            humanCondition.filterEqual(SceneElementModel::getArea, "loadUUID");
            humanCondition.filterEqual(SceneElementModel::getName, mpDevopsLoad.getToolName());
            humanCondition.filterEqual(SceneElementModel::getActionType, mpDevopsLoad.getActionType());
            List<SceneElementModel> sceneElementModels = sceneElementMapper.list(humanCondition);
            sceneElementMapper.filterUpdate(sceneElementModel, humanCondition);
            this.reportLoadSuccess(sceneId, sceneElementModels.get(0).getNodeId(), csceneLoadDto.getContext());
        }
        log.info("addLoad end.");
        return new ServiceResult<>();
    }

    /**
     * 上传载荷文件
     *
     * @param loadToolId
     * @param instanceHandler
     */
    private void uploadLoadFile(String loadToolId, CsceneInstanceHandler instanceHandler) {
        Map loadMap = devopsLoadSourceMapper.getLoadById(loadToolId);
        log.info("loadById info：{}", loadMap.toString());
        String scriptFilePath = loadMap.getOrDefault("script_file_path", "").toString();
        String scriptFile = loadMap.getOrDefault("script_file", "").toString();
        log.info("需要添加文件的载荷信息：{}", scriptFilePath);
        String username = null;
        String password = null;
        String remoteHost = null;
        Integer port;
        String remoteDir = "/tmp/";
        String localFile = null;
        if (StringUtils.isNotBlank(scriptFilePath)) {
            SceneHandler sceneHandler = new SceneHandler(instanceHandler.getSceneId(), null, false, null);
            if (Boolean.valueOf(loadMap.getOrDefault("delivery", false).toString())) {
//                投递文件上传至云资源服务器
                IAzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(sceneHandler.getScene().getCloudType());
                MicrosoftAzureUserModel user = azureResourceManager.getUser();
                username = user.getWazuhManagerUser();
                password = user.getWazuhManagerPassword();
                remoteHost = user.getWazuhManagerIp();
                port = 22;
                localFile = scriptFilePath;
            } else {
                CloudScenePluginHandler handler = sceneHandler.getPluginHandler(PluginConstant.CPCLOUD);
                Map<String, CloudPluginSceneElementPortBO> elementPortMap = handler.getElementPortMap();
                Map<String, CloudPluginSceneElementPortBO> elementNameMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(elementPortMap)) {
                    for (CloudPluginSceneElementPortBO value : elementPortMap.values()) {
                        elementNameMap.put(value.getSrcPort().getElement().getName(), value);
                    }
                }

                CloudPluginSceneElementPortBO kali = elementNameMap.get("kali");
                Map<String, CloudAccessMode> cloudAccessModeMap = new HashMap<>();
                for (CloudAccessMode accessMode : kali.getAccessModes()) {
                    cloudAccessModeMap.put(accessMode.getProtocol(), accessMode);
                }
                CloudAccessMode ssh = cloudAccessModeMap.get("ssh");
                remoteHost = ssh.getAccessIp();
                port = ssh.getAccessPort();
                username = ssh.getUsername();
                password = ssh.getPassword();
                localFile = scriptFilePath;
            }
            log.info("上传载荷文件 主机ip:{} 端口:{} 用户名:{} 密码:{}", remoteHost, port, username, password);
            try {
                JSch jsch = new JSch();
                com.jcraft.jsch.Session session = jsch.getSession(username, remoteHost, port);
                session.setPassword(password);
                // 配置会话的其他选项,如主机密钥检查等
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                log.info("config info:{}", JSON.toJSONString(config));
                session.setConfig(config);
                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();
                ChannelSftp sftpChannel = (ChannelSftp) channel;
                // 切换到远程目录
                sftpChannel.cd(remoteDir);
                // 上传文件
                File file = new File(localFile);
                sftpChannel.put(new FileInputStream(file), scriptFile);
                session.disconnect();
                sftpChannel.exit();
                log.info("载荷文件上传成功{}", scriptFilePath);
            } catch (Exception e) {
                log.error("exception:{}", e);
                e.printStackTrace();
            }
        }
    }

    private ServiceResult<Boolean> addLoad(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        boolean result = instanceHandler.addLoad(nodeId);
        return new ServiceResult<>(result);
    }

    private void reportLoadSuccess(String id, String nodeId, CsceneContextBO context) {
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(id, context, true);
        instanceHandler.reportLoadSuccess(nodeId);
    }

    /**
     * 根据所有载荷获取新增载荷位置
     *
     * @param csceneInstanceModel 场景实例模型
     * @return 新增载荷的位置字符串
     */
    private String getPosition(CsceneInstanceModel csceneInstanceModel, Function<SceneElementModel, String> positionFunction, Position arePosition) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(SceneElementModel::getScene, csceneInstanceModel.getScene())
                .filterEqual(SceneElementModel::getArea, "loadUUID");
        List<SceneElementModel> sceneElementModelList = sceneElementMapper.list(humanCondition);

        List<Origin> originList = sceneElementModelList.stream()
                .map(positionFunction)
                .filter(StringUtils::isNotBlank)
                .map(position -> JsonCommon.parse(position, Origin.class))
                .collect(Collectors.toList());

        double maxX = originList.stream()
                .mapToDouble(Origin::getX)
                .max()
                .orElse(0);
        double maxY = originList.stream()
                .mapToDouble(Origin::getY)
                .max()
                .orElse(0);


        if (maxX != 0 && maxY != 0) {
            Origin origin = new Origin(maxX + 100, maxY);
//            图层处理
            double newWidth = origin.getX() + 100 - arePosition.getOrigin().getX();
            if (newWidth > arePosition.getWidth()) {
                arePosition.setWidth(newWidth);
            }
            return JsonCommon.stringify(origin);
        }
        return "";
    }

    /**
     * 根据所有载荷获取新增载荷位置
     *
     * @param csceneInstanceModel 场景实例模型
     * @return 新增载荷的位置字符串
     */
    private String getPosition2(CsceneInstanceModel csceneInstanceModel, Position arePosition) {
        log.info("getPosition2 begin. sceneId:{}", csceneInstanceModel.getScene());
        String position = getPosition(csceneInstanceModel, SceneElementModel::getPosition2, arePosition);
        log.info("getPosition2 end. sceneId:{},position2:{}", csceneInstanceModel.getScene(), position);
        return position;
    }

    /**
     * 根据所有载荷获取新增载荷位置（外部）
     *
     * @param csceneInstanceModel 场景实例模型
     * @return 新增载荷的位置字符串
     */
    private String getOuterPosition2(CsceneInstanceModel csceneInstanceModel, Position arePosition) {
        log.info("getOuterPosition2 begin. sceneId:{}", csceneInstanceModel.getScene());
        String outerPosition2 = getPosition(csceneInstanceModel, sceneElementModel -> {
            String position = sceneElementModel.getOuterPosition2();
            if (StringUtils.isBlank(position)) {
                return position;
            }
            Map<String, String> parse = JsonCommon.parse(position, new TypeReference<Map<String, String>>() {
            });
            return parse.get("");
        }, arePosition);
        if (StringUtils.isNotBlank(outerPosition2)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("", outerPosition2);
            outerPosition2 = JsonCommon.stringify(map);
        }
        log.info("getOuterPosition2 end. sceneId:{},OuterPosition2:{}", csceneInstanceModel.getScene(), outerPosition2);
        return outerPosition2;
    }

    public ServiceResult<ElementCommandExecResult> execElementV2(String id, String nodeId, String command,
                                                                 String user, CsceneContextBO context,
                                                                 String loadNodeId, String resourceId) {
        String sceneId = getSceneId(id);
//        此处保存态势动效任务
        String destinationIp = extractIP(command);
        LocalLoadExecutionRecordModel localLoadExecutionRecordModel = new LocalLoadExecutionRecordModel();

        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        Map<String, String> ipNodeMap = sceneHandler.getElementPortMap().values().stream()
                .collect(Collectors.toMap(SceneElementPortBO::getIp, port -> port.getElement().getId(), (existingValue, newValue) -> newValue));
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        ipNodeMap.putAll(pluginHandler.getElementMap().values().stream()
                .collect(Collectors.toMap(CloudPluginSceneElementBO::getPublicIpAddress, element -> element.getSrcElement().getId(), (existingValue, newValue) -> newValue)));
        if (StringUtils.isNotBlank(destinationIp) && ipNodeMap.containsKey(destinationIp)) {
            SceneElementBO loadElement = sceneHandler.getElement(loadNodeId);
            String loadElementName = loadElement.getName();

            var element = sceneHandler.getElement(nodeId);
            String sourceIp = element.getPorts().get(0).getIp();
            localLoadExecutionRecordModel.setScene(id);
            localLoadExecutionRecordModel.setResourceId(resourceId);
            localLoadExecutionRecordModel.setLoadNode(loadNodeId);
            localLoadExecutionRecordModel.setLoadNodeName(loadElementName);
            localLoadExecutionRecordModel.setSourceIp(sourceIp);
            localLoadExecutionRecordModel.setDestinationNode(ipNodeMap.get(destinationIp));
            localLoadExecutionRecordModel.setDestinationIp(destinationIp);
            localLoadExecutionRecordModel.setStatus(LocalLoadExecutionRecordModel.Status.NOT_EXECUTED.getCode());
            localLoadExecutionRecordModel.setDemoStatus(LocalLoadExecutionRecordModel.Status.NOT_EXECUTED.getCode());
            localLoadExecutionRecordMapper.create(localLoadExecutionRecordModel);
        }

        var result = new ElementCommandExecResult();
        result.setStatus(true);
        result.setOutput(localLoadExecutionRecordModel.getId());
        return new ServiceResult<>(result);
    }

    public static String extractIP(String str) {
        String regex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public ServiceResult<Integer> updateElementDefaultCommand(String loadNodeId, String scene, String command) {
        SceneElementModel sceneElementModel = new SceneElementModel();
        sceneElementModel.setDefaultCmd(command);
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(SceneElementModel::getNodeId, loadNodeId);
        humanCondition.filterEqual(SceneElementModel::getScene, scene);
        int i = sceneElementMapper.filterUpdate(sceneElementModel, humanCondition);
        return new ServiceResult<>(i);
    }

    /**
     * 投递载荷文件
     *
     * @param deliveryLoadFileRequest
     * @return
     */
    public ServiceResult<Map> deliveryLoadFile(DeliveryLoadFileRequest deliveryLoadFileRequest) {
//        获取场景内载荷信息、目标节点信息
        String sceneId = deliveryLoadFileRequest.getSceneId();
        CsceneInstanceHandler instanceHandler = new CsceneInstanceHandler(sceneId, null, true);
        SceneHandler sceneHandler = new SceneHandler(instanceHandler.getSceneId(), null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        Map<String, CloudPluginSceneElementBO> elementMap = pluginHandler.getElementMap();
        CloudPluginSceneElementBO loadElement = elementMap.get(deliveryLoadFileRequest.getLoadNodeId());
        CloudPluginSceneElementBO targetElement = elementMap.get(deliveryLoadFileRequest.getTargetNodeId());
//        文件位置
        Map loadMap = devopsLoadSourceMapper.getLoadById(loadElement.getLoadToolId());
        String scriptFile = loadMap.getOrDefault("script_file", "").toString();

//        生成资源组名称
        String groupName = pluginHandler.getSourceSceneId();
        String groupNamePrefix = situationAwareConfig.getGroupNamePrefix();
        groupName = StringUtils.isNotBlank(situationAwareConfig.getGroupNamePrefix()) ? groupNamePrefix + "-" + groupName : groupName;
        // 生成虚拟机名称
        String vmName = HandlerUtils.genResourceName(targetElement.getSrcElement().getEid());
//        根据目标节点虚拟机类型不同使用不同的方法投递载荷文件
        String vmType = targetElement.getVmType().toLowerCase();
        String filePath = "";

        IAzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(sceneHandler.getScene().getCloudType());
        List<String> commands = new ArrayList<>();
        String wazuhManagerIp = azureResourceManager.getUser().getWazuhManagerIp();
        if (BaseSceneElementModel.SystemType.WINDOWS.equals(vmType)) {
            filePath = "C:\\" + scriptFile;
            String command = String.format(WINDOWS_DELIVERY_LOAD_FILE_COMMAND, filePath, wazuhManagerIp, scriptFile);
            commands.add(command);
        } else {
            filePath = "/tmp/" + scriptFile;
            String command = String.format(LINUX_DELIVERY_LOAD_FILE_COMMAND, filePath, wazuhManagerIp, scriptFile);
            commands.add(command);
        }
        if (BaseSceneModel.CloudType.PUBLIC == sceneHandler.getScene().getCloudType()) {
            if (BaseSceneElementModel.SystemType.WINDOWS.equals(vmType)) {
                azureResourceManager
                        .getManager()
                        .virtualMachines()
                        .runPowerShellScript(groupName, vmName, commands, new ArrayList<>());
            } else {
                azureResourceManager
                        .getManager()
                        .virtualMachines()
                        .runShellScript(groupName, vmName, commands, new ArrayList<>());
            }
        } else {
            try {
                String azureManagerUrl = situationAwareConfig.getMixAzureManagerUrl();
                if (sceneHandler.getScene().getCloudType() == BaseSceneModel.CloudType.PRIVATE) {
                    azureManagerUrl = situationAwareConfig.getPrivateAzureManagerUrl();
                }
                String url = azureManagerUrl + "/cloud/run_shell_script";
                Map<String, Object> formMap = new HashMap();
                formMap.put("azureUser", azureResourceManager.getUser());
                formMap.put("groupName", groupName);
                formMap.put("vmType", vmType);
                formMap.put("vmName", vmName);
                formMap.put("command", commands);
                //请求头设置
                HashMap<String, String> headersMap = new HashMap<>();
                String body = JsonUtils.stringify(formMap);
                HttpUtils.sendHttpPost(url, body, headersMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        阶段载荷上保存投递的节点id
        loadElement.setDeliveryNodeId(deliveryLoadFileRequest.getTargetNodeId());
        loadElement.setDeliveryFilePath(filePath);
        cloudPluginSceneElementMapper.update(loadElement.toModel());

//        返回远程信息
        List<CloudPluginSceneElementPortBO> ports = pluginHandler.getPorts(targetElement.getSrcElement());
        CloudAccessMode collect = ports.get(0).getAccessModes().get(0);

        HashMap<String, String> data = new HashMap<>();
        data.put("access_mode_id", collect.getId());
        data.put("node_id", deliveryLoadFileRequest.getTargetNodeId());
        return new ServiceResult<>(data);
    }

    public void updateWazuhManagerIp(MicrosoftAzureUserModel requestData) {
        Integer cloudType = requestData.getCloudType();
        String wazuhManagerIp = requestData.getWazuhManagerIp();

//        TODO 改bug暂时停止开发0422
//        updateWazuhManagerIpByCloudType(cloudType, wazuhManagerIp);


        /*IAzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(requestData.getCloudType());
        var humanCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene, SceneModel.class,
                        SceneModel::getId)
                .currentLink(SceneModel.class)
                .selectAs("pk", "pk")
                .selectAs(SceneModel::getId, CsceneInstanceStat::getId);
        humanCondition
                .filterEqual(SceneModel::getCloudType, requestData.getCloudType())
                .filterOrStart()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                .andEqual(SceneModel::getEventStatus, EventStatus.OVER)
                .orAndEnd()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.UNPAUSE)
                .andEqual(SceneModel::getEventStatus, EventStatus.OVER)
                .orAndEnd()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.RESUME)
                .andEqual(SceneModel::getEventStatus, EventStatus.OVER)
                .orAndEnd()
                .orAndStart()
                .andIn(SceneModel::getEvent, new Integer[]{
                        SceneModel.Event.START, SceneModel.Event.STOP, SceneModel.Event.RESTART,
                        SceneModel.Event.SNAPSHOT, SceneModel.Event.MODIFY
                })
                .andNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                .orAndEnd()
                .filterOrEnd();

        ArrayList<String> linuxCommands = new ArrayList<>();
        linuxCommands.add(String.format("export SIC_WAZUH_MANAGER_IP=%s", wazuhManagerIp));
        linuxCommands.add("sh /opt/wazuha/config.sh");
        linuxCommands.add("sh /opt/wazuha/start.sh");

        List<CsceneInstanceStat> list = instanceMapper.list(humanCondition, CsceneInstanceStat.class);
        for (CsceneInstanceStat csceneInstanceStat : list) {
            SceneHandler sceneHandler = new SceneHandler(csceneInstanceStat.getId(), null, false, null);
            sceneHandler.initializeProbeInstallationScripts();

            CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            String groupName = cloudPlugin.getSourceSceneId();
            String groupNamePrefix = situationAwareConfig.getGroupNamePrefix();
            groupName = StringUtils.isNotBlank(situationAwareConfig.getGroupNamePrefix()) ? groupNamePrefix + "-" + groupName : groupName;
            CollectionScenePluginHandler collectPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            for (CloudPluginSceneElementBO cloudPluginSceneElementBO : cloudPlugin.filterVirtualMachineElements()) {
                CollectionPluginSceneElementBO collectionElement = collectPlugin.getElementMap().get(cloudPluginSceneElementBO.getSrcElement().getId());
                if (collectionElement.getProbes() != null && !collectionElement.getProbes().isEmpty()) {
                    String vmName = HandlerUtils.genResourceName(cloudPluginSceneElementBO.getSrcElement().getEid());
                    if (BaseSceneModel.CloudType.PUBLIC == requestData.getCloudType()) {
                        if (BaseSceneElementModel.SystemType.WINDOWS.equals(cloudPluginSceneElementBO.getVmType().toLowerCase())) {
                            List<String> initCommands = cloudPluginSceneElementBO.getSrcElement().getInitCommands();
                            initCommands.add(0,"rd/s/q C:\\wazuha");
                            initCommands.add(0,"call C:\\wazuha\\uninstall.bat");
//                            先卸载删除目录再执行
                            *//*azureResourceManager
                                    .getManager()
                                    .virtualMachines()
                                    .runPowerShellScript(groupName, vmName, initCommands, new ArrayList<>());*//*

                            log.info("updateWazuhManagerIp initCommands : {}",JsonUtils.stringify(initCommands));
                            System.out.println(213);
                        } else {
                            azureResourceManager
                                    .getManager()
                                    .virtualMachines()
                                    .runShellScript(groupName, vmName, linuxCommands, new ArrayList<>());
                            log.info("updateWazuhManagerIp linuxCommands : {}",JsonUtils.stringify(linuxCommands));
                        }
                    }
                }
            }
        }*/

    }

    /**
     * 更新Wazuh管理器IP配置
     * 根据云类型替换相应配置中的IP地址
     *
     * @param cloudType 云类型，决定使用哪套配置
     * @param wazuhManagerIp 新的Wazuh管理器IP地址
     */
    private void updateWazuhManagerIpByCloudType(Integer cloudType, String wazuhManagerIp) {
        // 编译IP地址匹配模式
        Pattern pattern = Pattern.compile(IP_PATTERN);
        // 根据云类型执行相应的配置更新
        switch (cloudType) {
            case BaseSceneModel.CloudType.PUBLIC:
                // 创建匹配器并替换公共Wazuh API服务器地址中的IP
                Matcher matcherPublicWazuhApiServer = pattern.matcher(CollectionSingleton.collectionAppConfig.getPublicWazuhApiServer());
                String newPublicWazuhApiServer = matcherPublicWazuhApiServer.replaceAll(wazuhManagerIp);
                // 替换公共文件服务器地址中的IP
                Matcher matcherPublicFileServe = pattern.matcher(SceneSingleton.sceneAppConfig.getMetadataPublicFileServer());
                String newMetadataPublicFileServer = matcherPublicFileServe.replaceAll(wazuhManagerIp);
                // 替换公共Wazuh基础URL中的IP
                Matcher matcherPublicWazuhBaseUrl = pattern.matcher(situationAwareConfig.getPublicWazuhBaseUrl());
                String newPublicWazuhBaseUrl = matcherPublicWazuhBaseUrl.replaceAll(wazuhManagerIp);

                // 更新配置信息
                CollectionSingleton.collectionAppConfig.setWazuhPublicServerHost(wazuhManagerIp);
                CollectionSingleton.collectionAppConfig.setPublicWazuhApiServer(newPublicWazuhApiServer);
                SceneSingleton.sceneAppConfig.setMetadataPublicFileServer(newMetadataPublicFileServer);
                // 保留端口信息，仅替换IP地址
                situationAwareConfig.setPublicWazuhEsNodes(wazuhManagerIp + situationAwareConfig.getPublicWazuhEsNodes().substring(situationAwareConfig.getPublicWazuhEsNodes().indexOf(":")));
                situationAwareConfig.setPublicWazuhBaseUrl(newPublicWazuhBaseUrl);
                break;
            case BaseSceneModel.CloudType.PRIVATE:
                // 对私有云环境执行类似的替换操作
                Matcher privateMatcher = pattern.matcher(SceneSingleton.sceneAppConfig.getMetadataPrivateFileServer());
                String newMetadataPrivateFileServer = privateMatcher.replaceAll(wazuhManagerIp);
                Matcher matcherPrivateWazuhBaseUrl = pattern.matcher(situationAwareConfig.getPrivateWazuhBaseUrl());
                String newPrivateWazuhBaseUrl = matcherPrivateWazuhBaseUrl.replaceAll(wazuhManagerIp);

                CollectionSingleton.collectionAppConfig.setWazuhPrivateServerHost(wazuhManagerIp);
                SceneSingleton.sceneAppConfig.setMetadataPrivateFileServer(newMetadataPrivateFileServer);
                situationAwareConfig.setPrivateWazuhEsNodes(wazuhManagerIp + situationAwareConfig.getPrivateWazuhEsNodes().substring(situationAwareConfig.getPrivateWazuhEsNodes().indexOf(":")));
                situationAwareConfig.setPrivateWazuhBaseUrl(newPrivateWazuhBaseUrl);
                break;
            case BaseSceneModel.CloudType.MIX:
                // 对混合云环境执行类似的替换操作
                Matcher mixMatcher = pattern.matcher(SceneSingleton.sceneAppConfig.getMetadataMixFileServer());
                String newMetadataMixFileServer = mixMatcher.replaceAll(wazuhManagerIp);
                Matcher matcherMixWazuhBaseUrl = pattern.matcher(situationAwareConfig.getMixWazuhBaseUrl());
                String newMixWazuhBaseUrl = matcherMixWazuhBaseUrl.replaceAll(wazuhManagerIp);

                CollectionSingleton.collectionAppConfig.setWazuhMixServerHost(wazuhManagerIp);
                SceneSingleton.sceneAppConfig.setMetadataMixFileServer(newMetadataMixFileServer);
                situationAwareConfig.setMixWazuhEsNodes(wazuhManagerIp + situationAwareConfig.getMixWazuhEsNodes().substring(situationAwareConfig.getMixWazuhEsNodes().indexOf(":")));
                situationAwareConfig.setMixWazuhBaseUrl(newMixWazuhBaseUrl);
                break;
        }
    }

    /**
     * 初始化AzureWazuh管理器的IP配置
     * 此方法从MicrosoftAzure登录映射中获取用户列表，并为每个用户更新Wazuh管理器的IP配置
     * 根据云类型更新对应的Wazuh管理器IP
     */
    public void initializeAzureWazuhManagerIpConfigs() {
        HumanCondition humanCondition = new HumanCondition();
        List<MicrosoftAzureUserModel> list = microsoftAzureLoginMapper.list(humanCondition);
        for (MicrosoftAzureUserModel microsoftAzureUserModel : list) {
//            TODO 改bug暂时停止开发
//            updateWazuhManagerIpByCloudType(microsoftAzureUserModel.getCloudType(), microsoftAzureUserModel.getWazuhManagerIp());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    protected static class Origin {
        private double x;
        private double y;
    }

    @Getter
    @Setter
    protected static class Position {
        private Origin origin;
        private double height;
        private double width;
    }
}
