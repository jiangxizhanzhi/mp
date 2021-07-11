package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.extensions.security.event.UserEventPublisher;
import cn.cyberpeace.mp.auth.extensions.security.event.UserLogoutEvent;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;


@Slf4j
@Component
public class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper mapper;

    private final UserService userService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserEventPublisher userEventPublisher ;

    @Autowired
    public HttpLogoutSuccessHandler(MappingJackson2HttpMessageConverter messageConverter, UserService userService) {
        this.mapper = messageConverter.getObjectMapper();
        this.userService = userService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        UserModel user = null;
        try {
            user = JwtCommon.verifyToken(request);
        } catch (Exception e) {
            log.error("logout verify token error for {}", e.getMessage());
        }
        String userName = null;
        if (Objects.nonNull(user)) {
            if (Objects.nonNull(authConfig.getLogoutResetSecret()) && authConfig.getLogoutResetSecret()) {
                if (!StringUtils.equals(user.getId(), authConfig.getLogoutKeepSecretUser())) {
                    
                    var updateUser = new UserModel();
                    updateUser.setId(user.getId());
                    updateUser.initDefault("tokenSecret");
                    userService.update(updateUser);
                }
            }
            userName = user.getName();
            recordLogoutLog(user, request);
            userEventPublisher.publishEvent(new UserLogoutEvent(JsonCommon.beanToMap(user)));
        }

        if (StringUtils.isNotBlank(userName)) {
            log.info("user:{} has logout!", userName);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ServiceResult<Object> result = new ServiceResult<>(null);
        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result.asResult());
        writer.flush();
    }

    private void recordLogoutLog(UserModel user, HttpServletRequest request) {
        OperationLogRequest logModel = new OperationLogRequest();
        
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());
        logModel.setOpType(OpTypeEnums.LOGOUT.ordinal());
        logModel.setOpTarget(MessageCommon.get(OpTargetEnums.USER.getCode()));
        
        logModel.setTitle(MessageCommon.get(AuthBasei18nCode.LOGOUT));
        
        logModel.setDescription(MessageCommon.get(AuthBasei18nCode.LOGOUT));

        
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        logModel.setUserType(LogConstant.USER_TYPE_USER);

        logModel.setOpIp(SnUtils.getRealRemoteIp(request));

        
        operateLogService.create(logModel);
    }
}
