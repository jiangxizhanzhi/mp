package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetails;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.response.login.LoginResp;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Objects;


@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final ObjectMapper mapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private OperateLogService operateLogService;

    @Resource(name = AuthCacheConfig.LOGIN_FAILURE)
    private Cache<String, Integer> loginFailureCache;

    @Autowired
    public CustomAuthenticationSuccessHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        UserModel user = null;
        ServiceResult<Object> result;

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUserDetails) {
            user = ((AuthUserDetails) principal).getUser();
        }

        if (Objects.isNull(user)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result = new ServiceResult<>(null, BaseErrorCode.ERROR);
        } else {
            recordLoginLog(user, request);
            AuthErrorCode errorCode = checkUserErrorStatus(user);
            if (Objects.nonNull(errorCode)) {
                response.setStatus(HttpServletResponse.SC_OK);
                result = new ServiceResult<>(null, errorCode);
            } else {
                String jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(user.getId(), user.getUsername(), user.getTokenSecret()));
                ObjResult<UserModel> userResult = new ObjResult(user, new UserFields().getAvailableFields());
                UserBo userBo = userService.userModel2UserDto(userResult.getObj());
                LoginResp loginResp = new LoginResp();
                loginResp.setUser(userBo);
                loginResp.setToken(jwt);

                



                
                

                
                

                



                result = ServiceResult.builder().source(loginResp).build();

                
                if (user.getInitCheck() == UserModel.INT_CHECK_YES) {
                    result = ServiceResult.builder().source(loginResp).serviceCode(AuthErrorCode.NEED_UPDATE_CREDENTIALS).build();
                }
                String pwdErrorCountCacheKey = AuthConstant.PASSWORD_ERROR_COUNT_KEY + ":" + user.getUsername();
                loginFailureCache.invalidate(pwdErrorCountCacheKey);
                log.info("Login success! {}", user.getUsername());

            }
        }

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result.asResult());
        writer.flush();
    }


    private AuthErrorCode checkUserErrorStatus(UserModel user) {
        String errorStatusMsg = null;
        AuthErrorCode errorCode = null;

        
        if (Objects.equals(user.getIsAudited(), UserModel.Status.NOT_AUDITED)) {
            errorStatusMsg = "user not audited!";
            errorCode = AuthErrorCode.ACCOUNT_NOT_AUDITED;
        }

        
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(user.getExpiredTime()) && now.isAfter(user.getExpiredTime())) {
            errorStatusMsg = "user expired!";
            errorCode = AuthErrorCode.ACCOUNT_EXPIRED;
        }

        
        if (Objects.equals(user.getIsEnabled(), UserModel.Status.DISABLED)) {
            errorStatusMsg = "user is forbidend!";
            errorCode = AuthErrorCode.ACCOUNT_DISABLED;
        }

        log.info("Login blocked! {}", errorStatusMsg);
        return errorCode;
    }

    
    private boolean checkSysActivationStatus() {
        
        
        
        return false;
    }


    private void recordLoginLog(UserModel user, HttpServletRequest request) {
        OperationLogRequest logModel = new OperationLogRequest();
        
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());
        logModel.setOpType(OpTypeEnums.LOGIN.ordinal());
        logModel.setOpTarget(MessageCommon.get(OpTargetEnums.USER.getCode()));
        
        logModel.setTitle(MessageCommon.get(AuthBasei18nCode.LOGIN));
        
        logModel.setDescription(MessageCommon.get(AuthBasei18nCode.LOGIN_USERNAME_PASSWORD));

        
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        logModel.setUserType(LogConstant.USER_TYPE_USER);
        logModel.setOpIp(SnUtils.getRealRemoteIp(request));

        

        operateLogService.create(logModel);
    }


}
