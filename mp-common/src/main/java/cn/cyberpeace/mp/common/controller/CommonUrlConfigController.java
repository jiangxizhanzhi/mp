package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.bo.MpSysUrlConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.impl.CommonConfigServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/commonConfig")
public class CommonUrlConfigController {

    @Autowired
    private CommonConfigServiceImpl commonConfigService;

    /**
     * 根据key查找服务url
     * @return MpSysUrlConfig
     */
    @ApiOperation("根据key查找服务url")
    @GetMapping("/getUrlByKey")
    public ServiceResult getUrlByKey(@RequestParam(value = "code") String code) {
        MpSysUrlConfig getUrlByKey = commonConfigService.getUrlByKey(code);
        return new ServiceResult(getUrlByKey, BaseErrorCode.SUCCESS).asResult();
    }

}
