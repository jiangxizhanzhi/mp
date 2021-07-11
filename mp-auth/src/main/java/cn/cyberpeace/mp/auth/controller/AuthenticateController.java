package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@Api(value = "认证相关接口", tags = "AUTH-认证相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/authenticate")
public class AuthenticateController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "检查是否是有效token", notes = "")
    @ApiImplicitParam(name = "token", value = "token", dataTypeClass = String.class)
    @GetMapping
    public ServiceResult<UserModel> authenticate(@RequestParam String token) {
        JwtUtils.UserInfo user = verifyToken(token);
        UserModel userModel = UserUtils.getUser(user.getId());
        UserBo userBo = userService.userModel2UserDtoWithoutOrg(userModel);
        return ResultCommon.success(userBo);
    }

    private JwtUtils.UserInfo verifyToken(String token) {
        JwtUtils.UserInfo user = null;
        if (StringUtils.isNotBlank(token)) {
            user = JwtUtils.validateToken(token);
        }

        if (Objects.isNull(user)) {
            log.warn("user from jwt token is null, token:{}", token);
            throw new MpRuntimeException(AuthErrorCode.AUTHENTICATION_FAILED);
        }
        return user;
    }

}
