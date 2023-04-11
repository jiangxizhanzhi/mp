package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.ProbeOperateBO;
import cn.cyberpeace.mp.cscene.bo.ProbeOperateTimeRange;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneMonitorInfo;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.ProbeOperateRequest;
import cn.cyberpeace.mp.cscene.response.ProbeEventState;
import cn.cyberpeace.mp.cscene.service.constant.ProbeOperateConstant;
import cn.cyberpeace.mp.cscene.service.constant.ProbeOperateConstant.PROBEOPERATE_TYPE;
import cn.cyberpeace.mp.cscene.service.param.ProbeOperateQueryParams;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.cscene.service.constant.ProbeOperateConstant.*;

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
    private ProbeOperateLogService probeOperateLogService;

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
        if (Objects.isNull(fetchRealTimeHostState)) {
            fetchRealTimeHostState = true;
        }
        log.info("sceneId:{}, fetchRealTimeHostState:{}", sceneId, fetchRealTimeHostState);
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
            var finalFetchRealTimeHostState = fetchRealTimeHostState;
            var cf = CompletableFuture.supplyAsync(() -> processElementBasicInfo(csceneInstanceBO, sceneId, nodeId, finalFetchRealTimeHostState));
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

    /**
     * @description: 查询当前探针开关记录信息
     * @param sceneId 场景id
     * @throws
     * @return ProbeEventState
     */
    public ProbeEventState queryProbeInfo(String sceneId) {
        Map<String, ProbeOperateBO> newestMap = getNewestProbeOperateMap(sceneId);
        if(newestMap.isEmpty()){
            return null;
        }
        ProbeEventState probeEventState = ProbeEventState.builder()
            .changeEventState(newestMap.get(PROBEOPERATE_TYPE.CHANGE_EVENT_STATE.getValue()).getOperate())
            .originLog(newestMap.get(PROBEOPERATE_TYPE.ORIGIN_LOG.getValue()).getOperate())
            .logEvent(newestMap.get(PROBEOPERATE_TYPE.LOG_EVENT.getValue()).getOperate())
            .customCollection(newestMap.get(PROBEOPERATE_TYPE.CUSTOM_COLLECTION.getValue()).getOperate()).build();
        return probeEventState;

    }

    /**
     * @description: 记录开启或关闭探针（与最近一次开关状态完全相同不记录）
     * @param request 探针操作请求
     * @return boolean 更新结果
     */
    public boolean operateProbe(ProbeOperateRequest request) {
        Map<String, ProbeOperateBO> newestMap = getNewestProbeOperateMap(request.getSceneId());
        //ProbeOperateRequest转换为批量处理的业务list
        List<ProbeOperateBO> probeOperateBOList = request.transFormToProbeOperateBO();
        //比较最近操作和最新请求是否有变化,获取变化的记录
        if(!newestMap.isEmpty()){
            probeOperateBOList = probeOperateBOList.stream().filter(
                    probeOperateBO -> newestMap.get(probeOperateBO.getType()).getOperate() != probeOperateBO.getOperate())
                .collect(Collectors.toList());
        }
        //只新增变化的记录
        return probeOperateLogService.operateProbeBatchInsert(probeOperateBOList);
    }


    /**
     * @description: 通过场景id查询出最近的操作开关操作记录
     * @param sceneId 场景id
     * @return Map<String,ProbeOperateBO> 操作类型，ProbeOperateBO对象
     */
    public Map<String,ProbeOperateBO> getNewestProbeOperateMap(String sceneId){
        //构建查询参数
        ProbeOperateQueryParams queryParams =
            ProbeOperateQueryParams.builder().scene(sceneId).build();
        //获取对应场景所有操作记录
        List<ProbeOperateBO> operateList = probeOperateLogService.queryOperateProbe(queryParams);
        //处理获取最近探针操作所有类型的记录map
        return operateList.stream().collect(
            Collectors.groupingBy(ProbeOperateBO::getType, Collectors.collectingAndThen(
                Collectors.reducing((p1, p2) -> p1.getCreateTime().isAfter(p2.getCreateTime()) ? p1 : p2),
                Optional::get)));
    }

    /**
     * @description: 查询探针开关列表信息
     * @param queryParams 
     * @throws 
     * @return List<ProbeOperateBO>
     */
    public List<ProbeOperateBO> queryProbeOperateLog(ProbeOperateQueryParams queryParams){
        return probeOperateLogService.queryOperateProbe(queryParams);
    }



    /**
     * @description: 查询探针开关操作时间段
     * @param queryParams
     * @throws 
     * @return List<ProbeOperateTimeRange>
     */
    public List<ProbeOperateTimeRange> queryProbeOperateTimeRange(ProbeOperateQueryParams queryParams){
        List<ProbeOperateBO> probeOperateBOList = queryProbeOperateLog(queryParams);
        if(CollectionUtils.isEmpty(probeOperateBOList)){
            return new ArrayList<>();
        }
        //影响核心逻辑的关键在于第一条是1（开），还是0（关），以及记录数num
        ProbeOperateBO probeOperateBO = probeOperateBOList.get(0);
        if(ProbeOperateConstant.ONE_NUM == probeOperateBOList.size() && probeOperateBO.getOperate().equals(OPEN_SWITCH)){
            return Collections.singletonList(new ProbeOperateTimeRange(minTime,LocalDateTime.now()));
        }
        return handlerTimeRangeByList(probeOperateBOList);
    }

    /**
     * @description: 处理探针开关操作列表获取所有时间段
     * @param probeOperateBOList 
     * @throws
     * @return List<ProbeOperateTimeRange>
     */
    private List<ProbeOperateTimeRange> handlerTimeRangeByList(List<ProbeOperateBO> probeOperateBOList) {
        List<ProbeOperateTimeRange> timeRangeList = new ArrayList<>();
        LocalDateTime fromTime = null;
        LocalDateTime toTime = null;
        for (int i = 0; i < probeOperateBOList.size(); i++) {
            ProbeOperateBO bo = probeOperateBOList.get(i);
            if(i==0){
                fromTime = minTime;
                if(CLOSE_SWITCH==bo.getOperate()){
                    toTime = bo.getCreateTime();
                }
            }else{
                if(!Objects.isNull(fromTime) && CLOSE_SWITCH==bo.getOperate()){
                    toTime = bo.getCreateTime();
                }else if(Objects.isNull(fromTime) && OPEN_SWITCH==bo.getOperate()){
                    fromTime = bo.getCreateTime();
                }else{
                    //do nothing
                }
                //最后记录
                if(i==probeOperateBOList.size()-1 && OPEN_SWITCH==bo.getOperate()){
                    toTime = LocalDateTime.now();
                }
            }
            if(!Objects.isNull(fromTime) && !Objects.isNull(toTime)){
                timeRangeList.add(new ProbeOperateTimeRange(fromTime,toTime));
                fromTime = null;
                toTime = null;
            }
        }
        return timeRangeList;
    }

}
