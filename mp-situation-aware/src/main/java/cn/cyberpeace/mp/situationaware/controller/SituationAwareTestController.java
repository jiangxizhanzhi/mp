package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.ServiceMonitorDataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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

    @Autowired
    private ServiceMonitorDataCollectionService serviceMonitorDataCollectionService;



}
