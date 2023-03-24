package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.request.EventTrackRequest;
import cn.cyberpeace.mp.aggregation.service.EventTrackingService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "埋点上报", tags = "埋点上报")
@Slf4j
@RestController
@RequestMapping("/unauth/event_tracking")
public class EventTrackingController {

    @Autowired
    private EventTrackingService eventTrackingService;

    @ApiOperation("埋点记录")
    @PostMapping("/record")
    public ServiceResult<Boolean> page(@RequestBody EventTrackRequest request) {
        eventTrackingService.save(request);
        return new ServiceResult<>(true).asResult();
    }
}