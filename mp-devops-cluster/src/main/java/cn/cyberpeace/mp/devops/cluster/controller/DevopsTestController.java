package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.service.CpNodeMonitorScriptConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 14:00
 */
@RestController
public class DevopsTestController {

    @Autowired
    private CpNodeMonitorScriptConfigService monitorScriptConfigService;


    @PostMapping("/devops/monitor/init")
    public void test() {
        monitorScriptConfigService.init();
    }


}
