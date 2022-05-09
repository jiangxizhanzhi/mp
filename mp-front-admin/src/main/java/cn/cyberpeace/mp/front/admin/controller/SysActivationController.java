package cn.cyberpeace.mp.front.admin.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.front.admin.service.sys.SysActivationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        log.info("{}", activationRequest);
        activationService.activate(activationRequest);
        return ServiceResult.successful();
    }

    /*
    @ApiIgnore
    @ApiOperation("(生成校验码之前)查询授权信息，包括机器码，自定义授权信息，授权月数")
    @PostMapping("/verifyinfo")
    public ServiceResult getVerifyInfo(@RequestBody @Validated VerifyInfoRequest verifyInfoRequest) {
        log.info("{}", verifyInfoRequest);
        String customizedVerifyJSONInfo = activationService.generateCustomizedVerifyJSONInfo(verifyInfoRequest.getModuleItems(), verifyInfoRequest.getResourceItems());
        String sysUUID = activationService.querySysUUID();
        Integer deadline = verifyInfoRequest.getDeadline();

        VerifyInfoResponse verifyInfoResponse = VerifyInfoResponse.builder()
                .verifyInfo(customizedVerifyJSONInfo)
                .deadline(deadline)
                .machineCode(sysUUID)
                .edition(verifyInfoRequest.getEdition())
                .build();

        log.info("verifyInfoResponse:{}", verifyInfoResponse);

        return ServiceResult.success(verifyInfoResponse);
    }
    */

    @ApiOperation("查询激活状态")
    @GetMapping("/status")
    public ServiceResult<ActivationResponse> status() {
        ActivationResponse content = activationService.status();
        if (Objects.isNull(content)) {
            return ServiceResult.fail(AdminErrorCode.SYS_ACTIVATION_NONE);
        }
        return ServiceResult.success(content);
    }


    @ApiOperation("查询激活配置项")
    @GetMapping("/config_value")
    public ServiceResult<String> getConfigValue(@RequestParam String key) {
        String value = activationService.getValueByKey(key);

        return ServiceResult.success(value);
    }


    @ApiOperation("查询机器序列号")
    @GetMapping("/machinecode")
    public ServiceResult getMachineCode() {
        String code = activationService.querySysUUID();
        return ServiceResult.success(code);
    }


}
