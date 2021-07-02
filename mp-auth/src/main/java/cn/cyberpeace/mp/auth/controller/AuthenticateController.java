package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.ServletUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "认证相关接口", tags = "AuthenticateController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/authenticate")
public class AuthenticateController {


    @ApiOperation(value = "检查是否是有效token", notes = "")
    @GetMapping
    public ServiceResult authenticate(@RequestParam String token) {

        JwtUtils.UserInfo user;
        try {
            user = JwtUtils.validateToken(token);
        } catch (TokenExpiredException e) {
            log.info("token已经过期");
            return ServiceResult.builder().serviceCode(AuthErrorCode.TOKEN_EXPIRED).build().asResult();
        }
        if (user == null) {
            log.info("user is null");
            return ServiceResult.builder().serviceCode(AuthErrorCode.AUTHENTICATION_FAILED).build().asResult();
        }

        UserModel userModel = UserUtils.getUser(user.getId());
        return ServletUtils.commonResult(true, userModel);

    }

}
