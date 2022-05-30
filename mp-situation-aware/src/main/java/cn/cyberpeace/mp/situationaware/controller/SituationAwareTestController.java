package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Hardware;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/21 17:37
 */
@ApiIgnore
@Api(value = "SITUATION-TEST", tags = "SITUATION-TEST")
@Slf4j
@RestController
@RequestMapping("/situation/test")
public class SituationAwareTestController {

    @Autowired
    private WazuhRepository wazuhRepository;

    @Autowired
    private AgentService agentService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @GetMapping("/token")
    public String getToken() throws IOException, ExecutionException {
        String token = SituationalAwareConstant.wazuhToken.get("wazuhToken");
        return token;
    }

    @GetMapping("/page/ports")
    public ListObjServiceResult<Port> pagePorts(@RequestParam(required = false) String agentName) throws IOException {
        PageInfo<Port> portsPageInfo = sysCollectorService.pagePorts(new CollectorBaseRequest(agentName));
        return ResultUtils.pageInfo2ObjServiceResult(portsPageInfo);
    }


    @GetMapping("/page/processes")
    public ListObjServiceResult<Port> pageProcesses(@RequestParam(required = false) String agentName) throws IOException {
        PageInfo<Process> processesPageInfo = sysCollectorService.pageProcesses(new CollectorBaseRequest(agentName));
        return ResultUtils.pageInfo2ObjServiceResult(processesPageInfo);

    }

    @GetMapping("/page/netiface")
    public ListObjServiceResult pageNetifaces(@RequestParam(required = false) String agentName) throws IOException {
        PageInfo<Netiface> pageInfo = sysCollectorService.pageNetifaces(new CollectorBaseRequest(agentName));
        log.info("{}", pageInfo);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @GetMapping("/hardware")
    public ServiceResult info(@RequestParam(required = false) String agentName) throws IOException {
        List<Hardware> hardwares = sysCollectorService.getHardware(agentName);
        log.info("{}", hardwares);

        return ServiceResult.success(hardwares);
    }


    @GetMapping("/os")
    public ServiceResult osifo(@RequestParam(required = false) String agentName) throws IOException {
        List<OS> osList = sysCollectorService.getOS(agentName);
        log.info("{}", osList);

        return ServiceResult.success(osList);
    }


    @GetMapping("/page/agents")
    public ListObjServiceResult<Agent> agents(AgentRequest agentRequest) {
        PageInfo<Agent> agentPageInfo = agentService.pageAgents(agentRequest);

        return ResultUtils.pageInfo2ObjServiceResult(agentPageInfo);

    }

    @GetMapping("/delete/byAgentNames")
    public void deleteEsDocByAgentNames() {

        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(Lists.newArrayList("fa96d5f879614115a48ea0a9a7b7c315", "46017f42971e403f88d17d51b07a74de", "aaaa"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhEventRequest.setStartTime(LocalDateTime.parse("2022-03-15 16:00:00", formatter));
        wazuhEventRequest.setEndTime(LocalDateTime.parse("2022-03-17 10:15:00", formatter));
        Long updateCount = dataCollectionService.deleteDocByAgentNames(wazuhEventRequest);
    }

    @GetMapping("/count/byAgentNames")
    public void countByAgentNames() {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(Lists.newArrayList("ceea0967056e417193fb8883f27e7186", "46017f42971e403f88d17d51b07a74de", "aaaa"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhEventRequest.setStartTime(LocalDateTime.parse("2022-03-11 16:00:00", formatter));
        wazuhEventRequest.setEndTime(LocalDateTime.parse("2022-03-17 10:15:00", formatter));
        Map<String, Long> countMap = dataCollectionService.aggsCountByAgentNamesBucket(wazuhEventRequest);
        long count = dataCollectionService.countByAgentNames(wazuhEventRequest);
        System.out.println(count);
    }


    @GetMapping("/query/hoststate")
    public ServiceResult queryHostState(@RequestParam String agentName) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList("f7fb5620314a4a659ca4b82fd60a0da5"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhRequest.setStartTime(LocalDateTime.parse("2022-03-24 16:00:00", formatter));
        wazuhRequest.setEndTime(LocalDateTime.parse("2022-03-26 10:15:00", formatter));
        wazuhRequest.setPageSize(1);
        List<WazuhHostStateModel> list = dataCollectionService.queryHostStateEventsWithTimeRange(wazuhRequest);
        return ServiceResult.success(list);
    }


    @GetMapping("/query/net_conns")
    public ServiceResult queryNetConns(@RequestParam String agentName) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList("f7fb5620314a4a659ca4b82fd60a0da5"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhRequest.setStartTime(LocalDateTime.parse("2022-03-24 16:00:00", formatter));
        wazuhRequest.setEndTime(LocalDateTime.now());
        wazuhRequest.setPageSize(1);
        List<WazuhNetworkConnectionModel> list = dataCollectionService.queryNetConnEventsWithTimeRange(wazuhRequest);
        return ServiceResult.success(list);
    }


    @GetMapping("/query/changecnt/envvar")
    public ServiceResult queryEnvVar(@RequestParam List<String> agentNames) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(agentNames);
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        wazuhRequest.setStartTime(now.minusHours(10));
        wazuhRequest.setEndTime(LocalDateTime.now());
        wazuhRequest.setPageSize(1);
        dataCollectionService.sumEnvVarChangedCountByTimeRange(wazuhRequest);
        return ServiceResult.successful();
    }

    @GetMapping("/query/changecnt/processes")
    public ServiceResult queryProcessesCnt(@RequestParam List<String> agentNames) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(agentNames);
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        wazuhRequest.setStartTime(now.minusHours(10));
        wazuhRequest.setEndTime(LocalDateTime.now());
        wazuhRequest.setPageSize(1);
        dataCollectionService.sumProcessesChangedCountByTimeRange(wazuhRequest);
        return ServiceResult.successful();
    }


    @GetMapping("/query/changecnt/users")
    public ServiceResult queryUsersChangedCnt(@RequestParam List<String> agentNames) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(agentNames);
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        wazuhRequest.setStartTime(now.minusHours(10));
        wazuhRequest.setEndTime(LocalDateTime.now());
        wazuhRequest.setPageSize(1);
        dataCollectionService.sumSysUsersChangedCountByTimeRange(wazuhRequest);
        return ServiceResult.successful();
    }


    @GetMapping("/query/vulnerability_info")
    public ServiceResult queryVulnerabilityInfo(@RequestParam String agentName) {
        WazuhEventRequest wazuhRequest = new WazuhEventRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList("test_controller"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhRequest.setStartTime(LocalDateTime.parse("2022-04-14 16:00:00", formatter));
        wazuhRequest.setEndTime(LocalDateTime.now());
        wazuhRequest.setPageSize(1);
        wazuhRequest.setRuleGroups(CollectionUtil.toList("vulnerability-detector"));
        dataCollectionService.queryVulnerabilityInfo(wazuhRequest, true);
        dataCollectionService.queryVulnerabilityInfo(wazuhRequest, false);
        return ServiceResult.successful();
    }

    @GetMapping("/query/vulnerability_count_info")
    public ServiceResult queryVulnerabilityCountInfo(@RequestParam String agentName) {
        WazuhEventRequest wazuhRequest = new WazuhEventRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList("test_controller"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhRequest.setStartTime(LocalDateTime.parse("2022-04-14 16:00:00", formatter));
        wazuhRequest.setEndTime(LocalDateTime.now());
        wazuhRequest.setPageSize(1);
        wazuhRequest.setRuleGroups(CollectionUtil.toList("vulnerability-detector"));
        dataCollectionService.queryVulnerabilityCountInfo(wazuhRequest, false, "data.vulnerability.package.name", "package_name");
        return ServiceResult.successful();
    }

}
