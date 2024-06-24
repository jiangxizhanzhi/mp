package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.ExcelHeader;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.*;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.ImportFileBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.model.ElementFileCountModel;
import cn.cyberpeace.mp.sceneplugincollection.model.ElementFileListModel;
import cn.cyberpeace.mp.sceneplugincollection.model.ElementLogListModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.ElementFileCountMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.ElementFileListMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.ElementLogListMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.repository.WazuhProbeDataMapper;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 场景节点监控Service
 * @Author zhouxinghuai
 * @Date：2021/11/23 14:45
 */
@Slf4j
@Service
public class SceneElementMonitorService {

    private static final String CPCLOUD_PLUGIN = "cpcloud";

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private ElementFileListMapper elementFileListMapper;

    @Autowired
    private ElementFileCountMapper elementFileCountMapper;

    @Autowired
    private ElementLogListMapper elementLogListMapper;

    @Autowired
    private WazuhProbeDataMapper wazuhProbeDataMapper;

    private static final String FILE_LIST = "file_list";

    private static final String FILE_COUNT = "file_count";

    private static final String REGISTRY_LIST = "registry_list";

    private static final String REGISTRY_COUNT = "registry_count";

    private static final String LOG = "log";

    private static final String PROBE = "probe";

    private static final String ALARM = "alarm";


    private Integer getWazuhVulnerabilityScanInterval() {
        return CollectionSingleton.collectionAppConfig.getWazuhVulnerabilityScanInterval();
    }

    /**
     * CsceneInstanceBO转为ElementMonitorInfo
     *
     * @return
     */
    public ElementMonitorBasicInfo getElementMonitorInfo(BaseSceneElementRequest baseRequest, boolean fetchRealTimeHostState) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        String sceneId = baseRequest.getSceneId();
        String elementId = baseRequest.getElementIds().get(0);

        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        ElementMonitorBasicInfo elementMonitorBasicInfo = new ElementMonitorBasicInfo();
        elementMonitorBasicInfo.setSceneId(sceneId);
        //需要先获取plugin中imageType参数
        processPluginElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);
        processTopologyElement(elementMonitorBasicInfo, csceneInstanceBO, elementId, fetchRealTimeHostState);

        // todo 节点实时信息
        ElementRTInfo rtInfo = processRTInfo(baseRequest);
        elementMonitorBasicInfo.setRtInfo(rtInfo);
        return elementMonitorBasicInfo;
    }


    public ListObjServiceResult<ConnectionState> pageNetworkConnections(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);

        if (Objects.isNull(pageRequest.getFetchIfSceneInstanceShutdown()) || !pageRequest.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId())) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setPage(pageRequest.getPage());
        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollUtil.toList(pageRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(pageRequest.getElementIds());
        listPageInfoQueryParams.setLevels(pageRequest.getLevels());
        ListObjServiceResult<WazuhNetworkConnectionModel> netConns = scenePluginCollectionSceneService.listNetworkConnections(listPageInfoQueryParams).asResult();

        List<ConnectionState> connectionStates = new ArrayList<>();
        if (Objects.nonNull(netConns.getSource()) && CollUtil.isNotEmpty(netConns.getSource().getList())) {
            List<WazuhNetworkConnectionModel> list = netConns.getSource().getList();
            connectionStates = list.stream().map(model -> convertWazuhNetConn2ConnState(model)).collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(connectionStates, netConns.getSource().getTotal()).asResult()).asResult();
    }

    /**
     * 查询操作系统信息
     *
     * @param baseRequest
     * @return
     */
    public OS getOSInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(baseRequest.getSceneId(), baseRequest.getElementIds());
        if (CollectionUtil.isEmpty(agentNames)) {
            return null;
        }
        var sceneInstanceId = csceneCommonService.translateSceneId(baseRequest.getSceneId());
        SceneHandler sceneHandler = new SceneHandler(sceneInstanceId, null, null);
        String agentName = agentNames.get(0);
        List<OS> osList = sysCollectorService.getOS(agentName, sceneHandler.getScene().getCloudType());
        if (CollectionUtil.isEmpty(osList)) {
            return null;
        }

        return osList.get(0);
    }


    public int sumEnvVarChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumEnvVarChangedCount(listPageInfoQueryParams).getSource();
        return sum;
    }

    public int sumProcessesChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumProcessesChangedCount(listPageInfoQueryParams).getSource();
        return sum;
    }


    public int sumSysUserChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumSysUserChangedCount(listPageInfoQueryParams).getSource();
        return sum;
    }


    /**
     * 获取节点实时数据
     *
     * @param baseRequest
     * @return
     */
    public ElementRTInfo getElementRTInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        ElementRTInfo rtInfo = processRTInfo(baseRequest);

        return rtInfo;
    }


    public void processTopologyElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId, boolean fetchRealTimeHostState) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
            return;
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();
        Optional<SceneElementBO> tpoEleOptional = topologyElements.stream().filter(e -> Objects.equals(elementId, e.getId())).findFirst();

        if (tpoEleOptional.isPresent()) {
            SceneElementBO tpoEleBO = tpoEleOptional.get();
            sceneElementBO2ElementMonitorBasicInfo(basicInfo, tpoEleBO, csceneInstanceBO, fetchRealTimeHostState);
        }
    }

    public void processPluginElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return;
        }

        var scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);
        var pluginElements = scenePluginDataBO.getElements();
        var pluginEleOptional = pluginElements.entrySet().stream().filter(entry -> Objects.equals(entry.getKey(), elementId)).map(Map.Entry::getValue).findFirst();

        if (pluginEleOptional.isPresent()) {
            CloudPluginSceneElementBO cloudPluginSceneElementBO = JsonCommon.snakeMapToBean(pluginEleOptional.get(), CloudPluginSceneElementBO.class);
            cpSceneElementBO2ElementMonitorBasicInfo(basicInfo, cloudPluginSceneElementBO);
        }
    }


    public void sceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, SceneElementBO sceneElementBO, CsceneInstanceBO csceneInstanceBO, boolean fetchRealTimeHostState) {
        log.info("fetchHostState:{}", fetchRealTimeHostState);
        // 获取hostState比较耗时，这里加个开关

        if (fetchRealTimeHostState) {
            HostState resource = basicInfo.getHostState();
            if (Objects.isNull(resource)) {
                resource = new HostState();
            }

            BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
            request.setSceneId(basicInfo.getSceneId());
            request.setElementIds(Arrays.asList(sceneElementBO.getId()));
            request.setPage(1);
            request.setPageSize(1);
            List<HostState> hostStates = hostStateService.listHostStates(request);
            if (CollectionUtil.isNotEmpty(hostStates)) {
                HostState hostState = hostStates.get(0);
                resource.setAgentName(hostState.getAgentName());
                resource.setCpuCount(hostState.getCpuCount());
                resource.setMemoryTotal(hostState.getMemoryTotal());
                resource.setDiskSpace(hostState.getDiskSpace());
                //resource.setDiskUsed(hostState.getDiskUsed());
                resource.setDiskUnused(hostState.getDiskUnused());

                resource.setCpuPercent(Objects.nonNull(hostState.getCpuPercent()) ? NumberUtil.round(hostState.getCpuPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryPercent(Objects.nonNull(hostState.getMemoryPercent()) ? NumberUtil.round(hostState.getMemoryPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryFree(hostState.getMemoryFree());
                resource.setDiskPercent(Objects.nonNull(hostState.getDiskPercent()) ? NumberUtil.round(hostState.getDiskPercent(), 2).doubleValue() : 0.0);

                resource.setDownloadSpeed(hostState.getDownloadSpeed());
                resource.setUploadSpeed(hostState.getUploadSpeed());
                resource.setDiskReadBytes(hostState.getDiskReadBytes());
                resource.setMaxNetSpeed(hostState.getMaxNetSpeed());
                resource.setDiskWriteBytes(hostState.getDiskWriteBytes());
                resource.setDiskIoPercent(hostState.getDiskIoPercent());
                resource.setDiskIoPercent(hostState.getDiskIoPercent());
                resource.setDiskIoPercent(hostState.getDiskIoPercent());
            } else {
                // 如果从探针中查不到， 从拓扑图中获取数据
                resource.setCpuCount(sceneElementBO.getCpu());
                resource.setMemoryTotal(sceneElementBO.getRam().longValue());
                resource.setDiskSpace(sceneElementBO.getDisk().longValue());
            }
            basicInfo.setHostState(resource);
        }

        // ID
        basicInfo.setId(sceneElementBO.getId());
        basicInfo.setEid(sceneElementBO.getEid());
        // 实例名称
        basicInfo.setName(sceneElementBO.getName());
        // 图标
        basicInfo.setIcon2(sceneElementBO.getIcon2());

        basicInfo.setEvent(sceneElementBO.getEvent());
        basicInfo.setEventStatus(sceneElementBO.getEventStatus());
        basicInfo.setType(sceneElementBO.getType());

        // 操作系统
        // 参考SystemTypeUtils
        if (StringUtils.isNotBlank(sceneElementBO.getSystemType())) {
            basicInfo.setSystemType(sceneElementBO.getSystemType());
            var item = SystemTypeUtils.getSystemTypeItem(sceneElementBO.getSystemType());
            basicInfo.setSystemTypeName(item.getName());
        }

        // ip, 用户名，登录方式
        var ports = sceneElementBO.getPorts();
        List<ElementPort> ePorts = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ports)) {
            ePorts = ports.stream().map(p -> {
                ElementPort port = new ElementPort();
                port.setIp(p.getIp());
                port.setName(p.getName());
                port.setId(p.getId());
                port.setAccessModes(p.getAccessModes());
                return port;
            }).collect(Collectors.toList());
        }
        basicInfo.setPorts(ePorts);

        formatHostStateBySceneElementBo(basicInfo, sceneElementBO);
    }

    /**
     * 获取的主机信息，cpu,memory,disk直接使用靶标配置的值，从es中获取不一定都采得到/准确
     *
     * @param elementMonitorBasicInfo
     * @param sceneElementBO
     * @author
     * @date 17:09 2023/10/17
     **/
    private void formatHostStateBySceneElementBo(ElementMonitorBasicInfo elementMonitorBasicInfo, SceneElementBO sceneElementBO) {
        if (Objects.isNull(elementMonitorBasicInfo.getHostState())) {
            elementMonitorBasicInfo.setHostState(new HostState());
        }
        if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(elementMonitorBasicInfo.getImageType())) {
            //容器默认 1CPU 512MB内存 10GB硬盘
            elementMonitorBasicInfo.getHostState().setCpuCount(1);
            if (sceneElementBO.getRam() > 0) {
                elementMonitorBasicInfo.getHostState().setMemoryTotal(sceneElementBO.getRam().longValue());
            } else {
                elementMonitorBasicInfo.getHostState().setMemoryTotal(512L);
            }

            if (sceneElementBO.getDisk() > 0) {
                elementMonitorBasicInfo.getHostState().setDiskSpace(sceneElementBO.getDisk().longValue());
            } else {
                elementMonitorBasicInfo.getHostState().setDiskSpace(10L);
            }
        } else {
            elementMonitorBasicInfo.getHostState().setCpuCount(sceneElementBO.getCpu());
            elementMonitorBasicInfo.getHostState().setMemoryTotal(sceneElementBO.getRam().longValue());
            elementMonitorBasicInfo.getHostState().setDiskSpace(sceneElementBO.getDisk().longValue());
        }
    }

    public void cpSceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, CloudPluginSceneElementBO cpSceneElementBO) {
        // 镜像名称
        String imageName = null;
        if (StringUtils.contains(cpSceneElementBO.getImage(), cpSceneElementBO.getImageUuid())) {
            imageName = cpSceneElementBO.getImage().replaceFirst(cpSceneElementBO.getImageUuid(), "");
        } else {
            imageName = cpSceneElementBO.getImage();
        }
        basicInfo.setImageName(imageName);
        basicInfo.setImageType(cpSceneElementBO.getImageType());
        basicInfo.setRole(cpSceneElementBO.getRole());
        basicInfo.setIsBuildIn(cpSceneElementBO.getIsBuildIn());

        // 物理节点
        basicInfo.setHost(cpSceneElementBO.getHost());

        // 实例类型
        basicInfo.setVirtualType(cpSceneElementBO.getVirtualType());
        basicInfo.setExternal(cpSceneElementBO.getExternal());

        // 设备类型
        basicInfo.setCategory(cpSceneElementBO.getCategory());
    }

    public ListObjServiceResult<CollectionHostEventBO> getModifyData(ElementMonitorRequest pageRequest, List<String> ruleGroups,
                                                                     String path) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<String> elementIds = pageRequest.getElementIds();
        // 不传节点id默认查询全部节点
        if (pageRequest.getElementIds() == null || pageRequest.getElementIds().isEmpty()) {
            pageRequest.setElementIds(sceneHandler.getElementMap().values().stream().map(e -> e.getId()).collect(Collectors.toList()));
        }
        processRequest(pageRequest, sceneHandler);
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(pageRequest.getElementIds());
        hostEventQueryParams.setStartTime(pageRequest.getStartTime());
        hostEventQueryParams.setEndTime(pageRequest.getEndTime());
        hostEventQueryParams.setLevels(pageRequest.getLevels());
        hostEventQueryParams.setRuleGroups(ruleGroups);
        hostEventQueryParams.setPath(path);
        hostEventQueryParams.setPage(pageRequest.getPage());
        hostEventQueryParams.setPageSize(pageRequest.getPageSize());
        ListObjServiceResult<CollectionHostEventBO> hostEventsBos = csceneInstanceService.listHostEvents(pageRequest.getSceneId(), hostEventQueryParams);
        mergeModifyData(hostEventsBos, hostEventQueryParams, pageRequest, sceneHandler);
        mergeFileData(hostEventsBos, hostEventQueryParams, pageRequest, elementIds);

        return hostEventsBos;

    }

    /**
     * 合并已有采集变动数据
     *
     * @param hostEventsBos
     * @param hostEventQueryParams
     * @param pageRequest
     * @param sceneHandler
     */
    private void mergeModifyData(ListObjServiceResult<CollectionHostEventBO> hostEventsBos,
                                 HostEventQueryParams hostEventQueryParams, ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        // 无分页标识忽略
        if (hostEventQueryParams.getPage() == null) {
            return;
        }

        List<CollectionHostEventBO> bos = hostEventsBos.getSource().getObjs();
        List<String> ruleGroups = hostEventQueryParams.getRuleGroups();
        String sicLaunchId = null;
        if (ruleGroups == CsceneBizConstant.FILE_MODIFY_GROUP) {
            sicLaunchId = SicLaunchConfig.LAUNCH_ID_FILE;
        } else if (ruleGroups == CsceneBizConstant.REGISTRY_MODIFY_GROUP) {
            sicLaunchId = SicLaunchConfig.LAUNCH_ID_REGISTRY;
        } else {
            return;
        }

        WazuhEventRequest request = scenePluginCollectionSceneService.genWazuhEventRequest(hostEventQueryParams,
                sceneHandler.getScene().getCreateTime(), getAgentNames(pageRequest, sceneHandler));
        request.setSicLaunchId(sicLaunchId);
        request.setRuleGroups(null);

        // path -> null
        if (StringUtils.isNotBlank(hostEventQueryParams.getPath())) {
            request.setPath(null);
        }

        Map<String, SceneElementBO> eidMap = sceneHandler.getElementMap().values().stream()
                .collect(Collectors.toMap(SceneElementBO::getEid, Function.identity(), (v1, v2) -> v2));
        ListObjResult<FileEventModel> fileEventModels = dataCollectionService.queryFileOrRegisterEvents(request);
        if (CollectionUtils.isNotEmpty(fileEventModels.getObjs())) {
            List<FileEventModel> list = new ArrayList<>();
            Set<String> exist = new HashSet<>();
            for (var x : fileEventModels.getObjs()) {
                if (!exist.contains(x.getAgentId() + x.getFilePath())) {
                    if (StringUtils.isBlank(hostEventQueryParams.getPath())) {
                        list.add(x);
                        exist.add(x.getAgentId() + x.getFilePath());
                    } else if (StringUtils.contains(x.getFilePath(), hostEventQueryParams.getPath())) {
                        list.add(x);
                        exist.add(x.getAgentId() + x.getFilePath());
                    }
                }
            }
            fileEventModels = new ListObjResult<>(list, list.size());
        }

        // 设置total
        int alreadyHasNum = hostEventsBos.getSource().getTotal();
        hostEventsBos.getSource().setTotal(alreadyHasNum + fileEventModels.getTotal());

        // 当前页元素数量足够
        int pageSize = hostEventQueryParams.getPageSize();
        if (bos.size() == pageSize) {
            return;
        }

        // 计算分页起始位置
        int totalNeedNum = pageSize * hostEventQueryParams.getPage();
        int stillNeedNum = totalNeedNum - alreadyHasNum;
        int startIndex = stillNeedNum <= pageSize ? 0 : stillNeedNum - pageSize;
        List<FileEventModel> fEventModels = fileEventModels.getObjs();
        if (startIndex >= fEventModels.size()) {
            return;
        }
        fEventModels = fEventModels.subList(startIndex, Math.min(fEventModels.size(), stillNeedNum));

        for (FileEventModel file : fEventModels) {
            CollectionHostEventBO bo = new CollectionHostEventBO();
            SceneElementBO ele = eidMap.get(file.getAgentId());
            bo.setNodeId(ele.getId());
            bo.setNodeName(ele.getName());
            if (Objects.nonNull(file.getLevel())) {
                bo.setLevel(file.getLevel());
            } else {
                bo.setLevel(-1);
            }
            if (StringUtils.isNotEmpty(file.getFilePath()) && file.getFilePath().endsWith("---file_not_exists---")) {
                bo.setPath(StringUtils.replace(file.getFilePath(), "---file_not_exists---", ""));
                bo.setDescription("-");
            } else if (StringUtils.isNotEmpty(file.getFilePath()) && file.getFilePath().contains("---file_size---")) {
                String[] fileSize = file.getFilePath().split("---file_size---");
                if (fileSize.length > 0) {
                    bo.setPath(fileSize[0]);
                }
                if (fileSize.length > 1) {
                    if (NumberUtil.isNumber(fileSize[1])) {
                        bo.setFileSize(fileSize[1]);
                    }
                }
                bo.setDescription(StringUtils.EMPTY);
            } else {
                bo.setPath(file.getFilePath());
            }
            bo.setTime(file.getUpdateTime());
            bos.add(bo);
            if (bos.size() == hostEventQueryParams.getPageSize()) {
                return;
            }
        }
    }

    private void mergeFileData(ListObjServiceResult<CollectionHostEventBO> hostEventsBos,
                               HostEventQueryParams hostEventQueryParams, ElementMonitorRequest pageRequest, List<String> elementIds) {
        if (CollectionUtils.isNotEmpty(elementIds)) {
            return;
        }
        List<CollectionHostEventBO> bos = hostEventsBos.getSource().getObjs();
        HumanCondition humanCondition = new HumanCondition().currentLink(ElementFileListModel.class)
                .filterIn(ElementFileListModel::getGroup, hostEventQueryParams.getRuleGroups().toArray())
                .filterEqual(ElementFileListModel::getSceneId, pageRequest.getSceneId());
        if (CollectionUtils.isNotEmpty(pageRequest.getLevels())) {
            humanCondition.filterIn(ElementFileListModel::getLevel, pageRequest.getLevels().toArray());
        }
        var fileList = elementFileListMapper.list(humanCondition);

        // 设置total
        int alreadyHasNum = hostEventsBos.getSource().getTotal();
        hostEventsBos.getSource().setTotal(alreadyHasNum + fileList.size());

        // 当前页元素数量足够
        int pageSize = hostEventQueryParams.getPageSize();
        if (bos.size() == pageSize) {
            return;
        }

        // 计算分页起始位置
        int totalNeedNum = pageSize * hostEventQueryParams.getPage();
        int stillNeedNum = totalNeedNum - alreadyHasNum;
        int startIndex = stillNeedNum <= pageSize ? 0 : stillNeedNum - pageSize;
        if (startIndex >= fileList.size()) {
            return;
        }
        fileList = fileList.subList(startIndex, Math.min(fileList.size(), stillNeedNum));

        for (ElementFileListModel file : fileList) {
            CollectionHostEventBO bo = new CollectionHostEventBO();
            bo.setNodeName(file.getNodeName());
            if (Objects.nonNull(file.getLevel())) {
                bo.setLevel(file.getLevel());
            } else {
                bo.setLevel(-1);
            }
            if (StringUtils.isNotEmpty(file.getPath()) && file.getPath().endsWith("---file_not_exists---")) {
                bo.setPath(StringUtils.replace(file.getPath(), "---file_not_exists---", ""));
                bo.setDescription("-");
            } else if (StringUtils.isNotEmpty(file.getPath()) && file.getPath().contains("---file_size---")) {
                String[] fileSize = file.getPath().split("---file_size---");
                if (fileSize.length > 0) {
                    bo.setPath(fileSize[0]);
                }
                if (fileSize.length > 1) {
                    if (NumberUtil.isNumber(fileSize[1])) {
                        bo.setFileSize(fileSize[1]);
                    }
                }
                bo.setDescription(StringUtils.EMPTY);
            } else {
                bo.setPath(file.getPath());
            }
            bo.setEventDesc(file.getEventDesc());
            bo.setTime(file.getUpdateTime());
            bo.setFileSize(file.getFileSize());
            bo.setMd5Before(file.getMd5Before());
            bo.setMd5After(file.getMd5After());
            bos.add(bo);
            if (bos.size() == hostEventQueryParams.getPageSize()) {
                return;
            }
        }
    }

    /**
     * 按时间分桶查询事件数
     *
     * @param pageRequest
     * @param ruleGroups
     * @param intervalMinutes
     * @return
     */
    public List<ElementHistogramInfo> listEventHistogram(ElementMonitorRequest pageRequest, List<String> ruleGroups, Integer intervalMinutes, Integer bucketSize) {
        if (Objects.isNull(intervalMinutes) || Objects.isNull(bucketSize)) {
            log.error("intervalMinutes:{}, bucketSize:{} should not be null!", intervalMinutes, bucketSize);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        var agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(pageRequest.getSceneId(), pageRequest.getElementIds());
        if (CollectionUtil.isEmpty(agentNames)) {
            return Collections.emptyList();
        }

        List<ElementHistogramInfo> infos = new ArrayList<>();
        var wazuhEventRequest = buildWazuhRequest(pageRequest);
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setRuleGroups(ruleGroups);
        wazuhEventRequest.setLevels(pageRequest.getLevels());
        wazuhEventRequest.setPageSize(0);

        String minBounds = TimeUtils.localTime2yyyyMMddHHmm(wazuhEventRequest.getEndTime().minusMinutes((long) intervalMinutes * (bucketSize)));
        String maxBounds = TimeUtils.localTime2yyyyMMddHHmm(wazuhEventRequest.getEndTime().minusMinutes(1));

        var resultMap = dataCollectionService.aggsDateHistogram(wazuhEventRequest, intervalMinutes + "m", minBounds, maxBounds);

        if (CollectionUtil.isNotEmpty(resultMap)) {
            resultMap.forEach((key, value) -> {
                infos.add(ElementHistogramInfo.builder().time(key).count(Objects.isNull(value) ? 0 : value.intValue()).build());
            });
        }

        //LocalDateTime endTime = null;
        //if (Objects.nonNull(wazuhEventRequest) && Objects.nonNull(wazuhEventRequest.getEndTime())) {
        //    endTime = wazuhEventRequest.getEndTime();
        //} else {
        //    endTime = LocalDateTime.now();
        //}
        //var infos = processHistogramList(endTime, bucketSize, intervalMinutes, resultMap);
        List<ElementHistogramInfo> finalList = infos.stream().limit(bucketSize).collect(Collectors.toList());
        Collections.reverse(finalList);
        mergeFileCount(finalList, pageRequest.getSceneId(), ruleGroups);
        return finalList;
    }

    private List<ElementHistogramInfo> mergeFileCount(List<ElementHistogramInfo> histogramInfos,
                                                      String sceneId, List<String> group) {
        List<String> timeList = histogramInfos.stream().map(ElementHistogramInfo::getTime).collect(Collectors.toList());
        HumanCondition humanCondition = new HumanCondition().currentLink(ElementFileCountModel.class)
                .filterEqual(ElementFileCountModel::getSceneId, sceneId)
                .filterIn(ElementFileCountModel::getTime, timeList.toArray())
                .filterNotEqual(ElementFileCountModel::getCount, 0)
                .filterIn(ElementFileCountModel::getGroup, group.toArray());
        List<ElementFileCountModel> countModels = elementFileCountMapper.list(humanCondition);
        Map<String, ElementHistogramInfo> histogramInfoMap = histogramInfos.stream().collect(Collectors.toMap(ElementHistogramInfo::getTime, e -> e));
        for (ElementFileCountModel model : countModels) {
            histogramInfoMap.get(model.getTime()).setCount(histogramInfoMap.get(model.getTime()).getCount() + model.getCount());
        }
        return new ArrayList<>(histogramInfoMap.values());
    }

    @Deprecated
    public List<ElementHistogramInfo> getModifyHistogram(ElementMonitorRequest pageRequest, List<String> ruleGroups, Integer segmentCount) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        LocalDateTime startTime;
        LocalDateTime endTime;
        processRequest(pageRequest, sceneHandler);
        startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        // 根据配置设置查询的最大条数
        pageRequest.setPageSize(10000);
        var result = getModifyData(pageRequest, ruleGroups, null);
        if (result == null || result.getSource() == null || CollUtil.isEmpty(result.getSource().getObjs())) {
            return new ArrayList<>();
        }
        if (segmentCount == null) {
            // 默认时间分15段
            segmentCount = 15;
        }
        long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<ElementHistogramInfo> histogramInfos = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++) {
            ElementHistogramInfo histogramInfo = new ElementHistogramInfo();
            histogramInfo.setLocalDateTime(startTime.plusSeconds(intervalSeconds * i));
            histogramInfo.setTime(TimeUtils.localDateTimeToString(histogramInfo.getLocalDateTime()));
            histogramInfo.setCount(0);
            histogramInfos.add(histogramInfo);
        }
        result.getSource().getObjs().sort(Comparator.comparing(r -> r.getTime()));
        int currentHistogramInfo = 0;
        for (int i = 0; i < result.getSource().getObjs().size(); ) {
            var collectionData = result.getSource().getObjs().get(i);
            var histogramInfo = histogramInfos.get(currentHistogramInfo);
            // 超出时间区间的事件放到最后一个区间
            if ((currentHistogramInfo == histogramInfos.size() - 1) || collectionData.getTime().isBefore(histogramInfo.getLocalDateTime())) {
                histogramInfo.setCount(histogramInfo.getCount() + 1);
                i++;
            } else {
                currentHistogramInfo = Math.min(currentHistogramInfo + 1, histogramInfos.size() - 1);
            }
        }
        return histogramInfos;
    }

    public ListObjResult<ElementVulnerabilityInfo> getVulnerabilityInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        if (CollUtil.isEmpty(pageRequest.getElementIds())) {
            return new ListObjResult<>();
        }
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                .filterEqual(SceneModel::getId, sceneId);
        var sceneModel = sceneMapper.list(humanCondition);
        boolean isRunning = csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId());
        processVulnerabilityRequest(pageRequest, sceneModel.get(0), isRunning);
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        Map<String, String> elementEid2Name = sceneHandler.getElementMap().values().stream().collect(Collectors.toMap(e -> e.getEid(), e -> e.getName()));
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler);
        if (startTime.plusHours(getWazuhVulnerabilityScanInterval()).isEqual(endTime)) {
            processTimeRangeForVulnerabilityInfo(pageRequest, agentNames);
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPageNum(pageRequest.getPage());
        request.setPageSize(pageRequest.getPageSize());
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        var result = dataCollectionService.queryVulnerabilityInfo(request, true);
        List<ElementVulnerabilityInfo> vulnerabilityInfos = new ArrayList<>();
        result.getObjs().forEach(r -> {
            ElementVulnerabilityInfo vulnerabilityInfo = new ElementVulnerabilityInfo();
            vulnerabilityInfo.setNodeName(elementEid2Name.get(r.getAgentName()));
            vulnerabilityInfo.setCve(r.getVulnerability().getCve());
            // 等级转化为小写，便于前端转化
            vulnerabilityInfo.setSeverity(r.getVulnerability().getSeverity().toLowerCase());
            vulnerabilityInfo.setTitle(r.getVulnerability().getTitle());
            vulnerabilityInfo.setTime(r.getTime());
            vulnerabilityInfo.setPublished(convertDate(r.getVulnerability().getPublished()));
            vulnerabilityInfo.setUpdate(r.getVulnerability().getUpdated());
            vulnerabilityInfo.setTime(r.getTime());
            vulnerabilityInfo.setReferences(r.getVulnerability().getReferences());
            vulnerabilityInfo.setRationale(r.getVulnerability().getRationale());
            vulnerabilityInfos.add(vulnerabilityInfo);
        });
        return new ListObjResult<>(vulnerabilityInfos, result.getTotal());
    }

    private void processTimeRangeForVulnerabilityInfo(ElementMonitorRequest pageRequest, List<String> agentNames) {
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPageNum(1);
        request.setPageSize(1);
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        int halfScanTime = getWazuhVulnerabilityScanInterval() / 2;
        var first = dataCollectionService.queryVulnerabilityInfo(request, false);
        var last = dataCollectionService.queryVulnerabilityInfo(request, true);
        if (CollUtil.isEmpty(first.getObjs()) || CollUtil.isEmpty(last.getObjs())) {
            // 两次上报漏洞信息的间隔超过扫描间隔且查不到数据时，将开始时间往前推扫描间隔的一半进行查询
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(startTime.plusHours(-halfScanTime)));
            return;
        }
        var firstTime = first.getObjs().get(0).getTime();
        var lastTime = last.getObjs().get(0).getTime();
        if (firstTime.plusHours(halfScanTime).isBefore(lastTime)) {
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(lastTime.plusHours(-halfScanTime)));
        } else if (firstTime.plusHours(halfScanTime).isAfter(lastTime)) {
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(firstTime.plusHours(-halfScanTime)));
        }
    }

    public List<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChartInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        if (CollUtil.isEmpty(pageRequest.getElementIds())) {
            return new ArrayList<>();
        }
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                .filterEqual(SceneModel::getId, sceneId);
        var sceneModel = sceneMapper.list(humanCondition);
        boolean isRunning = csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId());
        processVulnerabilityRequest(pageRequest, sceneModel.get(0), isRunning);
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler);
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        if (startTime.plusHours(getWazuhVulnerabilityScanInterval()).isEqual(endTime)) {
            processTimeRangeForVulnerabilityInfo(pageRequest, agentNames);
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        var result = dataCollectionService.queryVulnerabilityCountInfo(request,
                false, "data.vulnerability.severity", "severity");
        if (CollUtil.isEmpty(result)) {
            return new ArrayList<>();
        }
        result = result.stream()
                .filter(countModel -> !countModel.getFieldName().equals("-"))
                .collect(Collectors.toList());
        long i = 0;
        for (WazuhVulnerabilityInfoCountModel countModel : result) {
            i = countModel.getCount() + i;
        }
        long sum = i;
        List<ElementVulnerabilityPieChartInfo> pieChartInfos = new ArrayList<>();
        result.forEach(r -> {
            ElementVulnerabilityPieChartInfo pieChartInfo = new ElementVulnerabilityPieChartInfo();
            // 漏洞等级转化为小写，便于前端转化
            pieChartInfo.setSeverity(r.getFieldName().toLowerCase());
            // 设置等级，便于排序
            if ("low".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(0);
            } else if ("medium".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(1);
            } else if ("high".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(2);
            } else if ("critical".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(3);
            }
            // 计算百分比 单位：%
            pieChartInfo.setPercent(BigDecimal.valueOf(r.getCount()).divide(BigDecimal.valueOf(sum), 4, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).doubleValue());
            pieChartInfos.add(pieChartInfo);
        });
        pieChartInfos.sort(Comparator.comparing(ElementVulnerabilityPieChartInfo::getSeverityLevel));
        return pieChartInfos;
    }

    public List<ElementVulnerabilityCountInfo> getVulnerabilityCountInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        if (CollUtil.isEmpty(pageRequest.getElementIds())) {
            return new ArrayList<>();
        }
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                .filterEqual(SceneModel::getId, sceneId);
        var sceneModel = sceneMapper.list(humanCondition);
        boolean isRunning = csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId());
        processVulnerabilityRequest(pageRequest, sceneModel.get(0), isRunning);
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler);
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        if (startTime.plusHours(getWazuhVulnerabilityScanInterval()).isEqual(endTime)) {
            processTimeRangeForVulnerabilityInfo(pageRequest, agentNames);
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        var result = dataCollectionService.queryVulnerabilityCountInfo(request,
                true, "data.vulnerability.package.name", "package_name");
        List<ElementVulnerabilityCountInfo> countInfos = new ArrayList<>();
        result.forEach(r -> {
            ElementVulnerabilityCountInfo countInfo = new ElementVulnerabilityCountInfo();
            countInfo.setPackageName(r.getFieldName());
            countInfo.setCount((int) r.getCount());
            countInfos.add(countInfo);
        });
        return countInfos;
    }


    public void exportData(HttpServletResponse response, String sceneId, String elementId, String type) {
        ElementMonitorRequest pageRequest = new ElementMonitorRequest();
        pageRequest.setSceneId(sceneId);
        pageRequest.setPage(1);
        pageRequest.setPageSize(1000);
        if (StringUtils.isNotBlank(elementId)) {
            pageRequest.setElementIds(Collections.singletonList(elementId));
        }
        JSONObject result = new JSONObject();

        //文件变更列表
        List<ElementFileListModel> fileList = getModifyData(JsonCommon.parse(JsonCommon.stringify(pageRequest), ElementMonitorRequest.class), CsceneBizConstant.FILE_MODIFY_GROUP, null)
                .asResult().sourceList().stream()
                .map(file -> collectionHostEventBO2ElementFileListModel(file, CsceneBizConstant.FILE_MODIFY_GROUP.get(0)))
                .collect(Collectors.toList());
        result.put(FILE_LIST, JSON.toJSON(fileList));

        //文件变更数据
        List<ElementFileCountModel> fileCount = listEventHistogram(JsonCommon.parse(JsonCommon.stringify(pageRequest), ElementMonitorRequest.class), CsceneBizConstant.FILE_MODIFY_GROUP, 15, 15)
                .stream()
                .map(count -> elementHistogramInfo2ElementFileCountModel(count, CsceneBizConstant.FILE_MODIFY_GROUP.get(0)))
                .collect(Collectors.toList());
        result.put(FILE_COUNT, JSON.toJSON(fileCount));

        //注册表变更列表
        List<ElementFileListModel> registryList = getModifyData(JsonCommon.parse(JsonCommon.stringify(pageRequest), ElementMonitorRequest.class), CsceneBizConstant.REGISTRY_MODIFY_GROUP, null)
                .asResult().sourceList().stream()
                .map(registry -> collectionHostEventBO2ElementFileListModel(registry, CsceneBizConstant.REGISTRY_MODIFY_GROUP.get(0)))
                .collect(Collectors.toList());
        result.put(REGISTRY_LIST, JSON.toJSON(registryList));

        //注册表变更数量
        List<ElementFileCountModel> registryCount = listEventHistogram(JsonCommon.parse(JsonCommon.stringify(pageRequest), ElementMonitorRequest.class), CsceneBizConstant.REGISTRY_MODIFY_GROUP, 15, 15)
                .stream()
                .map(count -> elementHistogramInfo2ElementFileCountModel(count, CsceneBizConstant.REGISTRY_MODIFY_GROUP.get(0)))
                .collect(Collectors.toList());
        result.put(REGISTRY_COUNT, JSON.toJSON(registryCount));

        HostEventQueryParams queryParams = getExportDataParams(sceneId, elementId);
        queryParams.setCatagories(Arrays.asList("020101", "020102", "020103", "020104", "020105", "020106", "020107",
                "020201", "020202", "020203", "020204", "020205", "020206", "020207", "020208", "020210",
                "020301", "020302", "020303", "020304", "020305", "020313"));

        //日志事件
        List<ElementLogListModel> logList = csceneInstanceService.listHostEvents(sceneId, queryParams)
                .asResult().sourceList().stream()
                .map(this::collectionHostEventBO2ElementLogListModel)
                .collect(Collectors.toList());
        result.put(LOG, JSON.toJSON(logList));

        //自定义采集
        List<WazuhProbeDataModel> probeList = csceneInstanceService.getProbeCollectionData(null, sceneId, null, queryParams, null)
                .asResult().sourceList().stream()
                .map(this::collectionHostEventBO2WazuhProbeDataModel)
                .collect(Collectors.toList());
        result.put(PROBE, JSON.toJSON(probeList));

        queryParams.setCatagories(Arrays.asList("020301", "020302", "020303", "020304", "020305", "020306", "020307", "020308", "020309", "020310", "020311", "020312"));
        //告警信息
        List<ElementLogListModel> alarmList = csceneInstanceService.listHostEvents(sceneId, queryParams)
                .asResult().sourceList().stream()
                .map(this::collectionHostEventBO2ElementLogListModel)
                .collect(Collectors.toList());
        result.put(ALARM, JSON.toJSON(alarmList));

        String fileName = FileUtil.getTmpDirPath() + File.separator + "data";

        if ("json".equals(type)) {
            try (FileWriter writer = new FileWriter(fileName + ".json")) {
                writer.write(result.toString());
            } catch (IOException e) {
                log.error("export data fail, sceneId = {}, elementId = {}, type = {}", sceneId, elementId, type);
                throw new MpRuntimeException(CsceneErrorCode.EXPORT_ERROR);
            }
            FileUtil.setResponse(new File(fileName + ".json"), true, response);
        } else if ("xml".equals(type)) {
            try (FileWriter fileWriter = new FileWriter(fileName + ".xml")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result.toJSONString());

                fileWriter.write(new XmlMapper().writeValueAsString(jsonNode));
            } catch (IOException e) {
                log.error("export data fail, sceneId = {}, elementId = {}, type = {}", sceneId, elementId, type);
                throw new MpRuntimeException(CsceneErrorCode.EXPORT_ERROR);
            }
            FileUtil.setResponse(new File(fileName + ".xml"), true, response);
        } else if ("xlsx".equals(type)) {

            String filePath = fileName + ".xlsx";
            ExcelDataHandler.exportExcel(fileCount, filePath, "文件变更数量",
                    Arrays.asList("sceneId", "group", "log"), ExcelHeader.FILE_COUNT);

            ExcelDataHandler.exportExcel(fileList, filePath, "文件变更列表",
                    Arrays.asList("sceneId", "group", "md5Before", "md5After", "log"), ExcelHeader.FILE_LIST);

            ExcelDataHandler.exportExcel(registryCount, filePath, "注册表变更数量",
                    Arrays.asList("sceneId", "group", "log"), ExcelHeader.FILE_COUNT);

            ExcelDataHandler.exportExcel(registryList, filePath, "注册表变更列表",
                    Arrays.asList("sceneId", "group", "fileSize", "log"), ExcelHeader.REGISTRY_LIST);

            ExcelDataHandler.exportExcel(logList, filePath, "日志事件",
                    Arrays.asList("sceneId", "log"), ExcelHeader.LOG_LIST);

            ExcelDataHandler.exportExcel(probeList, filePath, "自定义采集",
                    Arrays.asList("sceneId", "log"), ExcelHeader.PROBE_LIST);

            ExcelDataHandler.exportExcel(logList, filePath, "告警事件",
                    Arrays.asList("sceneId", "log"), ExcelHeader.LOG_LIST);

            FileUtil.setResponse(new File(fileName + ".xlsx"), true, response);
        }
    }

    private ElementFileListModel collectionHostEventBO2ElementFileListModel(CollectionHostEventBO bo, String group) {
        ElementFileListModel model = new ElementFileListModel();
        model.setNodeName(bo.getNodeName());
        model.setTime(bo.getTime());
        model.setPath(bo.getPath());
        model.setEventDesc(bo.getEventDesc());
        model.setLevel(bo.getLevel());
        model.setFileSize(bo.getFileSize());
        model.setGroup(group);
        if (StringUtils.isEmpty(bo.getMd5Before())) {
            model.setMd5Before("");
        } else {
            model.setMd5Before(bo.getMd5Before());
        }
        if (StringUtils.isEmpty(bo.getMd5After())) {
            model.setMd5After("");
        } else {
            model.setMd5After(bo.getMd5After());
        }
        return model;
    }

    private ElementFileCountModel elementHistogramInfo2ElementFileCountModel(ElementHistogramInfo info, String group) {
        ElementFileCountModel model = new ElementFileCountModel();
        model.setTime(info.getTime());
        model.setCount(info.getCount());
        model.setGroup(group);
        return model;
    }

    private ElementLogListModel collectionHostEventBO2ElementLogListModel(CollectionHostEventBO bo) {
        ElementLogListModel model = new ElementLogListModel();
        model.setNodeName(bo.getNodeName());
        model.setTime(bo.getTime());
        model.setAppName(bo.getAppName());
        model.setEventDesc(bo.getEventDesc());
        model.setLevel(bo.getLevel());
        model.setDescription(bo.getDescription());
        model.setCatagory(bo.getCatagory());
        return model;
    }

    private WazuhProbeDataModel collectionHostEventBO2WazuhProbeDataModel(CollectionHostEventBO bo) {
        WazuhProbeDataModel model = new WazuhProbeDataModel();
        model.setProbeName(bo.getProbeName());
        model.setEventDesc(bo.getEventDesc());
        model.setTime(bo.getTime());
        model.setNodeName(bo.getNodeName());
        return model;
    }

    private HostEventQueryParams getExportDataParams(String csceneId, String elementId) {
        var sceneId = csceneInstanceService.getSceneId(csceneId);
        var sceneHandler = new SceneHandler(sceneId, null, false, null);

        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        // 不传节点id默认查询全部节点
        if (elementId == null) {
            hostEventQueryParams.setNodeIds(new ArrayList<>(sceneHandler.getElementMap().keySet()));
        } else {
            hostEventQueryParams.setNodeIds(Collections.singletonList(elementId));
        }
        hostEventQueryParams.setPage(1);
        hostEventQueryParams.setPageSize(10000);
        hostEventQueryParams.setPerPage(10000);
        return hostEventQueryParams;
    }

    public void importData(ImportFileBO fileBO) {
        MultipartFile multipartFile = fileBO.getFile();
        String sceneId = fileBO.getSceneId();
        String originalFileName = multipartFile.getOriginalFilename();
        String fileSuffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        Integer type = fileBO.getType();

        File file = new File(FileUtil.getTmpDirPath() + File.separator, originalFileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<ElementFileCountModel> fileCountModels = new ArrayList<>();
        List<ElementFileListModel> fileListModels = new ArrayList<>();
        List<ElementFileCountModel> registryCountModels = new ArrayList<>();
        List<ElementFileListModel> registryListModels = new ArrayList<>();
        List<ElementLogListModel> logListModels = new ArrayList<>();
        List<WazuhProbeDataModel> wazuhProbeDataModels = new ArrayList<>();
        if ("xlsx".equals(fileSuffix)) {
            if (0 == type) {
                ExcelDataHandler excelDataHandler = new ExcelDataHandler(file.getPath(), "文件变更数量");
                List<List<String>> fileCount = excelDataHandler.getSheetData(1);
                for (int i = 0; i < fileCount.size(); i++) {
                    List<String> countData = fileCount.get(i);
                    if (0 == Integer.parseInt(countData.get(1))) {
                        continue;
                    }
                    ElementFileCountModel countModel = new ElementFileCountModel();
                    countModel.setSceneId(sceneId);
                    countModel.setGroup(CsceneBizConstant.FILE_MODIFY_GROUP.get(0));
                    countModel.setTime(countData.get(0));
                    countModel.setCount(Integer.parseInt(countData.get(1)));
                    fileCountModels.add(countModel);
                }

                excelDataHandler = new ExcelDataHandler(file.getPath(), "文件变更列表");
                List<List<String>> fileList = excelDataHandler.getSheetData(1);
                for (int i = 0; i < fileList.size(); i++) {
                    List<String> fileData = fileList.get(i);
                    ElementFileListModel fileListModel = new ElementFileListModel();
                    fileListModel.setSceneId(sceneId);
                    fileListModel.setGroup(CsceneBizConstant.FILE_MODIFY_GROUP.get(0));
                    fileListModel.setMd5After("");
                    fileListModel.setMd5Before("");
                    fileListModel.setNodeName(fileData.get(0));
                    fileListModel.setTime(LocalDateTime.parse(fileData.get(1)));
                    fileListModel.setPath(fileData.get(2));
                    fileListModel.setEventDesc(fileData.get(3));
                    fileListModel.setLevel(Integer.parseInt(fileData.get(4)));
                    fileListModel.setFileSize(fileData.get(5));
                    fileListModels.add(fileListModel);
                }
            } else if (1 == type) {
                ExcelDataHandler excelDataHandler = new ExcelDataHandler(file.getPath(), "注册表变更数量");
                List<List<String>> registryCount = excelDataHandler.getSheetData(1);
                for (int i = 0; i < registryCount.size(); i++) {
                    List<String> countData = registryCount.get(i);
                    if (0 == Integer.parseInt(countData.get(1))) {
                        continue;
                    }
                    ElementFileCountModel countModel = new ElementFileCountModel();
                    countModel.setSceneId(sceneId);
                    countModel.setGroup(CsceneBizConstant.REGISTRY_MODIFY_GROUP.get(0));
                    countModel.setTime(countData.get(0));
                    countModel.setCount(Integer.parseInt(countData.get(1)));
                    registryCountModels.add(countModel);
                }

                excelDataHandler = new ExcelDataHandler(file.getPath(), "注册表变更列表");
                List<List<String>> registryList = excelDataHandler.getSheetData(1);
                for (int i = 0; i < registryList.size(); i++) {
                    List<String> registryData = registryList.get(i);
                    ElementFileListModel registryListModel = new ElementFileListModel();
                    registryListModel.setSceneId(sceneId);
                    registryListModel.setGroup(CsceneBizConstant.REGISTRY_MODIFY_GROUP.get(0));
                    registryListModel.setFileSize("");
                    registryListModel.setNodeName(registryData.get(0));
                    registryListModel.setTime(LocalDateTime.parse(registryData.get(1)));
                    registryListModel.setPath(registryData.get(2));
                    registryListModel.setEventDesc(registryData.get(3));
                    registryListModel.setLevel(Integer.parseInt(registryData.get(4)));
                    registryListModel.setMd5Before(registryData.get(5));
                    registryListModel.setMd5After(registryData.get(6));
                    registryListModels.add(registryListModel);
                }
            } else if (2 == type) {
                ExcelDataHandler excelDataHandler = new ExcelDataHandler(file.getPath(), "日志事件");
                List<List<String>> logList = excelDataHandler.getSheetData(1);
                for (int i = 0; i < logList.size(); i++) {
                    List<String> logData = logList.get(i);
                    ElementLogListModel logListModel = new ElementLogListModel();
                    logListModel.setSceneId(sceneId);
                    logListModel.setNodeName(logData.get(0));
                    logListModel.setTime(LocalDateTime.parse(logData.get(1)));
                    logListModel.setAppName(logData.get(2));
                    logListModel.setEventDesc(logData.get(3));
                    logListModel.setLevel(Integer.parseInt(logData.get(4)));
                    logListModel.setDescription(logData.get(5));
                    logListModel.setCatagory(logData.get(6));
                    logListModels.add(logListModel);
                }
            } else if (3 == type) {
                ExcelDataHandler excelDataHandler = new ExcelDataHandler(file.getPath(), "自定义采集");
                List<List<String>> probeList = excelDataHandler.getSheetData(1);
                for (int i = 0; i < probeList.size(); i++) {
                    List<String> probeData = probeList.get(i);
                    WazuhProbeDataModel dataModel = new WazuhProbeDataModel();
                    dataModel.setSceneId(sceneId);
                    dataModel.setNodeName(probeData.get(0));
                    dataModel.setProbeName(probeData.get(1));
                    dataModel.setEventDesc(probeData.get(2));
                    dataModel.setTime(LocalDateTime.parse(probeData.get(3)));
                    wazuhProbeDataModels.add(dataModel);
                }
            } else if (4 == type) {
                ExcelDataHandler excelDataHandler = new ExcelDataHandler(file.getPath(), "告警事件");
                List<List<String>> logList = excelDataHandler.getSheetData(1);
                for (int i = 0; i < logList.size(); i++) {
                    List<String> logData = logList.get(i);
                    ElementLogListModel logListModel = new ElementLogListModel();
                    logListModel.setSceneId(sceneId);
                    logListModel.setNodeName(logData.get(0));
                    logListModel.setTime(LocalDateTime.parse(logData.get(1)));
                    logListModel.setAppName(logData.get(2));
                    logListModel.setEventDesc(logData.get(3));
                    logListModel.setLevel(Integer.parseInt(logData.get(4)));
                    logListModel.setDescription(logData.get(5));
                    logListModel.setCatagory(logData.get(6));
                    logListModels.add(logListModel);
                }
            }
        } else if ("json".equals(fileSuffix) || "xml".equals(fileSuffix)) {
            JSONObject jsonObject = new JSONObject();
            if ("json".equals(fileSuffix)) {
                String content = FileUtil.getContent(file);
                jsonObject = JSON.parseObject(content);
            } else {
                ObjectMapper objectMapper = new ObjectMapper(new XmlFactory());
                try {
                    jsonObject = (JSONObject) JSONObject.toJSON(objectMapper.readValue(file, Object.class));
                    object2array(jsonObject, FILE_LIST);
                    object2array(jsonObject, REGISTRY_LIST);
                    object2array(jsonObject, LOG);
                    object2array(jsonObject, PROBE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (0 == type) {
                String fileCount = jsonObject.getString(FILE_COUNT);
                fileCountModels = JSON.parseArray(fileCount, ElementFileCountModel.class)
                        .stream()
                        .filter(count -> 0 != count.getCount())
                        .collect(Collectors.toList());
                fileCountModels.forEach(count -> count.setSceneId(sceneId));

                String fileList = jsonObject.getString(FILE_LIST);
                fileListModels = JSON.parseArray(fileList, ElementFileListModel.class);
                fileListModels.forEach(list -> list.setSceneId(sceneId));
            } else if (1 == type) {
                String registryCount = jsonObject.getString(REGISTRY_COUNT);
                registryCountModels = JSON.parseArray(registryCount, ElementFileCountModel.class)
                        .stream()
                        .filter(count -> 0 != count.getCount())
                        .collect(Collectors.toList());
                registryCountModels.forEach(count -> count.setSceneId(sceneId));

                String registryList = jsonObject.getString(REGISTRY_LIST);
                registryListModels = JSON.parseArray(registryList, ElementFileListModel.class);
                registryListModels.forEach(list -> list.setSceneId(sceneId));
            } else if (2 == type) {
                String logList = jsonObject.getString(LOG);
                logListModels = JSON.parseArray(logList, ElementLogListModel.class);
                logListModels.forEach(log -> log.setSceneId(sceneId));
            } else if (3 == type) {
                String probeList = jsonObject.getString(PROBE);
                wazuhProbeDataModels = JSON.parseArray(probeList, WazuhProbeDataModel.class);
                wazuhProbeDataModels.forEach(dataModel -> dataModel.setSceneId(sceneId));
            } else if (4 == type) {
                String logList = jsonObject.getString(ALARM);
                logListModels = JSON.parseArray(logList, ElementLogListModel.class);
                logListModels.forEach(log -> log.setSceneId(sceneId));
            }
        }
        file.delete();
        elementFileCountMapper.bulkCreate(fileCountModels);
        elementFileListMapper.bulkCreate(fileListModels);
        elementFileCountMapper.bulkCreate(registryCountModels);
        elementFileListMapper.bulkCreate(registryListModels);
        elementLogListMapper.bulkCreate(logListModels);
        wazuhProbeDataMapper.bulkCreate(wazuhProbeDataModels);
    }

    private void object2array(JSONObject jsonObject, String key) {
        if (jsonObject.get(key) instanceof JSONObject) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(jsonObject.getJSONObject(key));
            jsonObject.put(key, jsonArray);
        }
    }

    private void processRequest(ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        if (StringUtils.isEmpty(pageRequest.getStartTime())) {
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(sceneHandler.getScene().getCreateTime()));
        }
        if (StringUtils.isEmpty(pageRequest.getEndTime())) {
            pageRequest.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        }
    }

    private void processVulnerabilityRequest(ElementMonitorRequest pageRequest, SceneModel sceneModel, boolean isRunning) {
        LocalDateTime endTime;
        if (isRunning) {
            endTime = LocalDateTime.now();
        } else {
            endTime = sceneModel.getUpdateTime();
        }
        if (StringUtils.isEmpty(pageRequest.getStartTime())) {
            //场景存在时间不足一个扫描周期，取全部漏洞数据
            if (endTime.plusHours(-getWazuhVulnerabilityScanInterval()).isBefore(sceneModel.getCreateTime())) {
                pageRequest.setStartTime(TimeUtils.localDateTimeToString(sceneModel.getCreateTime()));
            } else {
                //场景存在时间超过一个扫描周期，取最近一个扫描周期的漏洞数据
                pageRequest.setStartTime(TimeUtils.localDateTimeToString(endTime.plusHours(-getWazuhVulnerabilityScanInterval())));
            }
        }
        if (StringUtils.isEmpty(pageRequest.getEndTime())) {
            pageRequest.setEndTime(TimeUtils.localDateTimeToString(endTime));
        }
    }

    private List<String> getAgentNames(ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        List<String> agentNames;
        agentNames = sceneHandler.getElementMap().values().stream()
                .filter(e -> pageRequest.getElementIds().contains(e.getId()))
                .map(e -> e.getEid())
                .collect(Collectors.toList());
        return agentNames;
    }

    private ConnectionState convertWazuhNetConn2ConnState(WazuhNetworkConnectionModel model) {
        ConnectionState connectionState = new ConnectionState();
        connectionState.setState(model.getStatus());
        connectionState.setProtocol(model.getProto());
        connectionState.setPid(model.getPid());

        connectionState.setLocalAddress(model.getLaddrIp());
        connectionState.setLocalPort(model.getLaddrPort());

        connectionState.setForeignAddress(model.getRaddrIp());
        connectionState.setForeignPort(model.getRaddrPort());

        connectionState.setDescription(model.getDescription());
        connectionState.setTime(model.getTime());
        connectionState.setLevel(model.getLevel());

        return connectionState;
    }

    /**
     * 处理节点实时信息，用户，进程，端口，网络连接
     *
     * @param baseRequest
     * @return
     */
    private ElementRTInfo processRTInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ElementRTInfo rtInfo = new ElementRTInfo();

        // 用户
        rtInfo.setUserCount(countNodeSessionUsers(baseRequest));

        TimeListPageInfoQueryParams portsParams = new TimeListPageInfoQueryParams();
        portsParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        portsParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Port> portsResult = hostStateService.pagePorts(portsParams);

        // pagePorts内部有sceneId转换动作，这里pageProcesses不能共用一个param
        TimeListPageInfoQueryParams processParams = new TimeListPageInfoQueryParams();
        processParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        processParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Process> processesResult = hostStateService.pageProcesses(processParams);

        BaseSceneElementPageRequest pageRequest = new BaseSceneElementPageRequest();
        pageRequest.setSceneId(baseRequest.getSceneId());
        pageRequest.setElementIds(baseRequest.getElementIds());
        ListObjServiceResult<ConnectionState> conns = pageNetworkConnections(pageRequest);

        if (Objects.nonNull(conns.getSource().getTotal())) {
            // 网络连接
            rtInfo.setConnectionCount(conns.getSource().getTotal());
        }

        if (Objects.nonNull(portsResult.getSource().getTotal())) {
            // 端口
            rtInfo.setPortsCount(portsResult.getSource().getTotal());
        }

        if (Objects.nonNull(processesResult.getSource().getTotal())) {
            // 进程
            rtInfo.setProcessCount(processesResult.getSource().getTotal());
        }

        // 文件
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<CollectionHostEventBO> fileResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            // 文件数量包含其他所有类型，所以这里不会剔除注册表数量
            rtInfo.setFileOpCount(fileResult.getSource().getTotal());
        }

        // 注册表
        hostEventQueryParams.setRuleGroups(CollectionUtil.toList("syscheck_registry"));
        ListObjServiceResult<CollectionHostEventBO> registryResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            // 注册表数量
            rtInfo.setRegistryOpCount(registryResult.getSource().getTotal());
        }

        // todo 文件操作，注册表变动，环境变量变动
        // todo 环境变量
        int envVarChangedCount = sumEnvVarChangedCount(baseRequest);
        rtInfo.setEnvOpCount(envVarChangedCount);

        return rtInfo;
    }

    /**
     * 统计节点当前登录用户数量
     *
     * @param baseSceneElementRequest
     * @return
     */
    private int countNodeSessionUsers(BaseSceneElementRequest baseSceneElementRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(baseSceneElementRequest);
        ServiceResult<List<UserNodeSessionInfo>> result = csceneInstanceService.getNodeSessions(CollectionUtil.toList(baseSceneElementRequest.getSceneId()), baseSceneElementRequest.getElementIds(), null, null);
        if (CollectionUtil.isNotEmpty(result.getSource())) {
            return result.getSource().size();
        } else {
            return 0;
        }
    }

    private ListPageInfoQueryParams buildListPageInfoQueryParams(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(baseRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(baseRequest.getElementIds());
        return listPageInfoQueryParams;
    }


    private LocalDate convertDate(String date) {
        try {
            return TimeUtils.stringToLocalDate(date);
        } catch (Exception e) {
            log.warn("date parse error! {}", date);
        }

        try {
            LocalDateTime localDateTime = TimeUtils.utcToLocalDateTime(date);
            return localDateTime.toLocalDate();
        } catch (Exception e) {
            log.warn("date parse error! {}", date);
        }

        return null;
    }

    private List<ElementHistogramInfo> processHistogramList(LocalDateTime endTime, int bucketSize, int invervalMinutes, Map<String, Long> resultMap) {
        List<ElementHistogramInfo> infos = new ArrayList<>();
        var calculatedTime = LocalDateTime.of(endTime.toLocalDate(), LocalTime.of(endTime.getHour(), endTime.getMinute() / invervalMinutes * invervalMinutes));

        //int minutes = LocalDateTime.now().getMinute() / invervalMinutes * invervalMinutes;
        //int hour = Long.valueOf(Duration.between(LocalDate.now().atTime(LocalTime.MIN), LocalDateTime.now()).getSeconds() / 3600).intValue();
        //
        //var currentTime = LocalTime.of(hour, minutes);

        for (int i = 0; i < bucketSize; i++) {
            String timeStr = TimeUtils.localTime2yyyyMMddHHmm(calculatedTime);
            var info = new ElementHistogramInfo();
            if (Objects.nonNull(resultMap) && Objects.nonNull(resultMap.get(timeStr))) {
                info.setTime(timeStr);
                info.setCount(resultMap.get(timeStr).intValue());
            } else {
                info.setTime(timeStr);
                info.setCount(0);
            }
            infos.add(info);
            calculatedTime = calculatedTime.minusMinutes(invervalMinutes);
        }
        return infos;
    }


    private WazuhEventRequest buildWazuhRequest(ElementMonitorRequest pageRequest) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (StringUtils.isNotBlank(pageRequest.getStartTime())) {
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }

        if (StringUtils.isNotBlank(pageRequest.getEndTime())) {
            endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        }

        if (Objects.isNull(startTime)) {
            String translatedScendId = csceneCommonService.translateSceneId(pageRequest.getSceneId());
            startTime = csceneCommonService.getSceneCreateTime(translatedScendId);
        }

        if (Objects.isNull(startTime)) {
            startTime = LocalDateTime.now().minusHours(24);
        }

        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        wazuhEventRequest.setStartTime(startTime);
        wazuhEventRequest.setEndTime(endTime);
        return wazuhEventRequest;
    }


}
