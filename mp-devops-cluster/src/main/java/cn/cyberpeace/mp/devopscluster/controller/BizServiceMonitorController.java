package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.request.BizServicePageRequest;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/biz_monitor")
@Api(tags = "业务服务监控")
@RequiredArgsConstructor
public class BizServiceMonitorController {

    private final ServiceMonitorService serviceMonitorService;

    
    @ApiOperation("业务服务监控")
    @GetMapping("/page")
    public ServiceResult<ListObjResult<ServiceMonitorDto>> page(BizServicePageRequest request){
        ListObjResult<ServiceMonitorDto> result = serviceMonitorService.bizServicePage(request);
        return ResultCommon.success(result);
    }

}
