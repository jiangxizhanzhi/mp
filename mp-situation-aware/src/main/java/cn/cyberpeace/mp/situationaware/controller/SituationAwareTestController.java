package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Hardware;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/21 17:37
 */
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

    @GetMapping("/token")
    public String getToken() throws IOException, ExecutionException {
        String token = SituationalAwareConstant.wazuhToken.get("wazuhToken");
        return token;
    }

    @GetMapping("/info")
    public Map<String, Object> agents(@RequestParam(required = false) String agentName) throws IOException {
        Map<String, Object> map = new HashMap<>();
        List<Agent> agents = agentService.listAgents(new AgentRequest(agentName));
        log.info("{}", agents);
        map.put("agents", agents);

        List<Port> ports = sysCollectorService.listPorts(new CollectorBaseRequest(agentName));
        log.info("{}", ports);
        map.put("ports", ports);

        List<Process> processes = sysCollectorService.listProcesses(new CollectorBaseRequest(agentName));
        log.info("{}", processes);
        map.put("processes", processes);


        List<Hardware> hardwares = sysCollectorService.getHardware(agentName);
        log.info("{}", hardwares);
        if (CollectionUtil.isNotEmpty(hardwares)) {
            map.put("hardware", hardwares.get(0));
        }

        return map;
    }



}
