 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RegisterController.java.
 // File size 3501.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller.register;


import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.register.RegisterRequest;
import cn.cyberpeace.mp.auth.service.RegisterService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.KaptchaUtil;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

 /**
  * 注册控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "注册接口", tags = "AUTH-注册接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/register")
public class RegisterController {

     /**
      * 注册服务
      */
     @Autowired
    private RegisterService registerService;

     /**
      * 注册
      *
      * @param req 要求
      * @return {@link ServiceResult }<{@link UserModel }>
      */
     @ApiOperation(value = "注册接口", notes = "")
    @ApiImplicitParam(name = "req", value = "注册请求体", dataTypeClass = RegisterRequest.class, required = true, dataType = "body")
    @PostMapping
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "注册新用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> register(@RequestBody @Validated RegisterRequest req) {
        ServiceResult<Object> checkResult = KaptchaUtil.checkKaptcha(req.getVerifyCode());
        if (Boolean.FALSE.equals(checkResult.getSource())) {
            throw new MpRuntimeException(checkResult.getServiceCode(), checkResult.getDetail());
        }
        String encryptedPwd = req.getPassword();
        req.setPassword(PasswordUtils.desDecrypt(encryptedPwd));
        ObjServiceResult<UserModel> result = registerService.register(req);

        if (result.getSource() != null) {
            UserModel userModel = result.getSource().getObj();
            JwtUtils.addResponseHeaderWithJWT(userModel);
            HttpServletRequest request = ServletUtils.getRequest();
            request.setAttribute(LogConstant.USER_ID_KEY, userModel.getId());
            request.setAttribute(LogConstant.USER_NAME_KEY, userModel.getUsername());
            request.setAttribute(LogConstant.USER_TYPE_KEY,  LogConstant.USER_TYPE_USER);
            return ResultCommon.success(userModel);
        }

        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

}
