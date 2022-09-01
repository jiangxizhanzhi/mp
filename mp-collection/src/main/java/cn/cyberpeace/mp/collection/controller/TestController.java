package cn.cyberpeace.mp.collection.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.core.model.*;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.core.request.wazuh.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.request.*;
import cn.cyberpeace.mp.collection.service.DataCollectService;
import cn.cyberpeace.mp.collection.service.HostMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wazuh/test")
@Slf4j
public class TestController {
    @Resource(name = "dataCollectServiceImpl")
    private DataCollectService dataCollectService;

    @Resource(name = "hostMonitorServiceImpl")
    private HostMonitorService hostMonitorService;

    @GetMapping("/file_event")
    public ListObjServiceResult<FileEvent> queryFileEvent(DataCollectEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusHours(-4));
        request.setEndTime(LocalDateTime.now().plusHours(-2));
        return dataCollectService.queryFileEvents(request).asResult();
    }

    @GetMapping("/file_modify")
    public ListObjServiceResult<FileEvent> queryFileModify(SyscheckEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.queryFileModifyEvents(request).asResult();
    }

    @GetMapping("/registry_modify")
    public ListObjServiceResult<FileEvent> queryFileEvent(SyscheckEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.queryRegistryModifyEvents(request).asResult();
    }

    @GetMapping("/scroll_event")
    public ListObjServiceResult<FileEvent> scrollFileEvent(DataCollectEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.scrollFileEvents(request).asResult();
    }

    @GetMapping("/custom_probe")
    public ListObjServiceResult<WazuhProbeCommon> queryCustomProbeData(CustomDataQueryRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.queryCustomProbeData(request).asResult();
    }

    @GetMapping("/vulnerability_info")
    public ListObjServiceResult<WazuhVulnerabilityInfo> queryVulnerabilityInfo(VulnerabilityEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.queryVulnerabilityInfo(request).asResult();
    }

    @GetMapping("/vulnerability_count")
    public ServiceResult<List<WazuhVulnerabilityInfoCount>> queryVulnerabilityCountInfo(VulnerabilityEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.queryVulnerabilityCountInfo(request).asResult();
    }

    @GetMapping("/count_bucket")
    public ServiceResult<Map<String, Long>> countByAgentNamesBucket(BaseEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.countByAgentNamesBucket(request).asResult();
    }

    @GetMapping("/count_by_agent")
    public ServiceResult<Long> countByAgentNames(BaseEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.countByAgentNames(request).asResult();
    }

    @GetMapping("/count_rule_id")
    public ServiceResult<Map<String, Long>> aggsRuleIdsByAgentName(BaseEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.aggsRuleIdsByAgentName(request).asResult();
    }

    @GetMapping("/delete")
    public ServiceResult<Long> deleteDocByAgentNames(BaseEventRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return dataCollectService.deleteDocByAgentNames(request).asResult();
    }

    @GetMapping("/port")
    public ListObjServiceResult<Port> deleteDocByAgentNames(CollectorBaseRequest request) {
        return hostMonitorService.pagePorts(request).asResult();
    }

    @GetMapping("/netifaces")
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest request) {
        return hostMonitorService.pageNetifaces(request).asResult();
    }

    @GetMapping("/os")
    public ServiceResult<List<OS>> getOS(String agentName) {
        return hostMonitorService.getOS(agentName).asResult();
    }

    @GetMapping("/host_state")
    public ListObjServiceResult<WazuhHostState> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.queryHostStateEventsWithTimeRange(request).asResult();
    }

    @GetMapping("/net")
    public ListObjServiceResult<WazuhNetworkConnection> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.queryNetConnEventsWithTimeRange(request).asResult();
    }

    @GetMapping("/process")
    public ListObjServiceResult<WazuhProcess> queryProcessesWithTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.queryProcessesWithTimeRange(request).asResult();
    }

    @GetMapping("/user")
    public ListObjServiceResult<WazuhSystemUser> querySystemUserWithTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.querySystemUserWithTimeRange(request).asResult();
    }

    @GetMapping("/sum_env")
    public ServiceResult<Integer> sumEnvVarChangedCountByTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.sumEnvVarChangedCountByTimeRange(request).asResult();
    }

    @GetMapping("/sum_user")
    public ServiceResult<Integer> sumSysUsersChangedCountByTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.sumSysUsersChangedCountByTimeRange(request).asResult();
    }

    @GetMapping("/sum_process")
    public ServiceResult<Integer> sumProcessesChangedCountByTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.sumProcessesChangedCountByTimeRange(request).asResult();
    }

    @GetMapping("/sum_net")
    public ServiceResult<Integer> sumNetConnsChangedCountByTimeRange(HostMonitorPageRequest request) {
        request.setStartTime(LocalDateTime.now().plusDays(-2));
        request.setEndTime(LocalDateTime.now());
        return hostMonitorService.sumNetConnsChangedCountByTimeRange(request).asResult();
    }
}
