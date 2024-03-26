package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;

import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.BaseResponse;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.fst.Util;
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
    public ListObjServiceResult getMicrosoftAzureUserList(@RequestParam(value = "per_page", defaultValue = "10") Integer pageSize,
                                                          @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                                          @RequestParam(value = "cloudType", required = false) String cloudType) {
        JSONObject requestData = new JSONObject();
        requestData.put("cloudType", cloudType);
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        return microsoftAzureLoginService.getMicrosoftAzureUserList(requestData).asResult();
    }

    @ApiOperation(value = "获取当前云类型账号信息", notes = "")
    @GetMapping("/getLocalAzurer")
    public ServiceResult<MicrosoftAzureUserModel> getLocalAzurer(@RequestParam(value = "cloudType", required = true) String cloudType) {
        JSONObject requestData = new JSONObject();
        requestData.put("cloudType", cloudType);
        MicrosoftAzureUserModel localAzurer = microsoftAzureLoginService.getLocalAzurer(requestData);
        return new ServiceResult<>(localAzurer).asResult();
    }
}
