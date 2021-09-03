package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.auth.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.auth.service.sys.SysActivationService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @Description: 系统激活管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "系统激活管理接口", tags = "AUTH-系统激活管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/activation")
public class SysActivationController {

    @Autowired
    private SysActivationService activationService;


    @ApiOperation("激活")
    @PostMapping
    public ServiceResult activate(@RequestBody @Validated ActivationRequest activationRequest) {
        activationService.activate(activationRequest);
        return ServiceResult.successful();
    }


    @ApiOperation("查询激活状态")
    @GetMapping("/status")
    public ServiceResult<ActivationResponse> status() {
        ActivationResponse content = activationService.status();
        if (Objects.isNull(content)) {
            return ServiceResult.fail("未查询到激活状态信息！");
        }
        return ServiceResult.success(content);
    }



}
