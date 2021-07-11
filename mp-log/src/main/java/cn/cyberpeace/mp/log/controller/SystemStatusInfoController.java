package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.service.ISystemStatusInfo;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.thread.taskpool.ThreadPoolInfo;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.request.LogLevelBatchRequest;
import cn.cyberpeace.mp.log.response.LogLevelResponse;
import cn.cyberpeace.mp.log.service.ILogSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/system")
@Api(tags = "系统运行状态信息")
public class SystemStatusInfoController {

    @Autowired
    private ISystemStatusInfo systemStatusInfo;

    
    @GetMapping("/threadpool/info")
    @ApiOperation(value = "获取线程池状态信息")
    public ServiceResult getLogLevelList() {
        return new ServiceResult<>(systemStatusInfo.getThreadPoolInfos()).asResult();
    }

}
