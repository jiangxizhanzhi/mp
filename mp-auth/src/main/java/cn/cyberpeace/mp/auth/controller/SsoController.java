package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.event.UserEventPublisher;
import cn.cyberpeace.mp.auth.extensions.security.event.UserLogoutEvent;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/7/19 11:27
 */
@Api(value = "单点登录相关接口", tags = "单点登录相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/sso")
public class SsoController {

    @Autowired
    private UserService userService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserEventPublisher userEventPublisher ;

    @ApiOperation(value = "根据token查询用户信息")
    @ApiImplicitParam(name = "token", value = "token", dataTypeClass = String.class)
    @GetMapping("/query_user_by_token")
    public ServiceResult<UserBo> queryUserByToken(@RequestParam String token) {
        log.info("SsoController.queryUserByToken begin. token:{}", token);
        JwtUtils.UserInfo user = verifyToken(token);
        UserBo userBo = userService.getUserDtoNoRestrict(user.getId());
        log.info("SsoController.queryUserByToken end. userBo:{}", userBo);
        return ResultCommon.success(userBo);
    }

    @ApiOperation(value = "退出登录")
    @ApiImplicitParam(name = "token", value = "token", dataTypeClass = String.class)
    @PostMapping("/logout")
    public ServiceResult<Objects> logout(@RequestParam String token, HttpServletRequest request) {
        log.info("SsoController.logout begin. token:{}", token);
        UserModel user = null;
        try {
            user = JwtCommon.verifyToken(token);
        } catch (Exception e) {
            log.error("logout verify token error for {}", e.getMessage());
        }
        String userName = null;
        if (Objects.nonNull(user)) {
            if (Objects.nonNull(authConfig.getLogoutResetSecret()) && authConfig.getLogoutResetSecret()) {
                // 退出是否重置秘钥
                var updateUser = new UserModel();
                updateUser.setId(user.getId());
                updateUser.initDefault("tokenSecret");
                userService.update(updateUser);
            }
            userName = user.getName();
            recordLogoutLog(user, request);
            userEventPublisher.publishEvent(new UserLogoutEvent(JsonCommon.beanToMap(user)));
        }

        if (StringUtils.isNotBlank(userName)) {
            log.info("user:{} has logout!", userName);
        }
        return new ServiceResult().asResult();
    }

    private void recordLogoutLog(UserModel user, HttpServletRequest request) {
        OperationLogRequest logModel = new OperationLogRequest();
        //操作类型
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());
        logModel.setOpType(OpTypeEnums.LOGOUT.ordinal());
        logModel.setOpTarget(MessageCommon.get(OpTargetEnums.USER.getCode()));
        //操作主题
        logModel.setTitle(MessageCommon.get(AuthBasei18nCode.LOGOUT));
        //操作描述
        logModel.setDescription(MessageCommon.get(AuthBasei18nCode.LOGOUT));

        //操作用户
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        logModel.setUserType(LogConstant.USER_TYPE_USER);

        logModel.setOpIp(SnUtils.getRealRemoteIp(request));

        // 记录登出日志
        operateLogService.create(logModel);
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
