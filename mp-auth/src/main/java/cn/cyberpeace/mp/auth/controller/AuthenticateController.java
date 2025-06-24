 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthenticateController.java.
 // File size 3642.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

 /**
  * 验证控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "认证相关接口", tags = "AUTH-认证相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/authenticate")
public class AuthenticateController {

     /**
      * 用户服务
      */
     @Autowired
    private UserService userService;

     /**
      * 第三sso服务
      */
     @Autowired
    private ThirdSsoService thirdSsoService;

     /**
      *
      */
     @Autowired
    private TokenMapper tokenMapper;

     /**
      * auth配置
      */
     @Autowired
    private AuthConfig authConfig;

     /**
      * 进行身份验证
      *
      * @param request  请求
      * @param response 响应
      * @param token    令牌
      * @return {@link ServiceResult }<{@link UserModel }>
      */
     @ApiOperation(value = "检查是否是有效token", notes = "")
    @ApiImplicitParam(name = "token", value = "token", dataTypeClass = String.class)
    @GetMapping
    public ServiceResult<UserModel> authenticate(HttpServletRequest request, HttpServletResponse response, @RequestParam String token) {
        if (StringUtils.isBlank(token)) {
            log.warn("jwt token is blank");
            throw new MpRuntimeException(AuthErrorCode.AUTHENTICATION_FAILED);
        }
        TokenModel tokenModel = tokenMapper.getByJwt(token);
        if (tokenModel != null && !thirdSsoService.tokenCheck(tokenModel.getLoginToken())) {
            try {
                response.sendRedirect(String.format("%s", authConfig.getThirdIp()));
            } catch (IOException e) {
                log.error("sendRedirect error = ", e);
            }
        }
        JwtUtils.UserInfo user = verifyToken(token);
        UserModel userModel = UserUtils.getUser(user.getId());
        UserBo userBo = userService.userModel2UserDtoWithoutOrg(userModel);
        return ResultCommon.success(userBo);
    }

     /**
      * 验证令牌
      *
      * @param token 令牌
      * @return {@link JwtUtils.UserInfo }
      */
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
