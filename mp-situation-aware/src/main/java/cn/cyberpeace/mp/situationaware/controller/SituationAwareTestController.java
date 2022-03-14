package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
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
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
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


    @GetMapping("/hardware")
    public ServiceResult info(@RequestParam(required = false) String agentName) throws IOException {
        List<Hardware> hardwares = sysCollectorService.getHardware(agentName);
        log.info("{}", hardwares);

        return ServiceResult.success(hardwares);
    }


    @GetMapping("/page/agents")
    public ListObjServiceResult<Agent> agents(AgentRequest agentRequest) {
        PageInfo<Agent> agentPageInfo = agentService.pageAgents(agentRequest);

        return ResultUtils.pageInfo2ObjServiceResult(agentPageInfo);

    }



}
