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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "埋点上报", tags = "埋点上报")
@Slf4j
@RestController
@RequestMapping("/unauth/time")
@AuthTarget(TargetEnum.NO_AUTH)
public class TimeController {

    @Autowired
    private EventTrackingService eventTrackingService;

    @ApiOperation("埋点记录")
    @GetMapping("/get")
    public ServiceResult<String> page(@RequestParam(required = false) EventTrackRequest request) {
        return new ServiceResult<>("2024-08-15T10:10:10Z");
    }
}