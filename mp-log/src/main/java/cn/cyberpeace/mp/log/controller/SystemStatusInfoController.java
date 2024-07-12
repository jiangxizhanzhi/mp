 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemStatusInfoController.java.
 // File size 1430.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.ISystemStatusInfo;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName LogDynamicSettingController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/system")
@Api(tags = "系统运行状态信息")
@AuthTarget(TargetEnum.NO_AUTH)
public class SystemStatusInfoController {

    @Autowired
    private ISystemStatusInfo systemStatusInfo;

    /**
     * 获取日志级别列表
     *
     * @return 日志级别信息
     */
    @GetMapping("/threadpool/info")
    @ApiOperation(value = "获取线程池状态信息")
    public ServiceResult getLogLevelList() {
        return new ServiceResult<>(systemStatusInfo.getThreadPoolInfos()).asResult();
    }

}
