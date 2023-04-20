package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.response.SingleIpResponse;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "配置查询", tags = "配置查询")
@Slf4j
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Resource
    private CloudAppConfig config;

    @ApiOperation("查询单ip配置")
    @GetMapping(value = "/single_ip")
    public ServiceResult<SingleIpResponse> getSingIpConfig() {
        SingleIpResponse response = new SingleIpResponse();
        response.setPublicNetProxy(config.getPublicNetProxy());
        response.setPublicNetProxyIp(config.getPublicNetProxyIp());
        return new ServiceResult<>(response).asResult();
    }

}
