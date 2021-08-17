package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "系统激活管理接口", tags = "AUTH-系统激活管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/activation")
public class SysActivationController {


    @ApiOperation("激活")
    @PostMapping
    public ServiceResult activate(@RequestBody @Validated ActivationRequest activationRequest) {

        String verifyStr = JSON.toJSONString(activationRequest);

        return null;
    }


    @ApiOperation("查询激活状态")
    @GetMapping
    public ServiceResult status() {

        return null;
    }



}
