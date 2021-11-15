package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 认证接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "认证相关接口", tags = "AUTH-认证相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/authenticate")
public class AuthenticateController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "检查是否是有效token", notes = "")
    @GetMapping
    public ServiceResult<UserModel> authenticate(@RequestParam String token) {

        JwtUtils.UserInfo user;
        try {
            user = JwtUtils.validateToken(token);
        } catch (TokenExpiredException e) {
            log.info("token已经过期");
            throw new MpRuntimeException(AuthErrorCode.TOKEN_EXPIRED);
        }
        if (user == null) {
            log.info("user is null");
            throw new MpRuntimeException(AuthErrorCode.AUTHENTICATION_FAILED);
        }

        UserModel userModel = UserUtils.getUser(user.getId());

        UserDto userDto = userService.userModel2UserDtoWithoutOrg(userModel);

        return ServiceResult.success(userDto);

    }

}
