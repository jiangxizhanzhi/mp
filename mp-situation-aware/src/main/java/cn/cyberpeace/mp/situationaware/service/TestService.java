package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TestService {
    @Resource
    private DataCollectionService dataCollectionService;

    @Resource
    private SysCollectorService sysCollectorService;

    // expires in 10 minutes
    private TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000);

    /**
     * 查询主机文件变更事件
     * @param request
     * @return
     */
    public ListObjServiceResult<FileEventModel> queryFileModifyEvents(WazuhEventRequest request){
        request.setRuleGroups(SituationalAwareConstant.FILE_MODIFY_GROUP);
        return queryFileEvents(request);
    }

    /**
     * 查询主机注册表变更事件
     * @param request
     * @return
     */
    public ListObjServiceResult<FileEventModel> queryRegistryModifyEvents(WazuhEventRequest request){
        request.setRuleGroups(SituationalAwareConstant.REGISTRY_MODIFY_GROUP);
        return queryFileEvents(request);
    }

    /**
     * 查询主机采集事件
     * @param request
     * @return
     */
    public ListObjServiceResult<FileEventModel> queryFileEvents(WazuhEventRequest request) {
        return new ListObjServiceResult<>(dataCollectionService.queryFileEvents(request)).asResult();
    }

    /**
     * 滚动查询主机采集事件
     * @param request
     * @param requestId 请求id
     * @return
     */
    public ListObjServiceResult<FileEventModel> scrollFileEvents(WazuhEventRequest request, String requestId) {
        String scrollId = null;
        if (StringUtils.isNotBlank(requestId)) {
            String cacheValue = TIMED_CACHE.get(requestId, false);
            if (StringUtils.isNotEmpty(cacheValue)) {
                scrollId = cacheValue;
            }
        }
        ListObjResult<FileEventModel> result;
        if (StringUtils.isEmpty(scrollId)) {
            result = dataCollectionService.queryFileEvents(request);
        } else {
            result = dataCollectionService.scrollFileEvents(scrollId);
        }
        TIMED_CACHE.put(requestId, result.getList().get(0).getScrollId());
        return new ListObjServiceResult<>(result.asResult()).asResult();
    }

    public ListObjServiceResult<WazuhProbeCommonModel> queryCustomProbeData(WazuhEventRequest request, List<String> probeIds) {
        return new ListObjServiceResult<>(dataCollectionService.queryCustomProbeData(request, probeIds).asResult()).asResult();
    }

    /**
     * 分页查询漏洞信息
     * @param request
     * @return
     */
    public ListObjServiceResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(WazuhEventRequest request) {
        request.setRuleGroups(SituationalAwareConstant.VULNERABILITY_GROUP);
        return new ListObjServiceResult<>(dataCollectionService.queryVulnerabilityInfo(request, true).asResult()).asResult();
    }

    /**
     * 查询漏洞top10或安全等级饼状图
     * @param request
     * @param isPie 是否查询饼状图
     * @return
     */
    public ServiceResult<List<WazuhVulnerabilityInfoCountModel>> queryVulnerabilityCountInfo(WazuhEventRequest request, boolean isPie) {
        request.setRuleGroups(SituationalAwareConstant.VULNERABILITY_GROUP);
        if (isPie) {
            return new ServiceResult<>(dataCollectionService.queryVulnerabilityCountInfo(request, false,
                    "data.vulnerability.severity", "severity")).asResult();
        } else {
            return new ServiceResult<>(dataCollectionService.queryVulnerabilityCountInfo(request, true,
                    "data.vulnerability.package.name", "package_name")).asResult();
        }
    }

    /**
     * 查询主机状态信息
     * @param request
     * @return
     */
    public ServiceResult<List<WazuhHostStateModel>> queryHostStateEventsWithTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.queryHostStateEventsWithTimeRange(request)).asResult();
    }

    /**
     * 查询主机网络连接信息
     * @param request
     * @return
     */
    public ServiceResult<List<WazuhNetworkConnectionModel>> queryNetConnEventsWithTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.queryNetConnEventsWithTimeRange(request)).asResult();
    }

    /**
     * 查询主机进程信息
     * @param request
     * @return
     */
    public ServiceResult<List<WazuhProcessModel>> queryProcessesWithTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.queryProcessesWithTimeRange(request)).asResult();
    }

    /**
     * 查询主机用户变更信息
     * @param request
     * @return
     */
    public ServiceResult<List<WazuhSystemUserModel>> queryUserChangeEventWithTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.queryUserChangeEventWithTimeRange(request)).asResult();
    }

    /**
     * 查询主机环境变量变更数量
     * @param request
     * @return
     */
    public ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.sumEnvVarChangedCountByTimeRange(request)).asResult();
    }

    /**
     * 查询主机系统用户变更数量
     * @param request
     * @return
     */
    public ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.sumSysUsersChangedCountByTimeRange(request)).asResult();
    }

    /**
     * 查询主机进程变更数量
     * @param request
     * @return
     */
    public ServiceResult<Integer> sumProcessesChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.sumProcessesChangedCountByTimeRange(request)).asResult();
    }

    /**
     * 查询主机网络连接变更数量
     * @param request
     * @return
     */
    public ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(WazuhMonitorEventsRequest request) {
        return new ServiceResult<>(dataCollectionService.sumNetConnsChangedCountByTimeRange(request)).asResult();
    }

    /**
     * 查询指定时间段内每个主机事件总数
     * @param request
     * @return
     */
    public ServiceResult<Map<String, Long>> aggsCountByAgentNamesBucket(WazuhEventRequest request) {
        return new ServiceResult<>(dataCollectionService.aggsCountByAgentNamesBucket(request)).asResult();
    }

    /**
     * 查询指定节点查询事件总数
     * @param request
     * @return
     */
    public ServiceResult<Long> countByAgentNames(WazuhEventRequest request) {
        return new ServiceResult<>(dataCollectionService.countByAgentNames(request)).asResult();
    }

    /**
     * 查询主机端口信息
     * @param request
     * @return
     */
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest request) {
        var pagePorts = sysCollectorService.pagePorts(request);
        return ResultUtils.pageInfo2ObjServiceResult(pagePorts);
    }

    /**
     * 查询主机网卡信息
     * @param request
     * @return
     */
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request) {
        var pageNetifaces = sysCollectorService.pageNetifaces(request);
        return ResultUtils.pageInfo2ObjServiceResult(pageNetifaces);
    }

    /**
     * 查询主机操作系统信息
     * @param agentName
     * @return
     */
    public ServiceResult<List<OS>> getOS(String agentName) {
        return new ServiceResult<>(sysCollectorService.getOS(agentName));
    }
}
