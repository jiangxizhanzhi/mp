package cn.cyberpeace.mp.front.admin.controller;

import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
import cn.cyberpeace.mp.front.admin.service.sys.SysCtlService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/1/5 10:44
 */
@ApiIgnore
@Api(value = "SYS-ADMIN-TEST", tags = "ADMIN-TEST")
@Slf4j
@RestController
@RequestMapping("/sys/admin/test")
public class AdminTestController {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SysCtlService sysCtlService;


}
