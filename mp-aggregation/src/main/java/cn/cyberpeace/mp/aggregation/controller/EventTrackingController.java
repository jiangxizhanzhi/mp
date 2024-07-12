 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventTrackingController.java.
 // File size 1513.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.request.EventTrackRequest;
import cn.cyberpeace.mp.aggregation.service.EventTrackingService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName EventTrackingController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "埋点上报", tags = "埋点上报")
@Slf4j
@RestController
@RequestMapping("/unauth/event_tracking")
@AuthTarget(TargetEnum.NO_AUTH)
public class EventTrackingController {

    @Autowired
    private EventTrackingService eventTrackingService;

    /**
     * 埋点记录
     *
     * @param request
     * @return
     */
    @ApiOperation("埋点记录")
    @PostMapping("/record")
    public ServiceResult<Boolean> page(@RequestBody EventTrackRequest request) {
        eventTrackingService.save(request);
        return new ServiceResult<>(true).asResult();
    }
}