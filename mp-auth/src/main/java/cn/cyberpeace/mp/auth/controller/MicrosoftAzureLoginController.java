package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
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
    public ListObjServiceResult getMicrosoftAzureUserList(@RequestParam(value = "per_page", defaultValue = "10") Integer pageSize,
                                                          @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                                          @RequestParam(value = "cloud_type", required = false) String cloudType) {
        JSONObject requestData = new JSONObject();
        requestData.put("cloudType", cloudType);
        requestData.put("pageNum", pageNum);
        requestData.put("pageSize", pageSize);
        return microsoftAzureLoginService.getMicrosoftAzureUserList(requestData).asResult();
    }

    @ApiOperation(value = "获取当前云类型账号信息", notes = "")
    @GetMapping("/getLocalAzurer")
    public ServiceResult<MicrosoftAzureUserModel> getLocalAzurer(@RequestParam(value = "cloud_type") String cloudType) {
        JSONObject requestData = new JSONObject();
        requestData.put("cloudType", cloudType);
        MicrosoftAzureUserModel localAzurer = microsoftAzureLoginService.getLocalAzurer(requestData);
        return new ServiceResult<>(localAzurer).asResult();
    }

    @ApiOperation(value = "删除微软云用户", notes = "")
    @DeleteMapping("/deleteAzurer")
    public ServiceResult deleteAzurer(@RequestBody MicrosoftAzureUserModel requestData) {
        Boolean deleteAzurer = null;
        if (requestData != null && requestData.getPk() != null && !requestData.getPk().equals("")) {
            deleteAzurer = microsoftAzureLoginService.deleteAzurer(requestData.getPk().toString());
            if (deleteAzurer) {
                return new ServiceResult<>(null, BaseErrorCode.SUCCESS,"删除用户成功").asResult();
            }
        }
        return new ServiceResult<>(null, BaseErrorCode.ERROR,"删除用户失败").asResult();
    }

    @ApiOperation(value = "修改账号信息", notes = "")
    @PutMapping("/updateLocalAzurer")
    public ServiceResult updateLocalAzurer(@RequestBody MicrosoftAzureUserModel requestData) {
        ServiceResult updateLocalAzurer = microsoftAzureLoginService.updateLocalAzurer(requestData);
        return updateLocalAzurer;
    }

    @ApiOperation(value = "采集服务器地址修改", notes = "")
    @PutMapping("/updateWazuhManagerIp")
    public ServiceResult updateWazuhManagerIp(@RequestBody MicrosoftAzureUserModel requestData) {
        ServiceResult updateLocalAzurer = microsoftAzureLoginService.updateWazuhManagerIp(requestData);
        return updateLocalAzurer;
    }
}
