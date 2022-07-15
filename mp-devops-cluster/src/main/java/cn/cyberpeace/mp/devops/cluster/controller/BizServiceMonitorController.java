package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.request.BizServicePageRequest;
import cn.cyberpeace.mp.devops.cluster.service.BizServiceMonitorService;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 业务服务监控
 * @Author qiaokang
 * @Date：2022/7/15 14:18
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/biz_monitor")
@Api(tags = "业务服务监控")
@RequiredArgsConstructor
public class BizServiceMonitorController {

    private final BizServiceMonitorService bizServiceMonitorService;

    /**
     * 业务服务监控
     * @return
     */
    @ApiOperation("业务服务监控")
    @GetMapping("/page")
    public ListObjResult<ServiceMonitorDto> page(BizServicePageRequest request){
        return bizServiceMonitorService.page(request);
    }



}
