package cn.cyberpeace.mp.front.business.controller.register;


import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.register.RegisterRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.RegisterService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 注册接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "注册接口", tags = "AUTH-注册接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @ApiIgnore
    @ApiOperation(value = "获取注册短信", notes = "")
    @GetMapping("/sms")
    public ServiceResult sms(@RequestParam String mobile) {
        log.info("mobile:{}", mobile);

        return ServiceResult.builder().message("OK").data("no sms support!").serviceCode(BaseErrorCode.SUCCESS).build();
    }

    @ApiOperation(value = "注册接口", notes = "")
    @PostMapping
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "注册新用户", target = AuthLogConstant.TARGET_USER)
    public ServiceResult<UserModel> register(@RequestBody @Validated RegisterRequest req) {
        log.info("req:{}", req);
        String encryptedPwd = req.getPassword();
        req.setPassword(PasswordUtils.desDecrypt(encryptedPwd));
        ObjServiceResult<UserModel> result = registerService.register(req);

        if (result.getSource() != null) {
            UserModel userModel = result.getSource().getObj();
            JwtUtils.addResponseHeaderWithJWT(userModel);
            return ServiceResult.success(userModel);
        }

        return ServiceResult.fail("注册失败！");
    }

}
