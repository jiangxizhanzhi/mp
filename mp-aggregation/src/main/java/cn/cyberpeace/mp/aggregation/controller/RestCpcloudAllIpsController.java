package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.service.RestCpcloudAllIpsService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.OriginIpsResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ResetCplcoudAllIpsData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(value = "重置云平台ip接口", tags = "重置云平台ip接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/environment")
public class RestCpcloudAllIpsController {
    @Resource
    private RestCpcloudAllIpsService modelService;

    @Resource
    private CloudAppConfig config;

    @ApiOperation("删除所有运行中的场景和靶标示例")
    @DeleteMapping("/clear_all")
    public ServiceResult<Boolean> clearAll() {
        modelService.clearAll();
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("重新设置环境所有Ip")
    @PostMapping("/reset_all_ip")
    public ServiceResult<Boolean> resetAllIp(@RequestBody ResetCplcoudAllIpsData request) {
        
        if(Boolean.TRUE.equals(config.getPublicNetProxy())){
            return new ServiceResult<>(false);
        }
        return modelService.resetAllIps(request);
    }

    @ApiOperation("获得原始的所有Ip")
    @GetMapping("/get_origin_ip")
    public ServiceResult<OriginIpsResult> gerOriginIp() {
        return new ServiceResult<>(modelService.getOriginIps()).asResult();
    }
}
