package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.BaseResponse;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 微软云账号信息管理
 */
@Api(value = "微软云账号信息管理", tags = "AUTH-微软云账号信息管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/microsoftAzureLogin")
public class MicrosoftAzureLoginController {

    @Autowired
    private MicrosoftAzureLoginService microsoftAzureLoginService;

    @ApiOperation(value = "微软云账号信息管理列表", notes = "")
    @GetMapping("/list")
    public BaseResponse getMicrosoftAzureUserList(@RequestBody(required = false) JSONObject requestData) {
        return microsoftAzureLoginService.getMicrosoftAzureUserList(requestData);
    }
}
