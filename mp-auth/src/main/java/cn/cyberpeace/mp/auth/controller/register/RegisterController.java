package cn.cyberpeace.mp.auth.controller.register;


import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.register.RegisterRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.RegisterService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(value = "注册接口", tags = "AUTH-注册接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;


    @ApiOperation(value = "获取注册短信", notes = "")
    @GetMapping("/sms")
    public ServiceResult sms(@RequestParam String mobile) {
        log.info("mobile:{}", mobile);

        return ServiceResult.builder().message("OK").data("no sms support!").serviceCode(BaseErrorCode.SUCCESS).build();

    }

    @ApiOperation(value = "注册接口", notes = "")
    @PostMapping
    public ServiceResult register(@RequestBody @Validated RegisterRequest req) {
        log.info("req:{}", req);

        String encryptedPwd = req.getPassword();
        req.setPassword(PasswordUtils.desDecrypt(encryptedPwd));
        ObjServiceResult<UserModel> result = registerService.register(req);

        if (result.getSource() != null) {
            UserModel userModel = result.getSource().getObj();
            JwtUtils.addResponseHeaderWithJWT(userModel);
            return ResultUtils.commonResult(true, userModel);
        }

        return ResultUtils.commonResult(false, null);
    }

}
